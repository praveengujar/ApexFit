package com.zyva.feature.longevity

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.CornerRadius
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.engine.LongevityCategory
import com.zyva.core.engine.LongevityMetricID
import com.zyva.core.engine.LongevityMetricResult
import com.zyva.core.engine.LongevityResult
import com.zyva.feature.longevity.components.ZyvaAgeTrendChart
import com.zyva.feature.longevity.components.LongevityMetricCard
import com.zyva.feature.longevity.components.OrganicBlobView
import com.zyva.feature.longevity.components.PaceOfAgingGauge
import com.zyva.feature.longevity.components.PaceOfAgingTrendChart

@Composable
fun LongevityDashboardScreen(
    viewModel: LongevityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(Spacing.md))

        // Week Navigation Header
        WeekNavigationHeader(
            weekRange = viewModel.weekRangeString,
            daysUntilUpdate = viewModel.daysUntilNextMonday,
            isCurrentWeek = viewModel.isCurrentWeek,
            onPreviousWeek = { viewModel.navigateWeek(-1) },
            onNextWeek = { viewModel.navigateWeek(1) },
        )

        // Hero Blob
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            OrganicBlobView(
                zyvaAge = uiState.result?.zyvaAge ?: uiState.chronologicalAge,
                yearsYoungerOlder = uiState.result?.yearsYoungerOlder ?: 0.0,
            )
        }

        // Pace of Aging Gauge
        uiState.result?.let { result ->
            PaceOfAgingGauge(pace = result.paceOfAging)
        }

        Spacer(Modifier.height(Spacing.lg))

        // Insight Card
        uiState.result?.let { result ->
            InsightCard(
                title = result.overallInsightTitle,
                body = result.overallInsightBody,
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Average Legend
        AverageLegend()

        Spacer(Modifier.height(Spacing.sm))

        // Metric sections by category
        MetricSection(
            category = LongevityCategory.SLEEP,
            results = uiState.result?.metricResults?.filter {
                it.id.category == LongevityCategory.SLEEP
            } ?: emptyList(),
            expandedId = uiState.expandedMetricId,
            onToggle = viewModel::toggleMetricExpanded,
        )

        MetricSection(
            category = LongevityCategory.STRAIN,
            results = uiState.result?.metricResults?.filter {
                it.id.category == LongevityCategory.STRAIN
            } ?: emptyList(),
            expandedId = uiState.expandedMetricId,
            onToggle = viewModel::toggleMetricExpanded,
        )

        MetricSection(
            category = LongevityCategory.FITNESS,
            results = uiState.result?.metricResults?.filter {
                it.id.category == LongevityCategory.FITNESS
            } ?: emptyList(),
            expandedId = uiState.expandedMetricId,
            onToggle = viewModel::toggleMetricExpanded,
        )

        // Trend View
        Spacer(Modifier.height(Spacing.lg))
        TrendViewSection(weeklyTrend = uiState.weeklyTrend)

        Spacer(Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun WeekNavigationHeader(
    weekRange: String,
    daysUntilUpdate: Int,
    isCurrentWeek: Boolean,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "LONGEVITY",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 1.sp,
        )
        Text(
            text = "NEXT UPDATE IN $daysUntilUpdate DAYS",
            fontSize = 11.sp,
            color = TextTertiary,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = onPreviousWeek, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous week",
                    tint = TextSecondary,
                )
            }
            Spacer(Modifier.width(Spacing.md))
            Text(
                text = weekRange,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
            )
            Spacer(Modifier.width(Spacing.md))
            IconButton(
                onClick = onNextWeek,
                enabled = !isCurrentWeek,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next week",
                    tint = if (isCurrentWeek) TextTertiary.copy(alpha = 0.3f) else TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    body: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = body,
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp,
        )
        Spacer(Modifier.height(Spacing.sm))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "EXPLORE YOUR WEEKLY INSIGHTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun AverageLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "\u25BC", fontSize = 8.sp, color = TextSecondary)
        Spacer(Modifier.width(4.dp))
        Text(text = "6 Month avg.", fontSize = 12.sp, color = TextSecondary)
        Spacer(Modifier.width(Spacing.md))
        HorizontalDivider(
            modifier = Modifier
                .width(1.dp)
                .height(14.dp),
            color = TextTertiary,
        )
        Spacer(Modifier.width(Spacing.md))
        Text(text = "\u25B2", fontSize = 8.sp, color = TextTertiary)
        Spacer(Modifier.width(4.dp))
        Text(text = "30 Day avg.", fontSize = 12.sp, color = TextTertiary)
    }
}

@Composable
private fun MetricSection(
    category: LongevityCategory,
    results: List<LongevityMetricResult>,
    expandedId: LongevityMetricID?,
    onToggle: (LongevityMetricID) -> Unit,
) {
    if (results.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
    ) {
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = category.displayName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Spacer(Modifier.height(Spacing.sm))

        results.forEachIndexed { index, result ->
            LongevityMetricCard(
                result = result,
                isExpanded = expandedId == result.id,
                onToggle = { onToggle(result.id) },
            )
            if (index < results.lastIndex) {
                Spacer(Modifier.height(Spacing.sm))
            }
        }
    }
}

@Composable
private fun TrendViewSection(
    weeklyTrend: List<LongevityResult>,
) {
    if (weeklyTrend.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md),
    ) {
        Text(
            text = "Trend View",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
        Spacer(Modifier.height(Spacing.lg))

        ZyvaAgeTrendChart(weeklyTrend = weeklyTrend)

        Spacer(Modifier.height(Spacing.md))

        PaceOfAgingTrendChart(weeklyTrend = weeklyTrend)
    }
}
