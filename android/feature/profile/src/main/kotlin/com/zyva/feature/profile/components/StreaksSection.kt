package com.zyva.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.SleepDeep
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary

@Composable
fun StreaksSection(
    sleepStreak: Int,
    greenRecoveryStreak: Int,
    strainStreak: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        SectionDividerLabel("STREAKS")
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StreakItem(
                modifier = Modifier.weight(1f),
                count = sleepStreak,
                label = "70%+ Sleep",
                accentColor = SleepDeep,
                emoji = "\uD83C\uDF19",
            )
            StreakItem(
                modifier = Modifier.weight(1f),
                count = greenRecoveryStreak,
                label = "Green Recovery",
                accentColor = RecoveryGreen,
                emoji = "\u2728",
            )
            StreakItem(
                modifier = Modifier.weight(1f),
                count = strainStreak,
                label = "10+ Strain",
                accentColor = PrimaryBlue,
                emoji = "\uD83D\uDCAA",
            )
        }
    }
}

@Composable
private fun StreakItem(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    accentColor: Color,
    emoji: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$count Days",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun SectionDividerLabel(title: String) {
    Text(
        text = title,
        color = TextTertiary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
    )
}
