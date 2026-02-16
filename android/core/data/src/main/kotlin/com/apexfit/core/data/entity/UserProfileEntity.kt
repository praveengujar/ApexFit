package com.apexfit.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val firebaseUID: String? = null,
    val displayName: String = "",
    val email: String? = null,
    val dateOfBirth: Long? = null,
    val biologicalSex: String = "NOT_SET",
    val heightCM: Double? = null,
    val weightKG: Double? = null,
    val maxHeartRate: Int = 190,
    val maxHeartRateSource: String = "AGE_ESTIMATE",
    val sleepBaselineHours: Double = 7.5,
    val preferredUnits: String = "METRIC",
    val selectedJournalBehaviorIDs: String = "[]", // JSON array of strings
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
