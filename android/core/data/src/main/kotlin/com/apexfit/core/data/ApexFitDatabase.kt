package com.apexfit.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.apexfit.core.data.converter.Converters
import com.apexfit.core.data.dao.BaselineMetricDao
import com.apexfit.core.data.dao.DailyMetricDao
import com.apexfit.core.data.dao.HealthConnectAnchorDao
import com.apexfit.core.data.dao.JournalDao
import com.apexfit.core.data.dao.NotificationPreferenceDao
import com.apexfit.core.data.dao.SleepDao
import com.apexfit.core.data.dao.UserProfileDao
import com.apexfit.core.data.dao.WorkoutRecordDao
import com.apexfit.core.data.entity.BaselineMetricEntity
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.HealthConnectAnchorEntity
import com.apexfit.core.data.entity.JournalEntryEntity
import com.apexfit.core.data.entity.JournalResponseEntity
import com.apexfit.core.data.entity.NotificationPreferenceEntity
import com.apexfit.core.data.entity.SleepSessionEntity
import com.apexfit.core.data.entity.SleepStageEntity
import com.apexfit.core.data.entity.UserProfileEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity

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
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ApexFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyMetricDao(): DailyMetricDao
    abstract fun workoutRecordDao(): WorkoutRecordDao
    abstract fun sleepDao(): SleepDao
    abstract fun journalDao(): JournalDao
    abstract fun baselineMetricDao(): BaselineMetricDao
    abstract fun healthConnectAnchorDao(): HealthConnectAnchorDao
    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
}
