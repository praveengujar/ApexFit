package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apexfit.core.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET hasCompletedOnboarding = 1 WHERE id = :id")
    suspend fun markOnboardingComplete(id: String)

    @Query("UPDATE user_profiles SET maxHeartRate = :maxHR, maxHeartRateSource = :source WHERE id = :id")
    suspend fun updateMaxHeartRate(id: String, maxHR: Int, source: String)

    @Query("UPDATE user_profiles SET sleepBaselineHours = :hours WHERE id = :id")
    suspend fun updateSleepBaseline(id: String, hours: Double)

    @Query("UPDATE user_profiles SET selectedJournalBehaviorIDs = :ids WHERE id = :id")
    suspend fun updateJournalBehaviors(id: String, ids: String)

    @Query("UPDATE user_profiles SET wearableDevice = :device WHERE id = :id")
    suspend fun updateWearableDevice(id: String, device: String?)
}
