package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.DailyMetricDao
import com.apexfit.core.data.entity.DailyMetricEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyMetricRepository @Inject constructor(
    private val dao: DailyMetricDao,
) {
    fun observeByDate(date: Long): Flow<DailyMetricEntity?> = dao.observeByDate(date)

    suspend fun getByDate(date: Long): DailyMetricEntity? = dao.getByDate(date)

    fun observeRecent(limit: Int = 7): Flow<List<DailyMetricEntity>> = dao.observeRecent(limit)

    fun observeRange(startDate: Long, endDate: Long): Flow<List<DailyMetricEntity>> =
        dao.observeRange(startDate, endDate)

    suspend fun getRange(startDate: Long, endDate: Long): List<DailyMetricEntity> =
        dao.getRange(startDate, endDate)

    suspend fun insertOrUpdate(metric: DailyMetricEntity) {
        dao.insert(metric.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun getRecentHRV(days: Int = 28): List<Double> = dao.getRecentHRV(days)
    suspend fun getRecentRHR(days: Int = 28): List<Double> = dao.getRecentRHR(days)
    suspend fun getRecentSleepHours(days: Int = 7): List<Double> = dao.getRecentSleepHours(days)
    suspend fun getRecentSleepNeeds(days: Int = 7): List<Double> = dao.getRecentSleepNeeds(days)
    suspend fun getRecentRecoveryScores(days: Int = 28): List<Double> = dao.getRecentRecoveryScores(days)
    suspend fun getRecentStrainScores(days: Int = 28): List<Double> = dao.getRecentStrainScores(days)
}
