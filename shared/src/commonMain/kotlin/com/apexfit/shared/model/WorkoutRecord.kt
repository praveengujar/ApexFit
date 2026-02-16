package com.apexfit.shared.model

import com.apexfit.shared.platform.currentTimeMillis
import com.apexfit.shared.platform.randomUUID

data class WorkoutRecord(
    val id: String = randomUUID(),
    val workoutType: String,
    val workoutName: String,
    val startDate: Long,
    val endDate: Long,
    val durationMinutes: Double = (endDate - startDate) / 60_000.0,
    val strainScore: Double = 0.0,
    val averageHeartRate: Double? = null,
    val maxHeartRate: Double? = null,
    val activeCalories: Double = 0.0,
    val distanceMeters: Double? = null,
    val zone1Minutes: Double = 0.0,
    val zone2Minutes: Double = 0.0,
    val zone3Minutes: Double = 0.0,
    val zone4Minutes: Double = 0.0,
    val zone5Minutes: Double = 0.0,
    val muscularLoad: Double? = null,
    val isStrengthWorkout: Boolean = false,
    val healthConnectUUID: String? = null,
    val createdAt: Long = currentTimeMillis(),
    val dailyMetricId: String? = null,
) {
    val totalZoneMinutes: Double
        get() = zone1Minutes + zone2Minutes + zone3Minutes + zone4Minutes + zone5Minutes

    val primaryZone: Int
        get() {
            val zones = listOf(zone1Minutes, zone2Minutes, zone3Minutes, zone4Minutes, zone5Minutes)
            return (zones.indices.maxByOrNull { zones[it] } ?: 0) + 1
        }

    val formattedDuration: String
        get() {
            val hours = durationMinutes.toInt() / 60
            val mins = durationMinutes.toInt() % 60
            return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
        }
}
