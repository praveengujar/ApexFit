package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.UserProfileDao;
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
public final class DatabaseModule_ProvideUserProfileDaoFactory implements Factory<UserProfileDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideUserProfileDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UserProfileDao get() {
    return provideUserProfileDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideUserProfileDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideUserProfileDaoFactory(dbProvider);
  }

  public static UserProfileDao provideUserProfileDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUserProfileDao(db));
  }
}
