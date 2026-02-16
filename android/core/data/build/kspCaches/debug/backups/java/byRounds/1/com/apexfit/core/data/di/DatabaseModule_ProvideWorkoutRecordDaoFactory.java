package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.WorkoutRecordDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideWorkoutRecordDaoFactory implements Factory<WorkoutRecordDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideWorkoutRecordDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WorkoutRecordDao get() {
    return provideWorkoutRecordDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWorkoutRecordDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideWorkoutRecordDaoFactory(dbProvider);
  }

  public static WorkoutRecordDao provideWorkoutRecordDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWorkoutRecordDao(db));
  }
}
