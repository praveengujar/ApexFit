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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.apexfit.core.data.entity.UserProfileEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfileEntity> __insertionAdapterOfUserProfileEntity;

  private final EntityDeletionOrUpdateAdapter<UserProfileEntity> __updateAdapterOfUserProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkOnboardingComplete;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMaxHeartRate;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSleepBaseline;

  private final SharedSQLiteStatement __preparedStmtOfUpdateJournalBehaviors;

  private final SharedSQLiteStatement __preparedStmtOfUpdateWearableDevice;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfileEntity = new EntityInsertionAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profiles` (`id`,`firebaseUID`,`displayName`,`email`,`dateOfBirth`,`biologicalSex`,`heightCM`,`weightKG`,`maxHeartRate`,`maxHeartRateSource`,`sleepBaselineHours`,`preferredUnits`,`selectedJournalBehaviorIDs`,`hasCompletedOnboarding`,`wearableDevice`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getFirebaseUID() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFirebaseUID());
        }
        statement.bindString(3, entity.getDisplayName());
        if (entity.getEmail() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEmail());
        }
        if (entity.getDateOfBirth() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDateOfBirth());
        }
        statement.bindString(6, entity.getBiologicalSex());
        if (entity.getHeightCM() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getHeightCM());
        }
        if (entity.getWeightKG() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getWeightKG());
        }
        statement.bindLong(9, entity.getMaxHeartRate());
        statement.bindString(10, entity.getMaxHeartRateSource());
        statement.bindDouble(11, entity.getSleepBaselineHours());
        statement.bindString(12, entity.getPreferredUnits());
        statement.bindString(13, entity.getSelectedJournalBehaviorIDs());
        final int _tmp = entity.getHasCompletedOnboarding() ? 1 : 0;
        statement.bindLong(14, _tmp);
        if (entity.getWearableDevice() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getWearableDevice());
        }
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfUserProfileEntity = new EntityDeletionOrUpdateAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profiles` SET `id` = ?,`firebaseUID` = ?,`displayName` = ?,`email` = ?,`dateOfBirth` = ?,`biologicalSex` = ?,`heightCM` = ?,`weightKG` = ?,`maxHeartRate` = ?,`maxHeartRateSource` = ?,`sleepBaselineHours` = ?,`preferredUnits` = ?,`selectedJournalBehaviorIDs` = ?,`hasCompletedOnboarding` = ?,`wearableDevice` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getFirebaseUID() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFirebaseUID());
        }
        statement.bindString(3, entity.getDisplayName());
        if (entity.getEmail() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getEmail());
        }
        if (entity.getDateOfBirth() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDateOfBirth());
        }
        statement.bindString(6, entity.getBiologicalSex());
        if (entity.getHeightCM() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getHeightCM());
        }
        if (entity.getWeightKG() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getWeightKG());
        }
        statement.bindLong(9, entity.getMaxHeartRate());
        statement.bindString(10, entity.getMaxHeartRateSource());
        statement.bindDouble(11, entity.getSleepBaselineHours());
        statement.bindString(12, entity.getPreferredUnits());
        statement.bindString(13, entity.getSelectedJournalBehaviorIDs());
        final int _tmp = entity.getHasCompletedOnboarding() ? 1 : 0;
        statement.bindLong(14, _tmp);
        if (entity.getWearableDevice() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getWearableDevice());
        }
        statement.bindLong(16, entity.getCreatedAt());
        statement.bindLong(17, entity.getUpdatedAt());
        statement.bindString(18, entity.getId());
      }
    };
    this.__preparedStmtOfMarkOnboardingComplete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET hasCompletedOnboarding = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMaxHeartRate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET maxHeartRate = ?, maxHeartRateSource = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSleepBaseline = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET sleepBaselineHours = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateJournalBehaviors = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET selectedJournalBehaviorIDs = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateWearableDevice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profiles SET wearableDevice = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserProfileEntity.handle(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markOnboardingComplete(final String id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkOnboardingComplete.acquire();
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
          __preparedStmtOfMarkOnboardingComplete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMaxHeartRate(final String id, final int maxHR, final String source,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMaxHeartRate.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, maxHR);
        _argIndex = 2;
        _stmt.bindString(_argIndex, source);
        _argIndex = 3;
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
          __preparedStmtOfUpdateMaxHeartRate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSleepBaseline(final String id, final double hours,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSleepBaseline.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, hours);
        _argIndex = 2;
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
          __preparedStmtOfUpdateSleepBaseline.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateJournalBehaviors(final String id, final String ids,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateJournalBehaviors.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, ids);
        _argIndex = 2;
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
          __preparedStmtOfUpdateJournalBehaviors.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWearableDevice(final String id, final String device,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateWearableDevice.acquire();
        int _argIndex = 1;
        if (device == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, device);
        }
        _argIndex = 2;
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
          __preparedStmtOfUpdateWearableDevice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfileEntity> observeProfile() {
    final String _sql = "SELECT * FROM user_profiles LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profiles"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFirebaseUID = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUID");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDateOfBirth = CursorUtil.getColumnIndexOrThrow(_cursor, "dateOfBirth");
          final int _cursorIndexOfBiologicalSex = CursorUtil.getColumnIndexOrThrow(_cursor, "biologicalSex");
          final int _cursorIndexOfHeightCM = CursorUtil.getColumnIndexOrThrow(_cursor, "heightCM");
          final int _cursorIndexOfWeightKG = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKG");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfMaxHeartRateSource = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRateSource");
          final int _cursorIndexOfSleepBaselineHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepBaselineHours");
          final int _cursorIndexOfPreferredUnits = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredUnits");
          final int _cursorIndexOfSelectedJournalBehaviorIDs = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedJournalBehaviorIDs");
          final int _cursorIndexOfHasCompletedOnboarding = CursorUtil.getColumnIndexOrThrow(_cursor, "hasCompletedOnboarding");
          final int _cursorIndexOfWearableDevice = CursorUtil.getColumnIndexOrThrow(_cursor, "wearableDevice");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFirebaseUID;
            if (_cursor.isNull(_cursorIndexOfFirebaseUID)) {
              _tmpFirebaseUID = null;
            } else {
              _tmpFirebaseUID = _cursor.getString(_cursorIndexOfFirebaseUID);
            }
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final Long _tmpDateOfBirth;
            if (_cursor.isNull(_cursorIndexOfDateOfBirth)) {
              _tmpDateOfBirth = null;
            } else {
              _tmpDateOfBirth = _cursor.getLong(_cursorIndexOfDateOfBirth);
            }
            final String _tmpBiologicalSex;
            _tmpBiologicalSex = _cursor.getString(_cursorIndexOfBiologicalSex);
            final Double _tmpHeightCM;
            if (_cursor.isNull(_cursorIndexOfHeightCM)) {
              _tmpHeightCM = null;
            } else {
              _tmpHeightCM = _cursor.getDouble(_cursorIndexOfHeightCM);
            }
            final Double _tmpWeightKG;
            if (_cursor.isNull(_cursorIndexOfWeightKG)) {
              _tmpWeightKG = null;
            } else {
              _tmpWeightKG = _cursor.getDouble(_cursorIndexOfWeightKG);
            }
            final int _tmpMaxHeartRate;
            _tmpMaxHeartRate = _cursor.getInt(_cursorIndexOfMaxHeartRate);
            final String _tmpMaxHeartRateSource;
            _tmpMaxHeartRateSource = _cursor.getString(_cursorIndexOfMaxHeartRateSource);
            final double _tmpSleepBaselineHours;
            _tmpSleepBaselineHours = _cursor.getDouble(_cursorIndexOfSleepBaselineHours);
            final String _tmpPreferredUnits;
            _tmpPreferredUnits = _cursor.getString(_cursorIndexOfPreferredUnits);
            final String _tmpSelectedJournalBehaviorIDs;
            _tmpSelectedJournalBehaviorIDs = _cursor.getString(_cursorIndexOfSelectedJournalBehaviorIDs);
            final boolean _tmpHasCompletedOnboarding;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasCompletedOnboarding);
            _tmpHasCompletedOnboarding = _tmp != 0;
            final String _tmpWearableDevice;
            if (_cursor.isNull(_cursorIndexOfWearableDevice)) {
              _tmpWearableDevice = null;
            } else {
              _tmpWearableDevice = _cursor.getString(_cursorIndexOfWearableDevice);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserProfileEntity(_tmpId,_tmpFirebaseUID,_tmpDisplayName,_tmpEmail,_tmpDateOfBirth,_tmpBiologicalSex,_tmpHeightCM,_tmpWeightKG,_tmpMaxHeartRate,_tmpMaxHeartRateSource,_tmpSleepBaselineHours,_tmpPreferredUnits,_tmpSelectedJournalBehaviorIDs,_tmpHasCompletedOnboarding,_tmpWearableDevice,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getProfile(final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profiles LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFirebaseUID = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUID");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDateOfBirth = CursorUtil.getColumnIndexOrThrow(_cursor, "dateOfBirth");
          final int _cursorIndexOfBiologicalSex = CursorUtil.getColumnIndexOrThrow(_cursor, "biologicalSex");
          final int _cursorIndexOfHeightCM = CursorUtil.getColumnIndexOrThrow(_cursor, "heightCM");
          final int _cursorIndexOfWeightKG = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKG");
          final int _cursorIndexOfMaxHeartRate = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRate");
          final int _cursorIndexOfMaxHeartRateSource = CursorUtil.getColumnIndexOrThrow(_cursor, "maxHeartRateSource");
          final int _cursorIndexOfSleepBaselineHours = CursorUtil.getColumnIndexOrThrow(_cursor, "sleepBaselineHours");
          final int _cursorIndexOfPreferredUnits = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredUnits");
          final int _cursorIndexOfSelectedJournalBehaviorIDs = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedJournalBehaviorIDs");
          final int _cursorIndexOfHasCompletedOnboarding = CursorUtil.getColumnIndexOrThrow(_cursor, "hasCompletedOnboarding");
          final int _cursorIndexOfWearableDevice = CursorUtil.getColumnIndexOrThrow(_cursor, "wearableDevice");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFirebaseUID;
            if (_cursor.isNull(_cursorIndexOfFirebaseUID)) {
              _tmpFirebaseUID = null;
            } else {
              _tmpFirebaseUID = _cursor.getString(_cursorIndexOfFirebaseUID);
            }
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final Long _tmpDateOfBirth;
            if (_cursor.isNull(_cursorIndexOfDateOfBirth)) {
              _tmpDateOfBirth = null;
            } else {
              _tmpDateOfBirth = _cursor.getLong(_cursorIndexOfDateOfBirth);
            }
            final String _tmpBiologicalSex;
            _tmpBiologicalSex = _cursor.getString(_cursorIndexOfBiologicalSex);
            final Double _tmpHeightCM;
            if (_cursor.isNull(_cursorIndexOfHeightCM)) {
              _tmpHeightCM = null;
            } else {
              _tmpHeightCM = _cursor.getDouble(_cursorIndexOfHeightCM);
            }
            final Double _tmpWeightKG;
            if (_cursor.isNull(_cursorIndexOfWeightKG)) {
              _tmpWeightKG = null;
            } else {
              _tmpWeightKG = _cursor.getDouble(_cursorIndexOfWeightKG);
            }
            final int _tmpMaxHeartRate;
            _tmpMaxHeartRate = _cursor.getInt(_cursorIndexOfMaxHeartRate);
            final String _tmpMaxHeartRateSource;
            _tmpMaxHeartRateSource = _cursor.getString(_cursorIndexOfMaxHeartRateSource);
            final double _tmpSleepBaselineHours;
            _tmpSleepBaselineHours = _cursor.getDouble(_cursorIndexOfSleepBaselineHours);
            final String _tmpPreferredUnits;
            _tmpPreferredUnits = _cursor.getString(_cursorIndexOfPreferredUnits);
            final String _tmpSelectedJournalBehaviorIDs;
            _tmpSelectedJournalBehaviorIDs = _cursor.getString(_cursorIndexOfSelectedJournalBehaviorIDs);
            final boolean _tmpHasCompletedOnboarding;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHasCompletedOnboarding);
            _tmpHasCompletedOnboarding = _tmp != 0;
            final String _tmpWearableDevice;
            if (_cursor.isNull(_cursorIndexOfWearableDevice)) {
              _tmpWearableDevice = null;
            } else {
              _tmpWearableDevice = _cursor.getString(_cursorIndexOfWearableDevice);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserProfileEntity(_tmpId,_tmpFirebaseUID,_tmpDisplayName,_tmpEmail,_tmpDateOfBirth,_tmpBiologicalSex,_tmpHeightCM,_tmpWeightKG,_tmpMaxHeartRate,_tmpMaxHeartRateSource,_tmpSleepBaselineHours,_tmpPreferredUnits,_tmpSelectedJournalBehaviorIDs,_tmpHasCompletedOnboarding,_tmpWearableDevice,_tmpCreatedAt,_tmpUpdatedAt);
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
