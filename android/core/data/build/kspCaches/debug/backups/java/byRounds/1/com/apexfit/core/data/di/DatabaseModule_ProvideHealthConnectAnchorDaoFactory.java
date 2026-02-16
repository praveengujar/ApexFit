package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.HealthConnectAnchorDao;
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
public final class DatabaseModule_ProvideHealthConnectAnchorDaoFactory implements Factory<HealthConnectAnchorDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideHealthConnectAnchorDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public HealthConnectAnchorDao get() {
    return provideHealthConnectAnchorDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideHealthConnectAnchorDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideHealthConnectAnchorDaoFactory(dbProvider);
  }

  public static HealthConnectAnchorDao provideHealthConnectAnchorDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHealthConnectAnchorDao(db));
  }
}
