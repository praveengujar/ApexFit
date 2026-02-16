package com.apexfit.feature.journal;

import android.content.Context;
import com.apexfit.core.data.repository.DailyMetricRepository;
import com.apexfit.core.data.repository.JournalRepository;
import com.apexfit.core.data.repository.UserProfileRepository;
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
public final class JournalViewModel_Factory implements Factory<JournalViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<JournalRepository> journalRepoProvider;

  private final Provider<UserProfileRepository> userProfileRepoProvider;

  private final Provider<DailyMetricRepository> dailyMetricRepoProvider;

  public JournalViewModel_Factory(Provider<Context> contextProvider,
      Provider<JournalRepository> journalRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    this.contextProvider = contextProvider;
    this.journalRepoProvider = journalRepoProvider;
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.dailyMetricRepoProvider = dailyMetricRepoProvider;
  }

  @Override
  public JournalViewModel get() {
    return newInstance(contextProvider.get(), journalRepoProvider.get(), userProfileRepoProvider.get(), dailyMetricRepoProvider.get());
  }

  public static JournalViewModel_Factory create(Provider<Context> contextProvider,
      Provider<JournalRepository> journalRepoProvider,
      Provider<UserProfileRepository> userProfileRepoProvider,
      Provider<DailyMetricRepository> dailyMetricRepoProvider) {
    return new JournalViewModel_Factory(contextProvider, journalRepoProvider, userProfileRepoProvider, dailyMetricRepoProvider);
  }

  public static JournalViewModel newInstance(Context context, JournalRepository journalRepo,
      UserProfileRepository userProfileRepo, DailyMetricRepository dailyMetricRepo) {
    return new JournalViewModel(context, journalRepo, userProfileRepo, dailyMetricRepo);
  }
}
