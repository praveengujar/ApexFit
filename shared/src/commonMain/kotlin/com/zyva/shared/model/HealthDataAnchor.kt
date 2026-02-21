package com.zyva.shared.model

import com.zyva.shared.platform.randomUUID

data class HealthDataAnchor(
    val id: String = randomUUID(),
    val dataTypeIdentifier: String,
    val anchorToken: String? = null,
)
