package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.SleepDao
import com.apexfit.core.data.entity.SleepSessionEntity
import com.apexfit.core.data.entity.SleepStageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepository @Inject constructor(
    private val dao: SleepDao,
) {
    fun observeSessionsByDailyMetric(dailyMetricId: String): Flow<List<SleepSessionEntity>> =
        dao.observeSessionsByDailyMetric(dailyMetricId)

    suspend fun getMainSleep(dailyMetricId: String): SleepSessionEntity? =
        dao.getMainSleep(dailyMetricId)

    suspend fun getSessionsInRange(startDate: Long, endDate: Long): List<SleepSessionEntity> =
        dao.getSessionsInRange(startDate, endDate)

    fun observeStages(sessionId: String): Flow<List<SleepStageEntity>> =
        dao.observeStages(sessionId)

    suspend fun getStages(sessionId: String): List<SleepStageEntity> =
        dao.getStages(sessionId)

    suspend fun insertSessionWithStages(session: SleepSessionEntity, stages: List<SleepStageEntity>) =
        dao.insertSessionWithStages(session, stages)

    suspend fun getRecentBedtimes(count: Int = 4): List<Long> =
        dao.getRecentBedtimes(count)

    suspend fun getRecentWakeTimes(count: Int = 4): List<Long> =
        dao.getRecentWakeTimes(count)
}
