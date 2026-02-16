package com.apexfit.feature.strain;

import com.apexfit.core.data.repository.DailyMetricRepository;
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
public final class StrainViewModel_Factory implements Factory<StrainViewModel> {
  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  private final Provider<WorkoutRepository> workoutRepoProvider;

  public StrainViewModel_Factory(Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider) {
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
    this.workoutRepoProvider = workoutRepoProvider;
  }

  @Override
  public StrainViewModel get() {
    return newInstance(dailyMetricRepoProvider.get(), workoutRepoProvider.get());
  }

  public static StrainViewModel_Factory create(
      Provider<DailyMetricRepository> dailyMetricRepoProvider,
      Provider<WorkoutRepository> workoutRepoProvider) {
    return new StrainViewModel_Factory(dailyMetricRepoProvider, workoutRepoProvider);
  }

  public static StrainViewModel newInstance(DailyMetricRepository dailyMetricRepo,
      WorkoutRepository workoutRepo) {
    return new StrainViewModel(dailyMetricRepo, workoutRepo);
  }
}
