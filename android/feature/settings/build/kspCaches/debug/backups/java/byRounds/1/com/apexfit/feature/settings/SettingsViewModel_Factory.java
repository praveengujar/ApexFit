package com.apexfit.feature.settings;

import com.apexfit.core.data.repository.NotificationPreferenceRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.healthconnect.HealthConnectManager;
import com.apexfit.core.model.config.ScoringConfig;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<NotificationPreferenceRepository> notifRepoProvider;

  private final Provider<HealthConnectManager> healthConnectManagerProvider;

  private final Provider<ScoringConfig> configProvider;

  public SettingsViewModel_Factory(Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<NotificationPreferenceRepository> notifRepoProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider,
      Provider<ScoringConfig> configProvider) {
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.notifRepoProvider = notifRepoProvider;
    this.healthConnectManagerProvider = healthConnectManagerProvider;
    this.configProvider = configProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userProfileRepoProvider.get(), notifRepoProvider.get(), healthConnectManagerProvider.get(), configProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<NotificationPreferenceRepository> notifRepoProvider,
      Provider<HealthConnectManager> healthConnectManagerProvider,
      Provider<ScoringConfig> configProvider) {
    return new SettingsViewModel_Factory(userProfileRepoProvider, notifRepoProvider, healthConnectManagerProvider, configProvider);
  }

  public static SettingsViewModel newInstance(UserProfileRepository userProfileRepo,
      NotificationPreferenceRepository notifRepo, HealthConnectManager healthConnectManager,
      ScoringConfig config) {
    return new SettingsViewModel(userProfileRepo, notifRepo, healthConnectManager, config);
  }
}
