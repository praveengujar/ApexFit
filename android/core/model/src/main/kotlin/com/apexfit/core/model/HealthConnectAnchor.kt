package com.apexfit.core.model

data class HealthConnectAnchor(
    val id: String = java.util.UUID.randomUUID().toString(),
    val dataTypeIdentifier: String,
    val anchorToken: String? = null,
)
