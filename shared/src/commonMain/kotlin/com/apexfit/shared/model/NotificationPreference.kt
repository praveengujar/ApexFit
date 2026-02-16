package com.apexfit.shared.model

import com.apexfit.shared.platform.currentTimeMillis
import com.apexfit.shared.platform.randomUUID

data class NotificationPreference(
    val id: String = randomUUID(),
    val notificationType: NotificationType,
    val isEnabled: Boolean = true,
    val customTime: Long? = null,
    val updatedAt: Long = currentTimeMillis(),
)
