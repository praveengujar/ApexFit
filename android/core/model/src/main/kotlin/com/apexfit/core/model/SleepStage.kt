package com.apexfit.core.model

data class SleepStage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val stageType: SleepStageType,
    val startDate: Long,
    val endDate: Long,
    val durationMinutes: Double = (endDate - startDate) / 60_000.0,
    val sleepSessionId: String? = null,
)
