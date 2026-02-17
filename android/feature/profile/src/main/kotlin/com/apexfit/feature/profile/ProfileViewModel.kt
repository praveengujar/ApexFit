package com.apexfit.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.UserProfileRepository
import com.apexfit.core.data.repository.WorkoutRepository
import com.apexfit.core.engine.LongevityEngine
import com.apexfit.core.engine.LongevityMetricID
import com.apexfit.core.engine.LongevityMetricInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

enum class TimePeriod(val days: Int?) {
    ONE_MONTH(30),
    THREE_MONTHS(90),
    ALL_TIME(null),
}

data class ActivityTypeStats(
    val workoutType: String,
    val displayName: String,
    val count: Int,
    val avgStrain: Double,
    val proportion: Float,
)

data class ProfileUiState(
    val isLoading: Boolean = true,
    // Header
    val displayName: String = "",
    val initials: String = "",
    val age: Int? = null,
    val memberSince: String = "",
    // Achievement Cards
    val level: Int = 0,
    val greenRecoveryCount: Int = 0,
    val apexFitAge: Double? = null,
    val yearsYoungerOlder: Double = 0.0,
    // Day Streak
    val dayStreak: Int = 0,
    // Data Highlights
    val highlightsPeriod: TimePeriod = TimePeriod.ALL_TIME,
    val bestSleepPct: Double? = null,
    val peakRecoveryPct: Double? = null,
    val maxStrain: Double? = null,
    // Streaks
    val sleepStreak: Int = 0,
    val greenRecoveryStreak: Int = 0,
    val strainStreak: Int = 0,
    // Notable Stats
    val lowestRHR: Double? = null,
    val highestRHR: Double? = null,
    val lowestHRV: Double? = null,
    val highestHRV: Double? = null,
    val maxHeartRate: Double? = null,
    val longestSleepHours: Double? = null,
    val lowestRecovery: Double? = null,
    // Activity Summary
    val activityPeriod: TimePeriod = TimePeriod.ALL_TIME,
    val totalActivities: Int = 0,
    val activityBreakdown: List<ActivityTypeStats> = emptyList(),
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
    private val userProfileRepo: UserProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private var allMetrics: List<DailyMetricEntity> = emptyList()
    private var allWorkouts: List<WorkoutRecordEntity> = emptyList()
    private var chronologicalAge: Double = 30.0

    init {
        loadData()
    }

    fun setHighlightsPeriod(period: TimePeriod) {
        _uiState.value = _uiState.value.copy(highlightsPeriod = period)
        recomputeHighlights(period)
    }

    fun setActivityPeriod(period: TimePeriod) {
        _uiState.value = _uiState.value.copy(activityPeriod = period)
        recomputeActivity(period)
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val nowMillis = LocalDate.now().toEpochMillis()
            val profile = userProfileRepo.getProfile()

            // Header
            val name = profile?.displayName ?: ""
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
                .take(2)
                .joinToString("")
            val age = profile?.dateOfBirth?.let { dob ->
                val dobDate = Instant.ofEpochMilli(dob).atZone(ZoneId.systemDefault()).toLocalDate()
                Period.between(dobDate, LocalDate.now()).years
            }
            chronologicalAge = age?.toDouble() ?: 30.0
            val memberSince = profile?.createdAt?.let { millis ->
                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
            } ?: ""

            // Load all data
            allMetrics = dailyMetricRepo.getRange(0L, nowMillis + 86400000L)
            allWorkouts = workoutRepo.getRange(0L, nowMillis + 86400000L)

            val sortedByDate = allMetrics.sortedByDescending { it.date }

            // Level
            val greenCount = allMetrics.count { it.recoveryZone == "GREEN" }
            val level = greenCount / 6

            // ApexFit Age
            val (apexFitAge, yearsYounger) = computeApexFitAge()

            // Day streak
            val dayStreak = computeDayStreak(sortedByDate)

            // Conditional streaks
            val sleepStreak = computeConditionalStreak(sortedByDate) {
                it.sleepPerformance != null && it.sleepPerformance!! >= 70.0
            }
            val greenStreak = computeConditionalStreak(sortedByDate) {
                it.recoveryZone == "GREEN"
            }
            val strainStreak = computeConditionalStreak(sortedByDate) {
                it.strainScore != null && it.strainScore!! >= 10.0
            }

            // Notable stats (all time)
            val rhrValues = allMetrics.mapNotNull { it.restingHeartRate }
            val hrvValues = allMetrics.mapNotNull { it.hrvRMSSD }
            val sleepValues = allMetrics.mapNotNull { it.totalSleepHours }
            val recoveryValues = allMetrics.mapNotNull { it.recoveryScore }
            val workoutMaxHR = allWorkouts.mapNotNull { it.maxHeartRate }

            _uiState.value = ProfileUiState(
                isLoading = false,
                displayName = name,
                initials = initials,
                age = age,
                memberSince = memberSince,
                level = level,
                greenRecoveryCount = greenCount,
                apexFitAge = apexFitAge,
                yearsYoungerOlder = yearsYounger,
                dayStreak = dayStreak,
                highlightsPeriod = TimePeriod.ALL_TIME,
                sleepStreak = sleepStreak,
                greenRecoveryStreak = greenStreak,
                strainStreak = strainStreak,
                lowestRHR = rhrValues.minOrNull(),
                highestRHR = rhrValues.maxOrNull(),
                lowestHRV = hrvValues.minOrNull(),
                highestHRV = hrvValues.maxOrNull(),
                maxHeartRate = workoutMaxHR.maxOrNull(),
                longestSleepHours = sleepValues.maxOrNull(),
                lowestRecovery = recoveryValues.minOrNull(),
                activityPeriod = TimePeriod.ALL_TIME,
            )

            recomputeHighlights(TimePeriod.ALL_TIME)
            recomputeActivity(TimePeriod.ALL_TIME)
        }
    }

    private fun recomputeHighlights(period: TimePeriod) {
        val filtered = filterByPeriod(allMetrics, period)
        _uiState.value = _uiState.value.copy(
            bestSleepPct = filtered.mapNotNull { it.sleepPerformance }.maxOrNull(),
            peakRecoveryPct = filtered.mapNotNull { it.recoveryScore }.maxOrNull(),
            maxStrain = filtered.mapNotNull { it.strainScore }.maxOrNull(),
        )
    }

    private fun recomputeActivity(period: TimePeriod) {
        val filtered = filterWorkoutsByPeriod(allWorkouts, period)
        val grouped = filtered.groupBy { it.workoutType }
        val maxCount = grouped.values.maxOfOrNull { it.size } ?: 1
        val breakdown = grouped.map { (type, list) ->
            ActivityTypeStats(
                workoutType = type,
                displayName = type.replace("_", " ").uppercase(),
                count = list.size,
                avgStrain = list.mapNotNull { it.strainScore }.let { scores ->
                    if (scores.isEmpty()) 0.0 else scores.average()
                },
                proportion = list.size.toFloat() / maxCount.toFloat(),
            )
        }.sortedByDescending { it.count }

        _uiState.value = _uiState.value.copy(
            totalActivities = filtered.size,
            activityBreakdown = breakdown,
        )
    }

    private fun filterByPeriod(
        metrics: List<DailyMetricEntity>,
        period: TimePeriod,
    ): List<DailyMetricEntity> {
        val days = period.days ?: return metrics
        val cutoff = LocalDate.now().minusDays(days.toLong()).toEpochMillis()
        return metrics.filter { it.date >= cutoff }
    }

    private fun filterWorkoutsByPeriod(
        workouts: List<WorkoutRecordEntity>,
        period: TimePeriod,
    ): List<WorkoutRecordEntity> {
        val days = period.days ?: return workouts
        val cutoff = LocalDate.now().minusDays(days.toLong()).toEpochMillis()
        return workouts.filter { it.startDate >= cutoff }
    }

    private fun computeDayStreak(sortedDesc: List<DailyMetricEntity>): Int {
        if (sortedDesc.isEmpty()) return 0
        var streak = 0
        var checkDate = LocalDate.now()
        val metricDates = sortedDesc.map {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet()
        while (metricDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private fun computeConditionalStreak(
        sortedDesc: List<DailyMetricEntity>,
        condition: (DailyMetricEntity) -> Boolean,
    ): Int {
        if (sortedDesc.isEmpty()) return 0
        var streak = 0
        var checkDate = LocalDate.now()
        val metricByDate = sortedDesc.associateBy {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        while (true) {
            val metric = metricByDate[checkDate] ?: break
            if (!condition(metric)) break
            streak++
            checkDate = checkDate.minusDays(1)
        }
        return streak
    }

    private suspend fun computeApexFitAge(): Pair<Double?, Double> {
        val now = LocalDate.now()
        val nowMillis = now.toEpochMillis()
        val sixMonthAgo = now.minusDays(180)
        val thirtyDaysAgo = now.minusDays(30)
        val weekAgo = now.minusDays(7)

        val metrics180 = dailyMetricRepo.getRange(sixMonthAgo.toEpochMillis(), nowMillis)
        if (metrics180.isEmpty()) return null to 0.0

        val metrics30 = metrics180.filter { it.date >= thirtyDaysAgo.toEpochMillis() }
        val inputs = buildLongevityInputs(metrics180, metrics30)

        val result = LongevityEngine.compute(
            chronologicalAge = chronologicalAge,
            inputs = inputs,
            weekStartMillis = weekAgo.toEpochMillis(),
            weekEndMillis = nowMillis,
        )
        return result.apexFitAge to result.yearsYoungerOlder
    }

    private fun buildLongevityInputs(
        metrics180: List<DailyMetricEntity>,
        metrics30: List<DailyMetricEntity>,
    ): List<LongevityMetricInput> {
        val inputs = mutableListOf<LongevityMetricInput>()

        fun List<Double>.avgOrNull(): Double? = if (isEmpty()) null else average()

        fun addMetric(id: LongevityMetricID, extractor: (DailyMetricEntity) -> Double?) {
            inputs.add(LongevityMetricInput(
                id = id,
                sixMonthAvg = metrics180.mapNotNull(extractor).avgOrNull(),
                thirtyDayAvg = metrics30.mapNotNull(extractor).avgOrNull(),
            ))
        }

        addMetric(LongevityMetricID.SLEEP_CONSISTENCY) { it.sleepConsistency }
        addMetric(LongevityMetricID.HOURS_OF_SLEEP) { it.totalSleepHours }

        // Zone approximations
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

    private fun weeklyZoneAvg(metrics: List<DailyMetricEntity>, zone13: Boolean): Double {
        if (metrics.isEmpty()) return 0.0
        val totalWorkouts = metrics.sumOf { it.workoutCount }
        val totalDays = metrics.size.coerceAtLeast(1)
        val avgDailyWorkouts = totalWorkouts.toDouble() / totalDays
        val avgWeeklyMinutes = avgDailyWorkouts * 7 * 45.0
        val fraction = if (zone13) 0.7 else 0.3
        return (avgWeeklyMinutes * fraction) / 60.0
    }

    private fun LocalDate.toEpochMillis(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
