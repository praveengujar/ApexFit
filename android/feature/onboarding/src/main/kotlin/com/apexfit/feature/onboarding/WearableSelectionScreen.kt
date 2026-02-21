package com.apexfit.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundSecondary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.Lavender
import com.apexfit.core.designsystem.theme.MinimumTapTarget
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary

private data class WearableOption(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
)

private val wearableOptions = listOf(
    WearableOption("PIXEL_WATCH", "Pixel Watch", Icons.Filled.Watch, PrimaryBlue),
    WearableOption("GARMIN", "Garmin", Icons.Filled.Watch, RecoveryGreen),
    WearableOption("AMAZFIT", "Amazfit", Icons.Filled.Watch, RecoveryYellow),
    WearableOption("OURA_RING", "Oura Ring", Icons.Filled.Circle, Lavender),
    WearableOption("OTHER", "Other", Icons.Filled.Devices, TextSecondary),
)

@Composable
fun WearableSelectionScreen(
    selectedDevice: String?,
    onDeviceSelected: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        // Header
        Icon(
            imageVector = Icons.Filled.Watch,
            contentDescription = null,
            tint = RecoveryGreen,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Your Wearable",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Select the device you use to track your health data. This helps ApexFit optimize your experience.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg),
        )

        Spacer(Modifier.height(Spacing.xxl))

        // Device options
        Column {
            wearableOptions.forEach { option ->
                val isSelected = selectedDevice == option.id
                WearableOptionCard(
                    option = option,
                    isSelected = isSelected,
                    onClick = { onDeviceSelected(option.id) },
                )
                Spacer(Modifier.height(Spacing.sm))
            }
        }

        Spacer(Modifier.weight(1f))

        // Continue button
        Button(
            onClick = onContinue,
            enabled = selectedDevice != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(MinimumTapTarget + 8.dp),
            shape = RoundedCornerShape(CornerRadius.medium),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
        }

        Spacer(Modifier.height(Spacing.md))

        TextButton(onClick = onContinue) {
            Text(
                text = "Skip for now",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun WearableOptionCard(
    option: WearableOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(CornerRadius.medium)
    val background = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else BackgroundSecondary
    val borderModifier = if (isSelected) {
        Modifier.border(width = 1.5.dp, color = PrimaryBlue, shape = shape)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier)
            .background(color = background, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = option.color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = option.name,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
    }
}
