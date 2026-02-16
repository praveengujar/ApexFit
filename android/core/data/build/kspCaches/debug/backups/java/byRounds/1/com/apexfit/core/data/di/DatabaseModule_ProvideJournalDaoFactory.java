package com.apexfit.core.data.di;

import com.apexfit.core.data.ApexFitDatabase;
import com.apexfit.core.data.dao.JournalDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideJournalDaoFactory implements Factory<JournalDao> {
  private final Provider<ApexFitDatabase> dbProvider;

  public DatabaseModule_ProvideJournalDaoFactory(Provider<ApexFitDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public JournalDao get() {
    return provideJournalDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideJournalDaoFactory create(
      Provider<ApexFitDatabase> dbProvider) {
    return new DatabaseModule_ProvideJournalDaoFactory(dbProvider);
  }

  public static JournalDao provideJournalDao(ApexFitDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideJournalDao(db));
  }
}
