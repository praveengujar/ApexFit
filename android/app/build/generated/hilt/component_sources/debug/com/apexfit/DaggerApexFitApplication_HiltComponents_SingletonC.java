package com.apexfit;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.apexfit.core.background.SyncScheduler;
import com.apexfit.core.config.ConfigurationManager;
import com.apexfit.core.config.di.ConfigModule_ProvideScoringConfigFactory;
import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.DailyMetricDao;
import com.apexfit.core.data.dao.JournalDao;
import com.apexfit.core.data.dao.NotificationPreferenceDao;
import com.apexfit.core.data.dao.SleepDao;
import com.apexfit.core.data.dao.UserProfileDao;
import com.apexfit.core.data.dao.WorkoutRecordDao;
import com.apexfit.core.data.di.DatabaseModule_ProvideDailyMetricDaoFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideDatabaseFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideJournalDaoFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideNotificationPreferenceDaoFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideSleepDaoFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideUserProfileDaoFactory;
import com.apexfit.core.data.di.DatabaseModule_ProvideWorkoutRecordDaoFactory;
import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.JournalRepository;
import com.apexfit.core.data.repository.NotificationPreferenceRepository;
import com.apexfit.core.data.repository.SleepRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
import com.apexfit.core.data.repository.WorkoutRepository;
import com.apexfit.core.healthconnect.HealthConnectManager;
import com.apexfit.core.healthconnect.HealthConnectQueryService;
import com.apexfit.core.healthconnect.di.HealthConnectModule_ProvideHealthConnectQueryServiceFactory;
import com.apexfit.core.notifications.NotificationChannels;
import com.apexfit.feature.activity.ActivityViewModel;
import com.apexfit.feature.activity.ActivityViewModel_HiltModules;
import com.apexfit.feature.activity.ActivityViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.activity.ActivityViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.home.HomeViewModel;
import com.apexfit.feature.home.HomeViewModel_HiltModules;
import com.apexfit.feature.home.HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.home.HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.journal.JournalViewModel;
import com.apexfit.feature.journal.JournalViewModel_HiltModules;
import com.apexfit.feature.journal.JournalViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.journal.JournalViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.longevity.LongevityViewModel;
import com.apexfit.feature.longevity.LongevityViewModel_HiltModules;
import com.apexfit.feature.longevity.LongevityViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.longevity.LongevityViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.onboarding.OnboardingViewModel;
import com.apexfit.feature.onboarding.OnboardingViewModel_HiltModules;
import com.apexfit.feature.onboarding.OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.onboarding.OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.profile.ProfileViewModel;
import com.apexfit.feature.profile.ProfileViewModel_HiltModules;
import com.apexfit.feature.profile.ProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.profile.ProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.recovery.RecoveryViewModel;
import com.apexfit.feature.recovery.RecoveryViewModel_HiltModules;
import com.apexfit.feature.recovery.RecoveryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.recovery.RecoveryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.settings.SettingsViewModel;
import com.apexfit.feature.settings.SettingsViewModel_HiltModules;
import com.apexfit.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.sleep.SleepViewModel;
import com.apexfit.feature.sleep.SleepViewModel_HiltModules;
import com.apexfit.feature.sleep.SleepViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.sleep.SleepViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.strain.StrainViewModel;
import com.apexfit.feature.strain.StrainViewModel_HiltModules;
import com.apexfit.feature.strain.StrainViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.strain.StrainViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.feature.trends.TrendsViewModel;
import com.apexfit.feature.trends.TrendsViewModel_HiltModules;
import com.apexfit.feature.trends.TrendsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.feature.trends.TrendsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.navigation.MainViewModel;
import com.apexfit.navigation.MainViewModel_HiltModules;
import com.apexfit.navigation.MainViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.apexfit.navigation.MainViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.apexfit.shared.model.config.ScoringConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerApexFitApplication_HiltComponents_SingletonC {
  private DaggerApexFitApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ApexFitApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ApexFitApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ApexFitApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ApexFitApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ApexFitApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ApexFitApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ApexFitApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ApexFitApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ApexFitApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ApexFitApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ApexFitApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ApexFitApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ApexFitApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(12).put(ActivityViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ActivityViewModel_HiltModules.KeyModule.provide()).put(HomeViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HomeViewModel_HiltModules.KeyModule.provide()).put(JournalViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, JournalViewModel_HiltModules.KeyModule.provide()).put(LongevityViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LongevityViewModel_HiltModules.KeyModule.provide()).put(MainViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MainViewModel_HiltModules.KeyModule.provide()).put(OnboardingViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, OnboardingViewModel_HiltModules.KeyModule.provide()).put(ProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ProfileViewModel_HiltModules.KeyModule.provide()).put(RecoveryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RecoveryViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(SleepViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SleepViewModel_HiltModules.KeyModule.provide()).put(StrainViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, StrainViewModel_HiltModules.KeyModule.provide()).put(TrendsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TrendsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends ApexFitApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ActivityViewModel> activityViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<JournalViewModel> journalViewModelProvider;

    private Provider<LongevityViewModel> longevityViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<RecoveryViewModel> recoveryViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SleepViewModel> sleepViewModelProvider;

    private Provider<StrainViewModel> strainViewModelProvider;

    private Provider<TrendsViewModel> trendsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.activityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.journalViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.longevityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.recoveryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.sleepViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.strainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.trendsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(12).put(ActivityViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) activityViewModelProvider)).put(HomeViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) homeViewModelProvider)).put(JournalViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) journalViewModelProvider)).put(LongevityViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) longevityViewModelProvider)).put(MainViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) mainViewModelProvider)).put(OnboardingViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) onboardingViewModelProvider)).put(ProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) profileViewModelProvider)).put(RecoveryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) recoveryViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(SleepViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) sleepViewModelProvider)).put(StrainViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) strainViewModelProvider)).put(TrendsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) trendsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.apexfit.feature.activity.ActivityViewModel 
          return (T) new ActivityViewModel(singletonCImpl.workoutRepositoryProvider.get(), singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.userProfileRepositoryProvider.get(), singletonCImpl.provideHealthConnectQueryServiceProvider.get(), singletonCImpl.provideScoringConfigProvider.get());

          case 1: // com.apexfit.feature.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.workoutRepositoryProvider.get());

          case 2: // com.apexfit.feature.journal.JournalViewModel 
          return (T) new JournalViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.journalRepositoryProvider.get(), singletonCImpl.userProfileRepositoryProvider.get(), singletonCImpl.dailyMetricRepositoryProvider.get());

          case 3: // com.apexfit.feature.longevity.LongevityViewModel 
          return (T) new LongevityViewModel(singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.workoutRepositoryProvider.get(), singletonCImpl.userProfileRepositoryProvider.get());

          case 4: // com.apexfit.navigation.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.userProfileRepositoryProvider.get());

          case 5: // com.apexfit.feature.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.userProfileRepositoryProvider.get(), singletonCImpl.healthConnectManagerProvider.get());

          case 6: // com.apexfit.feature.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.workoutRepositoryProvider.get(), singletonCImpl.userProfileRepositoryProvider.get());

          case 7: // com.apexfit.feature.recovery.RecoveryViewModel 
          return (T) new RecoveryViewModel(singletonCImpl.dailyMetricRepositoryProvider.get());

          case 8: // com.apexfit.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.userProfileRepositoryProvider.get(), singletonCImpl.notificationPreferenceRepositoryProvider.get(), singletonCImpl.healthConnectManagerProvider.get(), singletonCImpl.provideScoringConfigProvider.get());

          case 9: // com.apexfit.feature.sleep.SleepViewModel 
          return (T) new SleepViewModel(singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.sleepRepositoryProvider.get());

          case 10: // com.apexfit.feature.strain.StrainViewModel 
          return (T) new StrainViewModel(singletonCImpl.dailyMetricRepositoryProvider.get(), singletonCImpl.workoutRepositoryProvider.get());

          case 11: // com.apexfit.feature.trends.TrendsViewModel 
          return (T) new TrendsViewModel(singletonCImpl.dailyMetricRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ApexFitApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ApexFitApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends ApexFitApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<NotificationChannels> notificationChannelsProvider;

    private Provider<SyncScheduler> syncSchedulerProvider;

    private Provider<ApexFitDatabase> provideDatabaseProvider;

    private Provider<WorkoutRepository> workoutRepositoryProvider;

    private Provider<DailyMetricRepository> dailyMetricRepositoryProvider;

    private Provider<UserProfileRepository> userProfileRepositoryProvider;

    private Provider<HealthConnectManager> healthConnectManagerProvider;

    private Provider<HealthConnectQueryService> provideHealthConnectQueryServiceProvider;

    private Provider<ConfigurationManager> configurationManagerProvider;

    private Provider<ScoringConfig> provideScoringConfigProvider;

    private Provider<JournalRepository> journalRepositoryProvider;

    private Provider<NotificationPreferenceRepository> notificationPreferenceRepositoryProvider;

    private Provider<SleepRepository> sleepRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private WorkoutRecordDao workoutRecordDao() {
      return DatabaseModule_ProvideWorkoutRecordDaoFactory.provideWorkoutRecordDao(provideDatabaseProvider.get());
    }

    private DailyMetricDao dailyMetricDao() {
      return DatabaseModule_ProvideDailyMetricDaoFactory.provideDailyMetricDao(provideDatabaseProvider.get());
    }

    private UserProfileDao userProfileDao() {
      return DatabaseModule_ProvideUserProfileDaoFactory.provideUserProfileDao(provideDatabaseProvider.get());
    }

    private JournalDao journalDao() {
      return DatabaseModule_ProvideJournalDaoFactory.provideJournalDao(provideDatabaseProvider.get());
    }

    private NotificationPreferenceDao notificationPreferenceDao() {
      return DatabaseModule_ProvideNotificationPreferenceDaoFactory.provideNotificationPreferenceDao(provideDatabaseProvider.get());
    }

    private SleepDao sleepDao() {
      return DatabaseModule_ProvideSleepDaoFactory.provideSleepDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.notificationChannelsProvider = DoubleCheck.provider(new SwitchingProvider<NotificationChannels>(singletonCImpl, 0));
      this.syncSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<SyncScheduler>(singletonCImpl, 1));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<ApexFitDatabase>(singletonCImpl, 3));
      this.workoutRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<WorkoutRepository>(singletonCImpl, 2));
      this.dailyMetricRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DailyMetricRepository>(singletonCImpl, 4));
      this.userProfileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserProfileRepository>(singletonCImpl, 5));
      this.healthConnectManagerProvider = DoubleCheck.provider(new SwitchingProvider<HealthConnectManager>(singletonCImpl, 7));
      this.provideHealthConnectQueryServiceProvider = DoubleCheck.provider(new SwitchingProvider<HealthConnectQueryService>(singletonCImpl, 6));
      this.configurationManagerProvider = DoubleCheck.provider(new SwitchingProvider<ConfigurationManager>(singletonCImpl, 9));
      this.provideScoringConfigProvider = DoubleCheck.provider(new SwitchingProvider<ScoringConfig>(singletonCImpl, 8));
      this.journalRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<JournalRepository>(singletonCImpl, 10));
      this.notificationPreferenceRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<NotificationPreferenceRepository>(singletonCImpl, 11));
      this.sleepRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SleepRepository>(singletonCImpl, 12));
    }

    @Override
    public void injectApexFitApplication(ApexFitApplication apexFitApplication) {
      injectApexFitApplication2(apexFitApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private ApexFitApplication injectApexFitApplication2(ApexFitApplication instance) {
      ApexFitApplication_MembersInjector.injectNotificationChannels(instance, notificationChannelsProvider.get());
      ApexFitApplication_MembersInjector.injectSyncScheduler(instance, syncSchedulerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.apexfit.core.notifications.NotificationChannels 
          return (T) new NotificationChannels(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.apexfit.core.background.SyncScheduler 
          return (T) new SyncScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.apexfit.core.data.repository.WorkoutRepository 
          return (T) new WorkoutRepository(singletonCImpl.workoutRecordDao());

          case 3: // com.apexfit.core.data.ApexFitDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.apexfit.core.data.repository.DailyMetricRepository 
          return (T) new DailyMetricRepository(singletonCImpl.dailyMetricDao());

          case 5: // com.apexfit.core.data.repository.UserProfileRepository 
          return (T) new UserProfileRepository(singletonCImpl.userProfileDao());

          case 6: // com.apexfit.core.healthconnect.HealthConnectQueryService 
          return (T) HealthConnectModule_ProvideHealthConnectQueryServiceFactory.provideHealthConnectQueryService(singletonCImpl.healthConnectManagerProvider.get());

          case 7: // com.apexfit.core.healthconnect.HealthConnectManager 
          return (T) new HealthConnectManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.apexfit.shared.model.config.ScoringConfig 
          return (T) ConfigModule_ProvideScoringConfigFactory.provideScoringConfig(singletonCImpl.configurationManagerProvider.get());

          case 9: // com.apexfit.core.config.ConfigurationManager 
          return (T) new ConfigurationManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.apexfit.core.data.repository.JournalRepository 
          return (T) new JournalRepository(singletonCImpl.journalDao());

          case 11: // com.apexfit.core.data.repository.NotificationPreferenceRepository 
          return (T) new NotificationPreferenceRepository(singletonCImpl.notificationPreferenceDao());

          case 12: // com.apexfit.core.data.repository.SleepRepository 
          return (T) new SleepRepository(singletonCImpl.sleepDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
