package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.SleepDao;
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
public final class DatabaseModule_ProvideSleepDaoFactory implements Factory<SleepDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideSleepDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SleepDao get() {
    return provideSleepDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSleepDaoFactory create(Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideSleepDaoFactory(dbProvider);
  }

  public static SleepDao provideSleepDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSleepDao(db));
  }
}
