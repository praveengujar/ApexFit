package com.zyva.feature.settings

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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

@Composable
fun MaxHRSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
            text = "Max Heart Rate",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Current value card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${uiState.maxHeartRate}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = RecoveryRed,
            )
            Text(
                text = "BPM",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Source: ${sourceLabel(uiState.maxHeartRateSource)}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }

        Spacer(Modifier.height(Spacing.md))

        // Manual entry card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Set Manually",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = uiState.manualHRInput,
                    onValueChange = { viewModel.setManualHRInput(it) },
                    placeholder = { Text("Enter BPM") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = BackgroundTertiary,
                        focusedContainerColor = BackgroundTertiary,
                        unfocusedTextColor = TextPrimary,
                        focusedTextColor = TextPrimary,
                        cursorColor = Teal,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                Spacer(Modifier.width(Spacing.sm))
                Button(
                    onClick = {
                        val value = uiState.manualHRInput.toIntOrNull()
                        if (value != null && value in 120..220) {
                            viewModel.updateMaxHR(value)
                            viewModel.setManualHRInput("")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Save")
                }
            }

            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Valid range: 120–220 BPM",
                fontSize = 11.sp,
                color = TextTertiary,
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        // Reset to age estimate
        if (uiState.dateOfBirth != null) {
            OutlinedButton(
                onClick = { viewModel.resetMaxHRToAge() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Reset to Age Estimate (220 - age)", color = TextSecondary)
            }
            Spacer(Modifier.height(Spacing.md))
        }

        // Zone explanation card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Heart Rate Zones",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "Your zones are calculated as a percentage of your max heart rate.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.md))

            uiState.hrZones.forEach { (zone, lower, upper) ->
                val color = zoneDisplayColor(zone)
                val label = zoneDisplayLabel(zone)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(color),
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = "Z$zone - $label",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                        )
                    }
                    Text(
                        text = "$lower–$upper BPM",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                    )
                }
            }
        }
    }
}

private fun sourceLabel(source: String): String = when (source) {
    "USER_INPUT" -> "Manual Entry"
    "OBSERVED" -> "Observed from Workouts"
    "AGE_ESTIMATE" -> "Age-Based Estimate"
    else -> source
}

private fun zoneDisplayColor(zone: Int): Color = when (zone) {
    1 -> Color(0xFF4A90D9)
    2 -> RecoveryGreen
    3 -> RecoveryYellow
    4 -> Color(0xFFFF8C00)
    5 -> RecoveryRed
    else -> Color(0xFF4A90D9)
}

private fun zoneDisplayLabel(zone: Int): String = when (zone) {
    1 -> "Warm-Up"
    2 -> "Fat Burn"
    3 -> "Aerobic"
    4 -> "Threshold"
    5 -> "Anaerobic"
    else -> "Zone $zone"
}
