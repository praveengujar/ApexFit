package com.apexfit.core.domain.usecase;

import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.SleepRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.model.config.ScoringConfig;
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
public final class ComputeSleepPlanUseCase_Factory implements Factory<ComputeSleepPlanUseCase> {
  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<SleepRepository> sleepRepoProvider;

  private final Provider<ScoringConfig> configProvider;

  public ComputeSleepPlanUseCase_Factory(Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<SleepRepository> sleepRepoProvider, Provider<ScoringConfig> configProvider) {
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.sleepRepoProvider = sleepRepoProvider;
    this.configProvider = configProvider;
  }

  @Override
  public ComputeSleepPlanUseCase get() {
    return newInstance(userProfileRepoProvider.get(), dailyMetricRepoProvider.get(), sleepRepoProvider.get(), configProvider.get());
  }

  public static ComputeSleepPlanUseCase_Factory create(
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<SleepRepository> sleepRepoProvider, Provider<ScoringConfig> configProvider) {
    return new ComputeSleepPlanUseCase_Factory(userProfileRepoProvider, dailyMetricRepoProvider, sleepRepoProvider, configProvider);
  }

  public static ComputeSleepPlanUseCase newInstance(UserProfileRepository userProfileRepo,
      DailyMetricRepository dailyMetricRepo, SleepRepository sleepRepo, ScoringConfig config) {
    return new ComputeSleepPlanUseCase(userProfileRepo, dailyMetricRepo, sleepRepo, config);
  }
}
