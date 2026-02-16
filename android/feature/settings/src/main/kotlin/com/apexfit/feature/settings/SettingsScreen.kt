package com.apexfit.feature.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary

@Composable
fun SettingsScreen(
    onMaxHRTap: () -> Unit = {},
    onSleepGoalTap: () -> Unit = {},
    onNotificationsTap: () -> Unit = {},
    onUnitsTap: () -> Unit = {},
    onHealthConnectTap: () -> Unit = {},
    onAboutTap: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md)
            .padding(bottom = Spacing.xl),
    ) {
        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.lg))

        // Profile section
        SectionHeader("Profile")
        Spacer(Modifier.height(Spacing.sm))
        SettingsRow(
            icon = Icons.Filled.FavoriteBorder,
            iconColor = RecoveryRed,
            title = "Max Heart Rate",
            subtitle = "${uiState.maxHeartRate} BPM",
            onClick = onMaxHRTap,
        )
        Spacer(Modifier.height(Spacing.xs))
        SettingsRow(
            icon = Icons.Filled.Nightlight,
            iconColor = PrimaryBlue,
            title = "Sleep Goal",
            subtitle = "%.1f hours".format(uiState.sleepBaselineHours),
            onClick = onSleepGoalTap,
        )

        Spacer(Modifier.height(Spacing.lg))

        // Preferences section
        SectionHeader("Preferences")
        Spacer(Modifier.height(Spacing.sm))
        SettingsRow(
            icon = Icons.Filled.Straighten,
            iconColor = Teal,
            title = "Units",
            subtitle = uiState.preferredUnits.lowercase().replaceFirstChar { it.uppercase() },
            onClick = onUnitsTap,
        )
        Spacer(Modifier.height(Spacing.xs))
        SettingsRow(
            icon = Icons.Filled.Notifications,
            iconColor = RecoveryGreen,
            title = "Notifications",
            subtitle = "Manage alerts and reminders",
            onClick = onNotificationsTap,
        )

        Spacer(Modifier.height(Spacing.lg))

        // Data section
        SectionHeader("Data")
        Spacer(Modifier.height(Spacing.sm))
        SettingsRow(
            icon = Icons.Filled.Settings,
            iconColor = PrimaryBlue,
            title = "Health Connect",
            subtitle = "Permission status and data",
            onClick = onHealthConnectTap,
        )

        Spacer(Modifier.height(Spacing.lg))

        // About
        SettingsRow(
            icon = Icons.Filled.Info,
            iconColor = TextSecondary,
            title = "About",
            subtitle = "Version, legal, credits",
            onClick = onAboutTap,
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp),
        )
    }
}
