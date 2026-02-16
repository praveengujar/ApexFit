package com.apexfit.core.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.BaselineMetricEntity;
import java.lang.Class;
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
public final class BaselineMetricDao_Impl implements BaselineMetricDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BaselineMetricEntity> __insertionAdapterOfBaselineMetricEntity;

  public BaselineMetricDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBaselineMetricEntity = new EntityInsertionAdapter<BaselineMetricEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `baseline_metrics` (`id`,`metricType`,`mean`,`standardDeviation`,`sampleCount`,`windowStartDate`,`windowEndDate`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BaselineMetricEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getMetricType());
        statement.bindDouble(3, entity.getMean());
        statement.bindDouble(4, entity.getStandardDeviation());
        statement.bindLong(5, entity.getSampleCount());
        if (entity.getWindowStartDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getWindowStartDate());
        }
        if (entity.getWindowEndDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getWindowEndDate());
        }
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
  }

  @Override
  public Object insert(final BaselineMetricEntity baseline,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBaselineMetricEntity.insert(baseline);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<BaselineMetricEntity> baselines,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBaselineMetricEntity.insert(baselines);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByType(final String metricType,
      final Continuation<? super BaselineMetricEntity> $completion) {
    final String _sql = "SELECT * FROM baseline_metrics WHERE metricType = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, metricType);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BaselineMetricEntity>() {
      @Override
      @Nullable
      public BaselineMetricEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfMean = CursorUtil.getColumnIndexOrThrow(_cursor, "mean");
          final int _cursorIndexOfStandardDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "standardDeviation");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfWindowStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "windowStartDate");
          final int _cursorIndexOfWindowEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "windowEndDate");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final BaselineMetricEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMetricType;
            _tmpMetricType = _cursor.getString(_cursorIndexOfMetricType);
            final double _tmpMean;
            _tmpMean = _cursor.getDouble(_cursorIndexOfMean);
            final double _tmpStandardDeviation;
            _tmpStandardDeviation = _cursor.getDouble(_cursorIndexOfStandardDeviation);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final Long _tmpWindowStartDate;
            if (_cursor.isNull(_cursorIndexOfWindowStartDate)) {
              _tmpWindowStartDate = null;
            } else {
              _tmpWindowStartDate = _cursor.getLong(_cursorIndexOfWindowStartDate);
            }
            final Long _tmpWindowEndDate;
            if (_cursor.isNull(_cursorIndexOfWindowEndDate)) {
              _tmpWindowEndDate = null;
            } else {
              _tmpWindowEndDate = _cursor.getLong(_cursorIndexOfWindowEndDate);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new BaselineMetricEntity(_tmpId,_tmpMetricType,_tmpMean,_tmpStandardDeviation,_tmpSampleCount,_tmpWindowStartDate,_tmpWindowEndDate,_tmpUpdatedAt);
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
  public Flow<List<BaselineMetricEntity>> observeAll() {
    final String _sql = "SELECT * FROM baseline_metrics";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"baseline_metrics"}, new Callable<List<BaselineMetricEntity>>() {
      @Override
      @NonNull
      public List<BaselineMetricEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMetricType = CursorUtil.getColumnIndexOrThrow(_cursor, "metricType");
          final int _cursorIndexOfMean = CursorUtil.getColumnIndexOrThrow(_cursor, "mean");
          final int _cursorIndexOfStandardDeviation = CursorUtil.getColumnIndexOrThrow(_cursor, "standardDeviation");
          final int _cursorIndexOfSampleCount = CursorUtil.getColumnIndexOrThrow(_cursor, "sampleCount");
          final int _cursorIndexOfWindowStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "windowStartDate");
          final int _cursorIndexOfWindowEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "windowEndDate");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<BaselineMetricEntity> _result = new ArrayList<BaselineMetricEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BaselineMetricEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpMetricType;
            _tmpMetricType = _cursor.getString(_cursorIndexOfMetricType);
            final double _tmpMean;
            _tmpMean = _cursor.getDouble(_cursorIndexOfMean);
            final double _tmpStandardDeviation;
            _tmpStandardDeviation = _cursor.getDouble(_cursorIndexOfStandardDeviation);
            final int _tmpSampleCount;
            _tmpSampleCount = _cursor.getInt(_cursorIndexOfSampleCount);
            final Long _tmpWindowStartDate;
            if (_cursor.isNull(_cursorIndexOfWindowStartDate)) {
              _tmpWindowStartDate = null;
            } else {
              _tmpWindowStartDate = _cursor.getLong(_cursorIndexOfWindowStartDate);
            }
            final Long _tmpWindowEndDate;
            if (_cursor.isNull(_cursorIndexOfWindowEndDate)) {
              _tmpWindowEndDate = null;
            } else {
              _tmpWindowEndDate = _cursor.getLong(_cursorIndexOfWindowEndDate);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new BaselineMetricEntity(_tmpId,_tmpMetricType,_tmpMean,_tmpStandardDeviation,_tmpSampleCount,_tmpWindowStartDate,_tmpWindowEndDate,_tmpUpdatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
