package com.apexfit.shared.engine

import com.apexfit.shared.model.config.HeartRateZoneConfig
import com.apexfit.shared.model.config.StrainConfig
import kotlin.math.log10
import kotlin.math.min

data class HeartRateSample(
    val timestampMillis: Long,
    val bpm: Double,
    val durationSeconds: Double,
)

data class StrainResult(
    val strain: Double,
    val weightedHRArea: Double,
    val zone1Minutes: Double,
    val zone2Minutes: Double,
    val zone3Minutes: Double,
    val zone4Minutes: Double,
    val zone5Minutes: Double,
)

class StrainEngine(
    maxHeartRate: Int,
    private val strainConfig: StrainConfig,
    hrZoneConfig: HeartRateZoneConfig,
) {
    private val k: Double = strainConfig.scalingFactor
    private val c: Double = strainConfig.logOffsetConstant
    private val zoneCalculator = HeartRateZoneCalculator(maxHeartRate, hrZoneConfig)

    fun computeStrain(samples: List<HeartRateSample>): StrainResult {
        var weightedHRArea = 0.0
        val zoneMinutes = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0)

        for (sample in samples) {
            val durationMinutes = sample.durationSeconds / 60.0
            val zoneMultiplier = zoneCalculator.multiplier(sample.bpm)
            val zoneNum = zoneCalculator.zoneNumber(sample.bpm)

            weightedHRArea += durationMinutes * zoneMultiplier

            if (zoneNum in 1..5) {
                zoneMinutes[zoneNum - 1] += durationMinutes
            }
        }

        val rawStrain = k * log10(weightedHRArea + c)
        val clampedStrain = rawStrain.clamped(strainConfig.minValue..strainConfig.maxValue)

        return StrainResult(
            strain = clampedStrain,
            weightedHRArea = weightedHRArea,
            zone1Minutes = zoneMinutes[0],
            zone2Minutes = zoneMinutes[1],
            zone3Minutes = zoneMinutes[2],
            zone4Minutes = zoneMinutes[3],
            zone5Minutes = zoneMinutes[4],
        )
    }

    fun computeWorkoutStrain(rawSamples: List<Pair<Long, Double>>): StrainResult {
        val hrSamples = estimateDurations(rawSamples)
        return computeStrain(hrSamples)
    }

    companion object {
        fun estimateDurations(
            rawSamples: List<Pair<Long, Double>>,
            maxDurationSeconds: Double = 60.0,
        ): List<HeartRateSample> {
            if (rawSamples.size <= 1) {
                return rawSamples.map {
                    HeartRateSample(it.first, it.second, 5.0)
                }
            }

            val result = mutableListOf<HeartRateSample>()
            for (i in rawSamples.indices) {
                val duration: Double = if (i < rawSamples.size - 1) {
                    min(
                        (rawSamples[i + 1].first - rawSamples[i].first) / 1000.0,
                        maxDurationSeconds,
                    )
                } else {
                    result.lastOrNull()?.durationSeconds ?: 5.0
                }

                result.add(
                    HeartRateSample(
                        timestampMillis = rawSamples[i].first,
                        bpm = rawSamples[i].second,
                        durationSeconds = duration,
                    ),
                )
            }
            return result
        }
    }
}
