package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apexfit.core.data.entity.NotificationPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationPreferenceDao {
    @Query("SELECT * FROM notification_preferences")
    fun observeAll(): Flow<List<NotificationPreferenceEntity>>

    @Query("SELECT * FROM notification_preferences WHERE notificationType = :type LIMIT 1")
    suspend fun getByType(type: String): NotificationPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: NotificationPreferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(preferences: List<NotificationPreferenceEntity>)

    @Query("UPDATE notification_preferences SET isEnabled = :enabled WHERE notificationType = :type")
    suspend fun setEnabled(type: String, enabled: Boolean)
}
