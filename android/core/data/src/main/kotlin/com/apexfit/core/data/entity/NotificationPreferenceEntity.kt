package com.apexfit.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_preferences")
data class NotificationPreferenceEntity(
    @PrimaryKey val notificationType: String, // NotificationType enum name
    val isEnabled: Boolean = true,
    val customTimeHour: Int? = null,
    val customTimeMinute: Int? = null,
)
