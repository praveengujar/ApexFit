package com.zyva.shared.model

import com.zyva.shared.platform.currentTimeMillis
import com.zyva.shared.platform.randomUUID

data class JournalEntry(
    val id: String = randomUUID(),
    val date: Long, // epoch millis, start of day
    val completedAt: Long? = null,
    val isComplete: Boolean = false,
    val streakDays: Int = 0,
    val createdAt: Long = currentTimeMillis(),
    val userProfileId: String? = null,
    val responses: List<JournalResponse> = emptyList(),
) {
    val responseCount: Int get() = responses.size
}
