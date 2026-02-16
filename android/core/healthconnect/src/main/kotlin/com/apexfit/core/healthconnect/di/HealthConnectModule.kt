package com.apexfit.core.healthconnect.di

import com.apexfit.core.healthconnect.HealthConnectManager
import com.apexfit.core.healthconnect.HealthConnectQueryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectModule {

    @Provides
    @Singleton
    fun provideHealthConnectQueryService(
        manager: HealthConnectManager,
    ): HealthConnectQueryService = HealthConnectQueryService(manager)
}
