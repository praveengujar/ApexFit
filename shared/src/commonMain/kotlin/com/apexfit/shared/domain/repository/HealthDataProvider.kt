package com.apexfit.shared.domain.repository

import com.apexfit.shared.engine.SleepSessionData

data class ExerciseSessionData(
    val id: String,
    val exerciseType: Int,
    val title: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Double,
)

data class HealthSleepSessionData(
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
    val stages: List<HealthSleepStageData>,
)

data class HealthSleepStageData(
    val type: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationMinutes: Double,
)

interface HealthDataProvider {
    suspend fun fetchRestingHeartRate(dateMillis: Long): Double?
    suspend fun fetchRespiratoryRate(dateMillis: Long): Double?
    suspend fun fetchSpO2(dateMillis: Long): Double?
    suspend fun fetchHRVSamples(startMillis: Long, endMillis: Long): List<Pair<Long, Double>>?
    suspend fun fetchSteps(dateMillis: Long): Long?
    suspend fun fetchActiveCalories(dateMillis: Long): Double?
    suspend fun fetchVO2Max(dateMillis: Long): Double?
    suspend fun fetchSleepSessions(startMillis: Long, endMillis: Long): List<HealthSleepSessionData>?
    suspend fun fetchExerciseSessions(startMillis: Long, endMillis: Long): List<ExerciseSessionData>?
    suspend fun fetchHeartRateSamples(startMillis: Long, endMillis: Long): List<Pair<Long, Double>>?
}
