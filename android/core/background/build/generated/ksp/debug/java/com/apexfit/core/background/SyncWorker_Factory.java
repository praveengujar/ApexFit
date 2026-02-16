package com.apexfit.core.background;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.apexfit.core.domain.usecase.SyncHealthDataUseCase;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<SyncHealthDataUseCase> syncUseCaseProvider;

  public SyncWorker_Factory(Provider<SyncHealthDataUseCase> syncUseCaseProvider) {
    this.syncUseCaseProvider = syncUseCaseProvider;
  }

  public SyncWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, syncUseCaseProvider.get());
  }

  public static SyncWorker_Factory create(Provider<SyncHealthDataUseCase> syncUseCaseProvider) {
    return new SyncWorker_Factory(syncUseCaseProvider);
  }

  public static SyncWorker newInstance(Context appContext, WorkerParameters workerParams,
      SyncHealthDataUseCase syncUseCase) {
    return new SyncWorker(appContext, workerParams, syncUseCase);
  }
}
