package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.WorkoutRecordDao;
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
public final class WorkoutRepository_Factory implements Factory<WorkoutRepository> {
  private final Provider<WorkoutRecordDao> daoProvider;

  public WorkoutRepository_Factory(Provider<WorkoutRecordDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public WorkoutRepository get() {
    return newInstance(daoProvider.get());
  }

  public static WorkoutRepository_Factory create(Provider<WorkoutRecordDao> daoProvider) {
    return new WorkoutRepository_Factory(daoProvider);
  }

  public static WorkoutRepository newInstance(WorkoutRecordDao dao) {
    return new WorkoutRepository(dao);
  }
}
