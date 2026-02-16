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
import com.apexfit.core.data.entity.NotificationPreferenceEntity;
import java.lang.Class;
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
public final class NotificationPreferenceDao_Impl implements NotificationPreferenceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NotificationPreferenceEntity> __insertionAdapterOfNotificationPreferenceEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetEnabled;

  public NotificationPreferenceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNotificationPreferenceEntity = new EntityInsertionAdapter<NotificationPreferenceEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `notification_preferences` (`notificationType`,`isEnabled`,`customTimeHour`,`customTimeMinute`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final NotificationPreferenceEntity entity) {
        statement.bindString(1, entity.getNotificationType());
        final int _tmp = entity.isEnabled() ? 1 : 0;
        statement.bindLong(2, _tmp);
        if (entity.getCustomTimeHour() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCustomTimeHour());
        }
        if (entity.getCustomTimeMinute() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCustomTimeMinute());
        }
      }
    };
    this.__preparedStmtOfSetEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE notification_preferences SET isEnabled = ? WHERE notificationType = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final NotificationPreferenceEntity preference,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNotificationPreferenceEntity.insert(preference);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<NotificationPreferenceEntity> preferences,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNotificationPreferenceEntity.insert(preferences);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setEnabled(final String type, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, type);
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
          __preparedStmtOfSetEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<NotificationPreferenceEntity>> observeAll() {
    final String _sql = "SELECT * FROM notification_preferences";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"notification_preferences"}, new Callable<List<NotificationPreferenceEntity>>() {
      @Override
      @NonNull
      public List<NotificationPreferenceEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNotificationType = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationType");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfCustomTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "customTimeHour");
          final int _cursorIndexOfCustomTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "customTimeMinute");
          final List<NotificationPreferenceEntity> _result = new ArrayList<NotificationPreferenceEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final NotificationPreferenceEntity _item;
            final String _tmpNotificationType;
            _tmpNotificationType = _cursor.getString(_cursorIndexOfNotificationType);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final Integer _tmpCustomTimeHour;
            if (_cursor.isNull(_cursorIndexOfCustomTimeHour)) {
              _tmpCustomTimeHour = null;
            } else {
              _tmpCustomTimeHour = _cursor.getInt(_cursorIndexOfCustomTimeHour);
            }
            final Integer _tmpCustomTimeMinute;
            if (_cursor.isNull(_cursorIndexOfCustomTimeMinute)) {
              _tmpCustomTimeMinute = null;
            } else {
              _tmpCustomTimeMinute = _cursor.getInt(_cursorIndexOfCustomTimeMinute);
            }
            _item = new NotificationPreferenceEntity(_tmpNotificationType,_tmpIsEnabled,_tmpCustomTimeHour,_tmpCustomTimeMinute);
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
  public Object getByType(final String type,
      final Continuation<? super NotificationPreferenceEntity> $completion) {
    final String _sql = "SELECT * FROM notification_preferences WHERE notificationType = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NotificationPreferenceEntity>() {
      @Override
      @Nullable
      public NotificationPreferenceEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNotificationType = CursorUtil.getColumnIndexOrThrow(_cursor, "notificationType");
          final int _cursorIndexOfIsEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isEnabled");
          final int _cursorIndexOfCustomTimeHour = CursorUtil.getColumnIndexOrThrow(_cursor, "customTimeHour");
          final int _cursorIndexOfCustomTimeMinute = CursorUtil.getColumnIndexOrThrow(_cursor, "customTimeMinute");
          final NotificationPreferenceEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpNotificationType;
            _tmpNotificationType = _cursor.getString(_cursorIndexOfNotificationType);
            final boolean _tmpIsEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEnabled);
            _tmpIsEnabled = _tmp != 0;
            final Integer _tmpCustomTimeHour;
            if (_cursor.isNull(_cursorIndexOfCustomTimeHour)) {
              _tmpCustomTimeHour = null;
            } else {
              _tmpCustomTimeHour = _cursor.getInt(_cursorIndexOfCustomTimeHour);
            }
            final Integer _tmpCustomTimeMinute;
            if (_cursor.isNull(_cursorIndexOfCustomTimeMinute)) {
              _tmpCustomTimeMinute = null;
            } else {
              _tmpCustomTimeMinute = _cursor.getInt(_cursorIndexOfCustomTimeMinute);
            }
            _result = new NotificationPreferenceEntity(_tmpNotificationType,_tmpIsEnabled,_tmpCustomTimeHour,_tmpCustomTimeMinute);
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
