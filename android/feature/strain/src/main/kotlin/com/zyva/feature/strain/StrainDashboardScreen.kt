package com.zyva.feature.strain

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
import com.zyva.core.data.entity.WorkoutRecordEntity
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundTertiary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.designsystem.theme.Zone1
import com.zyva.core.designsystem.theme.Zone2
import com.zyva.core.designsystem.theme.Zone3
import com.zyva.core.designsystem.theme.Zone4
import com.zyva.core.designsystem.theme.Zone5
import com.zyva.core.model.RecoveryZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrainDashboardScreen(
    viewModel: StrainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val metric = uiState.todayMetric
    val workouts = uiState.todayWorkouts

    val strainScore = metric?.strainScore ?: 0.0
    val recoveryZone = metric?.recoveryZone?.let {
        try { RecoveryZone.valueOf(it) } catch (_: Exception) { RecoveryZone.YELLOW }
    } ?: RecoveryZone.YELLOW

    // Workout zone totals
    val todayZone13 = workouts.sumOf { it.zone1Minutes + it.zone2Minutes + it.zone3Minutes }
    val todayZone45 = workouts.sumOf { it.zone4Minutes + it.zone5Minutes }
    val todayStrength = workouts.filter { it.isStrengthWorkout }.sumOf { it.durationMinutes }
    val todaySteps = metric?.steps ?: 0

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
        // Strain Gauge
        StrainGauge(
            score = strainScore,
            modifier = Modifier.padding(top = Spacing.lg),
        )

        // Sub-metrics card
        val baseSteps = viewModel.baselineSteps(uiState.weekMetrics)

        StrainSubMetricsCard(
            zone13 = todayZone13,
            zone45 = todayZone45,
            strength = todayStrength,
            steps = todaySteps,
            baselineSteps = baseSteps,
        )

        // Insight card
        StrainInsightCard(
            text = viewModel.strainInsight(strainScore, recoveryZone),
        )

        // Activities Section
        Text(
            text = "Activities",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = Spacing.md)
                .padding(top = Spacing.xl),
        )

        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = Spacing.md)
                    .padding(top = Spacing.sm)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
                    .padding(Spacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No workouts recorded today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        } else {
            workouts.forEach { workout ->
                WorkoutCard(workout = workout)
            }
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

        WeeklyStrainChart(
            title = "STRAIN",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.strainScore ?: 0.0 },
            maxValue = 21.0,
        )

        WeeklyStrainChart(
            title = "STEPS",
            metrics = uiState.weekMetrics,
            valueExtractor = { (it.steps ?: 0).toDouble() },
            maxValue = (uiState.weekMetrics.maxOfOrNull { (it.steps ?: 0).toDouble() } ?: 10000.0) * 1.2,
        )

        WeeklyStrainChart(
            title = "CALORIES",
            metrics = uiState.weekMetrics,
            valueExtractor = { it.activeCalories ?: 0.0 },
            maxValue = (uiState.weekMetrics.maxOfOrNull { it.activeCalories ?: 0.0 } ?: 2000.0) * 1.2,
        )
    }
    } // end PullToRefreshBox
}

@Composable
private fun StrainGauge(score: Double, modifier: Modifier = Modifier) {
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
                val progress = (score / 21.0).coerceIn(0.0, 1.0)

                drawArc(
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    startAngle = 135f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )

                drawArc(
                    color = PrimaryBlue,
                    startAngle = 135f,
                    sweepAngle = sweepAngle * progress.toFloat(),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (score > 0) "%.1f".format(score) else "--",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                Text(
                    text = "STRAIN",
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
private fun StrainSubMetricsCard(
    zone13: Double,
    zone45: Double,
    strength: Double,
    steps: Int,
    baselineSteps: Double,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard),
    ) {
        StrainMetricRow("HEART RATE ZONES 1-3", formatMinutes(zone13))
        HorizontalDivider(color = BackgroundTertiary)
        StrainMetricRow("HEART RATE ZONES 4-5", formatMinutes(zone45))
        HorizontalDivider(color = BackgroundTertiary)
        StrainMetricRow("STRENGTH ACTIVITY TIME", formatMinutes(strength))
        HorizontalDivider(color = BackgroundTertiary)
        StrainMetricRowWithBaseline(
            title = "STEPS",
            value = formatWithComma(steps),
            baseline = if (baselineSteps > 0) formatWithComma(baselineSteps.toInt()) else null,
            isAboveBaseline = steps.toDouble() >= baselineSteps,
        )

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

@Composable
private fun StrainMetricRow(title: String, value: String) {
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

        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
    }
}

@Composable
private fun StrainMetricRowWithBaseline(
    title: String,
    value: String,
    baseline: String?,
    isAboveBaseline: Boolean,
) {
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
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
                if (baseline != null) {
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (isAboveBaseline) "▲" else "▼",
                        fontSize = 8.sp,
                        color = if (isAboveBaseline) RecoveryGreen else RecoveryYellow,
                    )
                }
            }
            if (baseline != null) {
                Text(
                    text = baseline,
                    fontSize = 12.sp,
                    color = TextTertiary,
                )
            }
        }
    }
}

@Composable
private fun StrainInsightCard(text: String) {
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
                text = "EXPLORE YOUR STRAIN INSIGHTS",
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
private fun WorkoutCard(workout: WorkoutRecordEntity) {
    Column(
        modifier = Modifier
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.sm)
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.workoutName ?: workout.workoutType,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                )
                Text(
                    text = "${workout.durationMinutes.toInt()} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            val strainScore = workout.strainScore
            if (strainScore != null) {
                Text(
                    text = "%.1f".format(strainScore),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                )
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        // HR Zone breakdown bar
        HRZoneBar(workout = workout)
    }
}

@Composable
private fun HRZoneBar(workout: WorkoutRecordEntity) {
    val zones = listOf(
        workout.zone1Minutes to Zone1,
        workout.zone2Minutes to Zone2,
        workout.zone3Minutes to Zone3,
        workout.zone4Minutes to Zone4,
        workout.zone5Minutes to Zone5,
    )
    val total = zones.sumOf { it.first }

    if (total > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp)),
        ) {
            zones.forEach { (minutes, color) ->
                val fraction = (minutes / total).toFloat()
                if (fraction > 0.01f) {
                    Box(
                        modifier = Modifier
                            .weight(fraction)
                            .height(24.dp)
                            .background(color),
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.xs))

        // Zone legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            zones.forEachIndexed { index, (minutes, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color),
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "Z${index + 1} ${minutes.toInt()}m",
                        fontSize = 9.sp,
                        color = TextTertiary,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyStrainChart(
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
                val safeMax = maxValue.coerceAtLeast(1.0)
                metrics.forEachIndexed { index, metric ->
                    val value = valueExtractor(metric)
                    val barHeight = (value / safeMax * size.height).toFloat().coerceAtLeast(2f)
                    val x = (index * 2 + 0.5f) * barWidth
                    drawRoundRect(
                        color = PrimaryBlue,
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6f),
                    )
                }
            }
        }
    }
}

private fun formatMinutes(minutes: Double): String {
    val total = minutes.toInt()
    val h = total / 60
    val m = total % 60
    return "$h:${"%02d".format(m)}"
}

private fun formatWithComma(value: Int): String {
    return "%,d".format(value)
}
