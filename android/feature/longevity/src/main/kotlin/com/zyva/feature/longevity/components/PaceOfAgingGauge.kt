package com.zyva.feature.longevity.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyva.core.designsystem.theme.LongevityGreen
import com.zyva.core.designsystem.theme.LongevityOrange
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextTertiary

private const val MIN_PACE = -1.0
private const val MAX_PACE = 3.0

@Composable
fun PaceOfAgingGauge(pace: Double) {
    val paceColor = when {
        pace < 0.5 -> LongevityGreen
        pace < 1.5 -> RecoveryYellow
        else -> LongevityOrange
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "PACE OF AGING",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.5.sp,
            )
            Text(
                text = String.format("%.1fx", pace),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = paceColor,
            )
        }

        Spacer(Modifier.height(4.dp))

        // Slow / Fast labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Slow", fontSize = 12.sp, color = TextTertiary)
            Text(text = "Fast", fontSize = 12.sp, color = TextTertiary)
        }

        Spacer(Modifier.height(4.dp))

        // Tick marks gauge
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp),
        ) {
            val tickCount = 40
            val tickWidth = 1.5.dp.toPx()
            val tickHeight = 16.dp.toPx()
            val totalGap = size.width - (tickCount * tickWidth)
            val gap = totalGap / (tickCount - 1)
            val tickY = (size.height - tickHeight) / 2

            // Draw ticks
            for (i in 0 until tickCount) {
                val x = i * (tickWidth + gap)
                drawRect(
                    color = TextTertiary.copy(alpha = 0.3f),
                    topLeft = Offset(x, tickY),
                    size = Size(tickWidth, tickHeight),
                )
            }

            // Draw indicator (two white bars)
            val fraction = ((pace - MIN_PACE) / (MAX_PACE - MIN_PACE))
                .coerceIn(0.0, 1.0).toFloat()
            val indicatorX = size.width * fraction
            val barWidth = 2.5.dp.toPx()
            val barHeight = 22.dp.toPx()
            val barGap = 2.dp.toPx()

            drawRect(
                color = Color.White,
                topLeft = Offset(indicatorX - barWidth - barGap / 2, 0f),
                size = Size(barWidth, barHeight),
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(indicatorX + barGap / 2, 0f),
                size = Size(barWidth, barHeight),
            )
        }

        Spacer(Modifier.height(4.dp))

        // Scale labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "-1.0x", fontSize = 11.sp, color = TextTertiary)
            Text(text = "1.0x", fontSize = 11.sp, color = TextTertiary)
            Text(text = "3.0x", fontSize = 11.sp, color = TextTertiary)
        }
    }
}
