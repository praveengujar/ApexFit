package com.apexfit.core.domain.usecase;

import com.apexfit.core.data.repository.BaselineRepository;
import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.shared.model.config.ScoringConfig;
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
public final class UpdateBaselinesUseCase_Factory implements Factory<UpdateBaselinesUseCase> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<BaselineRepository> baselineRepoProvider;

  private final Provider<ScoringConfig> configProvider;

  public UpdateBaselinesUseCase_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<BaselineRepository> baselineRepoProvider, Provider<ScoringConfig> configProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.baselineRepoProvider = baselineRepoProvider;
    this.configProvider = configProvider;
  }

  @Override
  public UpdateBaselinesUseCase get() {
    return newInstance(dailyMetricRepoProvider.get(), baselineRepoProvider.get(), configProvider.get());
  }

  public static UpdateBaselinesUseCase_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<BaselineRepository> baselineRepoProvider, Provider<ScoringConfig> configProvider) {
    return new UpdateBaselinesUseCase_Factory(dailyMetricRepoProvider, baselineRepoProvider, configProvider);
  }

  public static UpdateBaselinesUseCase newInstance(DailyMetricRepository dailyMetricRepo,
      BaselineRepository baselineRepo, ScoringConfig config) {
    return new UpdateBaselinesUseCase(dailyMetricRepo, baselineRepo, config);
  }
}
