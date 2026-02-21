package com.zyva.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.zyva.core.data.entity.SleepSessionEntity
import com.zyva.core.data.entity.SleepStageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_sessions WHERE dailyMetricId = :dailyMetricId ORDER BY startDate ASC")
    fun observeSessionsByDailyMetric(dailyMetricId: String): Flow<List<SleepSessionEntity>>

    @Query("SELECT * FROM sleep_sessions WHERE dailyMetricId = :dailyMetricId AND isMainSleep = 1 LIMIT 1")
    suspend fun getMainSleep(dailyMetricId: String): SleepSessionEntity?

    @Query("SELECT * FROM sleep_sessions WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate ASC")
    suspend fun getSessionsInRange(startDate: Long, endDate: Long): List<SleepSessionEntity>

    @Query("SELECT * FROM sleep_stages WHERE sleepSessionId = :sessionId ORDER BY startDate ASC")
    fun observeStages(sessionId: String): Flow<List<SleepStageEntity>>

    @Query("SELECT * FROM sleep_stages WHERE sleepSessionId = :sessionId ORDER BY startDate ASC")
    suspend fun getStages(sessionId: String): List<SleepStageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SleepSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStages(stages: List<SleepStageEntity>)

    @Transaction
    suspend fun insertSessionWithStages(session: SleepSessionEntity, stages: List<SleepStageEntity>) {
        insertSession(session)
        insertStages(stages)
    }

    @Query("SELECT startDate FROM sleep_sessions WHERE isMainSleep = 1 ORDER BY startDate DESC LIMIT :count")
    suspend fun getRecentBedtimes(count: Int = 4): List<Long>

    @Query("SELECT endDate FROM sleep_sessions WHERE isMainSleep = 1 ORDER BY endDate DESC LIMIT :count")
    suspend fun getRecentWakeTimes(count: Int = 4): List<Long>
}
