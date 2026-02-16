package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.DailyMetricDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DailyMetricRepository_Factory implements Factory<DailyMetricRepository> {
  private final Provider<DailyMetricDao> daoProvider;

  public DailyMetricRepository_Factory(Provider<DailyMetricDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DailyMetricRepository get() {
    return newInstance(daoProvider.get());
  }

  public static DailyMetricRepository_Factory create(Provider<DailyMetricDao> daoProvider) {
    return new DailyMetricRepository_Factory(daoProvider);
  }

  public static DailyMetricRepository newInstance(DailyMetricDao dao) {
    return new DailyMetricRepository(dao);
  }
}
