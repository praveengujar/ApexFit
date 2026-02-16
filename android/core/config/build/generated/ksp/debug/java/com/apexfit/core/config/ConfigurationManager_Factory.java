package com.apexfit.core.config;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ConfigurationManager_Factory implements Factory<ConfigurationManager> {
  private final Provider<Context> contextProvider;

  public ConfigurationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ConfigurationManager get() {
    return newInstance(contextProvider.get());
  }

  public static ConfigurationManager_Factory create(Provider<Context> contextProvider) {
    return new ConfigurationManager_Factory(contextProvider);
  }

  public static ConfigurationManager newInstance(Context context) {
    return new ConfigurationManager(context);
  }
}
