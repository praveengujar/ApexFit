package com.apexfit.feature.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.healthconnect.HealthConnectAvailability

@Composable
fun HealthConnectPermissionScreen(
    uiState: OnboardingUiState,
    onPermissionsResult: (Set<String>) -> Unit,
    onRequestPermissions: () -> Unit,
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
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = RecoveryGreen,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Health Access",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "ApexFit uses Health Connect to provide recovery scores, strain tracking, and sleep analysis.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg),
        )

        Spacer(Modifier.height(Spacing.xxl))

        // Data types list
        Column {
            HealthDataRow(
                icon = Icons.Filled.FavoriteBorder,
                color = RecoveryRed,
                title = "Heart Rate",
                description = "Continuous heart rate & resting HR",
            )
            Spacer(Modifier.height(Spacing.md))
            HealthDataRow(
                icon = Icons.Filled.Nightlight,
                color = Lavender,
                title = "Sleep",
                description = "Sleep stages and duration",
            )
            Spacer(Modifier.height(Spacing.md))
            HealthDataRow(
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                color = RecoveryYellow,
                title = "Workouts",
                description = "Exercise type, duration, and intensity",
            )
            Spacer(Modifier.height(Spacing.md))
            HealthDataRow(
                icon = Icons.Filled.MonitorHeart,
                color = Teal,
                title = "HRV",
                description = "Heart rate variability for recovery",
            )
            Spacer(Modifier.height(Spacing.md))
            HealthDataRow(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                color = PrimaryBlue,
                title = "Steps",
                description = "Daily step count and activity",
            )
        }

        Spacer(Modifier.weight(1f))

        // Status messages
        when {
            uiState.healthConnectPermissionsGranted -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = RecoveryGreen,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "Health access granted",
                        style = MaterialTheme.typography.labelLarge,
                        color = RecoveryGreen,
                    )
                }
                Spacer(Modifier.height(Spacing.md))
            }
            uiState.healthConnectAvailability == HealthConnectAvailability.NOT_SUPPORTED -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "Health Connect is not available on this device",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Spacer(Modifier.height(Spacing.md))
            }
            uiState.healthConnectAvailability == HealthConnectAvailability.NOT_INSTALLED -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = RecoveryYellow,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Text(
                        text = "Health Connect needs to be installed or updated",
                        style = MaterialTheme.typography.bodySmall,
                        color = RecoveryYellow,
                    )
                }
                Spacer(Modifier.height(Spacing.md))
            }
        }

        // Buttons
        if (uiState.healthConnectPermissionsGranted) {
            Button(
                onClick = onContinue,
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
        } else {
            Button(
                onClick = onRequestPermissions,
                enabled = !uiState.isRequestingPermissions &&
                    uiState.healthConnectAvailability == HealthConnectAvailability.AVAILABLE,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MinimumTapTarget + 8.dp),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                if (uiState.isRequestingPermissions) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = "Grant Access",
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
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun HealthDataRow(
    icon: ImageVector,
    color: Color,
    title: String,
    description: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(CornerRadius.small),
                )
                .padding(10.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}
