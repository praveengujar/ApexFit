package com.zyva.feature.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddActivityScreen(
    onSaved: () -> Unit = {},
    viewModel: ActivityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.saveSuccess) {
        onSaved()
        return
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
            text = "Add Activity",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Workout type picker
        Text(
            text = "Activity Type",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.height(Spacing.sm))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(workoutTypes) { type ->
                val isSelected = uiState.selectedType.id == type.id
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Teal.copy(alpha = 0.15f) else BackgroundCard)
                        .then(
                            if (isSelected) {
                                Modifier.border(1.5.dp, Teal, RoundedCornerShape(12.dp))
                            } else {
                                Modifier
                            },
                        )
                        .clickable { viewModel.selectType(type) }
                        .padding(Spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = type.displayName,
                        tint = if (isSelected) Teal else TextSecondary,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = type.displayName,
                        fontSize = 12.sp,
                        color = if (isSelected) Teal else TextSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Time section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.md),
        ) {
            Text(
                text = "Time",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))

            TimeRow("Start", uiState.startDate)
            Spacer(Modifier.height(Spacing.xs))
            TimeRow("End", uiState.endDate)
            Spacer(Modifier.height(Spacing.sm))

            val durationMinutes = ((uiState.endDate - uiState.startDate) / 60_000.0).coerceAtLeast(0.0)
            val hours = (durationMinutes / 60).toInt()
            val mins = (durationMinutes % 60).toInt()
            val durationText = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Duration", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(durationText, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Optional fields toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .clickable { viewModel.toggleOptionalFields() }
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Optional Details",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Icon(
                imageVector = if (uiState.showOptionalFields) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = TextSecondary,
            )
        }

        AnimatedVisibility(visible = uiState.showOptionalFields) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
            ) {
                OptionalTextField(
                    label = "Activity Name",
                    value = uiState.activityName,
                    onValueChange = { viewModel.setActivityName(it) },
                )
                Spacer(Modifier.height(Spacing.sm))
                OptionalTextField(
                    label = "Calories",
                    value = uiState.caloriesText,
                    onValueChange = { viewModel.setCalories(it) },
                    keyboardType = KeyboardType.Number,
                )
                if (uiState.selectedType.supportsDistance) {
                    Spacer(Modifier.height(Spacing.sm))
                    OptionalTextField(
                        label = "Distance (miles)",
                        value = uiState.distanceText,
                        onValueChange = { viewModel.setDistance(it) },
                        keyboardType = KeyboardType.Decimal,
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // RPE section
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
                Text(
                    text = "Perceived Effort (RPE)",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Text(
                    text = "${uiState.rpe}",
                    style = MaterialTheme.typography.titleLarge,
                    color = rpeColor(uiState.rpe),
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(Spacing.sm))

            Slider(
                value = uiState.rpe.toFloat(),
                onValueChange = { viewModel.setRpe(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = rpeColor(uiState.rpe),
                    activeTrackColor = rpeColor(uiState.rpe),
                    inactiveTrackColor = BackgroundTertiary,
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Easy", fontSize = 11.sp, color = TextTertiary)
                Text("Max Effort", fontSize = 11.sp, color = TextTertiary)
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Save button
        Button(
            onClick = { viewModel.saveManualActivity() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving && (uiState.endDate - uiState.startDate) > 0,
            colors = ButtonDefaults.buttonColors(containerColor = Teal),
            shape = RoundedCornerShape(12.dp),
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(Spacing.sm))
            }
            Text(
                text = if (uiState.isSaving) "Saving..." else "Save Activity",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }
    }
}

@Composable
private fun TimeRow(label: String, millis: Long) {
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = formatter.format(Date(millis)),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
        )
    }
}

@Composable
private fun OptionalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = BackgroundCard,
            focusedContainerColor = BackgroundCard,
            unfocusedTextColor = TextPrimary,
            focusedTextColor = TextPrimary,
            unfocusedLabelColor = TextTertiary,
            focusedLabelColor = Teal,
            cursorColor = Teal,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
    )
}

private fun rpeColor(rpe: Int): Color = when (rpe) {
    in 1..3 -> RecoveryGreen
    in 4..6 -> RecoveryYellow
    in 7..8 -> Color(0xFFFF8C00)
    else -> RecoveryRed
}
