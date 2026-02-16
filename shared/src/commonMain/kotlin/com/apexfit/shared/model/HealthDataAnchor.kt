package com.apexfit.shared.model

import com.apexfit.shared.platform.randomUUID

data class HealthDataAnchor(
    val id: String = randomUUID(),
    val dataTypeIdentifier: String,
    val anchorToken: String? = null,
)
