package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.BaselineMetricDao
import com.apexfit.core.data.entity.BaselineMetricEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaselineRepository @Inject constructor(
    private val dao: BaselineMetricDao,
) {
    suspend fun getByType(metricType: String): BaselineMetricEntity? =
        dao.getByType(metricType)

    fun observeAll(): Flow<List<BaselineMetricEntity>> =
        dao.observeAll()

    suspend fun insert(baseline: BaselineMetricEntity) =
        dao.insert(baseline)

    suspend fun insertAll(baselines: List<BaselineMetricEntity>) =
        dao.insertAll(baselines)
}
