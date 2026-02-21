package com.zyva.feature.longevity.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.CornerRadius
import com.zyva.core.designsystem.theme.LongevityGreen
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.engine.LongevityResult

@Composable
fun ZyvaAgeTrendChart(
    weeklyTrend: List<LongevityResult>,
) {
    if (weeklyTrend.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "ZYVA AGE TREND",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        // Legend
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LongevityGreen),
            )
            Spacer(Modifier.width(4.dp))
            Text("YOUR ZYVA AGE", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
            Spacer(Modifier.width(Spacing.md))
            Spacer(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(TextTertiary),
            )
            Spacer(Modifier.width(4.dp))
            Text("CHRONOLOGICAL AGE", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextTertiary)
        }

        Spacer(Modifier.height(Spacing.md))

        val ages = weeklyTrend.map { it.zyvaAge.toFloat() }
        val chronoAge = weeklyTrend.first().chronologicalAge.toFloat()
        val allValues = ages + chronoAge
        val minY = (allValues.min() - 2f).coerceAtLeast(0f)
        val maxY = allValues.max() + 2f

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            val w = size.width
            val h = size.height
            val yRange = maxY - minY
            val pointCount = ages.size

            fun valueToY(v: Float) = h - ((v - minY) / yRange) * h
            fun indexToX(i: Int) = if (pointCount <= 1) w / 2 else (i.toFloat() / (pointCount - 1)) * w

            // Grid lines
            val gridCount = 4
            for (i in 0..gridCount) {
                val y = (i.toFloat() / gridCount) * h
                drawLine(
                    color = TextTertiary.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 0.5.dp.toPx(),
                )
            }

            // Chronological age dashed line
            val chronoY = valueToY(chronoAge)
            drawLine(
                color = TextTertiary,
                start = Offset(0f, chronoY),
                end = Offset(w, chronoY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())),
            )

            // Zyva Age line
            for (i in 0 until pointCount - 1) {
                drawLine(
                    color = LongevityGreen,
                    start = Offset(indexToX(i), valueToY(ages[i])),
                    end = Offset(indexToX(i + 1), valueToY(ages[i + 1])),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Dots
            for (i in ages.indices) {
                drawCircle(
                    color = LongevityGreen,
                    radius = 4.dp.toPx(),
                    center = Offset(indexToX(i), valueToY(ages[i])),
                )
            }
        }
    }
}

@Composable
fun PaceOfAgingTrendChart(
    weeklyTrend: List<LongevityResult>,
) {
    if (weeklyTrend.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "PACE OF AGING TREND",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.md))

        val paces = weeklyTrend.map { it.paceOfAging.toFloat() }
        val minY = -1f
        val maxY = 3f

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        ) {
            val w = size.width
            val h = size.height
            val yRange = maxY - minY
            val pointCount = paces.size

            fun valueToY(v: Float) = h - ((v - minY) / yRange) * h
            fun indexToX(i: Int) = if (pointCount <= 1) w / 2 else (i.toFloat() / (pointCount - 1)) * w

            // Grid lines at -1, 0, 1, 2, 3
            val gridValues = listOf(-1f, 0f, 1f, 2f, 3f)
            for (v in gridValues) {
                val y = valueToY(v)
                drawLine(
                    color = TextTertiary.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 0.5.dp.toPx(),
                )
            }

            // Reference line at 1.0x (dashed)
            val refY = valueToY(1f)
            drawLine(
                color = TextTertiary.copy(alpha = 0.5f),
                start = Offset(0f, refY),
                end = Offset(w, refY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())),
            )

            // Pace line
            for (i in 0 until pointCount - 1) {
                drawLine(
                    color = PrimaryBlue,
                    start = Offset(indexToX(i), valueToY(paces[i])),
                    end = Offset(indexToX(i + 1), valueToY(paces[i + 1])),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }

            // Dots
            for (i in paces.indices) {
                drawCircle(
                    color = PrimaryBlue,
                    radius = 4.dp.toPx(),
                    center = Offset(indexToX(i), valueToY(paces[i])),
                )
            }
        }
    }
}
