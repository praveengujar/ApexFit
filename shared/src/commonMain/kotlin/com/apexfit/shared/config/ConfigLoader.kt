package com.apexfit.shared.config

import com.apexfit.shared.model.config.ScoringConfig
import kotlinx.serialization.json.Json

object ConfigLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadScoringConfig(jsonString: String): ScoringConfig =
        json.decodeFromString<ScoringConfig>(jsonString)
}
