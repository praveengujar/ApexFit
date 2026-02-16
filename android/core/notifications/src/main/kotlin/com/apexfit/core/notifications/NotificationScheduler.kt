package com.apexfit.core.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.apexfit.core.data.repository.NotificationPreferenceRepository
import com.apexfit.core.model.NotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notifRepo: NotificationPreferenceRepository,
) {
    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    suspend fun showMorningRecovery(recoveryScore: Double, zone: String) {
        val pref = notifRepo.getByType(NotificationType.MORNING_RECOVERY.name)
        if (pref?.isEnabled != true) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_RECOVERY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Morning Recovery: %.0f%%".format(recoveryScore))
            .setContentText("Your recovery is $zone. ${recoveryAdvice(zone)}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_RECOVERY, notification)
    }

    suspend fun showJournalReminder() {
        val pref = notifRepo.getByType(NotificationType.JOURNAL_REMINDER.name)
        if (pref?.isEnabled != true) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Journal Reminder")
            .setContentText("Don't forget to log your daily behaviors!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_JOURNAL, notification)
    }

    suspend fun showBedtimeReminder() {
        val pref = notifRepo.getByType(NotificationType.BEDTIME_REMINDER.name)
        if (pref?.isEnabled != true) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Bedtime Reminder")
            .setContentText("Time to wind down and get ready for sleep.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BEDTIME, notification)
    }

    suspend fun showHealthAlert(title: String, message: String) {
        val pref = notifRepo.getByType(NotificationType.HEALTH_ALERT.name)
        if (pref?.isEnabled != true) return

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_HEALTH_ALERT, notification)
    }

    private fun recoveryAdvice(zone: String): String = when (zone.uppercase()) {
        "GREEN" -> "Great day for a challenging workout!"
        "YELLOW" -> "Consider a moderate effort today."
        "RED" -> "Focus on recovery activities today."
        else -> ""
    }

    companion object {
        private const val NOTIFICATION_ID_RECOVERY = 1001
        private const val NOTIFICATION_ID_JOURNAL = 1002
        private const val NOTIFICATION_ID_BEDTIME = 1003
        private const val NOTIFICATION_ID_HEALTH_ALERT = 1004
    }
}
