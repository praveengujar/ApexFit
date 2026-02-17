package com.apexfit.core.config.di;

import com.apexfit.core.config.ConfigurationManager;
import com.apexfit.shared.model.config.ScoringConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ConfigModule_ProvideScoringConfigFactory implements Factory<ScoringConfig> {
  private final Provider<ConfigurationManager> configurationManagerProvider;

  public ConfigModule_ProvideScoringConfigFactory(
      Provider<ConfigurationManager> configurationManagerProvider) {
    this.configurationManagerProvider = configurationManagerProvider;
  }

  @Override
  public ScoringConfig get() {
    return provideScoringConfig(configurationManagerProvider.get());
  }

  public static ConfigModule_ProvideScoringConfigFactory create(
      Provider<ConfigurationManager> configurationManagerProvider) {
    return new ConfigModule_ProvideScoringConfigFactory(configurationManagerProvider);
  }

  public static ScoringConfig provideScoringConfig(ConfigurationManager configurationManager) {
    return Preconditions.checkNotNullFromProvides(ConfigModule.INSTANCE.provideScoringConfig(configurationManager));
  }
}
