package com.apexfit.feature.longevity;

import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.data.repository.WorkoutRepository;
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
public final class LongevityViewModel_Factory implements Factory<LongevityViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<WorkoutRepository> workoutRepoProvider;

  private final Provider<UserProfileRepository> userProfileRepoProvider;

  public LongevityViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.workoutRepoProvider = workoutRepoProvider;
    this.userProfileRepoProvider = userProfileRepoProvider;
  }

  @Override
  public LongevityViewModel get() {
    return newInstance(dailyMetricRepoProvider.get(), workoutRepoProvider.get(), userProfileRepoProvider.get());
  }

  public static LongevityViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider) {
    return new LongevityViewModel_Factory(dailyMetricRepoProvider, workoutRepoProvider, userProfileRepoProvider);
  }

  public static LongevityViewModel newInstance(DailyMetricRepository dailyMetricRepo,
      WorkoutRepository workoutRepo, UserProfileRepository userProfileRepo) {
    return new LongevityViewModel(dailyMetricRepo, workoutRepo, userProfileRepo);
  }
}
