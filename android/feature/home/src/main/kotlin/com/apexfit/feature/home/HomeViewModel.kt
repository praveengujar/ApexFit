package com.apexfit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.WorkoutRepository
import com.apexfit.core.data.entity.WorkoutRecordEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
    val todayWorkouts: List<WorkoutRecordEntity> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
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
    ) { date, recentMetrics ->
        val dateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val todayMetric = recentMetrics.find { it.date == dateMillis }

        HomeUiState(
            todayMetric = todayMetric,
            weekMetrics = recentMetrics,
            selectedDate = date,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(),
    )

    // Baselines (28-day rolling averages from weekMetrics for now)
    fun hrvBaseline(): Double? {
        val values = uiState.value.weekMetrics.mapNotNull { it.hrvRMSSD }
        return if (values.isEmpty()) null else values.average()
    }

    fun rhrBaseline(): Double? {
        val values = uiState.value.weekMetrics.mapNotNull { it.restingHeartRate }
        return if (values.isEmpty()) null else values.average()
    }
}
