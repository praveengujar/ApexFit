package com.apexfit.feature.journal

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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.core.engine.CorrelationDirection
import com.apexfit.core.engine.CorrelationResult
import com.apexfit.core.engine.StatisticalEngine
import kotlin.math.abs

@Composable
fun JournalImpactScreen(
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.computeCorrelations()
    }

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
            text = "Impact Analysis",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Metric picker
        MetricPickerCard(
            selected = uiState.selectedTargetMetric,
            onSelect = { viewModel.setTargetMetric(it) },
        )

        Spacer(Modifier.height(Spacing.md))

        when {
            uiState.completedCount < 14 -> {
                InsufficientDataCard(count = uiState.completedCount)
            }
            uiState.isAnalyzing -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlue)
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = "Analyzing correlations...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                }
            }
            uiState.impactResults.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .padding(Spacing.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No Results Yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = "Keep logging journal entries to see how your behaviors impact ${uiState.selectedTargetMetric.displayName}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "Behaviors by Impact on ${uiState.selectedTargetMetric.displayName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.sm))

                uiState.impactResults.forEach { result ->
                    CorrelationRow(
                        result = result,
                        displayName = viewModel.behaviorDisplayName(result.behaviorName),
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }
            }
        }
    }
}

@Composable
private fun MetricPickerCard(
    selected: TargetMetric,
    onSelect: (TargetMetric) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "Target Metric",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.height(Spacing.sm))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TargetMetric.entries.forEachIndexed { index, metric ->
                SegmentedButton(
                    selected = selected == metric,
                    onClick = { onSelect(metric) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TargetMetric.entries.size,
                    ),
                    label = { Text(metric.displayName) },
                )
            }
        }
    }
}

@Composable
private fun InsufficientDataCard(count: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Need More Data",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Log at least 14 journal entries to see impact analysis. You have $count so far.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.md))
        LinearProgressIndicator(
            progress = { (count / 14f).coerceAtMost(1f) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl),
            color = PrimaryBlue,
            trackColor = BackgroundTertiary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "$count / 14 entries",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
        )
    }
}

@Composable
private fun CorrelationRow(
    result: CorrelationResult,
    displayName: String,
) {
    val directionColor = when (result.direction) {
        CorrelationDirection.POSITIVE -> RecoveryGreen
        CorrelationDirection.NEGATIVE -> RecoveryRed
        CorrelationDirection.NEUTRAL -> TextTertiary
    }
    val directionLabel = when (result.direction) {
        CorrelationDirection.POSITIVE -> "Positive"
        CorrelationDirection.NEGATIVE -> "Negative"
        CorrelationDirection.NEUTRAL -> "Neutral"
    }
    val directionIcon = when (result.direction) {
        CorrelationDirection.POSITIVE -> Icons.AutoMirrored.Filled.TrendingUp
        CorrelationDirection.NEGATIVE -> Icons.AutoMirrored.Filled.TrendingDown
        CorrelationDirection.NEUTRAL -> Icons.AutoMirrored.Filled.TrendingUp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Direction icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(directionColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = directionIcon,
                contentDescription = null,
                tint = directionColor,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Effect size badge
                Text(
                    text = StatisticalEngine.interpretEffectSize(result.effectSize),
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundTertiary)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )

                if (result.isSignificant) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Teal,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "Significant",
                            fontSize = 11.sp,
                            color = Teal,
                        )
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = directionLabel,
                style = MaterialTheme.typography.labelSmall,
                color = directionColor,
            )
            Text(
                text = "n=${result.sampleSizeWith + result.sampleSizeWithout}",
                fontSize = 10.sp,
                color = TextTertiary,
            )
        }
    }
}
