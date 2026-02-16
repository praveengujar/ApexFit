package com.apexfit.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_metrics",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userProfileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("userProfileId"),
        Index("date", unique = true),
    ],
)
data class DailyMetricEntity(
    @PrimaryKey val id: String,
    val userProfileId: String,
    val date: Long, // epoch millis, normalized to start of day

    // Recovery
    val recoveryScore: Double? = null,
    val recoveryZone: String? = null,

    // Strain
    val strainScore: Double? = null,

    // Sleep
    val sleepPerformance: Double? = null,
    val sleepScore: Double? = null,
    val sleepConsistency: Double? = null,
    val sleepEfficiency: Double? = null,
    val restorativeSleepPercentage: Double? = null,
    val deepSleepPercentage: Double? = null,
    val remSleepPercentage: Double? = null,

    // HRV
    val hrvRMSSD: Double? = null,
    val hrvSDNN: Double? = null,

    // Vitals
    val restingHeartRate: Double? = null,
    val respiratoryRate: Double? = null,
    val spo2: Double? = null,
    val skinTemperatureDeviation: Double? = null,

    // Activity
    val steps: Int? = null,
    val activeCalories: Double? = null,
    val vo2Max: Double? = null,

    // Workout summary
    val peakWorkoutStrain: Double? = null,
    val workoutCount: Int = 0,

    // Sleep metrics
    val totalSleepHours: Double? = null,
    val sleepDebtHours: Double? = null,
    val sleepNeedHours: Double? = null,

    // Stress
    val stressScore: Double? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
