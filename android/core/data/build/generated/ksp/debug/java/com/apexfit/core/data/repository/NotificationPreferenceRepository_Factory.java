package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.NotificationPreferenceDao;
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
public final class NotificationPreferenceRepository_Factory implements Factory<NotificationPreferenceRepository> {
  private final Provider<NotificationPreferenceDao> daoProvider;

  public NotificationPreferenceRepository_Factory(Provider<NotificationPreferenceDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public NotificationPreferenceRepository get() {
    return newInstance(daoProvider.get());
  }

  public static NotificationPreferenceRepository_Factory create(
      Provider<NotificationPreferenceDao> daoProvider) {
    return new NotificationPreferenceRepository_Factory(daoProvider);
  }

  public static NotificationPreferenceRepository newInstance(NotificationPreferenceDao dao) {
    return new NotificationPreferenceRepository(dao);
  }
}
