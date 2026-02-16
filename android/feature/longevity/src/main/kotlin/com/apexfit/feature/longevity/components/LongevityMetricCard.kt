package com.apexfit.feature.longevity.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.core.engine.LongevityEngine
import com.apexfit.core.engine.LongevityMetricID
import com.apexfit.core.engine.LongevityMetricResult

@Composable
fun LongevityMetricCard(
    result: LongevityMetricResult,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .clickable(onClick = onToggle)
            .padding(Spacing.md),
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = result.id.displayName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        // Value
        Text(
            text = LongevityEngine.formatValue(result.sixMonthAvg, result.id),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        // Gradient bar
        LongevityGradientBar(
            range = result.id.gradientRange,
            sixMonthAvg = result.sixMonthAvg,
            thirtyDayAvg = result.thirtyDayAvg,
            deltaYears = result.deltaYears,
            isHigherBetter = result.id.isHigherBetter,
            rangeMinLabel = rangeMinLabel(result.id),
            rangeMaxLabel = rangeMaxLabel(result.id),
        )

        // Expandable insight
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(modifier = Modifier.padding(top = Spacing.md)) {
                Text(
                    text = result.insightTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = result.insightBody,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "VIEW TREND \u2192",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                )
            }
        }
    }
}

private fun rangeMinLabel(id: LongevityMetricID): String {
    val range = id.gradientRange
    return when (id) {
        LongevityMetricID.SLEEP_CONSISTENCY,
        LongevityMetricID.LEAN_BODY_MASS -> "${range.start.toInt()}%"
        LongevityMetricID.HOURS_OF_SLEEP -> "${range.start.toInt()}h"
        LongevityMetricID.HR_ZONES_1_TO_3_WEEKLY,
        LongevityMetricID.HR_ZONES_4_TO_5_WEEKLY,
        LongevityMetricID.STRENGTH_ACTIVITY_WEEKLY -> "${range.start.toInt()}h"
        LongevityMetricID.DAILY_STEPS -> "${(range.start / 1000).toInt()}K"
        LongevityMetricID.VO2_MAX -> "${range.start.toInt()}"
        LongevityMetricID.RESTING_HEART_RATE -> "${range.start.toInt()}bpm"
    }
}

private fun rangeMaxLabel(id: LongevityMetricID): String {
    val range = id.gradientRange
    return when (id) {
        LongevityMetricID.SLEEP_CONSISTENCY,
        LongevityMetricID.LEAN_BODY_MASS -> "${range.endInclusive.toInt()}%"
        LongevityMetricID.HOURS_OF_SLEEP -> "${range.endInclusive.toInt()}h"
        LongevityMetricID.HR_ZONES_1_TO_3_WEEKLY,
        LongevityMetricID.HR_ZONES_4_TO_5_WEEKLY,
        LongevityMetricID.STRENGTH_ACTIVITY_WEEKLY -> "${range.endInclusive.toInt()}h"
        LongevityMetricID.DAILY_STEPS -> "${(range.endInclusive / 1000).toInt()}K"
        LongevityMetricID.VO2_MAX -> "${range.endInclusive.toInt()}"
        LongevityMetricID.RESTING_HEART_RATE -> "${range.endInclusive.toInt()}bpm"
    }
}
