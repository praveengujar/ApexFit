package com.apexfit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
    val todayWorkouts: List<WorkoutRecordEntity> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val streak: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedDate,
        dailyMetricRepo.observeRecent(7),
        dailyMetricRepo.observeRecent(365),
    ) { date, recentMetrics, allMetrics ->
        val dateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val todayMetric = recentMetrics.find { it.date == dateMillis }

        HomeUiState(
            todayMetric = todayMetric,
            weekMetrics = recentMetrics,
            selectedDate = date,
            streak = computeStreak(allMetrics),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(),
    )

    fun navigateDate(offset: Int) {
        val next = _selectedDate.value.plusDays(offset.toLong())
        if (next.isAfter(LocalDate.now())) return
        _selectedDate.value = next
    }

    // Baselines (28-day rolling averages from weekMetrics for now)
    fun hrvBaseline(): Double? {
        val values = uiState.value.weekMetrics.mapNotNull { it.hrvRMSSD }
        return if (values.isEmpty()) null else values.average()
    }

    fun rhrBaseline(): Double? {
        val values = uiState.value.weekMetrics.mapNotNull { it.restingHeartRate }
        return if (values.isEmpty()) null else values.average()
    }

    private fun computeStreak(metrics: List<DailyMetricEntity>): Int {
        if (metrics.isEmpty()) return 0
        val zone = ZoneId.systemDefault()
        val sortedDates = metrics
            .map { java.time.Instant.ofEpochMilli(it.date).atZone(zone).toLocalDate() }
            .distinct()
            .sortedDescending()
        var streak = 0
        var expected = LocalDate.now()
        for (date in sortedDates) {
            if (date == expected) {
                streak++
                expected = expected.minusDays(1)
            } else if (date.isBefore(expected)) {
                break
            }
        }
        return streak
    }
}
