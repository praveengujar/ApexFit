package com.apexfit.shared.engine

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

enum class CorrelationDirection {
    POSITIVE,  // Behavior improves the metric
    NEGATIVE,  // Behavior worsens the metric
    NEUTRAL,   // No significant effect
}

data class CorrelationResult(
    val behaviorName: String,
    val metricName: String,
    val effectSize: Double,
    val pValue: Double,
    val isSignificant: Boolean,
    val direction: CorrelationDirection,
    val sampleSizeWith: Int,
    val sampleSizeWithout: Int,
    val meanWith: Double,
    val meanWithout: Double,
)

object StatisticalEngine {

    fun tTest(
        withBehavior: List<Double>,
        withoutBehavior: List<Double>,
    ): Pair<Double, Double>? {
        if (withBehavior.size < 3 || withoutBehavior.size < 3) return null

        val n1 = withBehavior.size.toDouble()
        val n2 = withoutBehavior.size.toDouble()
        val mean1 = withBehavior.mean
        val mean2 = withoutBehavior.mean
        val var1 = withBehavior.standardDeviation.pow(2)
        val var2 = withoutBehavior.standardDeviation.pow(2)

        val pooledSE = sqrt(var1 / n1 + var2 / n2)
        if (pooledSE <= 0) return null

        val t = (mean1 - mean2) / pooledSE

        val pValue = approximatePValue(abs(t))

        return t to pValue
    }

    fun cohensD(
        withBehavior: List<Double>,
        withoutBehavior: List<Double>,
    ): Double? {
        if (withBehavior.size < 3 || withoutBehavior.size < 3) return null

        val mean1 = withBehavior.mean
        val mean2 = withoutBehavior.mean
        val sd1 = withBehavior.standardDeviation
        val sd2 = withoutBehavior.standardDeviation
        val n1 = withBehavior.size.toDouble()
        val n2 = withoutBehavior.size.toDouble()

        val pooledSD = sqrt(((n1 - 1) * sd1 * sd1 + (n2 - 1) * sd2 * sd2) / (n1 + n2 - 2))
        if (pooledSD <= 0) return null

        return (mean1 - mean2) / pooledSD
    }

    fun analyzeCorrelation(
        behaviorName: String,
        metricName: String,
        withBehavior: List<Double>,
        withoutBehavior: List<Double>,
        higherIsBetter: Boolean = true,
    ): CorrelationResult? {
        val testResult = tTest(withBehavior, withoutBehavior) ?: return null
        val effectSize = cohensD(withBehavior, withoutBehavior) ?: return null

        val meanDiff = withBehavior.mean - withoutBehavior.mean
        val direction = when {
            testResult.second >= 0.05 -> CorrelationDirection.NEUTRAL
            (higherIsBetter && meanDiff > 0) || (!higherIsBetter && meanDiff < 0) -> CorrelationDirection.POSITIVE
            else -> CorrelationDirection.NEGATIVE
        }

        return CorrelationResult(
            behaviorName = behaviorName,
            metricName = metricName,
            effectSize = effectSize,
            pValue = testResult.second,
            isSignificant = testResult.second < 0.05,
            direction = direction,
            sampleSizeWith = withBehavior.size,
            sampleSizeWithout = withoutBehavior.size,
            meanWith = withBehavior.mean,
            meanWithout = withoutBehavior.mean,
        )
    }

    fun interpretEffectSize(d: Double): String {
        val absD = abs(d)
        return when {
            absD < 0.2 -> "Negligible"
            absD < 0.5 -> "Small"
            absD < 0.8 -> "Medium"
            else -> "Large"
        }
    }

    private fun approximatePValue(t: Double): Double {
        val x = abs(t)
        return erfc(x / sqrt(2.0))
    }

    private fun erfc(x: Double): Double {
        val t = 1.0 / (1.0 + 0.3275911 * abs(x))
        val poly = t * (0.254829592 + t * (-0.284496736 + t * (1.421413741 + t * (-1.453152027 + t * 1.061405429))))
        val result = poly * kotlin.math.exp(-x * x)
        return if (x >= 0) result else 2.0 - result
    }
}
