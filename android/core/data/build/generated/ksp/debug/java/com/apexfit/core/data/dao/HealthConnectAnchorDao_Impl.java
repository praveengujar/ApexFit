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
import com.apexfit.core.data.entity.HealthConnectAnchorEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HealthConnectAnchorDao_Impl implements HealthConnectAnchorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HealthConnectAnchorEntity> __insertionAdapterOfHealthConnectAnchorEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public HealthConnectAnchorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHealthConnectAnchorEntity = new EntityInsertionAdapter<HealthConnectAnchorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `health_connect_anchors` (`dataTypeIdentifier`,`anchorToken`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HealthConnectAnchorEntity entity) {
        statement.bindString(1, entity.getDataTypeIdentifier());
        if (entity.getAnchorToken() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getAnchorToken());
        }
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM health_connect_anchors";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final HealthConnectAnchorEntity anchor,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHealthConnectAnchorEntity.insert(anchor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getByIdentifier(final String identifier,
      final Continuation<? super HealthConnectAnchorEntity> $completion) {
    final String _sql = "SELECT * FROM health_connect_anchors WHERE dataTypeIdentifier = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, identifier);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HealthConnectAnchorEntity>() {
      @Override
      @Nullable
      public HealthConnectAnchorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDataTypeIdentifier = CursorUtil.getColumnIndexOrThrow(_cursor, "dataTypeIdentifier");
          final int _cursorIndexOfAnchorToken = CursorUtil.getColumnIndexOrThrow(_cursor, "anchorToken");
          final HealthConnectAnchorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDataTypeIdentifier;
            _tmpDataTypeIdentifier = _cursor.getString(_cursorIndexOfDataTypeIdentifier);
            final String _tmpAnchorToken;
            if (_cursor.isNull(_cursorIndexOfAnchorToken)) {
              _tmpAnchorToken = null;
            } else {
              _tmpAnchorToken = _cursor.getString(_cursorIndexOfAnchorToken);
            }
            _result = new HealthConnectAnchorEntity(_tmpDataTypeIdentifier,_tmpAnchorToken);
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
