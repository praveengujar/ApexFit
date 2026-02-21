package com.zyva.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sleep_sessions",
    foreignKeys = [
        ForeignKey(
            entity = DailyMetricEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyMetricId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("dailyMetricId")],
)
data class SleepSessionEntity(
    @PrimaryKey val id: String,
    val dailyMetricId: String,
    val startDate: Long,
    val endDate: Long,
    val isMainSleep: Boolean = true,
    val isNap: Boolean = false,
    val totalSleepMinutes: Double = 0.0,
    val timeInBedMinutes: Double = 0.0,
    val lightMinutes: Double = 0.0,
    val deepMinutes: Double = 0.0,
    val remMinutes: Double = 0.0,
    val awakeMinutes: Double = 0.0,
    val awakenings: Int = 0,
    val sleepOnsetLatencyMinutes: Double? = null,
    val sleepEfficiency: Double = 0.0,
    val sleepPerformance: Double? = null,
    val sleepNeedHours: Double? = null,
    val healthConnectUUID: String? = null,
)
