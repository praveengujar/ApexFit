package com.apexfit.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import com.apexfit.core.model.RecoveryZone
import com.apexfit.core.model.SleepStageType

// Recovery Zones
val RecoveryGreen = Color(0xFF16EC06)
val RecoveryYellow = Color(0xFFFFDE00)
val RecoveryRed = Color(0xFFFF0026)

// Sleep Stages
val SleepLight = Color(0xFF7B8CDE)
val SleepDeep = Color(0xFF4A3FB5)
val SleepREM = Color(0xFF00F19F)
val SleepAwake = Color.White
val SleepInBed = Color(0xFF3A3A3C)

// Strain Zones
val Zone1 = Color(0xFF4A90D9)
val Zone2 = Color(0xFF16EC06)
val Zone3 = Color(0xFFFFDE00)
val Zone4 = Color(0xFFFF8C00)
val Zone5 = Color(0xFFFF0026)

// UI Colors
val PrimaryBlue = Color(0xFF0A84FF)
val Teal = Color(0xFF00F19F)
val DeepPurple = Color(0xFF4A3FB5)
val Lavender = Color(0xFF7B8CDE)

// Backgrounds
val BackgroundPrimary = Color(0xFF000000)
val BackgroundSecondary = Color(0xFF1C1C1E)
val BackgroundTertiary = Color(0xFF2C2C2E)
val BackgroundCard = Color(0xFF1C1C1E)

// Text
val TextPrimary = Color.White
val TextSecondary = Color(0xFF8E8E93)
val TextTertiary = Color(0xFF636366)

// Helper functions
fun recoveryColor(zone: RecoveryZone): Color = when (zone) {
    RecoveryZone.GREEN -> RecoveryGreen
    RecoveryZone.YELLOW -> RecoveryYellow
    RecoveryZone.RED -> RecoveryRed
}

fun recoveryColor(score: Double): Color = recoveryColor(RecoveryZone.from(score))

fun strainZoneColor(zone: Int): Color = when (zone) {
    1 -> Zone1
    2 -> Zone2
    3 -> Zone3
    4 -> Zone4
    5 -> Zone5
    else -> Zone1
}

fun sleepStageColor(stage: SleepStageType): Color = when (stage) {
    SleepStageType.AWAKE -> SleepAwake
    SleepStageType.LIGHT -> SleepLight
    SleepStageType.DEEP -> SleepDeep
    SleepStageType.REM -> SleepREM
    SleepStageType.IN_BED -> SleepInBed
}
