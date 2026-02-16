package com.apexfit.core.healthconnect;

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
public final class HealthConnectQueryService_Factory implements Factory<HealthConnectQueryService> {
  private final Provider<HealthConnectManager> managerProvider;

  public HealthConnectQueryService_Factory(Provider<HealthConnectManager> managerProvider) {
    this.managerProvider = managerProvider;
  }

  @Override
  public HealthConnectQueryService get() {
    return newInstance(managerProvider.get());
  }

  public static HealthConnectQueryService_Factory create(
      Provider<HealthConnectManager> managerProvider) {
    return new HealthConnectQueryService_Factory(managerProvider);
  }

  public static HealthConnectQueryService newInstance(HealthConnectManager manager) {
    return new HealthConnectQueryService(manager);
  }
}
