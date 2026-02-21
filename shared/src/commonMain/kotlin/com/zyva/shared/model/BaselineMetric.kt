package com.zyva.shared.model

import com.zyva.shared.platform.currentTimeMillis
import com.zyva.shared.platform.randomUUID

data class BaselineMetric(
    val id: String = randomUUID(),
    val metricType: BaselineMetricType,
    val mean: Double,
    val standardDeviation: Double,
    val sampleCount: Int,
    val windowStartDate: Long,
    val windowEndDate: Long,
    val updatedAt: Long = currentTimeMillis(),
) {
    val isValid: Boolean
        get() = sampleCount >= 3 && standardDeviation > 0

    fun zScore(value: Double): Double =
        if (standardDeviation > 0) (value - mean) / standardDeviation else 0.0
}
