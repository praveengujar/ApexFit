package com.apexfit.feature.onboarding

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundSecondary
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.MinimumTapTarget
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary

@Composable
fun ProfileSetupScreen(
    uiState: OnboardingUiState,
    onDisplayNameChanged: (String) -> Unit,
    onBiologicalSexChanged: (String) -> Unit,
    onPreferredUnitsChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onWeightChanged: (String) -> Unit,
    onContinue: () -> Unit,
) {
    val canContinue = uiState.displayName.trim().isNotEmpty()
    val isImperial = uiState.preferredUnits == "IMPERIAL"

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = BackgroundTertiary,
        cursorColor = PrimaryBlue,
        focusedContainerColor = BackgroundSecondary,
        unfocusedContainerColor = BackgroundSecondary,
        focusedPlaceholderColor = TextTertiary,
        unfocusedPlaceholderColor = TextTertiary,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextSecondary,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Spacing.lg))

        // Header
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(56.dp),
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Set Up Your Profile",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Help us personalize your experience",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )

        Spacer(Modifier.height(Spacing.lg))

        // Display Name
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = "Display Name",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = "*",
                    style = MaterialTheme.typography.labelLarge,
                    color = RecoveryRed,
                )
            }
            Spacer(Modifier.height(Spacing.sm))
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = onDisplayNameChanged,
                placeholder = { Text("Your name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = textFieldColors,
                singleLine = true,
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Biological Sex
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Biological Sex",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Row {
                listOf("NOT_SET" to "Not Set", "MALE" to "Male", "FEMALE" to "Female", "OTHER" to "Other").forEach { (value, label) ->
                    FilterChip(
                        selected = uiState.biologicalSex == value,
                        onClick = { onBiologicalSexChanged(value) },
                        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.padding(end = Spacing.sm),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = BackgroundSecondary,
                            labelColor = TextSecondary,
                        ),
                    )
                }
            }
            Text(
                text = "Affects recovery and heart rate calculations",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Preferred Units
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Preferred Units",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Row {
                listOf("IMPERIAL" to "Imperial", "METRIC" to "Metric").forEach { (value, label) ->
                    FilterChip(
                        selected = uiState.preferredUnits == value,
                        onClick = { onPreferredUnitsChanged(value) },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = Spacing.sm),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = BackgroundSecondary,
                            labelColor = TextSecondary,
                        ),
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Height
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = if (isImperial) "Height (inches)" else "Height (cm)",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = "(optional)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
            Spacer(Modifier.height(Spacing.sm))
            OutlinedTextField(
                value = uiState.heightValue,
                onValueChange = onHeightChanged,
                placeholder = { Text(if (isImperial) "e.g. 70" else "e.g. 178") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = textFieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        // Weight
        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(
                    text = if (isImperial) "Weight (lbs)" else "Weight (kg)",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                )
                Spacer(Modifier.width(Spacing.xs))
                Text(
                    text = "(optional)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
            Spacer(Modifier.height(Spacing.sm))
            OutlinedTextField(
                value = uiState.weightValue,
                onValueChange = onWeightChanged,
                placeholder = { Text(if (isImperial) "e.g. 165" else "e.g. 75") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = textFieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        // Continue button
        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(MinimumTapTarget + 8.dp),
            shape = RoundedCornerShape(CornerRadius.medium),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                disabledContainerColor = BackgroundTertiary,
            ),
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}
