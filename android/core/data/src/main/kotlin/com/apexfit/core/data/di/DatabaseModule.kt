package com.apexfit.core.data.di

import android.content.Context
import androidx.room.Room
import com.apexfit.core.data.ApexFitDatabase
import com.apexfit.core.data.dao.BaselineMetricDao
import com.apexfit.core.data.dao.DailyMetricDao
import com.apexfit.core.data.dao.HealthConnectAnchorDao
import com.apexfit.core.data.dao.JournalDao
import com.apexfit.core.data.dao.NotificationPreferenceDao
import com.apexfit.core.data.dao.SleepDao
import com.apexfit.core.data.dao.UserProfileDao
import com.apexfit.core.data.dao.WorkoutRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ApexFitDatabase {
        return Room.databaseBuilder(
            context,
            ApexFitDatabase::class.java,
            "apexfit.db",
        ).build()
    }

    @Provides fun provideUserProfileDao(db: ApexFitDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideDailyMetricDao(db: ApexFitDatabase): DailyMetricDao = db.dailyMetricDao()
    @Provides fun provideWorkoutRecordDao(db: ApexFitDatabase): WorkoutRecordDao = db.workoutRecordDao()
    @Provides fun provideSleepDao(db: ApexFitDatabase): SleepDao = db.sleepDao()
    @Provides fun provideJournalDao(db: ApexFitDatabase): JournalDao = db.journalDao()
    @Provides fun provideBaselineMetricDao(db: ApexFitDatabase): BaselineMetricDao = db.baselineMetricDao()
    @Provides fun provideHealthConnectAnchorDao(db: ApexFitDatabase): HealthConnectAnchorDao = db.healthConnectAnchorDao()
    @Provides fun provideNotificationPreferenceDao(db: ApexFitDatabase): NotificationPreferenceDao = db.notificationPreferenceDao()
}
