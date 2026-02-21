package com.zyva.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zyva.core.data.converter.Converters
import com.zyva.core.data.dao.BaselineMetricDao
import com.zyva.core.data.dao.DailyMetricDao
import com.zyva.core.data.dao.HealthConnectAnchorDao
import com.zyva.core.data.dao.JournalDao
import com.zyva.core.data.dao.NotificationPreferenceDao
import com.zyva.core.data.dao.SleepDao
import com.zyva.core.data.dao.UserProfileDao
import com.zyva.core.data.dao.WorkoutRecordDao
import com.zyva.core.data.entity.BaselineMetricEntity
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.entity.HealthConnectAnchorEntity
import com.zyva.core.data.entity.JournalEntryEntity
import com.zyva.core.data.entity.JournalResponseEntity
import com.zyva.core.data.entity.NotificationPreferenceEntity
import com.zyva.core.data.entity.SleepSessionEntity
import com.zyva.core.data.entity.SleepStageEntity
import com.zyva.core.data.entity.UserProfileEntity
import com.zyva.core.data.entity.WorkoutRecordEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        DailyMetricEntity::class,
        WorkoutRecordEntity::class,
        SleepSessionEntity::class,
        SleepStageEntity::class,
        JournalEntryEntity::class,
        JournalResponseEntity::class,
        BaselineMetricEntity::class,
        HealthConnectAnchorEntity::class,
        NotificationPreferenceEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ZyvaDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyMetricDao(): DailyMetricDao
    abstract fun workoutRecordDao(): WorkoutRecordDao
    abstract fun sleepDao(): SleepDao
    abstract fun journalDao(): JournalDao
    abstract fun baselineMetricDao(): BaselineMetricDao
    abstract fun healthConnectAnchorDao(): HealthConnectAnchorDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
}
