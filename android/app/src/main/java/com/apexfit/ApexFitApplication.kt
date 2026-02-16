package com.apexfit

import android.app.Application
import com.apexfit.core.background.SyncScheduler
import com.apexfit.core.notifications.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApexFitApplication : Application() {

    @Inject lateinit var notificationChannels: NotificationChannels
    @Inject lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        notificationChannels.createAll()
        syncScheduler.schedulePeriodicSync()
    }
}
