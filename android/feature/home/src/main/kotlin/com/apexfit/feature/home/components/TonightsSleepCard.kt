package com.apexfit.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.SleepDeep
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary

@Composable
fun TonightsSleepCard(
    sleepNeedHours: Double?,
    recommendedBedtime: String = "10:00 PM",
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "TONIGHT'S SLEEP",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Nightlight,
                contentDescription = null,
                tint = SleepDeep,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            Column {
                Text(
                    text = "Recommended Bedtime",
                    fontSize = 13.sp,
                    color = TextSecondary,
                )
                Text(
                    text = recommendedBedtime,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }
        }

        if (sleepNeedHours != null) {
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Sleep need: ${sleepNeedHours.toInt()}h ${((sleepNeedHours % 1) * 60).toInt()}m",
                fontSize = 13.sp,
                color = TextSecondary,
            )
        }
    }
}
