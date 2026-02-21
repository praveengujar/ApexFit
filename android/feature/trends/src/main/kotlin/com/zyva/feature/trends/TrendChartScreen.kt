package com.zyva.feature.trends

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundTertiary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.SleepDeep
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary

@Composable
fun TrendChartScreen(
    viewModel: TrendsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md)
            .padding(bottom = Spacing.xl),
    ) {
        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Trends",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Period picker
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TrendPeriod.entries.forEachIndexed { index, period ->
                SegmentedButton(
                    selected = uiState.selectedPeriod == period,
                    onClick = { viewModel.setPeriod(period) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TrendPeriod.entries.size,
                    ),
                    label = { Text(period.label) },
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Individual metric charts
        TrendMetric.entries.forEach { metric ->
            val values = uiState.metrics.mapNotNull { viewModel.extractValue(it, metric) }
            val color = metricColor(metric)

            TrendCard(
                title = metric.displayName,
                values = values,
                color = color,
            )
            Spacer(Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun TrendCard(
    title: String,
    values: List<Double>,
    color: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            if (values.isNotEmpty()) {
                val avg = values.sum() / values.size
                Text(
                    text = "Avg: ${"%.1f".format(avg)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        if (values.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No data for this period",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
        } else {
            // Line chart with area fill
            LineChartWithArea(
                values = values,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )

            Spacer(Modifier.height(Spacing.sm))

            // Stats row
            val minVal = values.min()
            val maxVal = values.max()
            val avg = values.sum() / values.size

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(label = "Min", value = "%.1f".format(minVal))
                StatItem(label = "Avg", value = "%.1f".format(avg))
                StatItem(label = "Max", value = "%.1f".format(maxVal))
            }
        }
    }
}

@Composable
private fun LineChartWithArea(
    values: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val avg = values.sum() / values.size

    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas

        val minVal = values.min()
        val maxVal = values.max()
        val range = (maxVal - minVal).coerceAtLeast(1.0)
        val padding = 8.dp.toPx()

        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        fun xFor(index: Int): Float = padding + (index.toFloat() / (values.size - 1)) * chartWidth
        fun yFor(value: Double): Float = padding + chartHeight - ((value - minVal) / range * chartHeight).toFloat()

        // Area fill
        val areaPath = Path().apply {
            moveTo(xFor(0), size.height)
            lineTo(xFor(0), yFor(values.first()))
            for (i in 1 until values.size) {
                lineTo(xFor(i), yFor(values[i]))
            }
            lineTo(xFor(values.size - 1), size.height)
            close()
        }
        drawPath(
            path = areaPath,
            color = color.copy(alpha = 0.15f),
        )

        // Line
        val linePath = Path().apply {
            moveTo(xFor(0), yFor(values.first()))
            for (i in 1 until values.size) {
                lineTo(xFor(i), yFor(values[i]))
            }
        }
        drawPath(
            path = linePath,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
        )

        // Average dashed line
        val avgY = yFor(avg)
        drawLine(
            color = color.copy(alpha = 0.4f),
            start = Offset(padding, avgY),
            end = Offset(size.width - padding, avgY),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)),
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextTertiary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun metricColor(metric: TrendMetric): Color {
    return when (metric) {
        TrendMetric.RECOVERY -> RecoveryGreen
        TrendMetric.STRAIN -> PrimaryBlue
        TrendMetric.SLEEP -> SleepDeep
        TrendMetric.HRV -> RecoveryGreen
        TrendMetric.RHR -> PrimaryBlue
        TrendMetric.STEPS -> PrimaryBlue
        TrendMetric.CALORIES -> PrimaryBlue
    }
}
