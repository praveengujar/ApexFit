package com.apexfit.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.CornerRadius as ThemeCornerRadius
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyStrainRecoveryChart(
    weekMetrics: List<DailyMetricEntity>,
    selectedDayIndex: Int = -1,
) {
    if (weekMetrics.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ThemeCornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "WEEKLY STRAIN & RECOVERY",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.md))

        val maxStrain = 21f
        val barCount = weekMetrics.size.coerceAtMost(7)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            val w = size.width
            val h = size.height
            val barSpacing = 8.dp.toPx()
            val barWidth = (w - barSpacing * (barCount + 1)) / barCount

            // Grid lines
            for (i in 0..4) {
                val y = (i.toFloat() / 4) * h
                drawLine(
                    color = TextTertiary.copy(alpha = 0.1f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 0.5.dp.toPx(),
                )
            }

            weekMetrics.take(7).forEachIndexed { index, metric ->
                val x = barSpacing + index * (barWidth + barSpacing)

                // Strain bar
                val strainHeight = ((metric.strainScore ?: 0.0).toFloat() / maxStrain * h)
                    .coerceIn(0f, h)
                drawRoundRect(
                    color = PrimaryBlue.copy(alpha = 0.7f),
                    topLeft = Offset(x, h - strainHeight),
                    size = Size(barWidth / 2 - 2.dp.toPx(), strainHeight),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )

                // Recovery bar
                val recoveryHeight = ((metric.recoveryScore ?: 0.0).toFloat() / 100f * h)
                    .coerceIn(0f, h)
                val recoveryColor = when {
                    (metric.recoveryScore ?: 0.0) >= 67 -> RecoveryGreen
                    (metric.recoveryScore ?: 0.0) >= 34 -> RecoveryYellow
                    else -> RecoveryRed
                }
                drawRoundRect(
                    color = recoveryColor.copy(alpha = 0.7f),
                    topLeft = Offset(x + barWidth / 2 + 2.dp.toPx(), h - recoveryHeight),
                    size = Size(barWidth / 2 - 2.dp.toPx(), recoveryHeight),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                )

                // Selected day highlight
                if (index == selectedDayIndex) {
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.15f),
                        topLeft = Offset(x - 2.dp.toPx(), 0f),
                        size = Size(barWidth + 4.dp.toPx(), h),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        // Day labels
        val dayLabels = weekMetrics.take(7).map { metric ->
            val date = Instant.ofEpochMilli(metric.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault(),
            ).first().uppercase()
        }

        Text(
            text = dayLabels.joinToString("       "),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextTertiary,
            modifier = Modifier.padding(start = Spacing.sm),
        )
    }
}
