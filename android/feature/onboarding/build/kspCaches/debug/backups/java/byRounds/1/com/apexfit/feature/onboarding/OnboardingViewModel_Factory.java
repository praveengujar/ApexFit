package com.apexfit.feature.onboarding;

import android.content.Context;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.healthconnect.HealthConnectManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  public OnboardingViewModel_Factory(Provider<Context> contextProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    this.contextProvider = contextProvider;
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.healthConnectManagerProvider = healthConnectManagerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(contextProvider.get(), userProfileRepoProvider.get(), healthConnectManagerProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<Context> contextProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider) {
    return new OnboardingViewModel_Factory(contextProvider, userProfileRepoProvider, healthConnectManagerProvider);
  }

  public static OnboardingViewModel newInstance(Context context,
      UserProfileRepository userProfileRepo, HealthConnectManager healthConnectManager) {
    return new OnboardingViewModel(context, userProfileRepo, healthConnectManager);
  }
}
