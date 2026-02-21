package com.zyva.feature.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.repository.DailyMetricRepository
import com.zyva.core.domain.usecase.SyncHealthDataUseCase
import com.zyva.core.model.RecoveryZone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.abs

data class RecoveryUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
)

@HiltViewModel
class RecoveryViewModel @Inject constructor(
    dailyMetricRepo: DailyMetricRepository,
    private val syncUseCase: SyncHealthDataUseCase,
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                syncUseCase.syncForDate()
            } catch (_: Exception) {}
            _isRefreshing.value = false
        }
    }

    val uiState: StateFlow<RecoveryUiState> = dailyMetricRepo.observeRecent(30)
        .map { metrics ->
            val todayMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val yesterdayMillis = LocalDate.now().minusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val today = metrics.find { it.date == todayMillis }
                ?: metrics.find { it.date == yesterdayMillis }

            val weekCutoff = LocalDate.now().minusDays(7)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val week = metrics.filter { it.date >= weekCutoff }.sortedBy { it.date }

            RecoveryUiState(
                todayMetric = today,
                weekMetrics = week,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecoveryUiState())

    private fun baseline(metrics: List<DailyMetricEntity>, extractor: (DailyMetricEntity) -> Double?): Double {
        val values = metrics.mapNotNull(extractor)
        return if (values.isEmpty()) 0.0 else values.average()
    }

    fun baselineHRV(metrics: List<DailyMetricEntity>): Double = baseline(metrics) { it.hrvRMSSD }

    fun baselineRHR(metrics: List<DailyMetricEntity>): Double = baseline(metrics) { it.restingHeartRate }

    fun baselineRespRate(metrics: List<DailyMetricEntity>): Double = baseline(metrics) { it.respiratoryRate }

    fun baselineSleepPerf(metrics: List<DailyMetricEntity>): Double = baseline(metrics) { it.sleepPerformance }

    fun generateInsight(todayHRV: Double, baselineHRV: Double): String {
        if (baselineHRV <= 0 || todayHRV <= 0) {
            return "Wear your device to sleep tonight for a full recovery analysis."
        }

        val hrvPctChange = ((todayHRV - baselineHRV) / baselineHRV) * 100
        val direction = if (hrvPctChange >= 0) "higher" else "lower"
        val pctStr = "${abs(hrvPctChange).toInt()}%"

        var text = "Your HRV is $pctStr $direction than usual"

        text += when {
            hrvPctChange < -10 -> " which can be due to stress, dehydration, or other lifestyle factors. Take it easy to let your body fully recover."
            hrvPctChange > 10 -> " indicating good recovery. Your body is primed for performance today."
            else -> ". Your metrics are within normal range. Maintain your current habits for consistent recovery."
        }

        return text
    }
}
