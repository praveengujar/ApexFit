package com.apexfit.core.notifications;

import android.content.Context;
import com.apexfit.core.data.repository.NotificationPreferenceRepository;
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
public final class NotificationScheduler_Factory implements Factory<NotificationScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<NotificationPreferenceRepository> notifRepoProvider;

  public NotificationScheduler_Factory(Provider<Context> contextProvider,
      Provider<NotificationPreferenceRepository> notifRepoProvider) {
    this.contextProvider = contextProvider;
    this.notifRepoProvider = notifRepoProvider;
  }

  @Override
  public NotificationScheduler get() {
    return newInstance(contextProvider.get(), notifRepoProvider.get());
  }

  public static NotificationScheduler_Factory create(Provider<Context> contextProvider,
      Provider<NotificationPreferenceRepository> notifRepoProvider) {
    return new NotificationScheduler_Factory(contextProvider, notifRepoProvider);
  }

  public static NotificationScheduler newInstance(Context context,
      NotificationPreferenceRepository notifRepo) {
    return new NotificationScheduler(context, notifRepo);
  }
}
