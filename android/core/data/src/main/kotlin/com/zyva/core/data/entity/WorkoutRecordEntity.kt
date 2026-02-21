package com.zyva.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_records",
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
data class WorkoutRecordEntity(
    @PrimaryKey val id: String,
    val dailyMetricId: String,
    val workoutType: String,
    val workoutName: String? = null,
    val startDate: Long,
    val endDate: Long,
    val durationMinutes: Double,
    val strainScore: Double? = null,
    val averageHeartRate: Double? = null,
    val maxHeartRate: Double? = null,
    val caloriesBurned: Double? = null,
    val distanceMeters: Double? = null,
    val zone1Minutes: Double = 0.0,
    val zone2Minutes: Double = 0.0,
    val zone3Minutes: Double = 0.0,
    val zone4Minutes: Double = 0.0,
    val zone5Minutes: Double = 0.0,
    val muscularLoad: Double? = null,
    val isStrengthWorkout: Boolean = false,
    val healthConnectUUID: String? = null,
)
