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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.core.model.UnitSystem

@Composable
fun UnitSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedUnit = try {
        UnitSystem.valueOf(uiState.preferredUnits)
    } catch (_: Exception) {
        UnitSystem.METRIC
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
            text = "Units",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Unit picker
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Measurement System",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                UnitSystem.entries.forEachIndexed { index, unit ->
                    SegmentedButton(
                        selected = selectedUnit == unit,
                        onClick = { viewModel.updateUnits(unit) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = UnitSystem.entries.size,
                        ),
                        label = { Text(unit.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Preview card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(Spacing.md))

            PreviewRow(
                icon = Icons.Filled.FitnessCenter,
                label = "Weight",
                value = if (selectedUnit == UnitSystem.METRIC) "75 kg" else "165 lbs",
            )
            PreviewRow(
                icon = Icons.Filled.Straighten,
                label = "Distance",
                value = if (selectedUnit == UnitSystem.METRIC) "5.0 km" else "3.1 miles",
            )
            PreviewRow(
                icon = Icons.Filled.Height,
                label = "Height",
                value = if (selectedUnit == UnitSystem.METRIC) "180 cm" else "5'11\"",
            )
            PreviewRow(
                icon = Icons.Filled.Thermostat,
                label = "Temperature",
                value = if (selectedUnit == UnitSystem.METRIC) "36.6 °C" else "97.9 °F",
            )
        }
    }
}

@Composable
private fun PreviewRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Teal,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}
