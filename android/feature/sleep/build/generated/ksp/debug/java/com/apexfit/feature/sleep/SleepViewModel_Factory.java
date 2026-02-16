package com.apexfit.feature.sleep;

import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.SleepRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SleepViewModel_Factory implements Factory<SleepViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<SleepRepository> sleepRepoProvider;

  public SleepViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<SleepRepository> sleepRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.sleepRepoProvider = sleepRepoProvider;
  }

  @Override
  public SleepViewModel get() {
    return newInstance(dailyMetricRepoProvider.get(), sleepRepoProvider.get());
  }

  public static SleepViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<SleepRepository> sleepRepoProvider) {
    return new SleepViewModel_Factory(dailyMetricRepoProvider, sleepRepoProvider);
  }

  public static SleepViewModel newInstance(DailyMetricRepository dailyMetricRepo,
      SleepRepository sleepRepo) {
    return new SleepViewModel(dailyMetricRepo, sleepRepo);
  }
}
