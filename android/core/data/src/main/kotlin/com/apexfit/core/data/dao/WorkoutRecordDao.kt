package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apexfit.core.data.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutRecordDao {
    @Query("SELECT * FROM workout_records WHERE dailyMetricId = :dailyMetricId ORDER BY startDate ASC")
    fun observeByDailyMetric(dailyMetricId: String): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records WHERE dailyMetricId = :dailyMetricId ORDER BY startDate ASC")
    suspend fun getByDailyMetric(dailyMetricId: String): List<WorkoutRecordEntity>

    @Query("SELECT * FROM workout_records WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate ASC")
    fun observeRange(startDate: Long, endDate: Long): Flow<List<WorkoutRecordEntity>>

    @Query("SELECT * FROM workout_records WHERE startDate BETWEEN :startDate AND :endDate ORDER BY startDate ASC")
    suspend fun getRange(startDate: Long, endDate: Long): List<WorkoutRecordEntity>

    @Query("SELECT * FROM workout_records WHERE id = :id")
    suspend fun getById(id: String): WorkoutRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workouts: List<WorkoutRecordEntity>)

    @Query("SELECT * FROM workout_records WHERE healthConnectUUID = :uuid LIMIT 1")
    suspend fun getByHealthConnectUUID(uuid: String): WorkoutRecordEntity?

    @Query("DELETE FROM workout_records WHERE id = :id")
    suspend fun delete(id: String)
}
