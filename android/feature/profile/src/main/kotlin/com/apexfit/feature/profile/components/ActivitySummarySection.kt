package com.apexfit.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.feature.profile.ActivityTypeStats
import com.apexfit.feature.profile.TimePeriod

@Composable
fun ActivitySummarySection(
    period: TimePeriod,
    onPeriodChange: (TimePeriod) -> Unit,
    totalActivities: Int,
    breakdown: List<ActivityTypeStats>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Activity Summary",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(16.dp),
        ) {
            TimePeriodSelector(selected = period, onSelect = onPeriodChange)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${totalActivities}x",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "TOTAL ACTIVITIES",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header row
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACTIVITY | AVG. STRAIN",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "TOTAL",
                    color = TextTertiary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            breakdown.take(5).forEachIndexed { index, activity ->
                ActivityRow(activity)
                if (index < breakdown.take(5).lastIndex) {
                    HorizontalDivider(
                        color = TextTertiary.copy(alpha = 0.15f),
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityTypeStats) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = activity.displayName,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "%.1f".format(activity.avgStrain),
                color = TextSecondary,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${activity.count}x",
                color = PrimaryBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Proportional bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BackgroundTertiary),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = activity.proportion)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PrimaryBlue),
            )
        }
    }
}
