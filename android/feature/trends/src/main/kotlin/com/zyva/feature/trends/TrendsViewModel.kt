package com.zyva.feature.trends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.repository.DailyMetricRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

enum class TrendPeriod(val label: String, val days: Int) {
    WEEK("7D", 7),
    MONTH("30D", 30),
    QUARTER("90D", 90),
}

enum class TrendMetric(val displayName: String) {
    RECOVERY("Recovery"),
    STRAIN("Strain"),
    SLEEP("Sleep Performance"),
    HRV("HRV"),
    RHR("Resting Heart Rate"),
    STEPS("Steps"),
    CALORIES("Calories"),
}

data class TrendsUiState(
    val metrics: List<DailyMetricEntity> = emptyList(),
    val selectedPeriod: TrendPeriod = TrendPeriod.MONTH,
    val selectedMetric: TrendMetric = TrendMetric.RECOVERY,
)

@HiltViewModel
class TrendsViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(TrendPeriod.MONTH)
    private val _selectedMetric = MutableStateFlow(TrendMetric.RECOVERY)

    val uiState: StateFlow<TrendsUiState> = combine(
        dailyMetricRepo.observeRecent(90),
        _selectedPeriod,
        _selectedMetric,
    ) { allMetrics, period, metric ->
        val cutoff = LocalDate.now().minusDays(period.days.toLong())
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val filtered = allMetrics.filter { it.date >= cutoff }.sortedBy { it.date }

        TrendsUiState(
            metrics = filtered,
            selectedPeriod = period,
            selectedMetric = metric,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrendsUiState())

    fun setPeriod(period: TrendPeriod) {
        _selectedPeriod.value = period
    }

    fun setMetric(metric: TrendMetric) {
        _selectedMetric.value = metric
    }

    fun extractValue(metric: DailyMetricEntity, trendMetric: TrendMetric): Double? {
        return when (trendMetric) {
            TrendMetric.RECOVERY -> metric.recoveryScore
            TrendMetric.STRAIN -> metric.strainScore
            TrendMetric.SLEEP -> metric.sleepPerformance
            TrendMetric.HRV -> metric.hrvRMSSD
            TrendMetric.RHR -> metric.restingHeartRate
            TrendMetric.STEPS -> metric.steps?.toDouble()
            TrendMetric.CALORIES -> metric.activeCalories
        }
    }
}
