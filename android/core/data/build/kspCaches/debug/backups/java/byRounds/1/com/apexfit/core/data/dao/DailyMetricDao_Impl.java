package com.apexfit.core.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.DailyMetricEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyMetricDao_Impl implements DailyMetricDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyMetricEntity> __insertionAdapterOfDailyMetricEntity;

  private final EntityDeletionOrUpdateAdapter<DailyMetricEntity> __updateAdapterOfDailyMetricEntity;

  public DailyMetricDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyMetricEntity = new EntityInsertionAdapter<DailyMetricEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_metrics` (`id`,`userProfileId`,`date`,`recoveryScore`,`recoveryZone`,`strainScore`,`sleepPerformance`,`sleepScore`,`sleepConsistency`,`sleepEfficiency`,`restorativeSleepPercentage`,`deepSleepPercentage`,`remSleepPercentage`,`hrvRMSSD`,`hrvSDNN`,`restingHeartRate`,`respiratoryRate`,`spo2`,`skinTemperatureDeviation`,`steps`,`activeCalories`,`vo2Max`,`peakWorkoutStrain`,`workoutCount`,`totalSleepHours`,`sleepDebtHours`,`sleepNeedHours`,`stressScore`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyMetricEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserProfileId());
        statement.bindLong(3, entity.getDate());
        if (entity.getRecoveryScore() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getRecoveryScore());
        }
        if (entity.getRecoveryZone() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRecoveryZone());
        }
        if (entity.getStrainScore() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getStrainScore());
        }
        if (entity.getSleepPerformance() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getSleepPerformance());
        }
        if (entity.getSleepScore() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getSleepScore());
        }
        if (entity.getSleepConsistency() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getSleepConsistency());
        }
        if (entity.getSleepEfficiency() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getSleepEfficiency());
        }
        if (entity.getRestorativeSleepPercentage() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getRestorativeSleepPercentage());
        }
        if (entity.getDeepSleepPercentage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getDeepSleepPercentage());
        }
        if (entity.getRemSleepPercentage() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getRemSleepPercentage());
        }
        if (entity.getHrvRMSSD() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getHrvRMSSD());
        }
        if (entity.getHrvSDNN() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getHrvSDNN());
        }
        if (entity.getRestingHeartRate() == null) {
          statement.bindNull(16);
        } else {
          statement.bindDouble(16, entity.getRestingHeartRate());
        }
        if (entity.getRespiratoryRate() == null) {
          statement.bindNull(17);
        } else {
          statement.bindDouble(17, entity.getRespiratoryRate());
        }
        if (entity.getSpo2() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getSpo2());
        }
        if (entity.getSkinTemperatureDeviation() == null) {
          statement.bindNull(19);
        } else {
          statement.bindDouble(19, entity.getSkinTemperatureDeviation());
        }
        if (entity.getSteps() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getSteps());
        }
        if (entity.getActiveCalories() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getActiveCalories());
        }
        if (entity.getVo2Max() == null) {
          statement.bindNull(22);
        } else {
          statement.bindDouble(22, entity.getVo2Max());
        }
        if (entity.getPeakWorkoutStrain() == null) {
          statement.bindNull(23);
        } else {
          statement.bindDouble(23, entity.getPeakWorkoutStrain());
        }
        statement.bindLong(24, entity.getWorkoutCount());
        if (entity.getTotalSleepHours() == null) {
          statement.bindNull(25);
        } else {
          statement.bindDouble(25, entity.getTotalSleepHours());
        }
        if (entity.getSleepDebtHours() == null) {
          statement.bindNull(26);
        } else {
          statement.bindDouble(26, entity.getSleepDebtHours());
        }
        if (entity.getSleepNeedHours() == null) {
          statement.bindNull(27);
        } else {
          statement.bindDouble(27, entity.getSleepNeedHours());
        }
        if (entity.getStressScore() == null) {
          statement.bindNull(28);
        } else {
          statement.bindDouble(28, entity.getStressScore());
        }
        statement.bindLong(29, entity.getCreatedAt());
        statement.bindLong(30, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfDailyMetricEntity = new EntityDeletionOrUpdateAdapter<DailyMetricEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `daily_metrics` SET `id` = ?,`userProfileId` = ?,`date` = ?,`recoveryScore` = ?,`recoveryZone` = ?,`strainScore` = ?,`sleepPerformance` = ?,`sleepScore` = ?,`sleepConsistency` = ?,`sleepEfficiency` = ?,`restorativeSleepPercentage` = ?,`deepSleepPercentage` = ?,`remSleepPercentage` = ?,`hrvRMSSD` = ?,`hrvSDNN` = ?,`restingHeartRate` = ?,`respiratoryRate` = ?,`spo2` = ?,`skinTemperatureDeviation` = ?,`steps` = ?,`activeCalories` = ?,`vo2Max` = ?,`peakWorkoutStrain` = ?,`workoutCount` = ?,`totalSleepHours` = ?,`sleepDebtHours` = ?,`sleepNeedHours` = ?,`stressScore` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyMetricEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserProfileId());
        statement.bindLong(3, entity.getDate());
        if (entity.getRecoveryScore() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getRecoveryScore());
        }
        if (entity.getRecoveryZone() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRecoveryZone());
        }
        if (entity.getStrainScore() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getStrainScore());
        }
        if (entity.getSleepPerformance() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getSleepPerformance());
        }
        if (entity.getSleepScore() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getSleepScore());
        }
        if (entity.getSleepConsistency() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getSleepConsistency());
        }
        if (entity.getSleepEfficiency() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getSleepEfficiency());
        }
        if (entity.getRestorativeSleepPercentage() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getRestorativeSleepPercentage());
        }
        if (entity.getDeepSleepPercentage() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getDeepSleepPercentage());
        }
        if (entity.getRemSleepPercentage() == null) {
          statement.bindNull(13);
        } else {
          statement.bindDouble(13, entity.getRemSleepPercentage());
        }
        if (entity.getHrvRMSSD() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getHrvRMSSD());
        }
        if (entity.getHrvSDNN() == null) {
          statement.bindNull(15);
        } else {
          statement.bindDouble(15, entity.getHrvSDNN());
        }
        if (entity.getRestingHeartRate() == null) {
          statement.bindNull(16);
        } else {
          statement.bindDouble(16, entity.getRestingHeartRate());
        }
        if (entity.getRespiratoryRate() == null) {
          statement.bindNull(17);
        } else {
          statement.bindDouble(17, entity.getRespiratoryRate());
        }
        if (entity.getSpo2() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getSpo2());
        }
        if (entity.getSkinTemperatureDeviation() == null) {
          statement.bindNull(19);
        } else {
          statement.bindDouble(19, entity.getSkinTemperatureDeviation());
        }
        if (entity.getSteps() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getSteps());
        }
        if (entity.getActiveCalories() == null) {
          statement.bindNull(21);
        } else {
          statement.bindDouble(21, entity.getActiveCalories());
        }
        if (entity.getVo2Max() == null) {
          statement.bindNull(22);
        } else {
          statement.bindDouble(22, entity.getVo2Max());
        }
        if (entity.getPeakWorkoutStrain() == null) {
          statement.bindNull(23);
        } else {
          statement.bindDouble(23, entity.getPeakWorkoutStrain());
        }
        statement.bindLong(24, entity.getWorkoutCount());
        if (entity.getTotalSleepHours() == null) {
          statement.bindNull(25);
        } else {
          statement.bindDouble(25, entity.getTotalSleepHours());
        }
        if (entity.getSleepDebtHours() == null) {
          statement.bindNull(26);
        } else {
          statement.bindDouble(26, entity.getSleepDebtHours());
        }
        if (entity.getSleepNeedHours() == null) {
          statement.bindNull(27);
        } else {
          statement.bindDouble(27, entity.getSleepNeedHours());
        }
        if (entity.getStressScore() == null) {
          statement.bindNull(28);
        } else {
          statement.bindDouble(28, entity.getStressScore());
        }
        statement.bindLong(29, entity.getCreatedAt());
        statement.bindLong(30, entity.getUpdatedAt());
        statement.bindString(31, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final DailyMetricEntity metric,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyMetricEntity.insert(metric);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DailyMetricEntity metric,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDailyMetricEntity.handle(metric);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DailyMetricEntity> observeByDate(final long date) {
    final String _sql = "SELECT * FROM daily_metrics WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_metrics"}, new Callable<DailyMetricEntity>() {
      @Override
      @Nullable
      public DailyMetricEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRecoveryScore = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryScore");
          final int _cursorIndexOfRecoveryZone = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryZone");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepScore = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepScore");
          final int _cursorIndexOfSleepConsistency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepConsistency");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfRestorativeSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "restorativeSleepPercentage");
          final int _cursorIndexOfDeepSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepPercentage");
          final int _cursorIndexOfRemSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "remSleepPercentage");
          final int _cursorIndexOfHrvRMSSD = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvRMSSD");
          final int _cursorIndexOfHrvSDNN = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvSDNN");
          final int _cursorIndexOfRestingHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "restingHeartRate");
          final int _cursorIndexOfRespiratoryRate = CursorUtil.getColumnIndexOrThrow(_cursor, "respiratoryRate");
          final int _cursorIndexOfSpo2 = CursorUtil.getColumnIndexOrThrow(_cursor, "spo2");
          final int _cursorIndexOfSkinTemperatureDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "skinTemperatureDeviation");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfActiveCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "activeCalories");
          final int _cursorIndexOfVo2Max = CursorUtil.getColumnIndexOrThrow(_cursor, "vo2Max");
          final int _cursorIndexOfPeakWorkoutStrain = CursorUtil.getColumnIndexOrThrow(_cursor, "peakWorkoutStrain");
          final int _cursorIndexOfWorkoutCount = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutCount");
          final int _cursorIndexOfTotalSleepHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepHours");
          final int _cursorIndexOfSleepDebtHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDebtHours");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfStressScore = CursorUtil.getColumnIndexOrThrow(_cursor, "stressScore");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyMetricEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Double _tmpRecoveryScore;
            if (_cursor.isNull(_cursorIndexOfRecoveryScore)) {
              _tmpRecoveryScore = null;
            } else {
              _tmpRecoveryScore = _cursor.getDouble(_cursorIndexOfRecoveryScore);
            }
            final String _tmpRecoveryZone;
            if (_cursor.isNull(_cursorIndexOfRecoveryZone)) {
              _tmpRecoveryZone = null;
            } else {
              _tmpRecoveryZone = _cursor.getString(_cursorIndexOfRecoveryZone);
            }
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepScore;
            if (_cursor.isNull(_cursorIndexOfSleepScore)) {
              _tmpSleepScore = null;
            } else {
              _tmpSleepScore = _cursor.getDouble(_cursorIndexOfSleepScore);
            }
            final Double _tmpSleepConsistency;
            if (_cursor.isNull(_cursorIndexOfSleepConsistency)) {
              _tmpSleepConsistency = null;
            } else {
              _tmpSleepConsistency = _cursor.getDouble(_cursorIndexOfSleepConsistency);
            }
            final Double _tmpSleepEfficiency;
            if (_cursor.isNull(_cursorIndexOfSleepEfficiency)) {
              _tmpSleepEfficiency = null;
            } else {
              _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            }
            final Double _tmpRestorativeSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRestorativeSleepPercentage)) {
              _tmpRestorativeSleepPercentage = null;
            } else {
              _tmpRestorativeSleepPercentage = _cursor.getDouble(_cursorIndexOfRestorativeSleepPercentage);
            }
            final Double _tmpDeepSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfDeepSleepPercentage)) {
              _tmpDeepSleepPercentage = null;
            } else {
              _tmpDeepSleepPercentage = _cursor.getDouble(_cursorIndexOfDeepSleepPercentage);
            }
            final Double _tmpRemSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRemSleepPercentage)) {
              _tmpRemSleepPercentage = null;
            } else {
              _tmpRemSleepPercentage = _cursor.getDouble(_cursorIndexOfRemSleepPercentage);
            }
            final Double _tmpHrvRMSSD;
            if (_cursor.isNull(_cursorIndexOfHrvRMSSD)) {
              _tmpHrvRMSSD = null;
            } else {
              _tmpHrvRMSSD = _cursor.getDouble(_cursorIndexOfHrvRMSSD);
            }
            final Double _tmpHrvSDNN;
            if (_cursor.isNull(_cursorIndexOfHrvSDNN)) {
              _tmpHrvSDNN = null;
            } else {
              _tmpHrvSDNN = _cursor.getDouble(_cursorIndexOfHrvSDNN);
            }
            final Double _tmpRestingHeartRate;
            if (_cursor.isNull(_cursorIndexOfRestingHeartRate)) {
              _tmpRestingHeartRate = null;
            } else {
              _tmpRestingHeartRate = _cursor.getDouble(_cursorIndexOfRestingHeartRate);
            }
            final Double _tmpRespiratoryRate;
            if (_cursor.isNull(_cursorIndexOfRespiratoryRate)) {
              _tmpRespiratoryRate = null;
            } else {
              _tmpRespiratoryRate = _cursor.getDouble(_cursorIndexOfRespiratoryRate);
            }
            final Double _tmpSpo2;
            if (_cursor.isNull(_cursorIndexOfSpo2)) {
              _tmpSpo2 = null;
            } else {
              _tmpSpo2 = _cursor.getDouble(_cursorIndexOfSpo2);
            }
            final Double _tmpSkinTemperatureDeviation;
            if (_cursor.isNull(_cursorIndexOfSkinTemperatureDeviation)) {
              _tmpSkinTemperatureDeviation = null;
            } else {
              _tmpSkinTemperatureDeviation = _cursor.getDouble(_cursorIndexOfSkinTemperatureDeviation);
            }
            final Integer _tmpSteps;
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _tmpSteps = null;
            } else {
              _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            }
            final Double _tmpActiveCalories;
            if (_cursor.isNull(_cursorIndexOfActiveCalories)) {
              _tmpActiveCalories = null;
            } else {
              _tmpActiveCalories = _cursor.getDouble(_cursorIndexOfActiveCalories);
            }
            final Double _tmpVo2Max;
            if (_cursor.isNull(_cursorIndexOfVo2Max)) {
              _tmpVo2Max = null;
            } else {
              _tmpVo2Max = _cursor.getDouble(_cursorIndexOfVo2Max);
            }
            final Double _tmpPeakWorkoutStrain;
            if (_cursor.isNull(_cursorIndexOfPeakWorkoutStrain)) {
              _tmpPeakWorkoutStrain = null;
            } else {
              _tmpPeakWorkoutStrain = _cursor.getDouble(_cursorIndexOfPeakWorkoutStrain);
            }
            final int _tmpWorkoutCount;
            _tmpWorkoutCount = _cursor.getInt(_cursorIndexOfWorkoutCount);
            final Double _tmpTotalSleepHours;
            if (_cursor.isNull(_cursorIndexOfTotalSleepHours)) {
              _tmpTotalSleepHours = null;
            } else {
              _tmpTotalSleepHours = _cursor.getDouble(_cursorIndexOfTotalSleepHours);
            }
            final Double _tmpSleepDebtHours;
            if (_cursor.isNull(_cursorIndexOfSleepDebtHours)) {
              _tmpSleepDebtHours = null;
            } else {
              _tmpSleepDebtHours = _cursor.getDouble(_cursorIndexOfSleepDebtHours);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final Double _tmpStressScore;
            if (_cursor.isNull(_cursorIndexOfStressScore)) {
              _tmpStressScore = null;
            } else {
              _tmpStressScore = _cursor.getDouble(_cursorIndexOfStressScore);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyMetricEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpRecoveryScore,_tmpRecoveryZone,_tmpStrainScore,_tmpSleepPerformance,_tmpSleepScore,_tmpSleepConsistency,_tmpSleepEfficiency,_tmpRestorativeSleepPercentage,_tmpDeepSleepPercentage,_tmpRemSleepPercentage,_tmpHrvRMSSD,_tmpHrvSDNN,_tmpRestingHeartRate,_tmpRespiratoryRate,_tmpSpo2,_tmpSkinTemperatureDeviation,_tmpSteps,_tmpActiveCalories,_tmpVo2Max,_tmpPeakWorkoutStrain,_tmpWorkoutCount,_tmpTotalSleepHours,_tmpSleepDebtHours,_tmpSleepNeedHours,_tmpStressScore,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByDate(final long date,
      final Continuation<? super DailyMetricEntity> $completion) {
    final String _sql = "SELECT * FROM daily_metrics WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyMetricEntity>() {
      @Override
      @Nullable
      public DailyMetricEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRecoveryScore = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryScore");
          final int _cursorIndexOfRecoveryZone = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryZone");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepScore = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepScore");
          final int _cursorIndexOfSleepConsistency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepConsistency");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfRestorativeSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "restorativeSleepPercentage");
          final int _cursorIndexOfDeepSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepPercentage");
          final int _cursorIndexOfRemSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "remSleepPercentage");
          final int _cursorIndexOfHrvRMSSD = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvRMSSD");
          final int _cursorIndexOfHrvSDNN = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvSDNN");
          final int _cursorIndexOfRestingHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "restingHeartRate");
          final int _cursorIndexOfRespiratoryRate = CursorUtil.getColumnIndexOrThrow(_cursor, "respiratoryRate");
          final int _cursorIndexOfSpo2 = CursorUtil.getColumnIndexOrThrow(_cursor, "spo2");
          final int _cursorIndexOfSkinTemperatureDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "skinTemperatureDeviation");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfActiveCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "activeCalories");
          final int _cursorIndexOfVo2Max = CursorUtil.getColumnIndexOrThrow(_cursor, "vo2Max");
          final int _cursorIndexOfPeakWorkoutStrain = CursorUtil.getColumnIndexOrThrow(_cursor, "peakWorkoutStrain");
          final int _cursorIndexOfWorkoutCount = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutCount");
          final int _cursorIndexOfTotalSleepHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepHours");
          final int _cursorIndexOfSleepDebtHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDebtHours");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfStressScore = CursorUtil.getColumnIndexOrThrow(_cursor, "stressScore");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DailyMetricEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Double _tmpRecoveryScore;
            if (_cursor.isNull(_cursorIndexOfRecoveryScore)) {
              _tmpRecoveryScore = null;
            } else {
              _tmpRecoveryScore = _cursor.getDouble(_cursorIndexOfRecoveryScore);
            }
            final String _tmpRecoveryZone;
            if (_cursor.isNull(_cursorIndexOfRecoveryZone)) {
              _tmpRecoveryZone = null;
            } else {
              _tmpRecoveryZone = _cursor.getString(_cursorIndexOfRecoveryZone);
            }
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepScore;
            if (_cursor.isNull(_cursorIndexOfSleepScore)) {
              _tmpSleepScore = null;
            } else {
              _tmpSleepScore = _cursor.getDouble(_cursorIndexOfSleepScore);
            }
            final Double _tmpSleepConsistency;
            if (_cursor.isNull(_cursorIndexOfSleepConsistency)) {
              _tmpSleepConsistency = null;
            } else {
              _tmpSleepConsistency = _cursor.getDouble(_cursorIndexOfSleepConsistency);
            }
            final Double _tmpSleepEfficiency;
            if (_cursor.isNull(_cursorIndexOfSleepEfficiency)) {
              _tmpSleepEfficiency = null;
            } else {
              _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            }
            final Double _tmpRestorativeSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRestorativeSleepPercentage)) {
              _tmpRestorativeSleepPercentage = null;
            } else {
              _tmpRestorativeSleepPercentage = _cursor.getDouble(_cursorIndexOfRestorativeSleepPercentage);
            }
            final Double _tmpDeepSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfDeepSleepPercentage)) {
              _tmpDeepSleepPercentage = null;
            } else {
              _tmpDeepSleepPercentage = _cursor.getDouble(_cursorIndexOfDeepSleepPercentage);
            }
            final Double _tmpRemSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRemSleepPercentage)) {
              _tmpRemSleepPercentage = null;
            } else {
              _tmpRemSleepPercentage = _cursor.getDouble(_cursorIndexOfRemSleepPercentage);
            }
            final Double _tmpHrvRMSSD;
            if (_cursor.isNull(_cursorIndexOfHrvRMSSD)) {
              _tmpHrvRMSSD = null;
            } else {
              _tmpHrvRMSSD = _cursor.getDouble(_cursorIndexOfHrvRMSSD);
            }
            final Double _tmpHrvSDNN;
            if (_cursor.isNull(_cursorIndexOfHrvSDNN)) {
              _tmpHrvSDNN = null;
            } else {
              _tmpHrvSDNN = _cursor.getDouble(_cursorIndexOfHrvSDNN);
            }
            final Double _tmpRestingHeartRate;
            if (_cursor.isNull(_cursorIndexOfRestingHeartRate)) {
              _tmpRestingHeartRate = null;
            } else {
              _tmpRestingHeartRate = _cursor.getDouble(_cursorIndexOfRestingHeartRate);
            }
            final Double _tmpRespiratoryRate;
            if (_cursor.isNull(_cursorIndexOfRespiratoryRate)) {
              _tmpRespiratoryRate = null;
            } else {
              _tmpRespiratoryRate = _cursor.getDouble(_cursorIndexOfRespiratoryRate);
            }
            final Double _tmpSpo2;
            if (_cursor.isNull(_cursorIndexOfSpo2)) {
              _tmpSpo2 = null;
            } else {
              _tmpSpo2 = _cursor.getDouble(_cursorIndexOfSpo2);
            }
            final Double _tmpSkinTemperatureDeviation;
            if (_cursor.isNull(_cursorIndexOfSkinTemperatureDeviation)) {
              _tmpSkinTemperatureDeviation = null;
            } else {
              _tmpSkinTemperatureDeviation = _cursor.getDouble(_cursorIndexOfSkinTemperatureDeviation);
            }
            final Integer _tmpSteps;
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _tmpSteps = null;
            } else {
              _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            }
            final Double _tmpActiveCalories;
            if (_cursor.isNull(_cursorIndexOfActiveCalories)) {
              _tmpActiveCalories = null;
            } else {
              _tmpActiveCalories = _cursor.getDouble(_cursorIndexOfActiveCalories);
            }
            final Double _tmpVo2Max;
            if (_cursor.isNull(_cursorIndexOfVo2Max)) {
              _tmpVo2Max = null;
            } else {
              _tmpVo2Max = _cursor.getDouble(_cursorIndexOfVo2Max);
            }
            final Double _tmpPeakWorkoutStrain;
            if (_cursor.isNull(_cursorIndexOfPeakWorkoutStrain)) {
              _tmpPeakWorkoutStrain = null;
            } else {
              _tmpPeakWorkoutStrain = _cursor.getDouble(_cursorIndexOfPeakWorkoutStrain);
            }
            final int _tmpWorkoutCount;
            _tmpWorkoutCount = _cursor.getInt(_cursorIndexOfWorkoutCount);
            final Double _tmpTotalSleepHours;
            if (_cursor.isNull(_cursorIndexOfTotalSleepHours)) {
              _tmpTotalSleepHours = null;
            } else {
              _tmpTotalSleepHours = _cursor.getDouble(_cursorIndexOfTotalSleepHours);
            }
            final Double _tmpSleepDebtHours;
            if (_cursor.isNull(_cursorIndexOfSleepDebtHours)) {
              _tmpSleepDebtHours = null;
            } else {
              _tmpSleepDebtHours = _cursor.getDouble(_cursorIndexOfSleepDebtHours);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final Double _tmpStressScore;
            if (_cursor.isNull(_cursorIndexOfStressScore)) {
              _tmpStressScore = null;
            } else {
              _tmpStressScore = _cursor.getDouble(_cursorIndexOfStressScore);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DailyMetricEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpRecoveryScore,_tmpRecoveryZone,_tmpStrainScore,_tmpSleepPerformance,_tmpSleepScore,_tmpSleepConsistency,_tmpSleepEfficiency,_tmpRestorativeSleepPercentage,_tmpDeepSleepPercentage,_tmpRemSleepPercentage,_tmpHrvRMSSD,_tmpHrvSDNN,_tmpRestingHeartRate,_tmpRespiratoryRate,_tmpSpo2,_tmpSkinTemperatureDeviation,_tmpSteps,_tmpActiveCalories,_tmpVo2Max,_tmpPeakWorkoutStrain,_tmpWorkoutCount,_tmpTotalSleepHours,_tmpSleepDebtHours,_tmpSleepNeedHours,_tmpStressScore,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DailyMetricEntity>> observeRecent(final int limit) {
    final String _sql = "SELECT * FROM daily_metrics ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_metrics"}, new Callable<List<DailyMetricEntity>>() {
      @Override
      @NonNull
      public List<DailyMetricEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRecoveryScore = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryScore");
          final int _cursorIndexOfRecoveryZone = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryZone");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepScore = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepScore");
          final int _cursorIndexOfSleepConsistency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepConsistency");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfRestorativeSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "restorativeSleepPercentage");
          final int _cursorIndexOfDeepSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepPercentage");
          final int _cursorIndexOfRemSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "remSleepPercentage");
          final int _cursorIndexOfHrvRMSSD = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvRMSSD");
          final int _cursorIndexOfHrvSDNN = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvSDNN");
          final int _cursorIndexOfRestingHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "restingHeartRate");
          final int _cursorIndexOfRespiratoryRate = CursorUtil.getColumnIndexOrThrow(_cursor, "respiratoryRate");
          final int _cursorIndexOfSpo2 = CursorUtil.getColumnIndexOrThrow(_cursor, "spo2");
          final int _cursorIndexOfSkinTemperatureDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "skinTemperatureDeviation");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfActiveCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "activeCalories");
          final int _cursorIndexOfVo2Max = CursorUtil.getColumnIndexOrThrow(_cursor, "vo2Max");
          final int _cursorIndexOfPeakWorkoutStrain = CursorUtil.getColumnIndexOrThrow(_cursor, "peakWorkoutStrain");
          final int _cursorIndexOfWorkoutCount = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutCount");
          final int _cursorIndexOfTotalSleepHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepHours");
          final int _cursorIndexOfSleepDebtHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDebtHours");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfStressScore = CursorUtil.getColumnIndexOrThrow(_cursor, "stressScore");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyMetricEntity> _result = new ArrayList<DailyMetricEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyMetricEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Double _tmpRecoveryScore;
            if (_cursor.isNull(_cursorIndexOfRecoveryScore)) {
              _tmpRecoveryScore = null;
            } else {
              _tmpRecoveryScore = _cursor.getDouble(_cursorIndexOfRecoveryScore);
            }
            final String _tmpRecoveryZone;
            if (_cursor.isNull(_cursorIndexOfRecoveryZone)) {
              _tmpRecoveryZone = null;
            } else {
              _tmpRecoveryZone = _cursor.getString(_cursorIndexOfRecoveryZone);
            }
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepScore;
            if (_cursor.isNull(_cursorIndexOfSleepScore)) {
              _tmpSleepScore = null;
            } else {
              _tmpSleepScore = _cursor.getDouble(_cursorIndexOfSleepScore);
            }
            final Double _tmpSleepConsistency;
            if (_cursor.isNull(_cursorIndexOfSleepConsistency)) {
              _tmpSleepConsistency = null;
            } else {
              _tmpSleepConsistency = _cursor.getDouble(_cursorIndexOfSleepConsistency);
            }
            final Double _tmpSleepEfficiency;
            if (_cursor.isNull(_cursorIndexOfSleepEfficiency)) {
              _tmpSleepEfficiency = null;
            } else {
              _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            }
            final Double _tmpRestorativeSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRestorativeSleepPercentage)) {
              _tmpRestorativeSleepPercentage = null;
            } else {
              _tmpRestorativeSleepPercentage = _cursor.getDouble(_cursorIndexOfRestorativeSleepPercentage);
            }
            final Double _tmpDeepSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfDeepSleepPercentage)) {
              _tmpDeepSleepPercentage = null;
            } else {
              _tmpDeepSleepPercentage = _cursor.getDouble(_cursorIndexOfDeepSleepPercentage);
            }
            final Double _tmpRemSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRemSleepPercentage)) {
              _tmpRemSleepPercentage = null;
            } else {
              _tmpRemSleepPercentage = _cursor.getDouble(_cursorIndexOfRemSleepPercentage);
            }
            final Double _tmpHrvRMSSD;
            if (_cursor.isNull(_cursorIndexOfHrvRMSSD)) {
              _tmpHrvRMSSD = null;
            } else {
              _tmpHrvRMSSD = _cursor.getDouble(_cursorIndexOfHrvRMSSD);
            }
            final Double _tmpHrvSDNN;
            if (_cursor.isNull(_cursorIndexOfHrvSDNN)) {
              _tmpHrvSDNN = null;
            } else {
              _tmpHrvSDNN = _cursor.getDouble(_cursorIndexOfHrvSDNN);
            }
            final Double _tmpRestingHeartRate;
            if (_cursor.isNull(_cursorIndexOfRestingHeartRate)) {
              _tmpRestingHeartRate = null;
            } else {
              _tmpRestingHeartRate = _cursor.getDouble(_cursorIndexOfRestingHeartRate);
            }
            final Double _tmpRespiratoryRate;
            if (_cursor.isNull(_cursorIndexOfRespiratoryRate)) {
              _tmpRespiratoryRate = null;
            } else {
              _tmpRespiratoryRate = _cursor.getDouble(_cursorIndexOfRespiratoryRate);
            }
            final Double _tmpSpo2;
            if (_cursor.isNull(_cursorIndexOfSpo2)) {
              _tmpSpo2 = null;
            } else {
              _tmpSpo2 = _cursor.getDouble(_cursorIndexOfSpo2);
            }
            final Double _tmpSkinTemperatureDeviation;
            if (_cursor.isNull(_cursorIndexOfSkinTemperatureDeviation)) {
              _tmpSkinTemperatureDeviation = null;
            } else {
              _tmpSkinTemperatureDeviation = _cursor.getDouble(_cursorIndexOfSkinTemperatureDeviation);
            }
            final Integer _tmpSteps;
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _tmpSteps = null;
            } else {
              _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            }
            final Double _tmpActiveCalories;
            if (_cursor.isNull(_cursorIndexOfActiveCalories)) {
              _tmpActiveCalories = null;
            } else {
              _tmpActiveCalories = _cursor.getDouble(_cursorIndexOfActiveCalories);
            }
            final Double _tmpVo2Max;
            if (_cursor.isNull(_cursorIndexOfVo2Max)) {
              _tmpVo2Max = null;
            } else {
              _tmpVo2Max = _cursor.getDouble(_cursorIndexOfVo2Max);
            }
            final Double _tmpPeakWorkoutStrain;
            if (_cursor.isNull(_cursorIndexOfPeakWorkoutStrain)) {
              _tmpPeakWorkoutStrain = null;
            } else {
              _tmpPeakWorkoutStrain = _cursor.getDouble(_cursorIndexOfPeakWorkoutStrain);
            }
            final int _tmpWorkoutCount;
            _tmpWorkoutCount = _cursor.getInt(_cursorIndexOfWorkoutCount);
            final Double _tmpTotalSleepHours;
            if (_cursor.isNull(_cursorIndexOfTotalSleepHours)) {
              _tmpTotalSleepHours = null;
            } else {
              _tmpTotalSleepHours = _cursor.getDouble(_cursorIndexOfTotalSleepHours);
            }
            final Double _tmpSleepDebtHours;
            if (_cursor.isNull(_cursorIndexOfSleepDebtHours)) {
              _tmpSleepDebtHours = null;
            } else {
              _tmpSleepDebtHours = _cursor.getDouble(_cursorIndexOfSleepDebtHours);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final Double _tmpStressScore;
            if (_cursor.isNull(_cursorIndexOfStressScore)) {
              _tmpStressScore = null;
            } else {
              _tmpStressScore = _cursor.getDouble(_cursorIndexOfStressScore);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyMetricEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpRecoveryScore,_tmpRecoveryZone,_tmpStrainScore,_tmpSleepPerformance,_tmpSleepScore,_tmpSleepConsistency,_tmpSleepEfficiency,_tmpRestorativeSleepPercentage,_tmpDeepSleepPercentage,_tmpRemSleepPercentage,_tmpHrvRMSSD,_tmpHrvSDNN,_tmpRestingHeartRate,_tmpRespiratoryRate,_tmpSpo2,_tmpSkinTemperatureDeviation,_tmpSteps,_tmpActiveCalories,_tmpVo2Max,_tmpPeakWorkoutStrain,_tmpWorkoutCount,_tmpTotalSleepHours,_tmpSleepDebtHours,_tmpSleepNeedHours,_tmpStressScore,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DailyMetricEntity>> observeRange(final long startDate, final long endDate) {
    final String _sql = "SELECT * FROM daily_metrics WHERE date BETWEEN ? AND ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_metrics"}, new Callable<List<DailyMetricEntity>>() {
      @Override
      @NonNull
      public List<DailyMetricEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRecoveryScore = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryScore");
          final int _cursorIndexOfRecoveryZone = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryZone");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepScore = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepScore");
          final int _cursorIndexOfSleepConsistency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepConsistency");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfRestorativeSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "restorativeSleepPercentage");
          final int _cursorIndexOfDeepSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepPercentage");
          final int _cursorIndexOfRemSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "remSleepPercentage");
          final int _cursorIndexOfHrvRMSSD = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvRMSSD");
          final int _cursorIndexOfHrvSDNN = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvSDNN");
          final int _cursorIndexOfRestingHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "restingHeartRate");
          final int _cursorIndexOfRespiratoryRate = CursorUtil.getColumnIndexOrThrow(_cursor, "respiratoryRate");
          final int _cursorIndexOfSpo2 = CursorUtil.getColumnIndexOrThrow(_cursor, "spo2");
          final int _cursorIndexOfSkinTemperatureDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "skinTemperatureDeviation");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfActiveCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "activeCalories");
          final int _cursorIndexOfVo2Max = CursorUtil.getColumnIndexOrThrow(_cursor, "vo2Max");
          final int _cursorIndexOfPeakWorkoutStrain = CursorUtil.getColumnIndexOrThrow(_cursor, "peakWorkoutStrain");
          final int _cursorIndexOfWorkoutCount = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutCount");
          final int _cursorIndexOfTotalSleepHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepHours");
          final int _cursorIndexOfSleepDebtHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDebtHours");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfStressScore = CursorUtil.getColumnIndexOrThrow(_cursor, "stressScore");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyMetricEntity> _result = new ArrayList<DailyMetricEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyMetricEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Double _tmpRecoveryScore;
            if (_cursor.isNull(_cursorIndexOfRecoveryScore)) {
              _tmpRecoveryScore = null;
            } else {
              _tmpRecoveryScore = _cursor.getDouble(_cursorIndexOfRecoveryScore);
            }
            final String _tmpRecoveryZone;
            if (_cursor.isNull(_cursorIndexOfRecoveryZone)) {
              _tmpRecoveryZone = null;
            } else {
              _tmpRecoveryZone = _cursor.getString(_cursorIndexOfRecoveryZone);
            }
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepScore;
            if (_cursor.isNull(_cursorIndexOfSleepScore)) {
              _tmpSleepScore = null;
            } else {
              _tmpSleepScore = _cursor.getDouble(_cursorIndexOfSleepScore);
            }
            final Double _tmpSleepConsistency;
            if (_cursor.isNull(_cursorIndexOfSleepConsistency)) {
              _tmpSleepConsistency = null;
            } else {
              _tmpSleepConsistency = _cursor.getDouble(_cursorIndexOfSleepConsistency);
            }
            final Double _tmpSleepEfficiency;
            if (_cursor.isNull(_cursorIndexOfSleepEfficiency)) {
              _tmpSleepEfficiency = null;
            } else {
              _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            }
            final Double _tmpRestorativeSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRestorativeSleepPercentage)) {
              _tmpRestorativeSleepPercentage = null;
            } else {
              _tmpRestorativeSleepPercentage = _cursor.getDouble(_cursorIndexOfRestorativeSleepPercentage);
            }
            final Double _tmpDeepSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfDeepSleepPercentage)) {
              _tmpDeepSleepPercentage = null;
            } else {
              _tmpDeepSleepPercentage = _cursor.getDouble(_cursorIndexOfDeepSleepPercentage);
            }
            final Double _tmpRemSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRemSleepPercentage)) {
              _tmpRemSleepPercentage = null;
            } else {
              _tmpRemSleepPercentage = _cursor.getDouble(_cursorIndexOfRemSleepPercentage);
            }
            final Double _tmpHrvRMSSD;
            if (_cursor.isNull(_cursorIndexOfHrvRMSSD)) {
              _tmpHrvRMSSD = null;
            } else {
              _tmpHrvRMSSD = _cursor.getDouble(_cursorIndexOfHrvRMSSD);
            }
            final Double _tmpHrvSDNN;
            if (_cursor.isNull(_cursorIndexOfHrvSDNN)) {
              _tmpHrvSDNN = null;
            } else {
              _tmpHrvSDNN = _cursor.getDouble(_cursorIndexOfHrvSDNN);
            }
            final Double _tmpRestingHeartRate;
            if (_cursor.isNull(_cursorIndexOfRestingHeartRate)) {
              _tmpRestingHeartRate = null;
            } else {
              _tmpRestingHeartRate = _cursor.getDouble(_cursorIndexOfRestingHeartRate);
            }
            final Double _tmpRespiratoryRate;
            if (_cursor.isNull(_cursorIndexOfRespiratoryRate)) {
              _tmpRespiratoryRate = null;
            } else {
              _tmpRespiratoryRate = _cursor.getDouble(_cursorIndexOfRespiratoryRate);
            }
            final Double _tmpSpo2;
            if (_cursor.isNull(_cursorIndexOfSpo2)) {
              _tmpSpo2 = null;
            } else {
              _tmpSpo2 = _cursor.getDouble(_cursorIndexOfSpo2);
            }
            final Double _tmpSkinTemperatureDeviation;
            if (_cursor.isNull(_cursorIndexOfSkinTemperatureDeviation)) {
              _tmpSkinTemperatureDeviation = null;
            } else {
              _tmpSkinTemperatureDeviation = _cursor.getDouble(_cursorIndexOfSkinTemperatureDeviation);
            }
            final Integer _tmpSteps;
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _tmpSteps = null;
            } else {
              _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            }
            final Double _tmpActiveCalories;
            if (_cursor.isNull(_cursorIndexOfActiveCalories)) {
              _tmpActiveCalories = null;
            } else {
              _tmpActiveCalories = _cursor.getDouble(_cursorIndexOfActiveCalories);
            }
            final Double _tmpVo2Max;
            if (_cursor.isNull(_cursorIndexOfVo2Max)) {
              _tmpVo2Max = null;
            } else {
              _tmpVo2Max = _cursor.getDouble(_cursorIndexOfVo2Max);
            }
            final Double _tmpPeakWorkoutStrain;
            if (_cursor.isNull(_cursorIndexOfPeakWorkoutStrain)) {
              _tmpPeakWorkoutStrain = null;
            } else {
              _tmpPeakWorkoutStrain = _cursor.getDouble(_cursorIndexOfPeakWorkoutStrain);
            }
            final int _tmpWorkoutCount;
            _tmpWorkoutCount = _cursor.getInt(_cursorIndexOfWorkoutCount);
            final Double _tmpTotalSleepHours;
            if (_cursor.isNull(_cursorIndexOfTotalSleepHours)) {
              _tmpTotalSleepHours = null;
            } else {
              _tmpTotalSleepHours = _cursor.getDouble(_cursorIndexOfTotalSleepHours);
            }
            final Double _tmpSleepDebtHours;
            if (_cursor.isNull(_cursorIndexOfSleepDebtHours)) {
              _tmpSleepDebtHours = null;
            } else {
              _tmpSleepDebtHours = _cursor.getDouble(_cursorIndexOfSleepDebtHours);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final Double _tmpStressScore;
            if (_cursor.isNull(_cursorIndexOfStressScore)) {
              _tmpStressScore = null;
            } else {
              _tmpStressScore = _cursor.getDouble(_cursorIndexOfStressScore);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyMetricEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpRecoveryScore,_tmpRecoveryZone,_tmpStrainScore,_tmpSleepPerformance,_tmpSleepScore,_tmpSleepConsistency,_tmpSleepEfficiency,_tmpRestorativeSleepPercentage,_tmpDeepSleepPercentage,_tmpRemSleepPercentage,_tmpHrvRMSSD,_tmpHrvSDNN,_tmpRestingHeartRate,_tmpRespiratoryRate,_tmpSpo2,_tmpSkinTemperatureDeviation,_tmpSteps,_tmpActiveCalories,_tmpVo2Max,_tmpPeakWorkoutStrain,_tmpWorkoutCount,_tmpTotalSleepHours,_tmpSleepDebtHours,_tmpSleepNeedHours,_tmpStressScore,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRange(final long startDate, final long endDate,
      final Continuation<? super List<DailyMetricEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_metrics WHERE date BETWEEN ? AND ? ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyMetricEntity>>() {
      @Override
      @NonNull
      public List<DailyMetricEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfRecoveryScore = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryScore");
          final int _cursorIndexOfRecoveryZone = CursorUtil.getColumnIndexOrThrow(_cursor, "recoveryZone");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepScore = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepScore");
          final int _cursorIndexOfSleepConsistency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepConsistency");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfRestorativeSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "restorativeSleepPercentage");
          final int _cursorIndexOfDeepSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "deepSleepPercentage");
          final int _cursorIndexOfRemSleepPercentage = CursorUtil.getColumnIndexOrThrow(_cursor, "remSleepPercentage");
          final int _cursorIndexOfHrvRMSSD = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvRMSSD");
          final int _cursorIndexOfHrvSDNN = CursorUtil.getColumnIndexOrThrow(_cursor, "hrvSDNN");
          final int _cursorIndexOfRestingHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "restingHeartRate");
          final int _cursorIndexOfRespiratoryRate = CursorUtil.getColumnIndexOrThrow(_cursor, "respiratoryRate");
          final int _cursorIndexOfSpo2 = CursorUtil.getColumnIndexOrThrow(_cursor, "spo2");
          final int _cursorIndexOfSkinTemperatureDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "skinTemperatureDeviation");
          final int _cursorIndexOfSteps = CursorUtil.getColumnIndexOrThrow(_cursor, "steps");
          final int _cursorIndexOfActiveCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "activeCalories");
          final int _cursorIndexOfVo2Max = CursorUtil.getColumnIndexOrThrow(_cursor, "vo2Max");
          final int _cursorIndexOfPeakWorkoutStrain = CursorUtil.getColumnIndexOrThrow(_cursor, "peakWorkoutStrain");
          final int _cursorIndexOfWorkoutCount = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutCount");
          final int _cursorIndexOfTotalSleepHours = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepHours");
          final int _cursorIndexOfSleepDebtHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepDebtHours");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfStressScore = CursorUtil.getColumnIndexOrThrow(_cursor, "stressScore");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DailyMetricEntity> _result = new ArrayList<DailyMetricEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyMetricEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Double _tmpRecoveryScore;
            if (_cursor.isNull(_cursorIndexOfRecoveryScore)) {
              _tmpRecoveryScore = null;
            } else {
              _tmpRecoveryScore = _cursor.getDouble(_cursorIndexOfRecoveryScore);
            }
            final String _tmpRecoveryZone;
            if (_cursor.isNull(_cursorIndexOfRecoveryZone)) {
              _tmpRecoveryZone = null;
            } else {
              _tmpRecoveryZone = _cursor.getString(_cursorIndexOfRecoveryZone);
            }
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepScore;
            if (_cursor.isNull(_cursorIndexOfSleepScore)) {
              _tmpSleepScore = null;
            } else {
              _tmpSleepScore = _cursor.getDouble(_cursorIndexOfSleepScore);
            }
            final Double _tmpSleepConsistency;
            if (_cursor.isNull(_cursorIndexOfSleepConsistency)) {
              _tmpSleepConsistency = null;
            } else {
              _tmpSleepConsistency = _cursor.getDouble(_cursorIndexOfSleepConsistency);
            }
            final Double _tmpSleepEfficiency;
            if (_cursor.isNull(_cursorIndexOfSleepEfficiency)) {
              _tmpSleepEfficiency = null;
            } else {
              _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            }
            final Double _tmpRestorativeSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRestorativeSleepPercentage)) {
              _tmpRestorativeSleepPercentage = null;
            } else {
              _tmpRestorativeSleepPercentage = _cursor.getDouble(_cursorIndexOfRestorativeSleepPercentage);
            }
            final Double _tmpDeepSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfDeepSleepPercentage)) {
              _tmpDeepSleepPercentage = null;
            } else {
              _tmpDeepSleepPercentage = _cursor.getDouble(_cursorIndexOfDeepSleepPercentage);
            }
            final Double _tmpRemSleepPercentage;
            if (_cursor.isNull(_cursorIndexOfRemSleepPercentage)) {
              _tmpRemSleepPercentage = null;
            } else {
              _tmpRemSleepPercentage = _cursor.getDouble(_cursorIndexOfRemSleepPercentage);
            }
            final Double _tmpHrvRMSSD;
            if (_cursor.isNull(_cursorIndexOfHrvRMSSD)) {
              _tmpHrvRMSSD = null;
            } else {
              _tmpHrvRMSSD = _cursor.getDouble(_cursorIndexOfHrvRMSSD);
            }
            final Double _tmpHrvSDNN;
            if (_cursor.isNull(_cursorIndexOfHrvSDNN)) {
              _tmpHrvSDNN = null;
            } else {
              _tmpHrvSDNN = _cursor.getDouble(_cursorIndexOfHrvSDNN);
            }
            final Double _tmpRestingHeartRate;
            if (_cursor.isNull(_cursorIndexOfRestingHeartRate)) {
              _tmpRestingHeartRate = null;
            } else {
              _tmpRestingHeartRate = _cursor.getDouble(_cursorIndexOfRestingHeartRate);
            }
            final Double _tmpRespiratoryRate;
            if (_cursor.isNull(_cursorIndexOfRespiratoryRate)) {
              _tmpRespiratoryRate = null;
            } else {
              _tmpRespiratoryRate = _cursor.getDouble(_cursorIndexOfRespiratoryRate);
            }
            final Double _tmpSpo2;
            if (_cursor.isNull(_cursorIndexOfSpo2)) {
              _tmpSpo2 = null;
            } else {
              _tmpSpo2 = _cursor.getDouble(_cursorIndexOfSpo2);
            }
            final Double _tmpSkinTemperatureDeviation;
            if (_cursor.isNull(_cursorIndexOfSkinTemperatureDeviation)) {
              _tmpSkinTemperatureDeviation = null;
            } else {
              _tmpSkinTemperatureDeviation = _cursor.getDouble(_cursorIndexOfSkinTemperatureDeviation);
            }
            final Integer _tmpSteps;
            if (_cursor.isNull(_cursorIndexOfSteps)) {
              _tmpSteps = null;
            } else {
              _tmpSteps = _cursor.getInt(_cursorIndexOfSteps);
            }
            final Double _tmpActiveCalories;
            if (_cursor.isNull(_cursorIndexOfActiveCalories)) {
              _tmpActiveCalories = null;
            } else {
              _tmpActiveCalories = _cursor.getDouble(_cursorIndexOfActiveCalories);
            }
            final Double _tmpVo2Max;
            if (_cursor.isNull(_cursorIndexOfVo2Max)) {
              _tmpVo2Max = null;
            } else {
              _tmpVo2Max = _cursor.getDouble(_cursorIndexOfVo2Max);
            }
            final Double _tmpPeakWorkoutStrain;
            if (_cursor.isNull(_cursorIndexOfPeakWorkoutStrain)) {
              _tmpPeakWorkoutStrain = null;
            } else {
              _tmpPeakWorkoutStrain = _cursor.getDouble(_cursorIndexOfPeakWorkoutStrain);
            }
            final int _tmpWorkoutCount;
            _tmpWorkoutCount = _cursor.getInt(_cursorIndexOfWorkoutCount);
            final Double _tmpTotalSleepHours;
            if (_cursor.isNull(_cursorIndexOfTotalSleepHours)) {
              _tmpTotalSleepHours = null;
            } else {
              _tmpTotalSleepHours = _cursor.getDouble(_cursorIndexOfTotalSleepHours);
            }
            final Double _tmpSleepDebtHours;
            if (_cursor.isNull(_cursorIndexOfSleepDebtHours)) {
              _tmpSleepDebtHours = null;
            } else {
              _tmpSleepDebtHours = _cursor.getDouble(_cursorIndexOfSleepDebtHours);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final Double _tmpStressScore;
            if (_cursor.isNull(_cursorIndexOfStressScore)) {
              _tmpStressScore = null;
            } else {
              _tmpStressScore = _cursor.getDouble(_cursorIndexOfStressScore);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DailyMetricEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpRecoveryScore,_tmpRecoveryZone,_tmpStrainScore,_tmpSleepPerformance,_tmpSleepScore,_tmpSleepConsistency,_tmpSleepEfficiency,_tmpRestorativeSleepPercentage,_tmpDeepSleepPercentage,_tmpRemSleepPercentage,_tmpHrvRMSSD,_tmpHrvSDNN,_tmpRestingHeartRate,_tmpRespiratoryRate,_tmpSpo2,_tmpSkinTemperatureDeviation,_tmpSteps,_tmpActiveCalories,_tmpVo2Max,_tmpPeakWorkoutStrain,_tmpWorkoutCount,_tmpTotalSleepHours,_tmpSleepDebtHours,_tmpSleepNeedHours,_tmpStressScore,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentRecoveryScores(final int days,
      final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT recoveryScore FROM daily_metrics WHERE recoveryScore IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentStrainScores(final int days,
      final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT strainScore FROM daily_metrics WHERE strainScore IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentHRV(final int days, final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT hrvRMSSD FROM daily_metrics WHERE hrvRMSSD IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentRHR(final int days, final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT restingHeartRate FROM daily_metrics WHERE restingHeartRate IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentSleepHours(final int days,
      final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT totalSleepHours FROM daily_metrics WHERE totalSleepHours IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecentSleepNeeds(final int days,
      final Continuation<? super List<Double>> $completion) {
    final String _sql = "SELECT sleepNeedHours FROM daily_metrics WHERE sleepNeedHours IS NOT NULL ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Double>>() {
      @Override
      @NonNull
      public List<Double> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Double> _result = new ArrayList<Double>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Double _item;
            _item = _cursor.getDouble(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
