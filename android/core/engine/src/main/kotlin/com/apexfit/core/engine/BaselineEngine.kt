package com.apexfit.core.engine

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

data class BaselineResult(
    val mean: Double,
    val standardDeviation: Double,
    val sampleCount: Int,
    val windowDays: Int,
) {
    val isValid: Boolean
        get() = sampleCount >= 3 && standardDeviation > 0
}

object BaselineEngine {

    fun computeBaseline(
        values: List<Double>,
        windowDays: Int = 28,
        minimumSamples: Int = 3,
    ): BaselineResult? {
        if (values.isEmpty()) return null

        val recentValues = values.takeLast(windowDays)

        if (recentValues.size >= minimumSamples) {
            return BaselineResult(
                mean = recentValues.mean,
                standardDeviation = max(recentValues.standardDeviation, 0.001),
                sampleCount = recentValues.size,
                windowDays = windowDays,
            )
        }

        // Fallback: use all available data if enough samples
        if (values.size >= minimumSamples) {
            val fallbackValues = values.takeLast(minimumSamples)
            return BaselineResult(
                mean = fallbackValues.mean,
                standardDeviation = max(fallbackValues.standardDeviation, 0.001),
                sampleCount = fallbackValues.size,
                windowDays = fallbackValues.size,
            )
        }

        return null
    }

    fun zScore(value: Double, baseline: BaselineResult): Double {
        if (baseline.standardDeviation <= 0) return 0.0
        return (value - baseline.mean) / baseline.standardDeviation
    }

    fun updateBaseline(
        current: BaselineResult,
        newValue: Double,
        alpha: Double = 0.1,
    ): BaselineResult {
        val newMean = current.mean * (1 - alpha) + newValue * alpha
        val newVariance = current.standardDeviation.pow(2) * (1 - alpha) +
            (newValue - newMean).pow(2) * alpha
        val newStdDev = sqrt(max(newVariance, 0.001))

        return BaselineResult(
            mean = newMean,
            standardDeviation = newStdDev,
            sampleCount = current.sampleCount + 1,
            windowDays = current.windowDays,
        )
    }
}
