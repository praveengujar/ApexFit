package com.apexfit.shared.model

import com.apexfit.shared.platform.calculateAge
import com.apexfit.shared.platform.currentTimeMillis
import com.apexfit.shared.platform.randomUUID

data class UserProfile(
    val id: String = randomUUID(),
    val firebaseUID: String? = null,
    val displayName: String = "",
    val email: String? = null,
    val dateOfBirthMillis: Long? = null,
    val biologicalSex: BiologicalSex = BiologicalSex.NOT_SET,
    val heightCM: Double? = null,
    val weightKG: Double? = null,
    val maxHeartRate: Int? = null,
    val maxHeartRateSource: MaxHRSource = MaxHRSource.AGE_ESTIMATE,
    val sleepBaselineHours: Double = 7.5,
    val preferredUnits: UnitSystem = UnitSystem.METRIC,
    val selectedJournalBehaviorIDs: List<String> = emptyList(),
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = currentTimeMillis(),
    val updatedAt: Long = currentTimeMillis(),
    val deviceToken: String? = null,
    val lastSyncedAt: Long? = null,
) {
    val age: Int?
        get() = dateOfBirthMillis?.let { calculateAge(it) }

    val estimatedMaxHR: Int
        get() = maxHeartRate ?: age?.let { 220 - it } ?: 190
}
