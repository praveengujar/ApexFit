package com.zyva.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Spacing tokens
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

// Corner radius tokens
object CornerRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val xl = 20.dp
}

// Gauge sizes
object GaugeSize {
    val large = 200.dp
    val medium = 120.dp
    val small = 80.dp
    val lineWidth = 12.dp
    val lineWidthSmall = 8.dp
}

// Card constants
object CardDefaults {
    val padding = 16.dp
    val spacing = 12.dp
}

// Minimum tap target
val MinimumTapTarget = 44.dp

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimary,
    secondary = Teal,
    onSecondary = BackgroundPrimary,
    tertiary = DeepPurple,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = BackgroundSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundTertiary,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
)

@Composable
fun ZyvaTheme(
    content: @Composable () -> Unit,
) {
    // Zyva is always dark-themed
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = ZyvaTypography,
        content = content,
    )
}
