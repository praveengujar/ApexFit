package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.UserProfileDao
import com.apexfit.core.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val dao: UserProfileDao,
) {
    fun observeProfile(): Flow<UserProfileEntity?> = dao.observeProfile()

    suspend fun getProfile(): UserProfileEntity? = dao.getProfile()

    suspend fun getOrCreateProfile(): UserProfileEntity {
        return dao.getProfile() ?: UserProfileEntity(
            id = UUID.randomUUID().toString(),
        ).also { dao.insert(it) }
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.insert(profile.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun markOnboardingComplete(profileId: String) {
        dao.markOnboardingComplete(profileId)
    }

    suspend fun updateMaxHeartRate(profileId: String, maxHR: Int, source: String) {
        dao.updateMaxHeartRate(profileId, maxHR, source)
    }

    suspend fun updateSleepBaseline(profileId: String, hours: Double) {
        dao.updateSleepBaseline(profileId, hours)
    }

    suspend fun updateJournalBehaviors(profileId: String, behaviorIds: List<String>) {
        val json = Json.encodeToString<List<String>>(behaviorIds)
        dao.updateJournalBehaviors(profileId, json)
    }
}
