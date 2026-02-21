package com.zyva.feature.sleep

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.entity.SleepSessionEntity
import com.zyva.core.data.entity.SleepStageEntity
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundTertiary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.SleepDeep
import com.zyva.core.designsystem.theme.SleepLight
import com.zyva.core.designsystem.theme.SleepREM
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.designsystem.theme.Zone4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepDashboardScreen(
    viewModel: SleepViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val metric = uiState.todayMetric
    val sleep = uiState.mainSleep
    val stages = uiState.sleepStages

    val sleepPerformance = metric?.sleepScore ?: metric?.sleepPerformance ?: 0.0
    val sleepConsistency = metric?.sleepConsistency ?: 0.0
    val sleepEfficiency = metric?.sleepEfficiency ?: sleep?.sleepEfficiency ?: 0.0
    val totalSleepHours = metric?.totalSleepHours ?: sleep?.let { it.totalSleepMinutes / 60.0 } ?: 0.0
    val sleepNeedHours = metric?.sleepNeedHours ?: 8.0

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(bottom = Spacing.xl),
    ) {
        // Sleep Performance Gauge
        SleepGauge(
            score = sleepPerformance,
            modifier = Modifier.padding(top = Spacing.lg),
        )

        // Sub-metrics card
        SleepSubMetricsCard(
            hoursVsNeeded = metric?.sleepPerformance ?: 0.0,
            consistency = sleepConsistency,
            efficiency = sleepEfficiency,
            stressScore = metric?.stressScore,
        )

        // Insight card
        SleepInsightCard(
            text = viewModel.sleepInsightText(sleepPerformance),
        )

        // Last Night's Sleep header
        Text(
            text = "Last Night's Sleep",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.xl),
        )
        Text(
            text = "Today vs. prior 30 days",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = Spacing.md),
        )

        // Hours of Sleep card
        HoursOfSleepCard(
            sleepHours = totalSleepHours,
            sleepNeed = sleepNeedHours,
        )

        // Sleep Stages breakdown
        SleepStagesCard(sleep = uiState.mainSleep)

        // Hypnogram
        if (stages.isNotEmpty()) {
            HypnogramCard(stages = stages.sortedBy { it.startDate })
        }

        // Restorative Sleep (Deep + REM)
        sleep?.let { s ->
            val restorativeMinutes = s.deepMinutes + s.remMinutes
            val restorativePct = if (s.totalSleepMinutes > 0) {
                (restorativeMinutes / s.totalSleepMinutes * 100)
            } else {
                0.0
            }
            RestorativeSleepCard(
                restorativePct = restorativePct,
                restorativeMinutes = restorativeMinutes,
                deepMinutes = s.deepMinutes,
                remMinutes = s.remMinutes,
            )
        }

        // Weekly Trends
        Text(
            text = "Weekly Trends",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.xl),
        )

        WeeklySleepChart(
            title = "SLEEP PERFORMANCE",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.sleepPerformance ?: 0.0 },
            maxValue = 100.0,
        )

        WeeklySleepChart(
            title = "SLEEP CONSISTENCY",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.sleepConsistency ?: 0.0 },
            maxValue = 100.0,
        )

        WeeklySleepChart(
            title = "SLEEP EFFICIENCY",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.sleepEfficiency ?: 0.0 },
            maxValue = 100.0,
        )

        WeeklySleepChart(
            title = "HOURS OF SLEEP",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.totalSleepHours ?: 0.0 },
            maxValue = 12.0,
        )

        WeeklySleepChart(
            title = "RESTORATIVE SLEEP",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.restorativeSleepPercentage ?: ((it.deepSleepPercentage ?: 0.0) + (it.remSleepPercentage ?: 0.0)) },
            maxValue = 100.0,
        )

        WeeklySleepChart(
            title = "SLEEP DEBT",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.sleepDebtHours ?: 0.0 },
            maxValue = (uiState.weekMetrics.maxOfOrNull { it.sleepDebtHours ?: 0.0 } ?: 4.0).coerceAtLeast(1.0) * 1.2,
        )
    }
    } // end PullToRefreshBox
}

@Composable
private fun SleepGauge(score: Double, modifier: Modifier = Modifier) {
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
                val progress = (score / 100.0).coerceIn(0.0, 1.0)

                drawArc(
                    color = SleepLight.copy(alpha = 0.2f),
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                drawArc(
                    color = SleepLight,
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
                    text = "SLEEP\nPERFORMANCE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun SleepSubMetricsCard(
    hoursVsNeeded: Double,
    consistency: Double,
    efficiency: Double,
    stressScore: Double?,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard),
    ) {
        SleepMetricRow("HOURS VS. NEEDED", hoursVsNeeded)
        HorizontalDivider(color = BackgroundTertiary)
        SleepMetricRow("SLEEP CONSISTENCY", consistency)
        HorizontalDivider(color = BackgroundTertiary)
        SleepMetricRow("SLEEP EFFICIENCY", efficiency)
        HorizontalDivider(color = BackgroundTertiary)
        SleepMetricRow(
            "SLEEP STRESS",
            stressScore?.let { (it * 33.3).coerceIn(0.0, 100.0) } ?: 0.0,
        )

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm, horizontal = Spacing.md),
            horizontalArrangement = Arrangement.Center,
        ) {
            LegendDot(Zone4, "Poor")
            Spacer(Modifier.width(Spacing.lg))
            LegendDot(TextTertiary, "Sufficient")
            Spacer(Modifier.width(Spacing.lg))
            LegendDot(RecoveryGreen, "Optimal")
        }
    }
}

@Composable
private fun SleepMetricRow(title: String, value: Double) {
    val status = when {
        value >= 85 -> "optimal"
        value >= 60 -> "sufficient"
        else -> "poor"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.weight(1f),
        )

        // Status bars
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            StatusBarSegment(active = status == "poor", color = Zone4)
            StatusBarSegment(active = status == "sufficient", color = TextTertiary)
            StatusBarSegment(active = status == "optimal", color = RecoveryGreen)
        }

        Spacer(Modifier.width(Spacing.sm))

        Text(
            text = if (value > 0) "${value.toInt()}%" else "--%",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
        )
    }
}

@Composable
private fun StatusBarSegment(active: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (active) color else TextTertiary.copy(alpha = 0.2f)),
    )
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Spacer(Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextTertiary)
    }
}

@Composable
private fun SleepInsightCard(text: String) {
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
                text = "EXPLORE YOUR SLEEP INSIGHTS",
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
private fun HoursOfSleepCard(sleepHours: Double, sleepNeed: Double) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "HOURS OF SLEEP",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = formatDuration(sleepHours),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Spacer(Modifier.width(4.dp))
            if (sleepHours < sleepNeed) {
                Text("▼", fontSize = 10.sp, color = RecoveryYellow)
            } else {
                Text("▲", fontSize = 10.sp, color = RecoveryGreen)
            }
        }

        Text(
            text = formatDuration(sleepNeed),
            fontSize = 12.sp,
            color = TextTertiary,
        )

        Spacer(Modifier.height(Spacing.sm))

        // Progress bar
        val fraction = if (sleepNeed > 0) (sleepHours / sleepNeed).coerceIn(0.0, 1.0) else 0.0
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(TextTertiary.copy(alpha = 0.2f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.toFloat())
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(PrimaryBlue),
            )
        }
    }
}

@Composable
private fun SleepStagesCard(sleep: SleepSessionEntity?) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "SLEEP STAGES",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        if (sleep == null) {
            Text(
                text = "No sleep stage data available",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.padding(vertical = Spacing.lg),
            )
        } else {
            val total = sleep.totalSleepMinutes + sleep.awakeMinutes
            if (total > 0) {
                val awakePct = (sleep.awakeMinutes / total * 100)
                val lightPct = if (sleep.totalSleepMinutes > 0) (sleep.lightMinutes / sleep.totalSleepMinutes * 100) else 0.0
                val deepPct = if (sleep.totalSleepMinutes > 0) (sleep.deepMinutes / sleep.totalSleepMinutes * 100) else 0.0
                val remPct = if (sleep.totalSleepMinutes > 0) (sleep.remMinutes / sleep.totalSleepMinutes * 100) else 0.0

                SleepStageRow("AWAKE", awakePct, sleep.awakeMinutes, Color.White.copy(alpha = 0.7f))
                SleepStageRow("LIGHT", lightPct, sleep.lightMinutes, SleepLight)
                SleepStageRow("DEEP", deepPct, sleep.deepMinutes, SleepDeep)
                SleepStageRow("REM", remPct, sleep.remMinutes, SleepREM)
            }
        }
    }
}

@Composable
private fun SleepStageRow(label: String, percentage: Double, minutes: Double, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color),
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = "${percentage.toInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = formatDuration(minutes / 60.0),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
        }

        Spacer(Modifier.height(4.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TextTertiary.copy(alpha = 0.15f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100.0).coerceIn(0.0, 1.0).toFloat())
                    .height(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color),
            )
        }
    }
}

@Composable
private fun HypnogramCard(stages: List<SleepStageEntity>) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "Hypnogram",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            if (stages.isEmpty()) return@Canvas

            val minTime = stages.minOf { it.startDate }
            val maxTime = stages.maxOf { it.endDate }
            val timeRange = (maxTime - minTime).coerceAtLeast(1)

            fun yForStage(type: String): Float = when (type.uppercase()) {
                "AWAKE" -> size.height * 0.1f
                "REM" -> size.height * 0.35f
                "LIGHT" -> size.height * 0.6f
                "DEEP" -> size.height * 0.85f
                else -> size.height * 0.1f
            }

            fun colorForStage(type: String): Color = when (type.uppercase()) {
                "AWAKE" -> Color.White.copy(alpha = 0.7f)
                "REM" -> SleepREM
                "LIGHT" -> SleepLight
                "DEEP" -> SleepDeep
                else -> TextTertiary
            }

            stages.forEachIndexed { index, stage ->
                val x1 = ((stage.startDate - minTime).toFloat() / timeRange) * size.width
                val x2 = ((stage.endDate - minTime).toFloat() / timeRange) * size.width
                val y = yForStage(stage.stageType)
                val color = colorForStage(stage.stageType)

                // Draw horizontal line for this stage
                drawLine(
                    color = color,
                    start = Offset(x1, y),
                    end = Offset(x2, y),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                )

                // Draw vertical connector to next stage
                if (index < stages.size - 1) {
                    val nextY = yForStage(stages[index + 1].stageType)
                    drawLine(
                        color = color.copy(alpha = 0.5f),
                        start = Offset(x2, y),
                        end = Offset(x2, nextY),
                        strokeWidth = 2f,
                    )
                }
            }
        }

        // Y-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("Deep", "Light", "REM", "Awake").forEach { label ->
                Text(text = label, fontSize = 10.sp, color = TextTertiary)
            }
        }
    }
}

@Composable
private fun RestorativeSleepCard(
    restorativePct: Double,
    restorativeMinutes: Double,
    deepMinutes: Double,
    remMinutes: Double,
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
            text = "RESTORATIVE SLEEP",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${restorativePct.toInt()}%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = formatDuration(restorativeMinutes / 60.0),
                fontSize = 16.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        Text(
            text = "Deep + REM sleep combined",
            fontSize = 12.sp,
            color = TextTertiary,
        )

        Spacer(Modifier.height(Spacing.md))

        // Stacked bar
        val total = (deepMinutes + remMinutes).coerceAtLeast(1.0)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .weight((deepMinutes / total).toFloat().coerceAtLeast(0.01f))
                    .height(12.dp)
                    .background(SleepDeep),
            )
            Box(
                modifier = Modifier
                    .weight((remMinutes / total).toFloat().coerceAtLeast(0.01f))
                    .height(12.dp)
                    .background(SleepREM),
            )
        }

        Spacer(Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SleepDeep),
                )
                Spacer(Modifier.width(4.dp))
                Text("Deep ${formatDuration(deepMinutes / 60.0)}", fontSize = 11.sp, color = TextTertiary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SleepREM),
                )
                Spacer(Modifier.width(4.dp))
                Text("REM ${formatDuration(remMinutes / 60.0)}", fontSize = 11.sp, color = TextTertiary)
            }
        }
    }
}

@Composable
private fun WeeklySleepChart(
    title: String,
    metrics: List<DailyMetricEntity>,
    valueExtractor: (DailyMetricEntity) -> Double,
    maxValue: Double,
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
                        color = SleepLight.copy(alpha = 0.7f),
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6f),
                    )
                }
            }
        }
    }
}

private fun formatDuration(hours: Double): String {
    val totalMinutes = (hours * 60).toInt()
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return "$h:${"%02d".format(m)}"
}
