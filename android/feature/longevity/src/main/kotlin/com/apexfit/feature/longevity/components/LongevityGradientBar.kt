package com.apexfit.feature.longevity.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.LongevityGreen
import com.apexfit.core.designsystem.theme.LongevityOrange
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import kotlin.math.abs

@Composable
fun LongevityGradientBar(
    range: ClosedFloatingPointRange<Double>,
    sixMonthAvg: Double,
    thirtyDayAvg: Double,
    deltaYears: Double,
    isHigherBetter: Boolean,
    rangeMinLabel: String,
    rangeMaxLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Gradient bar with markers
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
            ) {
                val segmentCount = 20
                val gapPx = 1.5.dp.toPx()
                val totalGaps = (segmentCount - 1) * gapPx
                val segmentWidth = (size.width - totalGaps) / segmentCount
                val barHeight = 12.dp.toPx()
                val barY = (size.height - barHeight) / 2

                // Draw segmented gradient
                for (i in 0 until segmentCount) {
                    val fraction = i.toFloat() / segmentCount
                    val x = i * (segmentWidth + gapPx)
                    drawRoundRect(
                        color = segmentColor(fraction, isHigherBetter),
                        topLeft = Offset(x, barY),
                        size = Size(segmentWidth, barHeight),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                    )
                }

                // 6-month marker (triangle pointing down, above bar)
                val sixMonthFraction = markerFraction(sixMonthAvg, range)
                val sixMonthX = size.width * sixMonthFraction
                drawTriangle(
                    center = Offset(sixMonthX, barY - 3.dp.toPx()),
                    size = 8.dp.toPx(),
                    pointingDown = true,
                    color = TextSecondary,
                )

                // 30-day marker (triangle pointing up, below bar)
                val thirtyDayFraction = markerFraction(thirtyDayAvg, range)
                val thirtyDayX = size.width * thirtyDayFraction
                drawTriangle(
                    center = Offset(thirtyDayX, barY + barHeight + 3.dp.toPx()),
                    size = 8.dp.toPx(),
                    pointingDown = false,
                    color = TextTertiary,
                )
            }

            Spacer(Modifier.height(2.dp))

            // Range labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = rangeMinLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isHigherBetter) LongevityOrange else LongevityGreen,
                )
                Text(
                    text = rangeMaxLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isHigherBetter) LongevityGreen else LongevityOrange,
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Delta years column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp),
        ) {
            val prefix = when {
                deltaYears >= 0.05 -> "+"
                deltaYears <= -0.05 -> ""
                else -> ""
            }
            val deltaColor = when {
                abs(deltaYears) < 0.05 -> TextTertiary
                deltaYears < 0 -> LongevityGreen
                else -> LongevityOrange
            }
            Text(
                text = "$prefix${String.format("%.1f", deltaYears)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = deltaColor,
            )
            Text(
                text = "years",
                fontSize = 11.sp,
                color = TextTertiary,
            )
        }
    }
}

private fun segmentColor(fraction: Float, isHigherBetter: Boolean): Color {
    val f = if (isHigherBetter) fraction else 1f - fraction
    return Color(
        red = 1f - f * 0.6f,
        green = 0.3f + f * 0.6f,
        blue = 0f + f * 0.1f,
    )
}

private fun markerFraction(
    value: Double,
    range: ClosedFloatingPointRange<Double>,
): Float {
    val lower = range.start
    val upper = range.endInclusive
    if (upper <= lower) return 0f
    return ((value - lower) / (upper - lower)).coerceIn(0.0, 1.0).toFloat()
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTriangle(
    center: Offset,
    size: Float,
    pointingDown: Boolean,
    color: Color,
) {
    val halfSize = size / 2f
    val path = Path().apply {
        if (pointingDown) {
            moveTo(center.x - halfSize, center.y - halfSize)
            lineTo(center.x + halfSize, center.y - halfSize)
            lineTo(center.x, center.y + halfSize)
        } else {
            moveTo(center.x - halfSize, center.y + halfSize)
            lineTo(center.x + halfSize, center.y + halfSize)
            lineTo(center.x, center.y - halfSize)
        }
        close()
    }
    drawPath(path, color)
}
