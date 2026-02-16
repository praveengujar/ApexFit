package com.apexfit.core.config.di

import com.apexfit.core.config.ConfigurationManager
import com.apexfit.core.model.config.ScoringConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideScoringConfig(configurationManager: ConfigurationManager): ScoringConfig {
        return configurationManager.config
    }
}
