package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.BaselineMetricDao;
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
public final class DatabaseModule_ProvideBaselineMetricDaoFactory implements Factory<BaselineMetricDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideBaselineMetricDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BaselineMetricDao get() {
    return provideBaselineMetricDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBaselineMetricDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideBaselineMetricDaoFactory(dbProvider);
  }

  public static BaselineMetricDao provideBaselineMetricDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBaselineMetricDao(db));
  }
}
