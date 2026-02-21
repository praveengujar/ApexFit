package com.zyva.shared.model

import com.zyva.shared.platform.randomUUID

data class SleepStage(
    val id: String = randomUUID(),
    val stageType: SleepStageType,
    val startDate: Long,
    val endDate: Long,
    val durationMinutes: Double = (endDate - startDate) / 60_000.0,
    val sleepSessionId: String? = null,
)
