package com.apexfit;

import com.apexfit.core.background.SyncScheduler;
import com.apexfit.core.notifications.NotificationChannels;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ApexFitApplication_MembersInjector implements MembersInjector<ApexFitApplication> {
  private final Provider<NotificationChannels> notificationChannelsProvider;

  private final Provider<SyncScheduler> syncSchedulerProvider;

  public ApexFitApplication_MembersInjector(
      Provider<NotificationChannels> notificationChannelsProvider,
      Provider<SyncScheduler> syncSchedulerProvider) {
    this.notificationChannelsProvider = notificationChannelsProvider;
    this.syncSchedulerProvider = syncSchedulerProvider;
  }

  public static MembersInjector<ApexFitApplication> create(
      Provider<NotificationChannels> notificationChannelsProvider,
      Provider<SyncScheduler> syncSchedulerProvider) {
    return new ApexFitApplication_MembersInjector(notificationChannelsProvider, syncSchedulerProvider);
  }

  @Override
  public void injectMembers(ApexFitApplication instance) {
    injectNotificationChannels(instance, notificationChannelsProvider.get());
    injectSyncScheduler(instance, syncSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.apexfit.ApexFitApplication.notificationChannels")
  public static void injectNotificationChannels(ApexFitApplication instance,
      NotificationChannels notificationChannels) {
    instance.notificationChannels = notificationChannels;
  }

  @InjectedFieldSignature("com.apexfit.ApexFitApplication.syncScheduler")
  public static void injectSyncScheduler(ApexFitApplication instance, SyncScheduler syncScheduler) {
    instance.syncScheduler = syncScheduler;
  }
}
