package com.apexfit.feature.recovery;

import com.apexfit.core.data.repository.DailyMetricRepository;
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
public final class RecoveryViewModel_Factory implements Factory<RecoveryViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  public RecoveryViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
  }

  @Override
  public RecoveryViewModel get() {
    return newInstance(dailyMetricRepoProvider.get());
  }

  public static RecoveryViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    return new RecoveryViewModel_Factory(dailyMetricRepoProvider);
  }

  public static RecoveryViewModel newInstance(DailyMetricRepository dailyMetricRepo) {
    return new RecoveryViewModel(dailyMetricRepo);
  }
}
