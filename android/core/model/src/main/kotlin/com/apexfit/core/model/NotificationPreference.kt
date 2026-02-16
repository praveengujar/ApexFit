package com.apexfit.core.model

data class NotificationPreference(
    val id: String = java.util.UUID.randomUUID().toString(),
    val notificationType: NotificationType,
    val isEnabled: Boolean = true,
    val customTime: Long? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)
