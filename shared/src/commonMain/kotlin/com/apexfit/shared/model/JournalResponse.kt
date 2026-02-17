package com.apexfit.shared.model

import com.apexfit.shared.platform.currentTimeMillis
import com.apexfit.shared.platform.randomUUID

data class JournalResponse(
    val id: String = randomUUID(),
    val behaviorID: String,
    val behaviorName: String,
    val category: String,
    val responseType: JournalResponseType,
    val toggleValue: Boolean? = null,
    val numericValue: Double? = null,
    val scaleValue: String? = null,
    val createdAt: Long = currentTimeMillis(),
    val journalEntryId: String? = null,
) {
    val displayValue: String
        get() = when (responseType) {
            JournalResponseType.TOGGLE -> if (toggleValue == true) "Yes" else "No"
            JournalResponseType.NUMERIC -> numericValue?.let { it.toLong().toString() } ?: "-"
            JournalResponseType.SCALE -> scaleValue ?: "-"
        }
}
