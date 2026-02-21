package com.zyva.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baseline_metrics")
data class BaselineMetricEntity(
    @PrimaryKey val id: String,
    val metricType: String, // BaselineMetricType enum name
    val mean: Double,
    val standardDeviation: Double,
    val sampleCount: Int,
    val windowStartDate: Long? = null,
    val windowEndDate: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)
