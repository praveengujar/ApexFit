package com.apexfit.feature.home;

import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.WorkoutRepository;
import com.apexfit.core.domain.usecase.SyncHealthDataUseCase;
import com.apexfit.core.healthconnect.HealthConnectManager;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<WorkoutRepository> workoutRepoProvider;

  private final Provider<SyncHealthDataUseCase> syncUseCaseProvider;

  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  public HomeViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider,
      Provider<SyncHealthDataUseCase> syncUseCaseProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.workoutRepoProvider = workoutRepoProvider;
    this.syncUseCaseProvider = syncUseCaseProvider;
    this.healthConnectManagerProvider = healthConnectManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(dailyMetricRepoProvider.get(), workoutRepoProvider.get(), syncUseCaseProvider.get(), healthConnectManagerProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider,
      Provider<SyncHealthDataUseCase> syncUseCaseProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    return new HomeViewModel_Factory(dailyMetricRepoProvider, workoutRepoProvider, syncUseCaseProvider, healthConnectManagerProvider);
  }

  public static HomeViewModel newInstance(DailyMetricRepository dailyMetricRepo,
      WorkoutRepository workoutRepo, SyncHealthDataUseCase syncUseCase,
      HealthConnectManager healthConnectManager) {
    return new HomeViewModel(dailyMetricRepo, workoutRepo, syncUseCase, healthConnectManager);
  }
}
