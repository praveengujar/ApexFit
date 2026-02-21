package com.zyva.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zyva.core.data.entity.BaselineMetricEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BaselineMetricDao {
    @Query("SELECT * FROM baseline_metrics WHERE metricType = :metricType LIMIT 1")
    suspend fun getByType(metricType: String): BaselineMetricEntity?

    @Query("SELECT * FROM baseline_metrics")
    fun observeAll(): Flow<List<BaselineMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(baseline: BaselineMetricEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(baselines: List<BaselineMetricEntity>)
}
