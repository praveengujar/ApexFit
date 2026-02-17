package com.apexfit.feature.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.SleepDeep
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.feature.profile.TimePeriod

@Composable
fun DataHighlightsSection(
    period: TimePeriod,
    onPeriodChange: (TimePeriod) -> Unit,
    bestSleepPct: Double?,
    peakRecoveryPct: Double?,
    maxStrain: Double?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Data Highlights",
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimePeriodSelector(selected = period, onSelect = onPeriodChange)

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                CircularGauge(
                    value = bestSleepPct,
                    maxValue = 100.0,
                    label = "Best Sleep",
                    suffix = "%",
                    color = SleepDeep,
                )
                CircularGauge(
                    value = peakRecoveryPct,
                    maxValue = 100.0,
                    label = "Peak Recovery",
                    suffix = "%",
                    color = RecoveryGreen,
                )
                CircularGauge(
                    value = maxStrain,
                    maxValue = 21.0,
                    label = "Max Strain",
                    suffix = "",
                    color = PrimaryBlue,
                )
            }
        }
    }
}

@Composable
private fun CircularGauge(
    value: Double?,
    maxValue: Double,
    label: String,
    suffix: String,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            val sweep = if (value != null) ((value / maxValue) * 270f).toFloat().coerceIn(0f, 270f) else 0f
            Canvas(modifier = Modifier.size(80.dp)) {
                // Background arc
                drawArc(
                    color = BackgroundTertiary,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                )
                // Value arc
                if (sweep > 0f) {
                    drawArc(
                        color = color,
                        startAngle = 135f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
            }
            val displayText = when {
                value == null -> "--"
                suffix == "%" -> "${value.toInt()}$suffix"
                else -> "%.1f".format(value)
            }
            Text(
                text = displayText,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
        )
    }
}
