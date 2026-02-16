package com.apexfit.core.config

import android.content.Context
import com.apexfit.shared.config.ConfigLoader
import com.apexfit.shared.model.config.ScoringConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    val config: ScoringConfig = loadConfig(context)

    private fun loadConfig(context: Context): ScoringConfig {
        val jsonString = context.assets.open("ScoringConfig.json")
            .bufferedReader()
            .use { it.readText() }
        return ConfigLoader.loadScoringConfig(jsonString)
    }
}
