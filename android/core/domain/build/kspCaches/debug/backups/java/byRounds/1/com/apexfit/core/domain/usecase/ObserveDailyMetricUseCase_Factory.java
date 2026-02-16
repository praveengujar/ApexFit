package com.apexfit.core.domain.usecase;

import com.apexfit.core.data.repository.DailyMetricRepository;
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
public final class ObserveDailyMetricUseCase_Factory implements Factory<ObserveDailyMetricUseCase> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  public ObserveDailyMetricUseCase_Factory(
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
  }

  @Override
  public ObserveDailyMetricUseCase get() {
    return newInstance(dailyMetricRepoProvider.get());
  }

  public static ObserveDailyMetricUseCase_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    return new ObserveDailyMetricUseCase_Factory(dailyMetricRepoProvider);
  }

  public static ObserveDailyMetricUseCase newInstance(DailyMetricRepository dailyMetricRepo) {
    return new ObserveDailyMetricUseCase(dailyMetricRepo);
  }
}
