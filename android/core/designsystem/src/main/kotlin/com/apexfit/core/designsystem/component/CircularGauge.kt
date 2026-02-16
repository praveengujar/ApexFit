package com.apexfit.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apexfit.core.designsystem.theme.TextSecondary

@Composable
fun CircularGauge(
    value: Double,
    maxValue: Double,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    dimension: Dp = 120.dp,
    lineWidth: Dp = 8.dp,
    showCenterText: Boolean = true,
) {
    val progress = if (maxValue > 0) (value / maxValue).coerceIn(0.0, 1.0) else 0.0

    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(800),
        label = "gauge_progress",
    )

    LaunchedEffect(progress) {
        targetProgress = progress.toFloat()
    }

    Box(
        modifier = modifier.size(dimension),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(dimension)) {
            val strokeWidthPx = lineWidth.toPx()
            val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
            val topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
            val sweepAngle = 270f

            // Background arc
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = sweepAngle * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }

        if (showCenterText) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatGaugeValue(value, maxValue),
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                    color = color,
                )
                Text(
                    text = label,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
        }
    }
}

private fun formatGaugeValue(value: Double, maxValue: Double): String {
    if (value == 0.0 && maxValue > 0) return "--"
    return if (maxValue <= 21) {
        String.format("%.1f", value)
    } else {
        "${value.toInt()}"
    }
}
