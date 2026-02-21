package com.zyva.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zyva.core.data.ZyvaDatabase
import com.zyva.core.data.dao.BaselineMetricDao
import com.zyva.core.data.dao.DailyMetricDao
import com.zyva.core.data.dao.HealthConnectAnchorDao
import com.zyva.core.data.dao.JournalDao
import com.zyva.core.data.dao.NotificationPreferenceDao
import com.zyva.core.data.dao.SleepDao
import com.zyva.core.data.dao.UserProfileDao
import com.zyva.core.data.dao.WorkoutRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE user_profiles ADD COLUMN wearableDevice TEXT")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZyvaDatabase {
        return Room.databaseBuilder(
            context,
            ZyvaDatabase::class.java,
            "zyva.db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides fun provideUserProfileDao(db: ZyvaDatabase): UserProfileDao = db.userProfileDao()
    @Provides fun provideDailyMetricDao(db: ZyvaDatabase): DailyMetricDao = db.dailyMetricDao()
    @Provides fun provideWorkoutRecordDao(db: ZyvaDatabase): WorkoutRecordDao = db.workoutRecordDao()
    @Provides fun provideSleepDao(db: ZyvaDatabase): SleepDao = db.sleepDao()
    @Provides fun provideJournalDao(db: ZyvaDatabase): JournalDao = db.journalDao()
    @Provides fun provideBaselineMetricDao(db: ZyvaDatabase): BaselineMetricDao = db.baselineMetricDao()
    @Provides fun provideHealthConnectAnchorDao(db: ZyvaDatabase): HealthConnectAnchorDao = db.healthConnectAnchorDao()
    @Provides fun provideNotificationPreferenceDao(db: ZyvaDatabase): NotificationPreferenceDao = db.notificationPreferenceDao()
}
