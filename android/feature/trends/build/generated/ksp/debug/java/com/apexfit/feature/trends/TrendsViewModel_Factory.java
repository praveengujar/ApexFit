package com.apexfit.feature.trends;

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
public final class TrendsViewModel_Factory implements Factory<TrendsViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  public TrendsViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
  }

  @Override
  public TrendsViewModel get() {
    return newInstance(dailyMetricRepoProvider.get());
  }

  public static TrendsViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    return new TrendsViewModel_Factory(dailyMetricRepoProvider);
  }

  public static TrendsViewModel newInstance(DailyMetricRepository dailyMetricRepo) {
    return new TrendsViewModel(dailyMetricRepo);
  }
}
