package com.apexfit.core.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.apexfit.core.data.dao.BaselineMetricDao;
import com.apexfit.core.data.dao.BaselineMetricDao_Impl;
import com.apexfit.core.data.dao.DailyMetricDao;
import com.apexfit.core.data.dao.DailyMetricDao_Impl;
import com.apexfit.core.data.dao.HealthConnectAnchorDao;
import com.apexfit.core.data.dao.HealthConnectAnchorDao_Impl;
import com.apexfit.core.data.dao.JournalDao;
import com.apexfit.core.data.dao.JournalDao_Impl;
import com.apexfit.core.data.dao.NotificationPreferenceDao;
import com.apexfit.core.data.dao.NotificationPreferenceDao_Impl;
import com.apexfit.core.data.dao.SleepDao;
import com.apexfit.core.data.dao.SleepDao_Impl;
import com.apexfit.core.data.dao.UserProfileDao;
import com.apexfit.core.data.dao.UserProfileDao_Impl;
import com.apexfit.core.data.dao.WorkoutRecordDao;
import com.apexfit.core.data.dao.WorkoutRecordDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ApexFitDatabase_Impl extends ApexFitDatabase {
  private volatile UserProfileDao _userProfileDao;

  private volatile DailyMetricDao _dailyMetricDao;

  private volatile WorkoutRecordDao _workoutRecordDao;

  private volatile SleepDao _sleepDao;

  private volatile JournalDao _journalDao;

  private volatile BaselineMetricDao _baselineMetricDao;

  private volatile HealthConnectAnchorDao _healthConnectAnchorDao;

  private volatile NotificationPreferenceDao _notificationPreferenceDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profiles` (`id` TEXT NOT NULL, `firebaseUID` TEXT, `displayName` TEXT NOT NULL, `email` TEXT, `dateOfBirth` INTEGER, `biologicalSex` TEXT NOT NULL, `heightCM` REAL, `weightKG` REAL, `maxHeartRate` INTEGER NOT NULL, `maxHeartRateSource` TEXT NOT NULL, `sleepBaselineHours` REAL NOT NULL, `preferredUnits` TEXT NOT NULL, `selectedJournalBehaviorIDs` TEXT NOT NULL, `hasCompletedOnboarding` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_metrics` (`id` TEXT NOT NULL, `userProfileId` TEXT NOT NULL, `date` INTEGER NOT NULL, `recoveryScore` REAL, `recoveryZone` TEXT, `strainScore` REAL, `sleepPerformance` REAL, `sleepScore` REAL, `sleepConsistency` REAL, `sleepEfficiency` REAL, `restorativeSleepPercentage` REAL, `deepSleepPercentage` REAL, `remSleepPercentage` REAL, `hrvRMSSD` REAL, `hrvSDNN` REAL, `restingHeartRate` REAL, `respiratoryRate` REAL, `spo2` REAL, `skinTemperatureDeviation` REAL, `steps` INTEGER, `activeCalories` REAL, `vo2Max` REAL, `peakWorkoutStrain` REAL, `workoutCount` INTEGER NOT NULL, `totalSleepHours` REAL, `sleepDebtHours` REAL, `sleepNeedHours` REAL, `stressScore` REAL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`userProfileId`) REFERENCES `user_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_metrics_userProfileId` ON `daily_metrics` (`userProfileId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_metrics_date` ON `daily_metrics` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `workout_records` (`id` TEXT NOT NULL, `dailyMetricId` TEXT NOT NULL, `workoutType` TEXT NOT NULL, `workoutName` TEXT, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `durationMinutes` REAL NOT NULL, `strainScore` REAL, `averageHeartRate` REAL, `maxHeartRate` REAL, `caloriesBurned` REAL, `distanceMeters` REAL, `zone1Minutes` REAL NOT NULL, `zone2Minutes` REAL NOT NULL, `zone3Minutes` REAL NOT NULL, `zone4Minutes` REAL NOT NULL, `zone5Minutes` REAL NOT NULL, `muscularLoad` REAL, `isStrengthWorkout` INTEGER NOT NULL, `healthConnectUUID` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`dailyMetricId`) REFERENCES `daily_metrics`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_records_dailyMetricId` ON `workout_records` (`dailyMetricId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_sessions` (`id` TEXT NOT NULL, `dailyMetricId` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `isMainSleep` INTEGER NOT NULL, `isNap` INTEGER NOT NULL, `totalSleepMinutes` REAL NOT NULL, `timeInBedMinutes` REAL NOT NULL, `lightMinutes` REAL NOT NULL, `deepMinutes` REAL NOT NULL, `remMinutes` REAL NOT NULL, `awakeMinutes` REAL NOT NULL, `awakenings` INTEGER NOT NULL, `sleepOnsetLatencyMinutes` REAL, `sleepEfficiency` REAL NOT NULL, `sleepPerformance` REAL, `sleepNeedHours` REAL, `healthConnectUUID` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`dailyMetricId`) REFERENCES `daily_metrics`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sleep_sessions_dailyMetricId` ON `sleep_sessions` (`dailyMetricId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sleep_stages` (`id` TEXT NOT NULL, `sleepSessionId` TEXT NOT NULL, `stageType` TEXT NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `durationMinutes` REAL NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`sleepSessionId`) REFERENCES `sleep_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sleep_stages_sleepSessionId` ON `sleep_stages` (`sleepSessionId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `journal_entries` (`id` TEXT NOT NULL, `userProfileId` TEXT NOT NULL, `date` INTEGER NOT NULL, `completedAt` INTEGER, `isComplete` INTEGER NOT NULL, `streakDays` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`userProfileId`) REFERENCES `user_profiles`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_userProfileId` ON `journal_entries` (`userProfileId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_date` ON `journal_entries` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `journal_responses` (`id` TEXT NOT NULL, `journalEntryId` TEXT NOT NULL, `behaviorID` TEXT NOT NULL, `behaviorName` TEXT NOT NULL, `category` TEXT NOT NULL, `responseType` TEXT NOT NULL, `toggleValue` INTEGER, `numericValue` REAL, `scaleValue` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`journalEntryId`) REFERENCES `journal_entries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_responses_journalEntryId` ON `journal_responses` (`journalEntryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `baseline_metrics` (`id` TEXT NOT NULL, `metricType` TEXT NOT NULL, `mean` REAL NOT NULL, `standardDeviation` REAL NOT NULL, `sampleCount` INTEGER NOT NULL, `windowStartDate` INTEGER, `windowEndDate` INTEGER, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `health_connect_anchors` (`dataTypeIdentifier` TEXT NOT NULL, `anchorToken` TEXT, PRIMARY KEY(`dataTypeIdentifier`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notification_preferences` (`notificationType` TEXT NOT NULL, `isEnabled` INTEGER NOT NULL, `customTimeHour` INTEGER, `customTimeMinute` INTEGER, PRIMARY KEY(`notificationType`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ef5d3f2d1aa71f248f11f0c2c033af64')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user_profiles`");
        db.execSQL("DROP TABLE IF EXISTS `daily_metrics`");
        db.execSQL("DROP TABLE IF EXISTS `workout_records`");
        db.execSQL("DROP TABLE IF EXISTS `sleep_sessions`");
        db.execSQL("DROP TABLE IF EXISTS `sleep_stages`");
        db.execSQL("DROP TABLE IF EXISTS `journal_entries`");
        db.execSQL("DROP TABLE IF EXISTS `journal_responses`");
        db.execSQL("DROP TABLE IF EXISTS `baseline_metrics`");
        db.execSQL("DROP TABLE IF EXISTS `health_connect_anchors`");
        db.execSQL("DROP TABLE IF EXISTS `notification_preferences`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUserProfiles = new HashMap<String, TableInfo.Column>(16);
        _columnsUserProfiles.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("firebaseUID", new TableInfo.Column("firebaseUID", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("dateOfBirth", new TableInfo.Column("dateOfBirth", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("biologicalSex", new TableInfo.Column("biologicalSex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("heightCM", new TableInfo.Column("heightCM", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("weightKG", new TableInfo.Column("weightKG", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("maxHeartRate", new TableInfo.Column("maxHeartRate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("maxHeartRateSource", new TableInfo.Column("maxHeartRateSource", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("sleepBaselineHours", new TableInfo.Column("sleepBaselineHours", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("preferredUnits", new TableInfo.Column("preferredUnits", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("selectedJournalBehaviorIDs", new TableInfo.Column("selectedJournalBehaviorIDs", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("hasCompletedOnboarding", new TableInfo.Column("hasCompletedOnboarding", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfiles.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfiles = new TableInfo("user_profiles", _columnsUserProfiles, _foreignKeysUserProfiles, _indicesUserProfiles);
        final TableInfo _existingUserProfiles = TableInfo.read(db, "user_profiles");
        if (!_infoUserProfiles.equals(_existingUserProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profiles(com.apexfit.core.data.entity.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfiles + "\n"
                  + " Found:\n" + _existingUserProfiles);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyMetrics = new HashMap<String, TableInfo.Column>(30);
        _columnsDailyMetrics.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("userProfileId", new TableInfo.Column("userProfileId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("recoveryScore", new TableInfo.Column("recoveryScore", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("recoveryZone", new TableInfo.Column("recoveryZone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("strainScore", new TableInfo.Column("strainScore", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepPerformance", new TableInfo.Column("sleepPerformance", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepScore", new TableInfo.Column("sleepScore", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepConsistency", new TableInfo.Column("sleepConsistency", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepEfficiency", new TableInfo.Column("sleepEfficiency", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("restorativeSleepPercentage", new TableInfo.Column("restorativeSleepPercentage", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("deepSleepPercentage", new TableInfo.Column("deepSleepPercentage", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("remSleepPercentage", new TableInfo.Column("remSleepPercentage", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("hrvRMSSD", new TableInfo.Column("hrvRMSSD", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("hrvSDNN", new TableInfo.Column("hrvSDNN", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("restingHeartRate", new TableInfo.Column("restingHeartRate", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("respiratoryRate", new TableInfo.Column("respiratoryRate", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("spo2", new TableInfo.Column("spo2", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("skinTemperatureDeviation", new TableInfo.Column("skinTemperatureDeviation", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("steps", new TableInfo.Column("steps", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("activeCalories", new TableInfo.Column("activeCalories", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("vo2Max", new TableInfo.Column("vo2Max", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("peakWorkoutStrain", new TableInfo.Column("peakWorkoutStrain", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("workoutCount", new TableInfo.Column("workoutCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("totalSleepHours", new TableInfo.Column("totalSleepHours", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepDebtHours", new TableInfo.Column("sleepDebtHours", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("sleepNeedHours", new TableInfo.Column("sleepNeedHours", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("stressScore", new TableInfo.Column("stressScore", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyMetrics.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyMetrics = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDailyMetrics.add(new TableInfo.ForeignKey("user_profiles", "CASCADE", "NO ACTION", Arrays.asList("userProfileId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDailyMetrics = new HashSet<TableInfo.Index>(2);
        _indicesDailyMetrics.add(new TableInfo.Index("index_daily_metrics_userProfileId", false, Arrays.asList("userProfileId"), Arrays.asList("ASC")));
        _indicesDailyMetrics.add(new TableInfo.Index("index_daily_metrics_date", true, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoDailyMetrics = new TableInfo("daily_metrics", _columnsDailyMetrics, _foreignKeysDailyMetrics, _indicesDailyMetrics);
        final TableInfo _existingDailyMetrics = TableInfo.read(db, "daily_metrics");
        if (!_infoDailyMetrics.equals(_existingDailyMetrics)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_metrics(com.apexfit.core.data.entity.DailyMetricEntity).\n"
                  + " Expected:\n" + _infoDailyMetrics + "\n"
                  + " Found:\n" + _existingDailyMetrics);
        }
        final HashMap<String, TableInfo.Column> _columnsWorkoutRecords = new HashMap<String, TableInfo.Column>(20);
        _columnsWorkoutRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("dailyMetricId", new TableInfo.Column("dailyMetricId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("workoutType", new TableInfo.Column("workoutType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("workoutName", new TableInfo.Column("workoutName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("durationMinutes", new TableInfo.Column("durationMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("strainScore", new TableInfo.Column("strainScore", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("averageHeartRate", new TableInfo.Column("averageHeartRate", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("maxHeartRate", new TableInfo.Column("maxHeartRate", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("distanceMeters", new TableInfo.Column("distanceMeters", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("zone1Minutes", new TableInfo.Column("zone1Minutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("zone2Minutes", new TableInfo.Column("zone2Minutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("zone3Minutes", new TableInfo.Column("zone3Minutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("zone4Minutes", new TableInfo.Column("zone4Minutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("zone5Minutes", new TableInfo.Column("zone5Minutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("muscularLoad", new TableInfo.Column("muscularLoad", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("isStrengthWorkout", new TableInfo.Column("isStrengthWorkout", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWorkoutRecords.put("healthConnectUUID", new TableInfo.Column("healthConnectUUID", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWorkoutRecords = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysWorkoutRecords.add(new TableInfo.ForeignKey("daily_metrics", "CASCADE", "NO ACTION", Arrays.asList("dailyMetricId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesWorkoutRecords = new HashSet<TableInfo.Index>(1);
        _indicesWorkoutRecords.add(new TableInfo.Index("index_workout_records_dailyMetricId", false, Arrays.asList("dailyMetricId"), Arrays.asList("ASC")));
        final TableInfo _infoWorkoutRecords = new TableInfo("workout_records", _columnsWorkoutRecords, _foreignKeysWorkoutRecords, _indicesWorkoutRecords);
        final TableInfo _existingWorkoutRecords = TableInfo.read(db, "workout_records");
        if (!_infoWorkoutRecords.equals(_existingWorkoutRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "workout_records(com.apexfit.core.data.entity.WorkoutRecordEntity).\n"
                  + " Expected:\n" + _infoWorkoutRecords + "\n"
                  + " Found:\n" + _existingWorkoutRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsSleepSessions = new HashMap<String, TableInfo.Column>(18);
        _columnsSleepSessions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("dailyMetricId", new TableInfo.Column("dailyMetricId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("isMainSleep", new TableInfo.Column("isMainSleep", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("isNap", new TableInfo.Column("isNap", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("totalSleepMinutes", new TableInfo.Column("totalSleepMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("timeInBedMinutes", new TableInfo.Column("timeInBedMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("lightMinutes", new TableInfo.Column("lightMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("deepMinutes", new TableInfo.Column("deepMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("remMinutes", new TableInfo.Column("remMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("awakeMinutes", new TableInfo.Column("awakeMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("awakenings", new TableInfo.Column("awakenings", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("sleepOnsetLatencyMinutes", new TableInfo.Column("sleepOnsetLatencyMinutes", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("sleepEfficiency", new TableInfo.Column("sleepEfficiency", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("sleepPerformance", new TableInfo.Column("sleepPerformance", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("sleepNeedHours", new TableInfo.Column("sleepNeedHours", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepSessions.put("healthConnectUUID", new TableInfo.Column("healthConnectUUID", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSleepSessions = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSleepSessions.add(new TableInfo.ForeignKey("daily_metrics", "CASCADE", "NO ACTION", Arrays.asList("dailyMetricId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSleepSessions = new HashSet<TableInfo.Index>(1);
        _indicesSleepSessions.add(new TableInfo.Index("index_sleep_sessions_dailyMetricId", false, Arrays.asList("dailyMetricId"), Arrays.asList("ASC")));
        final TableInfo _infoSleepSessions = new TableInfo("sleep_sessions", _columnsSleepSessions, _foreignKeysSleepSessions, _indicesSleepSessions);
        final TableInfo _existingSleepSessions = TableInfo.read(db, "sleep_sessions");
        if (!_infoSleepSessions.equals(_existingSleepSessions)) {
          return new RoomOpenHelper.ValidationResult(false, "sleep_sessions(com.apexfit.core.data.entity.SleepSessionEntity).\n"
                  + " Expected:\n" + _infoSleepSessions + "\n"
                  + " Found:\n" + _existingSleepSessions);
        }
        final HashMap<String, TableInfo.Column> _columnsSleepStages = new HashMap<String, TableInfo.Column>(6);
        _columnsSleepStages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepStages.put("sleepSessionId", new TableInfo.Column("sleepSessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepStages.put("stageType", new TableInfo.Column("stageType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepStages.put("startDate", new TableInfo.Column("startDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepStages.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSleepStages.put("durationMinutes", new TableInfo.Column("durationMinutes", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSleepStages = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSleepStages.add(new TableInfo.ForeignKey("sleep_sessions", "CASCADE", "NO ACTION", Arrays.asList("sleepSessionId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSleepStages = new HashSet<TableInfo.Index>(1);
        _indicesSleepStages.add(new TableInfo.Index("index_sleep_stages_sleepSessionId", false, Arrays.asList("sleepSessionId"), Arrays.asList("ASC")));
        final TableInfo _infoSleepStages = new TableInfo("sleep_stages", _columnsSleepStages, _foreignKeysSleepStages, _indicesSleepStages);
        final TableInfo _existingSleepStages = TableInfo.read(db, "sleep_stages");
        if (!_infoSleepStages.equals(_existingSleepStages)) {
          return new RoomOpenHelper.ValidationResult(false, "sleep_stages(com.apexfit.core.data.entity.SleepStageEntity).\n"
                  + " Expected:\n" + _infoSleepStages + "\n"
                  + " Found:\n" + _existingSleepStages);
        }
        final HashMap<String, TableInfo.Column> _columnsJournalEntries = new HashMap<String, TableInfo.Column>(6);
        _columnsJournalEntries.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("userProfileId", new TableInfo.Column("userProfileId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("isComplete", new TableInfo.Column("isComplete", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("streakDays", new TableInfo.Column("streakDays", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJournalEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysJournalEntries.add(new TableInfo.ForeignKey("user_profiles", "CASCADE", "NO ACTION", Arrays.asList("userProfileId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesJournalEntries = new HashSet<TableInfo.Index>(2);
        _indicesJournalEntries.add(new TableInfo.Index("index_journal_entries_userProfileId", false, Arrays.asList("userProfileId"), Arrays.asList("ASC")));
        _indicesJournalEntries.add(new TableInfo.Index("index_journal_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoJournalEntries = new TableInfo("journal_entries", _columnsJournalEntries, _foreignKeysJournalEntries, _indicesJournalEntries);
        final TableInfo _existingJournalEntries = TableInfo.read(db, "journal_entries");
        if (!_infoJournalEntries.equals(_existingJournalEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "journal_entries(com.apexfit.core.data.entity.JournalEntryEntity).\n"
                  + " Expected:\n" + _infoJournalEntries + "\n"
                  + " Found:\n" + _existingJournalEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsJournalResponses = new HashMap<String, TableInfo.Column>(9);
        _columnsJournalResponses.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("journalEntryId", new TableInfo.Column("journalEntryId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("behaviorID", new TableInfo.Column("behaviorID", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("behaviorName", new TableInfo.Column("behaviorName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("responseType", new TableInfo.Column("responseType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("toggleValue", new TableInfo.Column("toggleValue", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("numericValue", new TableInfo.Column("numericValue", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalResponses.put("scaleValue", new TableInfo.Column("scaleValue", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJournalResponses = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysJournalResponses.add(new TableInfo.ForeignKey("journal_entries", "CASCADE", "NO ACTION", Arrays.asList("journalEntryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesJournalResponses = new HashSet<TableInfo.Index>(1);
        _indicesJournalResponses.add(new TableInfo.Index("index_journal_responses_journalEntryId", false, Arrays.asList("journalEntryId"), Arrays.asList("ASC")));
        final TableInfo _infoJournalResponses = new TableInfo("journal_responses", _columnsJournalResponses, _foreignKeysJournalResponses, _indicesJournalResponses);
        final TableInfo _existingJournalResponses = TableInfo.read(db, "journal_responses");
        if (!_infoJournalResponses.equals(_existingJournalResponses)) {
          return new RoomOpenHelper.ValidationResult(false, "journal_responses(com.apexfit.core.data.entity.JournalResponseEntity).\n"
                  + " Expected:\n" + _infoJournalResponses + "\n"
                  + " Found:\n" + _existingJournalResponses);
        }
        final HashMap<String, TableInfo.Column> _columnsBaselineMetrics = new HashMap<String, TableInfo.Column>(8);
        _columnsBaselineMetrics.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("metricType", new TableInfo.Column("metricType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("mean", new TableInfo.Column("mean", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("standardDeviation", new TableInfo.Column("standardDeviation", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("sampleCount", new TableInfo.Column("sampleCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("windowStartDate", new TableInfo.Column("windowStartDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("windowEndDate", new TableInfo.Column("windowEndDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaselineMetrics.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBaselineMetrics = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBaselineMetrics = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBaselineMetrics = new TableInfo("baseline_metrics", _columnsBaselineMetrics, _foreignKeysBaselineMetrics, _indicesBaselineMetrics);
        final TableInfo _existingBaselineMetrics = TableInfo.read(db, "baseline_metrics");
        if (!_infoBaselineMetrics.equals(_existingBaselineMetrics)) {
          return new RoomOpenHelper.ValidationResult(false, "baseline_metrics(com.apexfit.core.data.entity.BaselineMetricEntity).\n"
                  + " Expected:\n" + _infoBaselineMetrics + "\n"
                  + " Found:\n" + _existingBaselineMetrics);
        }
        final HashMap<String, TableInfo.Column> _columnsHealthConnectAnchors = new HashMap<String, TableInfo.Column>(2);
        _columnsHealthConnectAnchors.put("dataTypeIdentifier", new TableInfo.Column("dataTypeIdentifier", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthConnectAnchors.put("anchorToken", new TableInfo.Column("anchorToken", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHealthConnectAnchors = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHealthConnectAnchors = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHealthConnectAnchors = new TableInfo("health_connect_anchors", _columnsHealthConnectAnchors, _foreignKeysHealthConnectAnchors, _indicesHealthConnectAnchors);
        final TableInfo _existingHealthConnectAnchors = TableInfo.read(db, "health_connect_anchors");
        if (!_infoHealthConnectAnchors.equals(_existingHealthConnectAnchors)) {
          return new RoomOpenHelper.ValidationResult(false, "health_connect_anchors(com.apexfit.core.data.entity.HealthConnectAnchorEntity).\n"
                  + " Expected:\n" + _infoHealthConnectAnchors + "\n"
                  + " Found:\n" + _existingHealthConnectAnchors);
        }
        final HashMap<String, TableInfo.Column> _columnsNotificationPreferences = new HashMap<String, TableInfo.Column>(4);
        _columnsNotificationPreferences.put("notificationType", new TableInfo.Column("notificationType", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("isEnabled", new TableInfo.Column("isEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("customTimeHour", new TableInfo.Column("customTimeHour", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotificationPreferences.put("customTimeMinute", new TableInfo.Column("customTimeMinute", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotificationPreferences = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotificationPreferences = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNotificationPreferences = new TableInfo("notification_preferences", _columnsNotificationPreferences, _foreignKeysNotificationPreferences, _indicesNotificationPreferences);
        final TableInfo _existingNotificationPreferences = TableInfo.read(db, "notification_preferences");
        if (!_infoNotificationPreferences.equals(_existingNotificationPreferences)) {
          return new RoomOpenHelper.ValidationResult(false, "notification_preferences(com.apexfit.core.data.entity.NotificationPreferenceEntity).\n"
                  + " Expected:\n" + _infoNotificationPreferences + "\n"
                  + " Found:\n" + _existingNotificationPreferences);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "ef5d3f2d1aa71f248f11f0c2c033af64", "c4adbca0001c934133f796f73d4417e0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user_profiles","daily_metrics","workout_records","sleep_sessions","sleep_stages","journal_entries","journal_responses","baseline_metrics","health_connect_anchors","notification_preferences");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `user_profiles`");
      _db.execSQL("DELETE FROM `daily_metrics`");
      _db.execSQL("DELETE FROM `workout_records`");
      _db.execSQL("DELETE FROM `sleep_sessions`");
      _db.execSQL("DELETE FROM `sleep_stages`");
      _db.execSQL("DELETE FROM `journal_entries`");
      _db.execSQL("DELETE FROM `journal_responses`");
      _db.execSQL("DELETE FROM `baseline_metrics`");
      _db.execSQL("DELETE FROM `health_connect_anchors`");
      _db.execSQL("DELETE FROM `notification_preferences`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserProfileDao.class, UserProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyMetricDao.class, DailyMetricDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WorkoutRecordDao.class, WorkoutRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SleepDao.class, SleepDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(JournalDao.class, JournalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BaselineMetricDao.class, BaselineMetricDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HealthConnectAnchorDao.class, HealthConnectAnchorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationPreferenceDao.class, NotificationPreferenceDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserProfileDao userProfileDao() {
    if (_userProfileDao != null) {
      return _userProfileDao;
    } else {
      synchronized(this) {
        if(_userProfileDao == null) {
          _userProfileDao = new UserProfileDao_Impl(this);
        }
        return _userProfileDao;
      }
    }
  }

  @Override
  public DailyMetricDao dailyMetricDao() {
    if (_dailyMetricDao != null) {
      return _dailyMetricDao;
    } else {
      synchronized(this) {
        if(_dailyMetricDao == null) {
          _dailyMetricDao = new DailyMetricDao_Impl(this);
        }
        return _dailyMetricDao;
      }
    }
  }

  @Override
  public WorkoutRecordDao workoutRecordDao() {
    if (_workoutRecordDao != null) {
      return _workoutRecordDao;
    } else {
      synchronized(this) {
        if(_workoutRecordDao == null) {
          _workoutRecordDao = new WorkoutRecordDao_Impl(this);
        }
        return _workoutRecordDao;
      }
    }
  }

  @Override
  public SleepDao sleepDao() {
    if (_sleepDao != null) {
      return _sleepDao;
    } else {
      synchronized(this) {
        if(_sleepDao == null) {
          _sleepDao = new SleepDao_Impl(this);
        }
        return _sleepDao;
      }
    }
  }

  @Override
  public JournalDao journalDao() {
    if (_journalDao != null) {
      return _journalDao;
    } else {
      synchronized(this) {
        if(_journalDao == null) {
          _journalDao = new JournalDao_Impl(this);
        }
        return _journalDao;
      }
    }
  }

  @Override
  public BaselineMetricDao baselineMetricDao() {
    if (_baselineMetricDao != null) {
      return _baselineMetricDao;
    } else {
      synchronized(this) {
        if(_baselineMetricDao == null) {
          _baselineMetricDao = new BaselineMetricDao_Impl(this);
        }
        return _baselineMetricDao;
      }
    }
  }

  @Override
  public HealthConnectAnchorDao healthConnectAnchorDao() {
    if (_healthConnectAnchorDao != null) {
      return _healthConnectAnchorDao;
    } else {
      synchronized(this) {
        if(_healthConnectAnchorDao == null) {
          _healthConnectAnchorDao = new HealthConnectAnchorDao_Impl(this);
        }
        return _healthConnectAnchorDao;
      }
    }
  }

  @Override
  public NotificationPreferenceDao notificationPreferenceDao() {
    if (_notificationPreferenceDao != null) {
      return _notificationPreferenceDao;
    } else {
      synchronized(this) {
        if(_notificationPreferenceDao == null) {
          _notificationPreferenceDao = new NotificationPreferenceDao_Impl(this);
        }
        return _notificationPreferenceDao;
      }
    }
  }
}
