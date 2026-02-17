package com.apexfit.core.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.WorkoutRecordEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
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
public final class WorkoutRecordDao_Impl implements WorkoutRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WorkoutRecordEntity> __insertionAdapterOfWorkoutRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public WorkoutRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWorkoutRecordEntity = new EntityInsertionAdapter<WorkoutRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `workout_records` (`id`,`dailyMetricId`,`workoutType`,`workoutName`,`startDate`,`endDate`,`durationMinutes`,`strainScore`,`averageHeartRate`,`maxHeartRate`,`caloriesBurned`,`distanceMeters`,`zone1Minutes`,`zone2Minutes`,`zone3Minutes`,`zone4Minutes`,`zone5Minutes`,`muscularLoad`,`isStrengthWorkout`,`healthConnectUUID`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WorkoutRecordEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDailyMetricId());
        statement.bindString(3, entity.getWorkoutType());
        if (entity.getWorkoutName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getWorkoutName());
        }
        statement.bindLong(5, entity.getStartDate());
        statement.bindLong(6, entity.getEndDate());
        statement.bindDouble(7, entity.getDurationMinutes());
        if (entity.getStrainScore() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getStrainScore());
        }
        if (entity.getAverageHeartRate() == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.getAverageHeartRate());
        }
        if (entity.getMaxHeartRate() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getMaxHeartRate());
        }
        if (entity.getCaloriesBurned() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getCaloriesBurned());
        }
        if (entity.getDistanceMeters() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getDistanceMeters());
        }
        statement.bindDouble(13, entity.getZone1Minutes());
        statement.bindDouble(14, entity.getZone2Minutes());
        statement.bindDouble(15, entity.getZone3Minutes());
        statement.bindDouble(16, entity.getZone4Minutes());
        statement.bindDouble(17, entity.getZone5Minutes());
        if (entity.getMuscularLoad() == null) {
          statement.bindNull(18);
        } else {
          statement.bindDouble(18, entity.getMuscularLoad());
        }
        final int _tmp = entity.isStrengthWorkout() ? 1 : 0;
        statement.bindLong(19, _tmp);
        if (entity.getHealthConnectUUID() == null) {
          statement.bindNull(20);
        } else {
          statement.bindString(20, entity.getHealthConnectUUID());
        }
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM workout_records WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final WorkoutRecordEntity workout,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutRecordEntity.insert(workout);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<WorkoutRecordEntity> workouts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWorkoutRecordEntity.insert(workouts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WorkoutRecordEntity>> observeByDailyMetric(final String dailyMetricId) {
    final String _sql = "SELECT * FROM workout_records WHERE dailyMetricId = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dailyMetricId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_records"}, new Callable<List<WorkoutRecordEntity>>() {
      @Override
      @NonNull
      public List<WorkoutRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<WorkoutRecordEntity> _result = new ArrayList<WorkoutRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutRecordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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
  public Object getByDailyMetric(final String dailyMetricId,
      final Continuation<? super List<WorkoutRecordEntity>> $completion) {
    final String _sql = "SELECT * FROM workout_records WHERE dailyMetricId = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dailyMetricId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WorkoutRecordEntity>>() {
      @Override
      @NonNull
      public List<WorkoutRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<WorkoutRecordEntity> _result = new ArrayList<WorkoutRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutRecordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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
  public Flow<List<WorkoutRecordEntity>> observeRange(final long startDate, final long endDate) {
    final String _sql = "SELECT * FROM workout_records WHERE startDate BETWEEN ? AND ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"workout_records"}, new Callable<List<WorkoutRecordEntity>>() {
      @Override
      @NonNull
      public List<WorkoutRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<WorkoutRecordEntity> _result = new ArrayList<WorkoutRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutRecordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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
      final Continuation<? super List<WorkoutRecordEntity>> $completion) {
    final String _sql = "SELECT * FROM workout_records WHERE startDate BETWEEN ? AND ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WorkoutRecordEntity>>() {
      @Override
      @NonNull
      public List<WorkoutRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<WorkoutRecordEntity> _result = new ArrayList<WorkoutRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WorkoutRecordEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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
  public Object getById(final String id,
      final Continuation<? super WorkoutRecordEntity> $completion) {
    final String _sql = "SELECT * FROM workout_records WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutRecordEntity>() {
      @Override
      @Nullable
      public WorkoutRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final WorkoutRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _result = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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
  public Object getByHealthConnectUUID(final String uuid,
      final Continuation<? super WorkoutRecordEntity> $completion) {
    final String _sql = "SELECT * FROM workout_records WHERE healthConnectUUID = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, uuid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<WorkoutRecordEntity>() {
      @Override
      @Nullable
      public WorkoutRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfWorkoutType = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutType");
          final int _cursorIndexOfWorkoutName = CursorUtil.getColumnIndexOrThrow(_cursor, "workoutName");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfStrainScore = CursorUtil.getColumnIndexOrThrow(_cursor, "strainScore");
          final int _cursorIndexOfAverageHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "averageHeartRate");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfZone1Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone1Minutes");
          final int _cursorIndexOfZone2Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone2Minutes");
          final int _cursorIndexOfZone3Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone3Minutes");
          final int _cursorIndexOfZone4Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone4Minutes");
          final int _cursorIndexOfZone5Minutes = CursorUtil.getColumnIndexOrThrow(_cursor, "zone5Minutes");
          final int _cursorIndexOfMuscularLoad = CursorUtil.getColumnIndexOrThrow(_cursor, "muscularLoad");
          final int _cursorIndexOfIsStrengthWorkout = CursorUtil.getColumnIndexOrThrow(_cursor, "isStrengthWorkout");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final WorkoutRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final String _tmpWorkoutType;
            _tmpWorkoutType = _cursor.getString(_cursorIndexOfWorkoutType);
            final String _tmpWorkoutName;
            if (_cursor.isNull(_cursorIndexOfWorkoutName)) {
              _tmpWorkoutName = null;
            } else {
              _tmpWorkoutName = _cursor.getString(_cursorIndexOfWorkoutName);
            }
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            final Double _tmpStrainScore;
            if (_cursor.isNull(_cursorIndexOfStrainScore)) {
              _tmpStrainScore = null;
            } else {
              _tmpStrainScore = _cursor.getDouble(_cursorIndexOfStrainScore);
            }
            final Double _tmpAverageHeartRate;
            if (_cursor.isNull(_cursorIndexOfAverageHeartRate)) {
              _tmpAverageHeartRate = null;
            } else {
              _tmpAverageHeartRate = _cursor.getDouble(_cursorIndexOfAverageHeartRate);
            }
            final Double _tmpMaxHeartRate;
            if (_cursor.isNull(_cursorIndexOfMaxHeartRate)) {
              _tmpMaxHeartRate = null;
            } else {
              _tmpMaxHeartRate = _cursor.getDouble(_cursorIndexOfMaxHeartRate);
            }
            final Double _tmpCaloriesBurned;
            if (_cursor.isNull(_cursorIndexOfCaloriesBurned)) {
              _tmpCaloriesBurned = null;
            } else {
              _tmpCaloriesBurned = _cursor.getDouble(_cursorIndexOfCaloriesBurned);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final double _tmpZone1Minutes;
            _tmpZone1Minutes = _cursor.getDouble(_cursorIndexOfZone1Minutes);
            final double _tmpZone2Minutes;
            _tmpZone2Minutes = _cursor.getDouble(_cursorIndexOfZone2Minutes);
            final double _tmpZone3Minutes;
            _tmpZone3Minutes = _cursor.getDouble(_cursorIndexOfZone3Minutes);
            final double _tmpZone4Minutes;
            _tmpZone4Minutes = _cursor.getDouble(_cursorIndexOfZone4Minutes);
            final double _tmpZone5Minutes;
            _tmpZone5Minutes = _cursor.getDouble(_cursorIndexOfZone5Minutes);
            final Double _tmpMuscularLoad;
            if (_cursor.isNull(_cursorIndexOfMuscularLoad)) {
              _tmpMuscularLoad = null;
            } else {
              _tmpMuscularLoad = _cursor.getDouble(_cursorIndexOfMuscularLoad);
            }
            final boolean _tmpIsStrengthWorkout;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsStrengthWorkout);
            _tmpIsStrengthWorkout = _tmp != 0;
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _result = new WorkoutRecordEntity(_tmpId,_tmpDailyMetricId,_tmpWorkoutType,_tmpWorkoutName,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes,_tmpStrainScore,_tmpAverageHeartRate,_tmpMaxHeartRate,_tmpCaloriesBurned,_tmpDistanceMeters,_tmpZone1Minutes,_tmpZone2Minutes,_tmpZone3Minutes,_tmpZone4Minutes,_tmpZone5Minutes,_tmpMuscularLoad,_tmpIsStrengthWorkout,_tmpHealthConnectUUID);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
