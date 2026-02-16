package com.apexfit.navigation;

import com.apexfit.core.data.repository.UserProfileRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<UserProfileRepository> userProfileRepoProvider;

  public MainViewModel_Factory(Provider<UserProfileRepository> userProfileRepoProvider) {
    this.userProfileRepoProvider = userProfileRepoProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(userProfileRepoProvider.get());
  }

  public static MainViewModel_Factory create(
      Provider<UserProfileRepository> userProfileRepoProvider) {
    return new MainViewModel_Factory(userProfileRepoProvider);
  }

  public static MainViewModel newInstance(UserProfileRepository userProfileRepo) {
    return new MainViewModel(userProfileRepo);
  }
}
