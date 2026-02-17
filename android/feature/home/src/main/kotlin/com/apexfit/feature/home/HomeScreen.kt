package com.apexfit.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.component.CircularGauge
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.SleepDeep
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.core.designsystem.theme.recoveryColor
import com.apexfit.feature.home.components.DateNavigationHeader
import com.apexfit.feature.home.components.JournalWeekCard
import com.apexfit.feature.home.components.MyPlanCard
import com.apexfit.feature.home.components.TonightsSleepCard
import com.apexfit.feature.home.components.WeeklyStrainRecoveryChart
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeScreen(
    onSleepTap: () -> Unit = {},
    onRecoveryTap: () -> Unit = {},
    onStrainTap: () -> Unit = {},
    onJournalTap: () -> Unit = {},
    onTrendsTap: () -> Unit = {},
    onAddActivityTap: () -> Unit = {},
    onStartActivityTap: () -> Unit = {},
    onSettingsTap: () -> Unit = {},
    onProfileTap: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md),
    ) {
        Spacer(Modifier.height(Spacing.sm))

        // Date Navigation Header (profile avatar + streak + date nav + watch)
        DateNavigationHeader(
            date = uiState.selectedDate,
            isToday = uiState.selectedDate == LocalDate.now(),
            streak = uiState.streak,
            onPreviousDay = { viewModel.navigateDate(-1) },
            onNextDay = { viewModel.navigateDate(1) },
            onProfileTap = onProfileTap,
        )

        Spacer(Modifier.height(Spacing.md))

        // Three Gauge Row
        DashboardGaugeRow(
            sleepPerformance = uiState.todayMetric?.sleepPerformance ?: 0.0,
            recoveryScore = uiState.todayMetric?.recoveryScore ?: 0.0,
            recoveryZone = uiState.todayMetric?.recoveryZone,
            strainScore = uiState.todayMetric?.strainScore ?: 0.0,
            onSleepTap = onSleepTap,
            onRecoveryTap = onRecoveryTap,
            onStrainTap = onStrainTap,
        )

        Spacer(Modifier.height(Spacing.md))

        // Health Monitor + Stress Monitor cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            MonitorCard(
                title = "HEALTH MONITOR",
                value = healthMetricsInRange(uiState),
                subtitle = "of 5 in range",
                color = RecoveryGreen,
                modifier = Modifier.weight(1f),
            )
            MonitorCard(
                title = "STRESS MONITOR",
                value = uiState.todayMetric?.stressScore?.let { "${it.toInt()}" } ?: "--",
                subtitle = stressLevel(uiState.todayMetric?.stressScore),
                color = RecoveryYellow,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(Spacing.md))

        // My Day section
        MyDaySection(
            uiState = uiState,
            onAddActivityTap = onAddActivityTap,
            onStartActivityTap = onStartActivityTap,
        )

        Spacer(Modifier.height(Spacing.md))

        // Tonight's Sleep
        TonightsSleepCard(
            sleepNeedHours = uiState.todayMetric?.sleepNeedHours,
        )

        Spacer(Modifier.height(Spacing.md))

        // Journal Week
        JournalWeekCard(
            selectedDate = uiState.selectedDate,
            loggedDates = uiState.weekMetrics.mapNotNull { metric ->
                Instant.ofEpochMilli(metric.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .takeIf { metric.recoveryScore != null || metric.strainScore != null }
            }.toSet(),
            onJournalTap = onJournalTap,
        )

        Spacer(Modifier.height(Spacing.md))

        // My Plan
        MyPlanCard()

        Spacer(Modifier.height(Spacing.md))

        // My Dashboard (Vitals)
        VitalMetricsSection(
            uiState = uiState,
            hrvBaseline = viewModel.hrvBaseline(),
            rhrBaseline = viewModel.rhrBaseline(),
        )

        Spacer(Modifier.height(Spacing.md))

        // Weekly Strain & Recovery Chart
        WeeklyStrainRecoveryChart(weekMetrics = uiState.weekMetrics)

        Spacer(Modifier.height(Spacing.md))

        // Bottom metric rows
        BottomMetricsSection(uiState = uiState)

        Spacer(Modifier.height(Spacing.xl))
    }
}

// -- Dashboard Gauge Row --

@Composable
private fun DashboardGaugeRow(
    sleepPerformance: Double,
    recoveryScore: Double,
    recoveryZone: String?,
    strainScore: Double,
    onSleepTap: () -> Unit,
    onRecoveryTap: () -> Unit,
    onStrainTap: () -> Unit,
) {
    val recoveryColor = if (recoveryScore > 0) {
        recoveryColor(recoveryScore)
    } else {
        TextTertiary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        GaugeItem(
            value = sleepPerformance,
            maxValue = 100.0,
            label = "SLEEP",
            color = SleepDeep,
            onClick = onSleepTap,
        )
        GaugeItem(
            value = recoveryScore,
            maxValue = 99.0,
            label = "RECOVERY",
            color = recoveryColor,
            onClick = onRecoveryTap,
        )
        GaugeItem(
            value = strainScore,
            maxValue = 21.0,
            label = "STRAIN",
            color = PrimaryBlue,
            onClick = onStrainTap,
        )
    }
}

@Composable
private fun GaugeItem(
    value: Double,
    maxValue: Double,
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        CircularGauge(
            value = value,
            maxValue = maxValue,
            label = if (maxValue <= 21) "" else "%",
            color = color,
            dimension = 110.dp,
            lineWidth = 8.dp,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
    }
}

// -- Monitor Cards --

@Composable
private fun MonitorCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextTertiary,
        )
    }
}

// -- My Day Section --

@Composable
private fun MyDaySection(
    uiState: HomeUiState,
    onAddActivityTap: () -> Unit,
    onStartActivityTap: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Day",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BackgroundTertiary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Today's Activities
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.small))
                .background(BackgroundTertiary.copy(alpha = 0.5f))
                .padding(Spacing.md),
        ) {
            Text(
                text = "TODAY'S ACTIVITIES",
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
            )

            Spacer(Modifier.height(Spacing.sm))

            // Sleep entry
            ActivityRow(
                icon = Icons.Filled.Nightlight,
                color = SleepDeep,
                title = "Sleep",
                value = uiState.todayMetric?.totalSleepHours?.let {
                    "${it.toInt()}h ${((it % 1) * 60).toInt()}m"
                } ?: "--",
            )

            // Workout count
            if ((uiState.todayMetric?.workoutCount ?: 0) > 0) {
                Spacer(Modifier.height(Spacing.sm))
                ActivityRow(
                    icon = Icons.Filled.LocalFireDepartment,
                    color = RecoveryRed,
                    title = "Workouts",
                    value = "${uiState.todayMetric?.workoutCount} activities",
                )
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Add/Start activity buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ActionButton(
                icon = Icons.Filled.Add,
                text = "ADD ACTIVITY",
                onClick = onAddActivityTap,
                modifier = Modifier.weight(1f),
            )
            ActionButton(
                icon = Icons.Filled.Timer,
                text = "START ACTIVITY",
                onClick = onStartActivityTap,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ActivityRow(
    icon: ImageVector,
    color: Color,
    title: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.small))
            .background(BackgroundTertiary)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.sm, horizontal = Spacing.md),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(Spacing.xs))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

// -- Vital Metrics Section --

@Composable
private fun VitalMetricsSection(
    uiState: HomeUiState,
    hrvBaseline: Double?,
    rhrBaseline: Double?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Dashboard",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Text(
                text = "CUSTOMIZE",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }

        Spacer(Modifier.height(Spacing.md))

        MetricRow(
            icon = Icons.Filled.MonitorHeart,
            label = "HEART RATE VARIABILITY",
            value = uiState.todayMetric?.hrvRMSSD?.toInt()?.toString() ?: "--",
            baseline = hrvBaseline?.toInt()?.toString(),
            trendUp = metricTrend(uiState.todayMetric?.hrvRMSSD, hrvBaseline),
        )

        Spacer(Modifier.height(Spacing.sm))

        MetricRow(
            icon = Icons.Filled.Favorite,
            label = "RESTING HEART RATE",
            value = uiState.todayMetric?.restingHeartRate?.toInt()?.toString() ?: "--",
            baseline = rhrBaseline?.toInt()?.toString(),
            trendUp = metricTrend(uiState.todayMetric?.restingHeartRate, rhrBaseline, invertedBetter = true),
        )

        Spacer(Modifier.height(Spacing.sm))

        MetricRow(
            icon = Icons.Filled.Favorite,
            label = "VO\u2082 MAX",
            value = uiState.todayMetric?.vo2Max?.toInt()?.toString() ?: "--",
            baseline = null,
            trendUp = null,
        )
    }
}

// -- Bottom Metrics --

@Composable
private fun BottomMetricsSection(uiState: HomeUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MetricRow(
            icon = Icons.Filled.LocalFireDepartment,
            label = "CALORIES",
            value = uiState.todayMetric?.activeCalories?.toInt()?.let { formatNumber(it) } ?: "--",
            baseline = null,
            trendUp = null,
        )

        Spacer(Modifier.height(Spacing.sm))

        MetricRow(
            icon = Icons.Filled.Favorite,
            label = "STEPS",
            value = uiState.todayMetric?.steps?.let { formatNumber(it) } ?: "--",
            baseline = null,
            trendUp = null,
        )
    }
}

// -- Shared Metric Row --

@Composable
private fun MetricRow(
    icon: ImageVector,
    label: String,
    value: String,
    baseline: String?,
    trendUp: Boolean?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.small))
            .background(BackgroundCard)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Spacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                letterSpacing = 0.5.sp,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        if (baseline != null) {
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = baseline,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }
        if (trendUp != null) {
            Spacer(Modifier.width(Spacing.xs))
            Icon(
                imageVector = if (trendUp) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = null,
                tint = if (trendUp) RecoveryGreen else RecoveryRed,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// -- Helpers --

private fun healthMetricsInRange(uiState: HomeUiState): String {
    val metric = uiState.todayMetric ?: return "--"
    var count = 0
    val spo2 = metric.spo2
    val resp = metric.respiratoryRate
    if (spo2 != null && spo2 >= 95) count++
    if (resp != null && resp in 12.0..20.0) count++
    if (metric.hrvRMSSD != null) count++
    if (metric.restingHeartRate != null) count++
    if (metric.vo2Max != null) count++
    return "$count"
}

private fun stressLevel(score: Double?): String {
    if (score == null) return "No data"
    return when {
        score < 1 -> "LOW"
        score < 2 -> "MEDIUM"
        score < 2.5 -> "HIGH"
        else -> "VERY HIGH"
    }
}

private fun metricTrend(current: Double?, baseline: Double?, invertedBetter: Boolean = false): Boolean? {
    if (current == null || baseline == null || baseline <= 0) return null
    val isHigher = current > baseline
    return if (invertedBetter) !isHigher else isHigher
}

private fun formatNumber(n: Int): String {
    return String.format("%,d", n)
}
