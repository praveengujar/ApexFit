package com.apexfit.feature.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.SleepSessionEntity
import com.apexfit.core.data.entity.SleepStageEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class SleepUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
    val mainSleep: SleepSessionEntity? = null,
    val sleepStages: List<SleepStageEntity> = emptyList(),
)

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val sleepRepo: SleepRepository,
) : ViewModel() {

    private val _sleepData = MutableStateFlow<Pair<SleepSessionEntity?, List<SleepStageEntity>>>(null to emptyList())

    val uiState: StateFlow<SleepUiState> = dailyMetricRepo.observeRecent(30)
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

            SleepUiState(
                todayMetric = today,
                weekMetrics = week,
                mainSleep = _sleepData.value.first,
                sleepStages = _sleepData.value.second,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SleepUiState())

    init {
        loadSleepData()
    }

    private fun loadSleepData() {
        viewModelScope.launch {
            val todayMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val yesterdayMillis = LocalDate.now().minusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Get today or yesterday's metrics to find dailyMetricId
            val metric = dailyMetricRepo.getByDate(todayMillis)
                ?: dailyMetricRepo.getByDate(yesterdayMillis)

            if (metric != null) {
                val mainSleep = sleepRepo.getMainSleep(metric.id)
                val stages = if (mainSleep != null) {
                    sleepRepo.getStages(mainSleep.id)
                } else {
                    emptyList()
                }
                _sleepData.value = mainSleep to stages
            }
        }
    }

    fun sleepInsightText(sleepPerformance: Double): String {
        return when {
            sleepPerformance >= 85 -> "Great sleep! Your sleep metrics are looking solid. Keep up your current sleep habits for optimal recovery."
            sleepPerformance >= 60 -> "Some aspects of your sleep need improvement. Maintaining Sleep Efficiency while working on consistency could help improve your overall sleep."
            else -> "Your sleep performance is below optimal. Focus on getting more hours of sleep and maintaining a consistent sleep schedule."
        }
    }
}
