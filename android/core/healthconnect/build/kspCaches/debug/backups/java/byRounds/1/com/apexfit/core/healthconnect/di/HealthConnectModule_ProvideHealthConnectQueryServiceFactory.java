package com.apexfit.core.healthconnect.di;

import com.apexfit.core.healthconnect.HealthConnectManager;
import com.apexfit.core.healthconnect.HealthConnectQueryService;
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
public final class HealthConnectModule_ProvideHealthConnectQueryServiceFactory implements Factory<HealthConnectQueryService> {
  private final Provider<HealthConnectManager> managerProvider;

  public HealthConnectModule_ProvideHealthConnectQueryServiceFactory(
      Provider<HealthConnectManager> managerProvider) {
    this.managerProvider = managerProvider;
  }

  @Override
  public HealthConnectQueryService get() {
    return provideHealthConnectQueryService(managerProvider.get());
  }

  public static HealthConnectModule_ProvideHealthConnectQueryServiceFactory create(
      Provider<HealthConnectManager> managerProvider) {
    return new HealthConnectModule_ProvideHealthConnectQueryServiceFactory(managerProvider);
  }

  public static HealthConnectQueryService provideHealthConnectQueryService(
      HealthConnectManager manager) {
    return Preconditions.checkNotNullFromProvides(HealthConnectModule.INSTANCE.provideHealthConnectQueryService(manager));
  }
}
