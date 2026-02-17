package com.apexfit.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.feature.profile.TimePeriod

@Composable
fun TimePeriodSelector(
    selected: TimePeriod,
    onSelect: (TimePeriod) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundCard)
            .padding(4.dp),
    ) {
        TimePeriod.entries.forEach { period ->
            val isSelected = period == selected
            val label = when (period) {
                TimePeriod.ONE_MONTH -> "1M"
                TimePeriod.THREE_MONTHS -> "3M"
                TimePeriod.ALL_TIME -> "ALL TIME"
            }
            Text(
                text = label,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .then(
                        if (isSelected) Modifier.background(BackgroundTertiary)
                        else Modifier,
                    )
                    .clickable { onSelect(period) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}
