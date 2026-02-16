package com.apexfit.core.model

data class JournalResponse(
    val id: String = java.util.UUID.randomUUID().toString(),
    val behaviorID: String,
    val behaviorName: String,
    val category: String,
    val responseType: JournalResponseType,
    val toggleValue: Boolean? = null,
    val numericValue: Double? = null,
    val scaleValue: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val journalEntryId: String? = null,
) {
    val displayValue: String
        get() = when (responseType) {
            JournalResponseType.TOGGLE -> if (toggleValue == true) "Yes" else "No"
            JournalResponseType.NUMERIC -> numericValue?.let { "%.0f".format(it) } ?: "-"
            JournalResponseType.SCALE -> scaleValue ?: "-"
        }
}
