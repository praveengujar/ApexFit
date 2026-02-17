package com.apexfit.core.domain.usecase;

import com.apexfit.core.data.repository.BaselineRepository;
import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.SleepRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.data.repository.WorkoutRepository;
import com.apexfit.core.healthconnect.HealthConnectQueryService;
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
public final class SyncHealthDataUseCase_Factory implements Factory<SyncHealthDataUseCase> {
  private final Provider<HealthConnectQueryService> queryServiceProvider;

  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<WorkoutRepository> workoutRepoProvider;

  private final Provider<SleepRepository> sleepRepoProvider;

  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<BaselineRepository> baselineRepoProvider;

  private final Provider<ScoringConfig> configProvider;

  public SyncHealthDataUseCase_Factory(Provider<HealthConnectQueryService> queryServiceProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider, Provider<SleepRepository> sleepRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<BaselineRepository> baselineRepoProvider, Provider<ScoringConfig> configProvider) {
    this.queryServiceProvider = queryServiceProvider;
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.workoutRepoProvider = workoutRepoProvider;
    this.sleepRepoProvider = sleepRepoProvider;
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.baselineRepoProvider = baselineRepoProvider;
    this.configProvider = configProvider;
  }

  @Override
  public SyncHealthDataUseCase get() {
    return newInstance(queryServiceProvider.get(), dailyMetricRepoProvider.get(), workoutRepoProvider.get(), sleepRepoProvider.get(), userProfileRepoProvider.get(), baselineRepoProvider.get(), configProvider.get());
  }

  public static SyncHealthDataUseCase_Factory create(
      Provider<HealthConnectQueryService> queryServiceProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider, Provider<SleepRepository> sleepRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<BaselineRepository> baselineRepoProvider, Provider<ScoringConfig> configProvider) {
    return new SyncHealthDataUseCase_Factory(queryServiceProvider, dailyMetricRepoProvider, workoutRepoProvider, sleepRepoProvider, userProfileRepoProvider, baselineRepoProvider, configProvider);
  }

  public static SyncHealthDataUseCase newInstance(HealthConnectQueryService queryService,
      DailyMetricRepository dailyMetricRepo, WorkoutRepository workoutRepo,
      SleepRepository sleepRepo, UserProfileRepository userProfileRepo,
      BaselineRepository baselineRepo, ScoringConfig config) {
    return new SyncHealthDataUseCase(queryService, dailyMetricRepo, workoutRepo, sleepRepo, userProfileRepo, baselineRepo, config);
  }
}
