package com.apexfit.core.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.JournalEntryEntity;
import com.apexfit.core.data.entity.JournalResponseEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
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
public final class JournalDao_Impl implements JournalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<JournalEntryEntity> __insertionAdapterOfJournalEntryEntity;

  private final EntityInsertionAdapter<JournalResponseEntity> __insertionAdapterOfJournalResponseEntity;

  private final EntityDeletionOrUpdateAdapter<JournalEntryEntity> __updateAdapterOfJournalEntryEntity;

  public JournalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJournalEntryEntity = new EntityInsertionAdapter<JournalEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `journal_entries` (`id`,`userProfileId`,`date`,`completedAt`,`isComplete`,`streakDays`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserProfileId());
        statement.bindLong(3, entity.getDate());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCompletedAt());
        }
        final int _tmp = entity.isComplete() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getStreakDays());
      }
    };
    this.__insertionAdapterOfJournalResponseEntity = new EntityInsertionAdapter<JournalResponseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `journal_responses` (`id`,`journalEntryId`,`behaviorID`,`behaviorName`,`category`,`responseType`,`toggleValue`,`numericValue`,`scaleValue`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalResponseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getJournalEntryId());
        statement.bindString(3, entity.getBehaviorID());
        statement.bindString(4, entity.getBehaviorName());
        statement.bindString(5, entity.getCategory());
        statement.bindString(6, entity.getResponseType());
        final Integer _tmp = entity.getToggleValue() == null ? null : (entity.getToggleValue() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp);
        }
        if (entity.getNumericValue() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getNumericValue());
        }
        if (entity.getScaleValue() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getScaleValue());
        }
      }
    };
    this.__updateAdapterOfJournalEntryEntity = new EntityDeletionOrUpdateAdapter<JournalEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `journal_entries` SET `id` = ?,`userProfileId` = ?,`date` = ?,`completedAt` = ?,`isComplete` = ?,`streakDays` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntryEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserProfileId());
        statement.bindLong(3, entity.getDate());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCompletedAt());
        }
        final int _tmp = entity.isComplete() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getStreakDays());
        statement.bindString(7, entity.getId());
      }
    };
  }

  @Override
  public Object insertEntry(final JournalEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJournalEntryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertResponses(final List<JournalResponseEntity> responses,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJournalResponseEntity.insert(responses);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEntry(final JournalEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfJournalEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntryWithResponses(final JournalEntryEntity entry,
      final List<JournalResponseEntity> responses, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> JournalDao.DefaultImpls.insertEntryWithResponses(JournalDao_Impl.this, entry, responses, __cont), $completion);
  }

  @Override
  public Flow<JournalEntryEntity> observeEntryByDate(final long date) {
    final String _sql = "SELECT * FROM journal_entries WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<JournalEntryEntity>() {
      @Override
      @Nullable
      public JournalEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfIsComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "isComplete");
          final int _cursorIndexOfStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDays");
          final JournalEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final boolean _tmpIsComplete;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsComplete);
            _tmpIsComplete = _tmp != 0;
            final int _tmpStreakDays;
            _tmpStreakDays = _cursor.getInt(_cursorIndexOfStreakDays);
            _result = new JournalEntryEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpCompletedAt,_tmpIsComplete,_tmpStreakDays);
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
  public Object getEntryByDate(final long date,
      final Continuation<? super JournalEntryEntity> $completion) {
    final String _sql = "SELECT * FROM journal_entries WHERE date = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<JournalEntryEntity>() {
      @Override
      @Nullable
      public JournalEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfIsComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "isComplete");
          final int _cursorIndexOfStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDays");
          final JournalEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final boolean _tmpIsComplete;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsComplete);
            _tmpIsComplete = _tmp != 0;
            final int _tmpStreakDays;
            _tmpStreakDays = _cursor.getInt(_cursorIndexOfStreakDays);
            _result = new JournalEntryEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpCompletedAt,_tmpIsComplete,_tmpStreakDays);
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
  public Flow<List<JournalEntryEntity>> observeRecentEntries(final int limit) {
    final String _sql = "SELECT * FROM journal_entries ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfIsComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "isComplete");
          final int _cursorIndexOfStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDays");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final boolean _tmpIsComplete;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsComplete);
            _tmpIsComplete = _tmp != 0;
            final int _tmpStreakDays;
            _tmpStreakDays = _cursor.getInt(_cursorIndexOfStreakDays);
            _item = new JournalEntryEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpCompletedAt,_tmpIsComplete,_tmpStreakDays);
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
  public Object getCompletedEntries(
      final Continuation<? super List<JournalEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM journal_entries WHERE isComplete = 1 ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserProfileId = CursorUtil.getColumnIndexOrThrow(_cursor, "userProfileId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfIsComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "isComplete");
          final int _cursorIndexOfStreakDays = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDays");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserProfileId;
            _tmpUserProfileId = _cursor.getString(_cursorIndexOfUserProfileId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final boolean _tmpIsComplete;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsComplete);
            _tmpIsComplete = _tmp != 0;
            final int _tmpStreakDays;
            _tmpStreakDays = _cursor.getInt(_cursorIndexOfStreakDays);
            _item = new JournalEntryEntity(_tmpId,_tmpUserProfileId,_tmpDate,_tmpCompletedAt,_tmpIsComplete,_tmpStreakDays);
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
  public Flow<List<JournalResponseEntity>> observeResponses(final String entryId) {
    final String _sql = "SELECT * FROM journal_responses WHERE journalEntryId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, entryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_responses"}, new Callable<List<JournalResponseEntity>>() {
      @Override
      @NonNull
      public List<JournalResponseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalEntryId");
          final int _cursorIndexOfBehaviorID = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorID");
          final int _cursorIndexOfBehaviorName = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfResponseType = CursorUtil.getColumnIndexOrThrow(_cursor, "responseType");
          final int _cursorIndexOfToggleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "toggleValue");
          final int _cursorIndexOfNumericValue = CursorUtil.getColumnIndexOrThrow(_cursor, "numericValue");
          final int _cursorIndexOfScaleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "scaleValue");
          final List<JournalResponseEntity> _result = new ArrayList<JournalResponseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalResponseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalEntryId;
            _tmpJournalEntryId = _cursor.getString(_cursorIndexOfJournalEntryId);
            final String _tmpBehaviorID;
            _tmpBehaviorID = _cursor.getString(_cursorIndexOfBehaviorID);
            final String _tmpBehaviorName;
            _tmpBehaviorName = _cursor.getString(_cursorIndexOfBehaviorName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpResponseType;
            _tmpResponseType = _cursor.getString(_cursorIndexOfResponseType);
            final Boolean _tmpToggleValue;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfToggleValue)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfToggleValue);
            }
            _tmpToggleValue = _tmp == null ? null : _tmp != 0;
            final Double _tmpNumericValue;
            if (_cursor.isNull(_cursorIndexOfNumericValue)) {
              _tmpNumericValue = null;
            } else {
              _tmpNumericValue = _cursor.getDouble(_cursorIndexOfNumericValue);
            }
            final Integer _tmpScaleValue;
            if (_cursor.isNull(_cursorIndexOfScaleValue)) {
              _tmpScaleValue = null;
            } else {
              _tmpScaleValue = _cursor.getInt(_cursorIndexOfScaleValue);
            }
            _item = new JournalResponseEntity(_tmpId,_tmpJournalEntryId,_tmpBehaviorID,_tmpBehaviorName,_tmpCategory,_tmpResponseType,_tmpToggleValue,_tmpNumericValue,_tmpScaleValue);
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
  public Object getResponses(final String entryId,
      final Continuation<? super List<JournalResponseEntity>> $completion) {
    final String _sql = "SELECT * FROM journal_responses WHERE journalEntryId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, entryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<JournalResponseEntity>>() {
      @Override
      @NonNull
      public List<JournalResponseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalEntryId");
          final int _cursorIndexOfBehaviorID = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorID");
          final int _cursorIndexOfBehaviorName = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfResponseType = CursorUtil.getColumnIndexOrThrow(_cursor, "responseType");
          final int _cursorIndexOfToggleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "toggleValue");
          final int _cursorIndexOfNumericValue = CursorUtil.getColumnIndexOrThrow(_cursor, "numericValue");
          final int _cursorIndexOfScaleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "scaleValue");
          final List<JournalResponseEntity> _result = new ArrayList<JournalResponseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalResponseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalEntryId;
            _tmpJournalEntryId = _cursor.getString(_cursorIndexOfJournalEntryId);
            final String _tmpBehaviorID;
            _tmpBehaviorID = _cursor.getString(_cursorIndexOfBehaviorID);
            final String _tmpBehaviorName;
            _tmpBehaviorName = _cursor.getString(_cursorIndexOfBehaviorName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpResponseType;
            _tmpResponseType = _cursor.getString(_cursorIndexOfResponseType);
            final Boolean _tmpToggleValue;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfToggleValue)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfToggleValue);
            }
            _tmpToggleValue = _tmp == null ? null : _tmp != 0;
            final Double _tmpNumericValue;
            if (_cursor.isNull(_cursorIndexOfNumericValue)) {
              _tmpNumericValue = null;
            } else {
              _tmpNumericValue = _cursor.getDouble(_cursorIndexOfNumericValue);
            }
            final Integer _tmpScaleValue;
            if (_cursor.isNull(_cursorIndexOfScaleValue)) {
              _tmpScaleValue = null;
            } else {
              _tmpScaleValue = _cursor.getInt(_cursorIndexOfScaleValue);
            }
            _item = new JournalResponseEntity(_tmpId,_tmpJournalEntryId,_tmpBehaviorID,_tmpBehaviorName,_tmpCategory,_tmpResponseType,_tmpToggleValue,_tmpNumericValue,_tmpScaleValue);
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
  public Object getResponsesByBehavior(final String behaviorId,
      final Continuation<? super List<JournalResponseEntity>> $completion) {
    final String _sql = "SELECT * FROM journal_responses WHERE behaviorID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, behaviorId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<JournalResponseEntity>>() {
      @Override
      @NonNull
      public List<JournalResponseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfJournalEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "journalEntryId");
          final int _cursorIndexOfBehaviorID = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorID");
          final int _cursorIndexOfBehaviorName = CursorUtil.getColumnIndexOrThrow(_cursor, "behaviorName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfResponseType = CursorUtil.getColumnIndexOrThrow(_cursor, "responseType");
          final int _cursorIndexOfToggleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "toggleValue");
          final int _cursorIndexOfNumericValue = CursorUtil.getColumnIndexOrThrow(_cursor, "numericValue");
          final int _cursorIndexOfScaleValue = CursorUtil.getColumnIndexOrThrow(_cursor, "scaleValue");
          final List<JournalResponseEntity> _result = new ArrayList<JournalResponseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalResponseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpJournalEntryId;
            _tmpJournalEntryId = _cursor.getString(_cursorIndexOfJournalEntryId);
            final String _tmpBehaviorID;
            _tmpBehaviorID = _cursor.getString(_cursorIndexOfBehaviorID);
            final String _tmpBehaviorName;
            _tmpBehaviorName = _cursor.getString(_cursorIndexOfBehaviorName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpResponseType;
            _tmpResponseType = _cursor.getString(_cursorIndexOfResponseType);
            final Boolean _tmpToggleValue;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfToggleValue)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfToggleValue);
            }
            _tmpToggleValue = _tmp == null ? null : _tmp != 0;
            final Double _tmpNumericValue;
            if (_cursor.isNull(_cursorIndexOfNumericValue)) {
              _tmpNumericValue = null;
            } else {
              _tmpNumericValue = _cursor.getDouble(_cursorIndexOfNumericValue);
            }
            final Integer _tmpScaleValue;
            if (_cursor.isNull(_cursorIndexOfScaleValue)) {
              _tmpScaleValue = null;
            } else {
              _tmpScaleValue = _cursor.getInt(_cursorIndexOfScaleValue);
            }
            _item = new JournalResponseEntity(_tmpId,_tmpJournalEntryId,_tmpBehaviorID,_tmpBehaviorName,_tmpCategory,_tmpResponseType,_tmpToggleValue,_tmpNumericValue,_tmpScaleValue);
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
  public Object getCompletedCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM journal_entries WHERE isComplete = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
