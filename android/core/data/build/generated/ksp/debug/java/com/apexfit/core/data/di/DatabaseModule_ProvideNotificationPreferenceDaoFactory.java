package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.NotificationPreferenceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideNotificationPreferenceDaoFactory implements Factory<NotificationPreferenceDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideNotificationPreferenceDaoFactory(
      Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public NotificationPreferenceDao get() {
    return provideNotificationPreferenceDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideNotificationPreferenceDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideNotificationPreferenceDaoFactory(dbProvider);
  }

  public static NotificationPreferenceDao provideNotificationPreferenceDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideNotificationPreferenceDao(db));
  }
}
