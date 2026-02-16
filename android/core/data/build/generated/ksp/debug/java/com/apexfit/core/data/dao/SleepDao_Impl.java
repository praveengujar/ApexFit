package com.apexfit.core.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.SleepSessionEntity;
import com.apexfit.core.data.entity.SleepStageEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
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
public final class SleepDao_Impl implements SleepDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SleepSessionEntity> __insertionAdapterOfSleepSessionEntity;

  private final EntityInsertionAdapter<SleepStageEntity> __insertionAdapterOfSleepStageEntity;

  public SleepDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSleepSessionEntity = new EntityInsertionAdapter<SleepSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sleep_sessions` (`id`,`dailyMetricId`,`startDate`,`endDate`,`isMainSleep`,`isNap`,`totalSleepMinutes`,`timeInBedMinutes`,`lightMinutes`,`deepMinutes`,`remMinutes`,`awakeMinutes`,`awakenings`,`sleepOnsetLatencyMinutes`,`sleepEfficiency`,`sleepPerformance`,`sleepNeedHours`,`healthConnectUUID`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepSessionEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDailyMetricId());
        statement.bindLong(3, entity.getStartDate());
        statement.bindLong(4, entity.getEndDate());
        final int _tmp = entity.isMainSleep() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.isNap() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindDouble(7, entity.getTotalSleepMinutes());
        statement.bindDouble(8, entity.getTimeInBedMinutes());
        statement.bindDouble(9, entity.getLightMinutes());
        statement.bindDouble(10, entity.getDeepMinutes());
        statement.bindDouble(11, entity.getRemMinutes());
        statement.bindDouble(12, entity.getAwakeMinutes());
        statement.bindLong(13, entity.getAwakenings());
        if (entity.getSleepOnsetLatencyMinutes() == null) {
          statement.bindNull(14);
        } else {
          statement.bindDouble(14, entity.getSleepOnsetLatencyMinutes());
        }
        statement.bindDouble(15, entity.getSleepEfficiency());
        if (entity.getSleepPerformance() == null) {
          statement.bindNull(16);
        } else {
          statement.bindDouble(16, entity.getSleepPerformance());
        }
        if (entity.getSleepNeedHours() == null) {
          statement.bindNull(17);
        } else {
          statement.bindDouble(17, entity.getSleepNeedHours());
        }
        if (entity.getHealthConnectUUID() == null) {
          statement.bindNull(18);
        } else {
          statement.bindString(18, entity.getHealthConnectUUID());
        }
      }
    };
    this.__insertionAdapterOfSleepStageEntity = new EntityInsertionAdapter<SleepStageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sleep_stages` (`id`,`sleepSessionId`,`stageType`,`startDate`,`endDate`,`durationMinutes`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SleepStageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getSleepSessionId());
        statement.bindString(3, entity.getStageType());
        statement.bindLong(4, entity.getStartDate());
        statement.bindLong(5, entity.getEndDate());
        statement.bindDouble(6, entity.getDurationMinutes());
      }
    };
  }

  @Override
  public Object insertSession(final SleepSessionEntity session,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSleepSessionEntity.insert(session);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertStages(final List<SleepStageEntity> stages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSleepStageEntity.insert(stages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSessionWithStages(final SleepSessionEntity session,
      final List<SleepStageEntity> stages, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> SleepDao.DefaultImpls.insertSessionWithStages(SleepDao_Impl.this, session, stages, __cont), $completion);
  }

  @Override
  public Flow<List<SleepSessionEntity>> observeSessionsByDailyMetric(final String dailyMetricId) {
    final String _sql = "SELECT * FROM sleep_sessions WHERE dailyMetricId = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dailyMetricId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_sessions"}, new Callable<List<SleepSessionEntity>>() {
      @Override
      @NonNull
      public List<SleepSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsMainSleep = CursorUtil.getColumnIndexOrThrow(_cursor, "isMainSleep");
          final int _cursorIndexOfIsNap = CursorUtil.getColumnIndexOrThrow(_cursor, "isNap");
          final int _cursorIndexOfTotalSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepMinutes");
          final int _cursorIndexOfTimeInBedMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timeInBedMinutes");
          final int _cursorIndexOfLightMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "lightMinutes");
          final int _cursorIndexOfDeepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepMinutes");
          final int _cursorIndexOfRemMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "remMinutes");
          final int _cursorIndexOfAwakeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "awakeMinutes");
          final int _cursorIndexOfAwakenings = CursorUtil.getColumnIndexOrThrow(_cursor, "awakenings");
          final int _cursorIndexOfSleepOnsetLatencyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepOnsetLatencyMinutes");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<SleepSessionEntity> _result = new ArrayList<SleepSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final boolean _tmpIsMainSleep;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMainSleep);
            _tmpIsMainSleep = _tmp != 0;
            final boolean _tmpIsNap;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsNap);
            _tmpIsNap = _tmp_1 != 0;
            final double _tmpTotalSleepMinutes;
            _tmpTotalSleepMinutes = _cursor.getDouble(_cursorIndexOfTotalSleepMinutes);
            final double _tmpTimeInBedMinutes;
            _tmpTimeInBedMinutes = _cursor.getDouble(_cursorIndexOfTimeInBedMinutes);
            final double _tmpLightMinutes;
            _tmpLightMinutes = _cursor.getDouble(_cursorIndexOfLightMinutes);
            final double _tmpDeepMinutes;
            _tmpDeepMinutes = _cursor.getDouble(_cursorIndexOfDeepMinutes);
            final double _tmpRemMinutes;
            _tmpRemMinutes = _cursor.getDouble(_cursorIndexOfRemMinutes);
            final double _tmpAwakeMinutes;
            _tmpAwakeMinutes = _cursor.getDouble(_cursorIndexOfAwakeMinutes);
            final int _tmpAwakenings;
            _tmpAwakenings = _cursor.getInt(_cursorIndexOfAwakenings);
            final Double _tmpSleepOnsetLatencyMinutes;
            if (_cursor.isNull(_cursorIndexOfSleepOnsetLatencyMinutes)) {
              _tmpSleepOnsetLatencyMinutes = null;
            } else {
              _tmpSleepOnsetLatencyMinutes = _cursor.getDouble(_cursorIndexOfSleepOnsetLatencyMinutes);
            }
            final double _tmpSleepEfficiency;
            _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new SleepSessionEntity(_tmpId,_tmpDailyMetricId,_tmpStartDate,_tmpEndDate,_tmpIsMainSleep,_tmpIsNap,_tmpTotalSleepMinutes,_tmpTimeInBedMinutes,_tmpLightMinutes,_tmpDeepMinutes,_tmpRemMinutes,_tmpAwakeMinutes,_tmpAwakenings,_tmpSleepOnsetLatencyMinutes,_tmpSleepEfficiency,_tmpSleepPerformance,_tmpSleepNeedHours,_tmpHealthConnectUUID);
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
  public Object getMainSleep(final String dailyMetricId,
      final Continuation<? super SleepSessionEntity> $completion) {
    final String _sql = "SELECT * FROM sleep_sessions WHERE dailyMetricId = ? AND isMainSleep = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dailyMetricId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SleepSessionEntity>() {
      @Override
      @Nullable
      public SleepSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsMainSleep = CursorUtil.getColumnIndexOrThrow(_cursor, "isMainSleep");
          final int _cursorIndexOfIsNap = CursorUtil.getColumnIndexOrThrow(_cursor, "isNap");
          final int _cursorIndexOfTotalSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepMinutes");
          final int _cursorIndexOfTimeInBedMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timeInBedMinutes");
          final int _cursorIndexOfLightMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "lightMinutes");
          final int _cursorIndexOfDeepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepMinutes");
          final int _cursorIndexOfRemMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "remMinutes");
          final int _cursorIndexOfAwakeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "awakeMinutes");
          final int _cursorIndexOfAwakenings = CursorUtil.getColumnIndexOrThrow(_cursor, "awakenings");
          final int _cursorIndexOfSleepOnsetLatencyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepOnsetLatencyMinutes");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final SleepSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final boolean _tmpIsMainSleep;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMainSleep);
            _tmpIsMainSleep = _tmp != 0;
            final boolean _tmpIsNap;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsNap);
            _tmpIsNap = _tmp_1 != 0;
            final double _tmpTotalSleepMinutes;
            _tmpTotalSleepMinutes = _cursor.getDouble(_cursorIndexOfTotalSleepMinutes);
            final double _tmpTimeInBedMinutes;
            _tmpTimeInBedMinutes = _cursor.getDouble(_cursorIndexOfTimeInBedMinutes);
            final double _tmpLightMinutes;
            _tmpLightMinutes = _cursor.getDouble(_cursorIndexOfLightMinutes);
            final double _tmpDeepMinutes;
            _tmpDeepMinutes = _cursor.getDouble(_cursorIndexOfDeepMinutes);
            final double _tmpRemMinutes;
            _tmpRemMinutes = _cursor.getDouble(_cursorIndexOfRemMinutes);
            final double _tmpAwakeMinutes;
            _tmpAwakeMinutes = _cursor.getDouble(_cursorIndexOfAwakeMinutes);
            final int _tmpAwakenings;
            _tmpAwakenings = _cursor.getInt(_cursorIndexOfAwakenings);
            final Double _tmpSleepOnsetLatencyMinutes;
            if (_cursor.isNull(_cursorIndexOfSleepOnsetLatencyMinutes)) {
              _tmpSleepOnsetLatencyMinutes = null;
            } else {
              _tmpSleepOnsetLatencyMinutes = _cursor.getDouble(_cursorIndexOfSleepOnsetLatencyMinutes);
            }
            final double _tmpSleepEfficiency;
            _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _result = new SleepSessionEntity(_tmpId,_tmpDailyMetricId,_tmpStartDate,_tmpEndDate,_tmpIsMainSleep,_tmpIsNap,_tmpTotalSleepMinutes,_tmpTimeInBedMinutes,_tmpLightMinutes,_tmpDeepMinutes,_tmpRemMinutes,_tmpAwakeMinutes,_tmpAwakenings,_tmpSleepOnsetLatencyMinutes,_tmpSleepEfficiency,_tmpSleepPerformance,_tmpSleepNeedHours,_tmpHealthConnectUUID);
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
  public Object getSessionsInRange(final long startDate, final long endDate,
      final Continuation<? super List<SleepSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM sleep_sessions WHERE startDate BETWEEN ? AND ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SleepSessionEntity>>() {
      @Override
      @NonNull
      public List<SleepSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyMetricId = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyMetricId");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfIsMainSleep = CursorUtil.getColumnIndexOrThrow(_cursor, "isMainSleep");
          final int _cursorIndexOfIsNap = CursorUtil.getColumnIndexOrThrow(_cursor, "isNap");
          final int _cursorIndexOfTotalSleepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalSleepMinutes");
          final int _cursorIndexOfTimeInBedMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "timeInBedMinutes");
          final int _cursorIndexOfLightMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "lightMinutes");
          final int _cursorIndexOfDeepMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "deepMinutes");
          final int _cursorIndexOfRemMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "remMinutes");
          final int _cursorIndexOfAwakeMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "awakeMinutes");
          final int _cursorIndexOfAwakenings = CursorUtil.getColumnIndexOrThrow(_cursor, "awakenings");
          final int _cursorIndexOfSleepOnsetLatencyMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepOnsetLatencyMinutes");
          final int _cursorIndexOfSleepEfficiency = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepEfficiency");
          final int _cursorIndexOfSleepPerformance = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepPerformance");
          final int _cursorIndexOfSleepNeedHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepNeedHours");
          final int _cursorIndexOfHealthConnectUUID = CursorUtil.getColumnIndexOrThrow(_cursor, "healthConnectUUID");
          final List<SleepSessionEntity> _result = new ArrayList<SleepSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepSessionEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDailyMetricId;
            _tmpDailyMetricId = _cursor.getString(_cursorIndexOfDailyMetricId);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final boolean _tmpIsMainSleep;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsMainSleep);
            _tmpIsMainSleep = _tmp != 0;
            final boolean _tmpIsNap;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsNap);
            _tmpIsNap = _tmp_1 != 0;
            final double _tmpTotalSleepMinutes;
            _tmpTotalSleepMinutes = _cursor.getDouble(_cursorIndexOfTotalSleepMinutes);
            final double _tmpTimeInBedMinutes;
            _tmpTimeInBedMinutes = _cursor.getDouble(_cursorIndexOfTimeInBedMinutes);
            final double _tmpLightMinutes;
            _tmpLightMinutes = _cursor.getDouble(_cursorIndexOfLightMinutes);
            final double _tmpDeepMinutes;
            _tmpDeepMinutes = _cursor.getDouble(_cursorIndexOfDeepMinutes);
            final double _tmpRemMinutes;
            _tmpRemMinutes = _cursor.getDouble(_cursorIndexOfRemMinutes);
            final double _tmpAwakeMinutes;
            _tmpAwakeMinutes = _cursor.getDouble(_cursorIndexOfAwakeMinutes);
            final int _tmpAwakenings;
            _tmpAwakenings = _cursor.getInt(_cursorIndexOfAwakenings);
            final Double _tmpSleepOnsetLatencyMinutes;
            if (_cursor.isNull(_cursorIndexOfSleepOnsetLatencyMinutes)) {
              _tmpSleepOnsetLatencyMinutes = null;
            } else {
              _tmpSleepOnsetLatencyMinutes = _cursor.getDouble(_cursorIndexOfSleepOnsetLatencyMinutes);
            }
            final double _tmpSleepEfficiency;
            _tmpSleepEfficiency = _cursor.getDouble(_cursorIndexOfSleepEfficiency);
            final Double _tmpSleepPerformance;
            if (_cursor.isNull(_cursorIndexOfSleepPerformance)) {
              _tmpSleepPerformance = null;
            } else {
              _tmpSleepPerformance = _cursor.getDouble(_cursorIndexOfSleepPerformance);
            }
            final Double _tmpSleepNeedHours;
            if (_cursor.isNull(_cursorIndexOfSleepNeedHours)) {
              _tmpSleepNeedHours = null;
            } else {
              _tmpSleepNeedHours = _cursor.getDouble(_cursorIndexOfSleepNeedHours);
            }
            final String _tmpHealthConnectUUID;
            if (_cursor.isNull(_cursorIndexOfHealthConnectUUID)) {
              _tmpHealthConnectUUID = null;
            } else {
              _tmpHealthConnectUUID = _cursor.getString(_cursorIndexOfHealthConnectUUID);
            }
            _item = new SleepSessionEntity(_tmpId,_tmpDailyMetricId,_tmpStartDate,_tmpEndDate,_tmpIsMainSleep,_tmpIsNap,_tmpTotalSleepMinutes,_tmpTimeInBedMinutes,_tmpLightMinutes,_tmpDeepMinutes,_tmpRemMinutes,_tmpAwakeMinutes,_tmpAwakenings,_tmpSleepOnsetLatencyMinutes,_tmpSleepEfficiency,_tmpSleepPerformance,_tmpSleepNeedHours,_tmpHealthConnectUUID);
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
  public Flow<List<SleepStageEntity>> observeStages(final String sessionId) {
    final String _sql = "SELECT * FROM sleep_stages WHERE sleepSessionId = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sleep_stages"}, new Callable<List<SleepStageEntity>>() {
      @Override
      @NonNull
      public List<SleepStageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSleepSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepSessionId");
          final int _cursorIndexOfStageType = CursorUtil.getColumnIndexOrThrow(_cursor, "stageType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final List<SleepStageEntity> _result = new ArrayList<SleepStageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepStageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSleepSessionId;
            _tmpSleepSessionId = _cursor.getString(_cursorIndexOfSleepSessionId);
            final String _tmpStageType;
            _tmpStageType = _cursor.getString(_cursorIndexOfStageType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            _item = new SleepStageEntity(_tmpId,_tmpSleepSessionId,_tmpStageType,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes);
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
  public Object getStages(final String sessionId,
      final Continuation<? super List<SleepStageEntity>> $completion) {
    final String _sql = "SELECT * FROM sleep_stages WHERE sleepSessionId = ? ORDER BY startDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SleepStageEntity>>() {
      @Override
      @NonNull
      public List<SleepStageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSleepSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepSessionId");
          final int _cursorIndexOfStageType = CursorUtil.getColumnIndexOrThrow(_cursor, "stageType");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final List<SleepStageEntity> _result = new ArrayList<SleepStageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SleepStageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpSleepSessionId;
            _tmpSleepSessionId = _cursor.getString(_cursorIndexOfSleepSessionId);
            final String _tmpStageType;
            _tmpStageType = _cursor.getString(_cursorIndexOfStageType);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getDouble(_cursorIndexOfDurationMinutes);
            _item = new SleepStageEntity(_tmpId,_tmpSleepSessionId,_tmpStageType,_tmpStartDate,_tmpEndDate,_tmpDurationMinutes);
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
  public Object getRecentBedtimes(final int count,
      final Continuation<? super List<Long>> $completion) {
    final String _sql = "SELECT startDate FROM sleep_sessions WHERE isMainSleep = 1 ORDER BY startDate DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, count);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            _item = _cursor.getLong(0);
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
  public Object getRecentWakeTimes(final int count,
      final Continuation<? super List<Long>> $completion) {
    final String _sql = "SELECT endDate FROM sleep_sessions WHERE isMainSleep = 1 ORDER BY endDate DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, count);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            _item = _cursor.getLong(0);
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
