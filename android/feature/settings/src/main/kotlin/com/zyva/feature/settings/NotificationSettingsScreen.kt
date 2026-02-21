package com.zyva.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.model.NotificationType

@Composable
fun NotificationSettingsScreen(
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
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        NotificationType.entries.forEach { type ->
            val pref = uiState.notificationPrefs.find { it.notificationType == type.name }
            val isEnabled = pref?.isEnabled ?: true

            NotificationRow(
                type = type,
                isEnabled = isEnabled,
                onToggle = { viewModel.toggleNotification(type, it) },
            )

            // Bedtime time picker
            AnimatedVisibility(
                visible = type == NotificationType.BEDTIME_REMINDER && isEnabled,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Spacing.xl, bottom = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Reminder time: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                    val hour = pref?.customTimeHour ?: 22
                    val minute = pref?.customTimeMinute ?: 0
                    Text(
                        text = "%d:%02d PM".format(if (hour > 12) hour - 12 else hour, minute),
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xs))
        }
    }
}

@Composable
private fun NotificationRow(
    type: NotificationType,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val icon = notificationIcon(type)
    val color = notificationColor(type)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = type.label,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Text(
                text = type.description,
                fontSize = 11.sp,
                color = TextTertiary,
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue,
            ),
        )
    }
}

private fun notificationIcon(type: NotificationType): ImageVector = when (type) {
    NotificationType.MORNING_RECOVERY -> Icons.Filled.Notifications
    NotificationType.BEDTIME_REMINDER -> Icons.Filled.Nightlight
    NotificationType.STRAIN_TARGET -> Icons.Filled.Speed
    NotificationType.WEEKLY_REPORT -> Icons.Filled.Star
    NotificationType.HEALTH_ALERT -> Icons.Filled.Warning
    NotificationType.JOURNAL_REMINDER -> Icons.Filled.Campaign
    NotificationType.COACH_INSIGHT -> Icons.Filled.Psychology
}

private fun notificationColor(type: NotificationType): Color = when (type) {
    NotificationType.MORNING_RECOVERY -> RecoveryGreen
    NotificationType.BEDTIME_REMINDER -> PrimaryBlue
    NotificationType.STRAIN_TARGET -> RecoveryYellow
    NotificationType.WEEKLY_REPORT -> Teal
    NotificationType.HEALTH_ALERT -> RecoveryRed
    NotificationType.JOURNAL_REMINDER -> RecoveryYellow
    NotificationType.COACH_INSIGHT -> Teal
}
