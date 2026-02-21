package com.zyva.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_connect_anchors")
data class HealthConnectAnchorEntity(
    @PrimaryKey val dataTypeIdentifier: String,
    val anchorToken: String? = null,
)
