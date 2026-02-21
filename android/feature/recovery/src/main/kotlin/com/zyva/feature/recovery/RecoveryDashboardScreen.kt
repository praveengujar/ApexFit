package com.zyva.feature.recovery

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundTertiary
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.designsystem.theme.recoveryColor
import com.zyva.core.model.RecoveryZone

@Composable
fun RecoveryDashboardScreen(
    viewModel: RecoveryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val metric = uiState.todayMetric
    val weekMetrics = uiState.weekMetrics

    val recoveryScore = metric?.recoveryScore ?: 0.0
    val zone = metric?.recoveryZone?.let { RecoveryZone.valueOf(it) }
        ?: RecoveryZone.from(recoveryScore)
    val zoneColor = recoveryColor(zone)

    val todayHRV = metric?.hrvRMSSD ?: 0.0
    val todayRHR = metric?.restingHeartRate ?: 0.0
    val todayRespRate = metric?.respiratoryRate ?: 0.0
    val todaySleepPerf = metric?.sleepPerformance ?: 0.0

    val baseHRV = viewModel.baselineHRV(weekMetrics)
    val baseRHR = viewModel.baselineRHR(weekMetrics)
    val baseRespRate = viewModel.baselineRespRate(weekMetrics)
    val baseSleepPerf = viewModel.baselineSleepPerf(weekMetrics)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.xl),
    ) {
        // Recovery Overview - Large Gauge
        RecoveryGauge(
            score = recoveryScore,
            color = zoneColor,
            modifier = Modifier.padding(top = Spacing.lg),
        )

        // Sub-metrics card
        SubMetricsCard(
            todayHRV = todayHRV,
            todayRHR = todayRHR,
            todayRespRate = todayRespRate,
            todaySleepPerf = todaySleepPerf,
            baseHRV = baseHRV,
            baseRHR = baseRHR,
            baseRespRate = baseRespRate,
            baseSleepPerf = baseSleepPerf,
        )

        // Insight card
        InsightCard(
            text = viewModel.generateInsight(todayHRV, baseHRV),
        )

        // Recovery Contributors
        RecoveryContributorsCard(
            strainScore = metric?.strainScore,
            sleepPerformance = metric?.sleepPerformance,
            todayHRV = todayHRV,
            baseHRV = baseHRV,
            todayRHR = todayRHR,
            baseRHR = baseRHR,
        )

        // Weekly Trends Header
        Text(
            text = "Weekly Trends",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.xl),
        )

        // Weekly Recovery Chart
        WeeklyBarChart(
            title = "RECOVERY",
            metrics = weekMetrics,
            valueExtractor = { it.recoveryScore ?: 0.0 },
            maxValue = 100.0,
            colorMapper = { recoveryColor(it) },
        )

        // Weekly HRV Chart
        WeeklyLineChart(
            title = "HEART RATE VARIABILITY",
            metrics = weekMetrics,
            valueExtractor = { it.hrvRMSSD ?: 0.0 },
        )

        // Weekly RHR Chart
        WeeklyLineChart(
            title = "RESTING HEART RATE",
            metrics = weekMetrics,
            valueExtractor = { it.restingHeartRate ?: 0.0 },
        )

        // Weekly Respiratory Rate Chart
        WeeklyLineChart(
            title = "RESPIRATORY RATE",
            metrics = weekMetrics,
            valueExtractor = { it.respiratoryRate ?: 0.0 },
        )

        // Weekly Sleep Performance Chart
        WeeklyBarChart(
            title = "SLEEP PERFORMANCE",
            metrics = weekMetrics,
            valueExtractor = { it.sleepPerformance ?: 0.0 },
            maxValue = 100.0,
            colorMapper = { Color(0xFF7B8CDE).copy(alpha = 0.7f) },
        )
    }
}

@Composable
private fun RecoveryGauge(
    score: Double,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(240.dp)) {
                val strokeWidth = 14.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val sweepAngle = 270f
                val progress = if (score > 0) (score / 99.0).coerceIn(0.0, 1.0) else 0.0

                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(color.copy(alpha = 0.6f), color),
                    ),
                    startAngle = 135f,
                    sweepAngle = sweepAngle * progress.toFloat(),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (score > 0) "${score.toInt()}" else "--",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Text(
                    text = "RECOVERY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                )
            }
        }
    }
}

@Composable
private fun SubMetricsCard(
    todayHRV: Double,
    todayRHR: Double,
    todayRespRate: Double,
    todaySleepPerf: Double,
    baseHRV: Double,
    baseRHR: Double,
    baseRespRate: Double,
    baseSleepPerf: Double,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard),
    ) {
        MetricRow("HEART RATE VARIABILITY", todayHRV, baseHRV, MetricFormat.INTEGER, higherIsBetter = true)
        HorizontalDivider(color = BackgroundTertiary)
        MetricRow("RESTING HEART RATE", todayRHR, baseRHR, MetricFormat.INTEGER, higherIsBetter = false)
        HorizontalDivider(color = BackgroundTertiary)
        MetricRow("RESPIRATORY RATE", todayRespRate, baseRespRate, MetricFormat.ONE_DECIMAL, higherIsBetter = false)
        HorizontalDivider(color = BackgroundTertiary)
        MetricRow("SLEEP PERFORMANCE", todaySleepPerf, baseSleepPerf, MetricFormat.PERCENT, higherIsBetter = true)

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundTertiary.copy(alpha = 0.5f))
                .padding(vertical = Spacing.sm, horizontal = Spacing.md),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("▲", fontSize = 8.sp, color = RecoveryGreen)
            Spacer(Modifier.width(2.dp))
            Text("▼", fontSize = 8.sp, color = RecoveryRed)
            Spacer(Modifier.width(4.dp))
            Text("Today", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(Modifier.width(4.dp))
            Text("vs. last 30 days", fontSize = 12.sp, color = TextTertiary)
        }
    }
}

private enum class MetricFormat { INTEGER, ONE_DECIMAL, PERCENT }

@Composable
private fun MetricRow(
    title: String,
    current: Double,
    baseline: Double,
    format: MetricFormat,
    higherIsBetter: Boolean,
) {
    val isWorse = if (higherIsBetter) current < baseline else current > baseline
    val arrowUp = if (higherIsBetter) current >= baseline else current <= baseline
    val arrowColor = if (isWorse && baseline > 0) RecoveryYellow else RecoveryGreen

    val formattedCurrent = when (format) {
        MetricFormat.INTEGER -> if (current > 0) "${current.toInt()}" else "--"
        MetricFormat.ONE_DECIMAL -> if (current > 0) "%.1f".format(current) else "--"
        MetricFormat.PERCENT -> if (current > 0) "${current.toInt()}%" else "--%"
    }

    val formattedBaseline = when (format) {
        MetricFormat.INTEGER -> if (baseline > 0) "${baseline.toInt()}" else "--"
        MetricFormat.ONE_DECIMAL -> if (baseline > 0) "%.1f".format(baseline) else "--"
        MetricFormat.PERCENT -> if (baseline > 0) "${baseline.toInt()}%" else "--%"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattedCurrent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                if (current > 0 && baseline > 0) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (arrowUp) "▲" else "▼",
                        fontSize = 8.sp,
                        color = arrowColor,
                    )
                }
            }
            Text(
                text = formattedBaseline,
                fontSize = 12.sp,
                color = TextTertiary,
            )
        }
    }
}

@Composable
private fun InsightCard(text: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "EXPLORE YOUR RECOVERY INSIGHTS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Teal,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.width(Spacing.xs))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Teal,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun RecoveryContributorsCard(
    strainScore: Double?,
    sleepPerformance: Double?,
    todayHRV: Double,
    baseHRV: Double,
    todayRHR: Double,
    baseRHR: Double,
) {
    val tags = buildList {
        if (strainScore != null) {
            if (strainScore >= 13) {
                add(Triple("13+ Strain", false, RecoveryRed))
            } else if (strainScore >= 8) {
                add(Triple("Moderate Strain", true, RecoveryYellow))
            } else {
                add(Triple("Low Strain", true, RecoveryGreen))
            }
        }
        if (sleepPerformance != null) {
            if (sleepPerformance >= 85) {
                add(Triple("Good Sleep", true, RecoveryGreen))
            } else if (sleepPerformance >= 60) {
                add(Triple("Fair Sleep", false, RecoveryYellow))
            } else {
                add(Triple("Low Sleep", false, RecoveryRed))
            }
        }
        if (baseHRV > 0 && todayHRV > 0) {
            val pct = ((todayHRV - baseHRV) / baseHRV) * 100
            if (pct > 10) {
                add(Triple("Good HRV", true, RecoveryGreen))
            } else if (pct < -10) {
                add(Triple("Low HRV", false, RecoveryRed))
            }
        }
        if (baseRHR > 0 && todayRHR > 0) {
            val pct = ((todayRHR - baseRHR) / baseRHR) * 100
            if (pct > 5) {
                add(Triple("Elevated RHR", false, RecoveryYellow))
            } else if (pct < -5) {
                add(Triple("Low RHR", true, RecoveryGreen))
            }
        }
    }

    if (tags.isEmpty()) return

    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "RECOVERY INSIGHTS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.md))

        // Wrap flow of tags
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            tags.take(4).forEach { (label, isPositive, color) ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isPositive) "▲" else "▼",
                        fontSize = 8.sp,
                        color = color,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(
    title: String,
    metrics: List<DailyMetricEntity>,
    valueExtractor: (DailyMetricEntity) -> Double,
    maxValue: Double,
    colorMapper: (Double) -> Color,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        if (metrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No data available", color = TextTertiary, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                val barWidth = size.width / (metrics.size * 2f)
                metrics.forEachIndexed { index, metric ->
                    val value = valueExtractor(metric)
                    val barHeight = (value / maxValue * size.height).toFloat().coerceAtLeast(2f)
                    val x = (index * 2 + 0.5f) * barWidth
                    drawRoundRect(
                        color = colorMapper(value),
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyLineChart(
    title: String,
    metrics: List<DailyMetricEntity>,
    valueExtractor: (DailyMetricEntity) -> Double,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        if (metrics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No data available", color = TextTertiary, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            val values = metrics.map { valueExtractor(it) }
            val minVal = (values.minOrNull() ?: 0.0) - 5
            val maxVal = (values.maxOrNull() ?: 100.0) + 5
            val range = (maxVal - minVal).coerceAtLeast(1.0)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            ) {
                val stepX = size.width / (values.size - 1).coerceAtLeast(1)
                val points = values.mapIndexed { index, value ->
                    Offset(
                        x = index * stepX,
                        y = (size.height - ((value - minVal) / range * size.height)).toFloat(),
                    )
                }

                // Draw lines
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color(0xFF7B8CDE),
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 4f,
                        cap = StrokeCap.Round,
                    )
                }

                // Draw dots
                points.forEach { point ->
                    drawCircle(
                        color = Color(0xFF7B8CDE),
                        radius = 6f,
                        center = point,
                    )
                }
            }
        }
    }
}
