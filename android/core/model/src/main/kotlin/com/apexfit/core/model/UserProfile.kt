package com.apexfit.core.model

import java.time.LocalDate
import java.time.Period

data class UserProfile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val firebaseUID: String? = null,
    val displayName: String = "",
    val email: String? = null,
    val dateOfBirth: LocalDate? = null,
    val biologicalSex: BiologicalSex = BiologicalSex.NOT_SET,
    val heightCM: Double? = null,
    val weightKG: Double? = null,
    val maxHeartRate: Int? = null,
    val maxHeartRateSource: MaxHRSource = MaxHRSource.AGE_ESTIMATE,
    val sleepBaselineHours: Double = 7.5,
    val preferredUnits: UnitSystem = UnitSystem.METRIC,
    val selectedJournalBehaviorIDs: List<String> = emptyList(),
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deviceToken: String? = null,
    val lastSyncedAt: Long? = null,
) {
    val age: Int?
        get() = dateOfBirth?.let { Period.between(it, LocalDate.now()).years }

    val estimatedMaxHR: Int
        get() = maxHeartRate ?: age?.let { 220 - it } ?: 190
}
