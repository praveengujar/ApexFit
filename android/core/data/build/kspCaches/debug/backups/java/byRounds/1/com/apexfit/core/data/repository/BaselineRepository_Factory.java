package com.apexfit.core.data.repository;

import com.apexfit.core.data.dao.BaselineMetricDao;
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
public final class BaselineRepository_Factory implements Factory<BaselineRepository> {
  private final Provider<BaselineMetricDao> daoProvider;

  public BaselineRepository_Factory(Provider<BaselineMetricDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public BaselineRepository get() {
    return newInstance(daoProvider.get());
  }

  public static BaselineRepository_Factory create(Provider<BaselineMetricDao> daoProvider) {
    return new BaselineRepository_Factory(daoProvider);
  }

  public static BaselineRepository newInstance(BaselineMetricDao dao) {
    return new BaselineRepository(dao);
  }
}
