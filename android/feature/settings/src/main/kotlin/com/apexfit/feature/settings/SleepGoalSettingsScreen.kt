package com.apexfit.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.apexfit.core.designsystem.theme.SleepDeep
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary

@Composable
fun SleepGoalSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var sliderValue by remember(uiState.sleepBaselineHours) {
        mutableDoubleStateOf(uiState.sleepBaselineHours)
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
            text = "Sleep Goal",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Current goal card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "%.1f".format(sliderValue),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = SleepDeep,
            )
            Text(
                text = "hours",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.xs))
            val hours = sliderValue.toInt()
            val mins = ((sliderValue - hours) * 60).toInt()
            Text(
                text = "${hours}h ${mins}m",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }

        Spacer(Modifier.height(Spacing.md))

        // Slider card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("6h", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                Text("10h", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            }

            Slider(
                value = sliderValue.toFloat(),
                onValueChange = { sliderValue = snapToQuarter(it.toDouble()) },
                onValueChangeFinished = { viewModel.updateSleepGoal(sliderValue) },
                valueRange = 6f..10f,
                colors = SliderDefaults.colors(
                    thumbColor = SleepDeep,
                    activeTrackColor = SleepDeep,
                    inactiveTrackColor = BackgroundTertiary,
                ),
            )

            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Recommended: 7-9 hours for most adults",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(Spacing.md))

        // Breakdown card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Sleep Need Calculation",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.md))

            BreakdownRow("Base Sleep Need", "%.1fh".format(sliderValue))
            BreakdownRow("Time to Fall Asleep", "~15 min")
            BreakdownRow("Target In-Bed Time", "%.1fh".format(sliderValue + 0.25))
            Spacer(Modifier.height(Spacing.sm))
            BreakdownRow("Sleep Performance Target", "85–100%")
        }
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

private fun snapToQuarter(value: Double): Double {
    return (Math.round(value * 4.0) / 4.0)
}
