package com.apexfit.feature.activity;

import com.apexfit.core.data.repository.DailyMetricRepository;
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
public final class ActivityViewModel_Factory implements Factory<ActivityViewModel> {
  private final Provider<WorkoutRepository> workoutRepoProvider;

  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<HealthConnectQueryService> queryServiceProvider;

  private final Provider<ScoringConfig> configProvider;

  public ActivityViewModel_Factory(Provider<WorkoutRepository> workoutRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<HealthConnectQueryService> queryServiceProvider,
      Provider<ScoringConfig> configProvider) {
    this.workoutRepoProvider = workoutRepoProvider;
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.queryServiceProvider = queryServiceProvider;
    this.configProvider = configProvider;
  }

  @Override
  public ActivityViewModel get() {
    return newInstance(workoutRepoProvider.get(), dailyMetricRepoProvider.get(), userProfileRepoProvider.get(), queryServiceProvider.get(), configProvider.get());
  }

  public static ActivityViewModel_Factory create(Provider<WorkoutRepository> workoutRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<HealthConnectQueryService> queryServiceProvider,
      Provider<ScoringConfig> configProvider) {
    return new ActivityViewModel_Factory(workoutRepoProvider, dailyMetricRepoProvider, userProfileRepoProvider, queryServiceProvider, configProvider);
  }

  public static ActivityViewModel newInstance(WorkoutRepository workoutRepo,
      DailyMetricRepository dailyMetricRepo, UserProfileRepository userProfileRepo,
      HealthConnectQueryService queryService, ScoringConfig config) {
    return new ActivityViewModel(workoutRepo, dailyMetricRepo, userProfileRepo, queryService, config);
  }
}
