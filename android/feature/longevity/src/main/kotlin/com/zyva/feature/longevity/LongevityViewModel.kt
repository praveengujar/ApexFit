package com.zyva.feature.longevity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.entity.WorkoutRecordEntity
import com.zyva.core.data.repository.DailyMetricRepository
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.data.repository.WorkoutRepository
import com.zyva.core.domain.usecase.SyncHealthDataUseCase
import com.zyva.core.engine.LongevityEngine
import com.zyva.core.engine.LongevityMetricID
import com.zyva.core.engine.LongevityMetricInput
import com.zyva.core.engine.LongevityResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class LongevityUiState(
    val isLoading: Boolean = true,
    val result: LongevityResult? = null,
    val weeklyTrend: List<LongevityResult> = emptyList(),
    val selectedWeekEnd: LocalDate = LocalDate.now(),
    val chronologicalAge: Double = 30.0,
    val expandedMetricId: LongevityMetricID? = null,
)

@HiltViewModel
class LongevityViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
    private val userProfileRepo: UserProfileRepository,
    private val syncUseCase: SyncHealthDataUseCase,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow(LongevityUiState())
    val uiState: StateFlow<LongevityUiState> = _uiState

    init {
        loadData()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                syncUseCase.syncForDate()
                loadData()
            } catch (_: Exception) {}
            _isRefreshing.value = false
        }
    }

    fun navigateWeek(offset: Int) {
        val current = _uiState.value.selectedWeekEnd
        val next = current.plusDays((offset * 7).toLong())
        if (next.isAfter(LocalDate.now())) return
        _uiState.value = _uiState.value.copy(selectedWeekEnd = next)
        loadData()
    }

    fun toggleMetricExpanded(id: LongevityMetricID) {
        val current = _uiState.value.expandedMetricId
        _uiState.value = _uiState.value.copy(
            expandedMetricId = if (current == id) null else id,
        )
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val profile = userProfileRepo.getProfile()
            val dobMillis = profile?.dateOfBirth
            val age = if (dobMillis != null) {
                val dob = Instant.ofEpochMilli(dobMillis)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                Period.between(dob, LocalDate.now()).years.toDouble()
            } else {
                30.0
            }

            val weekEnd = _uiState.value.selectedWeekEnd

            // Current week result
            val result = computeForWeekEnd(weekEnd, age)

            // 8-week trend
            val trend = (7 downTo 0).mapNotNull { weeksAgo ->
                val trendWeekEnd = weekEnd.minusDays((weeksAgo * 7).toLong())
                computeForWeekEnd(trendWeekEnd, age)
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                result = result,
                weeklyTrend = trend,
                chronologicalAge = age,
            )
        }
    }

    private suspend fun computeForWeekEnd(
        weekEnd: LocalDate,
        age: Double,
    ): LongevityResult? {
        val weekEndMillis = weekEnd.toEpochMillis()
        val sixMonthAgo = weekEnd.minusDays(180)
        val thirtyDaysAgo = weekEnd.minusDays(30)
        val weekAgo = weekEnd.minusDays(7)

        val metrics180 = dailyMetricRepo.getRange(sixMonthAgo.toEpochMillis(), weekEndMillis)
        if (metrics180.isEmpty()) return null

        val metrics30 = metrics180.filter { it.date >= thirtyDaysAgo.toEpochMillis() }

        // Get workouts for zone/strength aggregation
        val workouts180 = workoutRepo.observeRange(sixMonthAgo.toEpochMillis(), weekEndMillis)
            .let { flow ->
                // Use a suspend query instead
                emptyList<WorkoutRecordEntity>()
            }

        val inputs = buildInputs(metrics180, metrics30, weekEndMillis)

        return LongevityEngine.compute(
            chronologicalAge = age,
            inputs = inputs,
            weekStartMillis = weekAgo.toEpochMillis(),
            weekEndMillis = weekEndMillis,
        )
    }

    private fun buildInputs(
        metrics180: List<DailyMetricEntity>,
        metrics30: List<DailyMetricEntity>,
        weekEndMillis: Long,
    ): List<LongevityMetricInput> {
        val inputs = mutableListOf<LongevityMetricInput>()

        fun addMetric(id: LongevityMetricID, extractor: (DailyMetricEntity) -> Double?) {
            inputs.add(LongevityMetricInput(
                id = id,
                sixMonthAvg = metrics180.mapNotNull(extractor).averageOrNull(),
                thirtyDayAvg = metrics30.mapNotNull(extractor).averageOrNull(),
            ))
        }

        addMetric(LongevityMetricID.SLEEP_CONSISTENCY) { it.sleepConsistency }
        addMetric(LongevityMetricID.HOURS_OF_SLEEP) { it.totalSleepHours }

        // HR Zones — approximated from workout data
        val z13_180 = weeklyZoneAvg(metrics180, zone13 = true)
        val z13_30 = weeklyZoneAvg(metrics30, zone13 = true)
        inputs.add(LongevityMetricInput(
            id = LongevityMetricID.HR_ZONES_1_TO_3_WEEKLY,
            sixMonthAvg = z13_180.takeIf { it > 0 },
            thirtyDayAvg = z13_30.takeIf { it > 0 },
        ))
        val z45_180 = weeklyZoneAvg(metrics180, zone13 = false)
        val z45_30 = weeklyZoneAvg(metrics30, zone13 = false)
        inputs.add(LongevityMetricInput(
            id = LongevityMetricID.HR_ZONES_4_TO_5_WEEKLY,
            sixMonthAvg = z45_180.takeIf { it > 0 },
            thirtyDayAvg = z45_30.takeIf { it > 0 },
        ))

        inputs.add(LongevityMetricInput(id = LongevityMetricID.STRENGTH_ACTIVITY_WEEKLY, sixMonthAvg = null, thirtyDayAvg = null))

        addMetric(LongevityMetricID.DAILY_STEPS) { it.steps?.toDouble() }
        addMetric(LongevityMetricID.VO2_MAX) { it.vo2Max }
        addMetric(LongevityMetricID.RESTING_HEART_RATE) { it.restingHeartRate }

        inputs.add(LongevityMetricInput(id = LongevityMetricID.LEAN_BODY_MASS, sixMonthAvg = null, thirtyDayAvg = null))

        return inputs
    }

    /**
     * Approximate weekly zone hours from daily metrics.
     * Since WorkoutRecordEntity stores zone minutes per workout, and DailyMetricEntity
     * doesn't store aggregated zone data directly, we estimate from workout count and strain.
     * This is a simplified approximation — a more accurate version would query WorkoutRepository.
     */
    private fun weeklyZoneAvg(metrics: List<DailyMetricEntity>, zone13: Boolean): Double {
        if (metrics.isEmpty()) return 0.0
        // Rough approximation: assume average workout is 45 min,
        // zone 1-3 gets ~70% of time, zone 4-5 gets ~30%
        val totalWorkouts = metrics.sumOf { it.workoutCount }
        val totalDays = metrics.size.coerceAtLeast(1)
        val avgDailyWorkouts = totalWorkouts.toDouble() / totalDays
        val avgWeeklyMinutes = avgDailyWorkouts * 7 * 45.0 // 45 min per workout
        val fraction = if (zone13) 0.7 else 0.3
        return (avgWeeklyMinutes * fraction) / 60.0 // convert to hours
    }

    private fun List<Double>.averageOrNull(): Double? =
        if (isEmpty()) null else sum() / size

    private fun LocalDate.toEpochMillis(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val isCurrentWeek: Boolean
        get() {
            val today = LocalDate.now()
            return !_uiState.value.selectedWeekEnd.isBefore(today)
        }

    val daysUntilNextMonday: Int
        get() {
            val today = LocalDate.now()
            val dayOfWeek = today.dayOfWeek
            return when (dayOfWeek) {
                DayOfWeek.MONDAY -> 7
                DayOfWeek.SUNDAY -> 1
                else -> (DayOfWeek.MONDAY.value - dayOfWeek.value + 7) % 7
            }
        }

    val weekRangeString: String
        get() {
            val end = _uiState.value.selectedWeekEnd
            val start = end.minusDays(6)
            val formatter = DateTimeFormatter.ofPattern("MMM d")
            return "${start.format(formatter)} - ${end.format(formatter)}"
        }
}
