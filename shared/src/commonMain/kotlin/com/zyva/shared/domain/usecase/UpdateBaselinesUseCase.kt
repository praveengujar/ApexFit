package com.zyva.shared.domain.usecase

import com.zyva.shared.domain.repository.BaselineRepository
import com.zyva.shared.domain.repository.DailyMetricRepository
import com.zyva.shared.engine.BaselineEngine
import com.zyva.shared.model.BaselineMetric
import com.zyva.shared.model.BaselineMetricType
import com.zyva.shared.model.config.ScoringConfig
import com.zyva.shared.platform.currentTimeMillis

class UpdateBaselinesUseCase(
    private val dailyMetricRepo: DailyMetricRepository,
    private val baselineRepo: BaselineRepository,
    private val config: ScoringConfig,
) {
    suspend fun updateAll() {
        val windowDays = config.baselines.windowDays
        val minimumSamples = config.baselines.minimumSamples

        updateMetric(
            type = BaselineMetricType.HRV,
            values = dailyMetricRepo.getRecentHRV(windowDays),
            windowDays = windowDays,
            minimumSamples = minimumSamples,
        )

        updateMetric(
            type = BaselineMetricType.RESTING_HEART_RATE,
            values = dailyMetricRepo.getRecentRHR(windowDays),
            windowDays = windowDays,
            minimumSamples = minimumSamples,
        )

        updateMetric(
            type = BaselineMetricType.STRAIN,
            values = dailyMetricRepo.getRecentStrainScores(windowDays),
            windowDays = windowDays,
            minimumSamples = minimumSamples,
        )

        updateMetric(
            type = BaselineMetricType.SLEEP_PERFORMANCE,
            values = dailyMetricRepo.getRecentSleepHours(windowDays),
            windowDays = windowDays,
            minimumSamples = minimumSamples,
        )
    }

    private suspend fun updateMetric(
        type: BaselineMetricType,
        values: List<Double>,
        windowDays: Int,
        minimumSamples: Int,
    ) {
        val baseline = BaselineEngine.computeBaseline(
            values = values,
            windowDays = windowDays,
            minimumSamples = minimumSamples,
        ) ?: return

        val now = currentTimeMillis()
        val metric = BaselineMetric(
            id = type.name,
            metricType = type,
            mean = baseline.mean,
            standardDeviation = baseline.standardDeviation,
            sampleCount = baseline.sampleCount,
            windowStartDate = now - windowDays.toLong() * 86_400_000L,
            windowEndDate = now,
            updatedAt = now,
        )
        baselineRepo.insert(metric)
    }
}
