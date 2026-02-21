package com.zyva.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.entity.WorkoutRecordEntity
import com.zyva.core.data.repository.DailyMetricRepository
import com.zyva.core.data.repository.WorkoutRepository
import com.zyva.core.domain.usecase.SyncHealthDataUseCase
import com.zyva.core.healthconnect.HealthConnectAvailability
import com.zyva.core.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class SyncState {
    IDLE, SYNCING, DONE, ERROR
}

data class HomeUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
    val todayWorkouts: List<WorkoutRecordEntity> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val streak: Int = 0,
    val syncProgress: String = "",
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
    private val syncUseCase: SyncHealthDataUseCase,
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {

    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _syncProgress = MutableStateFlow("")

    private var hasSyncedThisSession = false

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    init {
        triggerInitialSync()
    }

    private fun triggerInitialSync() {
        viewModelScope.launch {
            if (hasSyncedThisSession) return@launch
            if (healthConnectManager.availability != HealthConnectAvailability.AVAILABLE) return@launch

            try {
                if (!healthConnectManager.hasAllPermissions()) return@launch
            } catch (_: Exception) {
                return@launch
            }

            hasSyncedThisSession = true
            _syncState.value = SyncState.SYNCING

            try {
                val today = LocalDate.now()
                val zone = ZoneId.systemDefault()

                // Backfill past 7 days (oldest first) for baselines
                for (dayOffset in 7 downTo 1) {
                    val date = today.minusDays(dayOffset.toLong())
                    val dateMillis = date.atStartOfDay(zone).toInstant().toEpochMilli()
                    if (dailyMetricRepo.isComputed(dateMillis)) continue
                    _syncProgress.value = "Processing ${date}..."
                    syncUseCase.syncForDate(date)
                }

                // Sync today (always recompute)
                _syncProgress.value = "Processing today..."
                syncUseCase.syncForDate(today)

                _syncState.value = SyncState.DONE
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Sync failed", e)
                _syncState.value = SyncState.ERROR
            }
        }
    }

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
