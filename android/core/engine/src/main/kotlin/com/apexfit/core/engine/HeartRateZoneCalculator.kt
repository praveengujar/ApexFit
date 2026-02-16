package com.apexfit.core.engine

import com.apexfit.core.model.config.HeartRateZoneConfig

data class HeartRateZone(
    val zone: Int,
    val name: String,
    val lowerBound: Double,
    val upperBound: Double,
    val multiplier: Double,
)

class HeartRateZoneCalculator(
    val maxHeartRate: Int,
    private val hrConfig: HeartRateZoneConfig,
) {
    val zones: List<HeartRateZone> by lazy {
        val maxHR = maxHeartRate.toDouble()
        val b = hrConfig.boundaries
        val m = hrConfig.multipliers
        listOf(
            HeartRateZone(1, "Warm-Up", maxHR * b[0], maxHR * b[1], m[0]),
            HeartRateZone(2, "Fat Burn", maxHR * b[1], maxHR * b[2], m[1]),
            HeartRateZone(3, "Aerobic", maxHR * b[2], maxHR * b[3], m[2]),
            HeartRateZone(4, "Threshold", maxHR * b[3], maxHR * b[4], m[3]),
            HeartRateZone(5, "Anaerobic", maxHR * b[4], maxHR * b[5], m[4]),
        )
    }

    fun zone(heartRate: Double): HeartRateZone? {
        val maxHR = maxHeartRate.toDouble()
        val percentage = heartRate / maxHR
        val b = hrConfig.boundaries

        return when {
            percentage < b[0] -> null
            percentage < b[1] -> zones[0]
            percentage < b[2] -> zones[1]
            percentage < b[3] -> zones[2]
            percentage < b[4] -> zones[3]
            else -> zones[4]
        }
    }

    fun zoneNumber(heartRate: Double): Int =
        zone(heartRate)?.zone ?: 0

    fun multiplier(heartRate: Double): Double =
        zone(heartRate)?.multiplier ?: 0.0

    fun zoneBoundaries(): List<Triple<Int, Int, Int>> =
        zones.map { Triple(it.zone, it.lowerBound.toInt(), it.upperBound.toInt()) }
}
