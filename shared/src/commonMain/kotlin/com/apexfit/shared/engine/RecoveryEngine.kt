package com.apexfit.shared.engine

import com.apexfit.shared.model.RecoveryZone
import com.apexfit.shared.model.config.RecoveryConfig
import kotlin.math.abs
import kotlin.math.exp

data class RecoveryInput(
    val hrv: Double? = null,
    val restingHeartRate: Double? = null,
    val sleepPerformance: Double? = null,
    val respiratoryRate: Double? = null,
    val spo2: Double? = null,
    val skinTemperatureDeviation: Double? = null,
)

data class RecoveryBaselines(
    val hrv: BaselineResult? = null,
    val restingHeartRate: BaselineResult? = null,
    val sleepPerformance: BaselineResult? = null,
    val respiratoryRate: BaselineResult? = null,
    val spo2: BaselineResult? = null,
    val skinTemperature: BaselineResult? = null,
)

data class RecoveryResult(
    val score: Double,
    val zone: RecoveryZone,
    val hrvScore: Double?,
    val rhrScore: Double?,
    val sleepScore: Double?,
    val respRateScore: Double?,
    val spo2Score: Double?,
    val skinTempScore: Double?,
    val contributorCount: Int,
)

class RecoveryEngine(private val config: RecoveryConfig) {

    private fun sigmoid(z: Double): Double {
        return 100.0 / (1.0 + exp(-config.sigmoidSteepness * z))
    }

    fun computeRecovery(input: RecoveryInput, baselines: RecoveryBaselines): RecoveryResult {
        data class Contributor(val value: Double?, val baseline: BaselineResult?, val invert: Boolean, val weight: Double)

        val contributors = listOf(
            Contributor(input.hrv, baselines.hrv, false, config.weights.hrv),
            Contributor(input.restingHeartRate, baselines.restingHeartRate, true, config.weights.restingHeartRate),
            Contributor(input.sleepPerformance, baselines.sleepPerformance, false, config.weights.sleep),
            Contributor(input.respiratoryRate, baselines.respiratoryRate, true, config.weights.respiratoryRate),
            Contributor(input.spo2, baselines.spo2, false, config.weights.spo2),
            Contributor(input.skinTemperatureDeviation, baselines.skinTemperature, true, config.weights.skinTemperature),
        )

        val scores = contributors.map { c -> computeScore(c.value, c.baseline, c.invert) }
        val validPairs = contributors.zip(scores).filter { it.second != null }

        val totalWeight = validPairs.sumOf { it.first.weight }
        val weightedSum = validPairs.sumOf { it.second!! * it.first.weight }

        val rawScore = if (totalWeight > 0) weightedSum / totalWeight else 50.0
        val finalScore = rawScore.clamped(
            config.scoreRange.min.toDouble()..config.scoreRange.max.toDouble(),
        )
        val zone = RecoveryZone.from(finalScore)

        return RecoveryResult(
            score = finalScore,
            zone = zone,
            hrvScore = scores[0],
            rhrScore = scores[1],
            sleepScore = scores[2],
            respRateScore = scores[3],
            spo2Score = scores[4],
            skinTempScore = scores[5],
            contributorCount = validPairs.size,
        )
    }

    private fun computeScore(value: Double?, baseline: BaselineResult?, invert: Boolean): Double? {
        if (value == null || baseline == null || !baseline.isValid) return null
        var z = BaselineEngine.zScore(value, baseline)
        if (invert) z = -z
        return sigmoid(z)
    }

    fun strainTarget(zone: RecoveryZone): ClosedFloatingPointRange<Double> {
        val targets = config.strainTargets
        return when (zone) {
            RecoveryZone.GREEN -> targets.green.min..targets.green.max
            RecoveryZone.YELLOW -> targets.yellow.min..targets.yellow.max
            RecoveryZone.RED -> targets.red.min..targets.red.max
        }
    }

    fun generateInsight(
        result: RecoveryResult,
        input: RecoveryInput,
        baselines: RecoveryBaselines,
    ): String {
        val thresholds = config.insightThresholds
        val insights = mutableListOf<String>()

        if (input.hrv != null && baselines.hrv != null) {
            val pctChange = ((input.hrv - baselines.hrv.mean) / baselines.hrv.mean) * 100
            if (abs(pctChange) > thresholds.hrvPercentChange) {
                val direction = if (pctChange > 0) "above" else "below"
                insights.add("HRV was ${abs(pctChange.toInt())}% $direction your baseline")
            }
        }

        if (input.restingHeartRate != null && baselines.restingHeartRate != null) {
            val delta = input.restingHeartRate - baselines.restingHeartRate.mean
            if (abs(delta) > thresholds.rhrDeltaBPM) {
                val direction = if (delta > 0) "elevated by" else "lower by"
                insights.add("RHR was $direction ${abs(delta.toInt())} BPM")
            }
        }

        if (input.sleepPerformance != null) {
            if (input.sleepPerformance >= thresholds.sleepPerformanceHigh) {
                insights.add("you got ${input.sleepPerformance.toInt()}% of your sleep need")
            } else if (input.sleepPerformance < thresholds.sleepPerformanceLow) {
                insights.add("you only got ${input.sleepPerformance.toInt()}% of your sleep need")
            }
        }

        if (input.skinTemperatureDeviation != null && abs(input.skinTemperatureDeviation) > thresholds.skinTempDeviationCelsius) {
            val direction = if (input.skinTemperatureDeviation > 0) "elevated" else "lower"
            val rounded = ((abs(input.skinTemperatureDeviation) * 10).toLong()) / 10.0
            insights.add("skin temperature was $direction by ${rounded}\u00B0C")
        }

        val prefix = "Your Recovery is ${result.score.toInt()}% (${result.zone.label}). "
        return if (insights.isEmpty()) {
            prefix + "Your metrics are within normal range."
        } else {
            prefix + insights.joinToString(", and ") + "."
        }
    }
}
