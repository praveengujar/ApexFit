package com.zyva.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sleep_stages",
    foreignKeys = [
        ForeignKey(
            entity = SleepSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sleepSessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sleepSessionId")],
)
data class SleepStageEntity(
    @PrimaryKey val id: String,
    val sleepSessionId: String,
    val stageType: String, // AWAKE, LIGHT, DEEP, REM, IN_BED
    val startDate: Long,
    val endDate: Long,
    val durationMinutes: Double,
)
