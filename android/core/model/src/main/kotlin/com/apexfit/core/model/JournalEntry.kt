package com.apexfit.core.model

data class JournalEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val date: Long, // epoch millis, start of day
    val completedAt: Long? = null,
    val isComplete: Boolean = false,
    val streakDays: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val userProfileId: String? = null,
    val responses: List<JournalResponse> = emptyList(),
) {
    val responseCount: Int get() = responses.size
}
