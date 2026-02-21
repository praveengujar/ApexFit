package com.zyva

import android.app.Application
import com.zyva.core.background.SyncScheduler
import com.zyva.core.notifications.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZyvaApplication : Application() {

    @Inject lateinit var notificationChannels: NotificationChannels
    @Inject lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        notificationChannels.createAll()
        syncScheduler.schedulePeriodicSync()
    }
}
