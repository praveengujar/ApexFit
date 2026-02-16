package com.apexfit.core.domain.usecase

import com.apexfit.core.data.entity.BaselineMetricEntity
import com.apexfit.core.data.repository.BaselineRepository
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.engine.BaselineEngine
import com.apexfit.core.model.BaselineMetricType
import com.apexfit.core.model.config.ScoringConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateBaselinesUseCase @Inject constructor(
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

        // Use metricType as ID to ensure upsert behavior
        val entity = BaselineMetricEntity(
            id = type.name,
            metricType = type.name,
            mean = baseline.mean,
            standardDeviation = baseline.standardDeviation,
            sampleCount = baseline.sampleCount,
            updatedAt = System.currentTimeMillis(),
        )
        baselineRepo.insert(entity)
    }
}
