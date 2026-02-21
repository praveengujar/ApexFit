package com.zyva.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary

@Composable
fun NotableStatsSection(
    lowestRHR: Double?,
    highestRHR: Double?,
    lowestHRV: Double?,
    highestHRV: Double?,
    maxHeartRate: Double?,
    longestSleepHours: Double?,
    lowestRecovery: Double?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        SectionDividerLabel("NOTABLE STATS")
        Spacer(modifier = Modifier.height(12.dp))

        val stats = listOfNotNull(
            lowestRHR?.let {
                StatItem("Lowest RHR", "${it.toInt()}", "bpm",
                    Icons.AutoMirrored.Filled.TrendingDown, RecoveryYellow)
            },
            highestRHR?.let {
                StatItem("Highest RHR", "${it.toInt()}", "bpm",
                    Icons.AutoMirrored.Filled.TrendingUp, RecoveryYellow)
            },
            lowestHRV?.let {
                StatItem("Lowest HRV", "${it.toInt()}", "ms",
                    Icons.AutoMirrored.Filled.TrendingDown, RecoveryYellow)
            },
            highestHRV?.let {
                StatItem("Highest HRV", "${it.toInt()}", "ms",
                    Icons.AutoMirrored.Filled.TrendingUp, RecoveryYellow)
            },
            maxHeartRate?.let {
                StatItem("Max Heart Rate", "${it.toInt()}", "bpm",
                    Icons.Filled.Favorite, RecoveryYellow)
            },
            longestSleepHours?.let {
                val hours = it.toInt()
                val mins = ((it - hours) * 60).toInt()
                StatItem("Longest Sleep", "$hours:%02d".format(mins), "hr",
                    Icons.Filled.Nightlight, RecoveryYellow)
            },
            lowestRecovery?.let {
                StatItem("Lowest Recovery", "${it.toInt()}", "%",
                    Icons.Filled.MonitorHeart, RecoveryYellow)
            },
        )

        stats.forEachIndexed { index, stat ->
            NotableStatRow(stat)
            if (index < stats.lastIndex) {
                HorizontalDivider(
                    color = TextTertiary.copy(alpha = 0.2f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}

private data class StatItem(
    val label: String,
    val value: String,
    val unit: String,
    val icon: ImageVector,
    val iconColor: Color,
)

@Composable
private fun NotableStatRow(stat: StatItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(stat.iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = null,
                tint = stat.iconColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stat.label,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = stat.value,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stat.unit,
            color = TextSecondary,
            fontSize = 13.sp,
        )
    }
}
