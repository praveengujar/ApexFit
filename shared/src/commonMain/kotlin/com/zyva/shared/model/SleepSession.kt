package com.zyva.shared.model

import com.zyva.shared.platform.currentTimeMillis
import com.zyva.shared.platform.randomUUID

data class SleepSession(
    val id: String = randomUUID(),
    val startDate: Long,
    val endDate: Long,
    val isMainSleep: Boolean = true,
    val isNap: Boolean = false,
    val totalSleepMinutes: Double = 0.0,
    val timeInBedMinutes: Double = (endDate - startDate) / 60_000.0,
    val lightSleepMinutes: Double = 0.0,
    val deepSleepMinutes: Double = 0.0,
    val remSleepMinutes: Double = 0.0,
    val awakeMinutes: Double = 0.0,
    val awakenings: Int = 0,
    val sleepOnsetLatencyMinutes: Double? = null,
    val sleepEfficiency: Double = 0.0,
    val sleepPerformance: Double? = null,
    val sleepNeedHours: Double? = null,
    val createdAt: Long = currentTimeMillis(),
    val dailyMetricId: String? = null,
    val stages: List<SleepStage> = emptyList(),
) {
    val totalSleepHours: Double get() = totalSleepMinutes / 60.0

    val deepSleepPercentage: Double
        get() = if (totalSleepMinutes > 0) (deepSleepMinutes / totalSleepMinutes) * 100.0 else 0.0

    val remSleepPercentage: Double
        get() = if (totalSleepMinutes > 0) (remSleepMinutes / totalSleepMinutes) * 100.0 else 0.0

    val lightSleepPercentage: Double
        get() = if (totalSleepMinutes > 0) (lightSleepMinutes / totalSleepMinutes) * 100.0 else 0.0

    val formattedDuration: String
        get() {
            val hours = totalSleepMinutes.toInt() / 60
            val mins = totalSleepMinutes.toInt() % 60
            return "${hours}h ${mins}m"
        }
}
