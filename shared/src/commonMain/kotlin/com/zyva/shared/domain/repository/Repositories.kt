package com.zyva.shared.domain.repository

import com.zyva.shared.model.BaselineMetric
import com.zyva.shared.model.DailyMetric
import com.zyva.shared.model.SleepSession
import com.zyva.shared.model.UserProfile
import com.zyva.shared.model.WorkoutRecord

interface DailyMetricRepository {
    suspend fun getByDate(dateMillis: Long): DailyMetric?
    suspend fun insertOrUpdate(metric: DailyMetric)
    suspend fun getRecentHRV(days: Int = 28): List<Double>
    suspend fun getRecentRHR(days: Int = 28): List<Double>
    suspend fun getRecentSleepHours(days: Int = 7): List<Double>
    suspend fun getRecentSleepNeeds(days: Int = 7): List<Double>
    suspend fun getRecentStrainScores(days: Int = 28): List<Double>
    suspend fun getRange(startDate: Long, endDate: Long): List<DailyMetric>
}

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile?
    suspend fun updateProfile(profile: UserProfile)
}

interface WorkoutRepository {
    suspend fun insert(workout: WorkoutRecord)
    suspend fun getByHealthConnectUUID(uuid: String): WorkoutRecord?
    suspend fun getForDate(dateMillis: Long): List<WorkoutRecord>
}

interface SleepRepository {
    suspend fun insertSession(session: SleepSession)
    suspend fun getRecentBedtimes(count: Int): List<Long>
    suspend fun getRecentWakeTimes(count: Int): List<Long>
    suspend fun getForDate(dateMillis: Long): List<SleepSession>
}

interface BaselineRepository {
    suspend fun getByType(metricType: String): BaselineMetric?
    suspend fun insert(metric: BaselineMetric)
}
