package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.WorkoutRecordDao
import com.apexfit.core.data.entity.WorkoutRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val dao: WorkoutRecordDao,
) {
    fun observeByDailyMetric(dailyMetricId: String): Flow<List<WorkoutRecordEntity>> =
        dao.observeByDailyMetric(dailyMetricId)

    suspend fun getByDailyMetric(dailyMetricId: String): List<WorkoutRecordEntity> =
        dao.getByDailyMetric(dailyMetricId)

    fun observeRange(startDate: Long, endDate: Long): Flow<List<WorkoutRecordEntity>> =
        dao.observeRange(startDate, endDate)

    suspend fun getById(id: String): WorkoutRecordEntity? = dao.getById(id)

    suspend fun insert(workout: WorkoutRecordEntity) = dao.insert(workout)

    suspend fun insertAll(workouts: List<WorkoutRecordEntity>) = dao.insertAll(workouts)

    suspend fun getByHealthConnectUUID(uuid: String): WorkoutRecordEntity? =
        dao.getByHealthConnectUUID(uuid)

    suspend fun delete(id: String) = dao.delete(id)
}
