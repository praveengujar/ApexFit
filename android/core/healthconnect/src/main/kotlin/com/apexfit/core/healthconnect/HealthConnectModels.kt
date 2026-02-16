package com.apexfit.core.healthconnect

data class HealthConnectSleepSession(
    val id: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val totalSleepMinutes: Double,
    val timeInBedMinutes: Double,
    val lightMinutes: Double,
    val deepMinutes: Double,
    val remMinutes: Double,
    val awakeMinutes: Double,
    val awakenings: Int,
    val sleepEfficiency: Double,
    val stages: List<HealthConnectSleepStage>,
)

data class HealthConnectSleepStage(
    val type: String, // "light", "deep", "rem", "awake"
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Double,
)

data class HealthConnectExerciseSession(
    val id: String,
    val exerciseType: Int,
    val title: String?,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Double,
)
