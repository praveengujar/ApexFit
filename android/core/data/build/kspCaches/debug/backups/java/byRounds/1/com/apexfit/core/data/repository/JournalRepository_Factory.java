package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.JournalDao;
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
public final class JournalRepository_Factory implements Factory<JournalRepository> {
  private final Provider<JournalDao> daoProvider;

  public JournalRepository_Factory(Provider<JournalDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public JournalRepository get() {
    return newInstance(daoProvider.get());
  }

  public static JournalRepository_Factory create(Provider<JournalDao> daoProvider) {
    return new JournalRepository_Factory(daoProvider);
  }

  public static JournalRepository newInstance(JournalDao dao) {
    return new JournalRepository(dao);
  }
}
