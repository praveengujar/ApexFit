package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apexfit.core.data.entity.DailyMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyMetricDao {
    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    fun observeByDate(date: Long): Flow<DailyMetricEntity?>

    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: Long): DailyMetricEntity?

    @Query("SELECT * FROM daily_metrics ORDER BY date DESC LIMIT :limit")
    fun observeRecent(limit: Int = 7): Flow<List<DailyMetricEntity>>

    @Query("SELECT * FROM daily_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeRange(startDate: Long, endDate: Long): Flow<List<DailyMetricEntity>>

    @Query("SELECT * FROM daily_metrics WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getRange(startDate: Long, endDate: Long): List<DailyMetricEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metric: DailyMetricEntity)

    @Update
    suspend fun update(metric: DailyMetricEntity)

    @Query("SELECT recoveryScore FROM daily_metrics WHERE recoveryScore IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentRecoveryScores(days: Int = 28): List<Double>

    @Query("SELECT strainScore FROM daily_metrics WHERE strainScore IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentStrainScores(days: Int = 28): List<Double>

    @Query("SELECT hrvRMSSD FROM daily_metrics WHERE hrvRMSSD IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentHRV(days: Int = 28): List<Double>

    @Query("SELECT restingHeartRate FROM daily_metrics WHERE restingHeartRate IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentRHR(days: Int = 28): List<Double>

    @Query("SELECT totalSleepHours FROM daily_metrics WHERE totalSleepHours IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentSleepHours(days: Int = 7): List<Double>

    @Query("SELECT sleepNeedHours FROM daily_metrics WHERE sleepNeedHours IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getRecentSleepNeeds(days: Int = 7): List<Double>
}
