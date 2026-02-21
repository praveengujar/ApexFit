package com.zyva.shared.model

import com.zyva.shared.platform.currentTimeMillis
import com.zyva.shared.platform.randomUUID

data class NotificationPreference(
    val id: String = randomUUID(),
    val notificationType: NotificationType,
    val isEnabled: Boolean = true,
    val customTime: Long? = null,
    val updatedAt: Long = currentTimeMillis(),
)
