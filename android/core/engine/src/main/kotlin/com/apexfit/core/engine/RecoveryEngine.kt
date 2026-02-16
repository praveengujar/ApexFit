package com.apexfit.core.engine

import com.apexfit.core.model.RecoveryZone
import com.apexfit.core.model.config.RecoveryConfig
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
        var totalWeight = 0.0
        var weightedSum = 0.0
        var contributorCount = 0

        val hrvScore = computeContributor(
            value = input.hrv,
            baseline = baselines.hrv,
            invert = false,
            weight = config.weights.hrv,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val rhrScore = computeContributor(
            value = input.restingHeartRate,
            baseline = baselines.restingHeartRate,
            invert = true,
            weight = config.weights.restingHeartRate,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val sleepScore = computeContributor(
            value = input.sleepPerformance,
            baseline = baselines.sleepPerformance,
            invert = false,
            weight = config.weights.sleep,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val respRateScore = computeContributor(
            value = input.respiratoryRate,
            baseline = baselines.respiratoryRate,
            invert = true,
            weight = config.weights.respiratoryRate,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val spo2Score = computeContributor(
            value = input.spo2,
            baseline = baselines.spo2,
            invert = false,
            weight = config.weights.spo2,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val skinTempScore = computeContributor(
            value = input.skinTemperatureDeviation,
            baseline = baselines.skinTemperature,
            invert = true,
            weight = config.weights.skinTemperature,
            totalWeight = { totalWeight += it },
            weightedSum = { weightedSum += it },
            contributorCount = { contributorCount++ },
        )

        val rawScore = if (totalWeight > 0) weightedSum / totalWeight else 50.0
        val finalScore = rawScore.clamped(
            config.scoreRange.min.toDouble()..config.scoreRange.max.toDouble(),
        )
        val zone = RecoveryZone.from(finalScore)

        return RecoveryResult(
            score = finalScore,
            zone = zone,
            hrvScore = hrvScore,
            rhrScore = rhrScore,
            sleepScore = sleepScore,
            respRateScore = respRateScore,
            spo2Score = spo2Score,
            skinTempScore = skinTempScore,
            contributorCount = contributorCount,
        )
    }

    private fun computeContributor(
        value: Double?,
        baseline: BaselineResult?,
        invert: Boolean,
        weight: Double,
        totalWeight: (Double) -> Unit,
        weightedSum: (Double) -> Unit,
        contributorCount: () -> Unit,
    ): Double? {
        if (value == null || baseline == null || !baseline.isValid) return null

        var z = BaselineEngine.zScore(value, baseline)
        if (invert) z = -z

        val score = sigmoid(z)
        totalWeight(weight)
        weightedSum(score * weight)
        contributorCount()

        return score
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
            insights.add("skin temperature was $direction by ${"%.1f".format(abs(input.skinTemperatureDeviation))}°C")
        }

        val prefix = "Your Recovery is ${result.score.toInt()}% (${result.zone.label}). "
        return if (insights.isEmpty()) {
            prefix + "Your metrics are within normal range."
        } else {
            prefix + insights.joinToString(", and ") + "."
        }
    }
}
