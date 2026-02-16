package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.SleepDao;
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
public final class SleepRepository_Factory implements Factory<SleepRepository> {
  private final Provider<SleepDao> daoProvider;

  public SleepRepository_Factory(Provider<SleepDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SleepRepository get() {
    return newInstance(daoProvider.get());
  }

  public static SleepRepository_Factory create(Provider<SleepDao> daoProvider) {
    return new SleepRepository_Factory(daoProvider);
  }

  public static SleepRepository newInstance(SleepDao dao) {
    return new SleepRepository(dao);
  }
}
