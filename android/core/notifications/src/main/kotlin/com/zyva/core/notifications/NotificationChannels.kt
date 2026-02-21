package com.zyva.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannels @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val CHANNEL_RECOVERY = "zyva_recovery"
        const val CHANNEL_REMINDERS = "zyva_reminders"
        const val CHANNEL_ALERTS = "zyva_alerts"
        const val CHANNEL_REPORTS = "zyva_reports"
    }

    fun createAll() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        val channels = listOf(
            NotificationChannel(
                CHANNEL_RECOVERY,
                "Recovery Updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Daily recovery score notifications"
            },
            NotificationChannel(
                CHANNEL_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Bedtime and journal reminders"
            },
            NotificationChannel(
                CHANNEL_ALERTS,
                "Health Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Important health metric alerts"
            },
            NotificationChannel(
                CHANNEL_REPORTS,
                "Reports",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Weekly performance reports"
            },
        )

        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
}
