package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.UserProfileDao;
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
public final class UserProfileRepository_Factory implements Factory<UserProfileRepository> {
  private final Provider<UserProfileDao> daoProvider;

  public UserProfileRepository_Factory(Provider<UserProfileDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UserProfileRepository get() {
    return newInstance(daoProvider.get());
  }

  public static UserProfileRepository_Factory create(Provider<UserProfileDao> daoProvider) {
    return new UserProfileRepository_Factory(daoProvider);
  }

  public static UserProfileRepository newInstance(UserProfileDao dao) {
    return new UserProfileRepository(dao);
  }
}
