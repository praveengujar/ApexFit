package com.apexfit.feature.journal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import com.apexfit.core.model.JournalResponseType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun JournalEntryScreen(
    onSetupEdit: () -> Unit = {},
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPrimary)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md)
                .padding(bottom = Spacing.xl),
        ) {
            Spacer(Modifier.height(Spacing.md))

            // Date header
            DateHeader(todayEntry = uiState.todayEntry != null)

            Spacer(Modifier.height(Spacing.md))

            if (uiState.selectedBehaviors.isEmpty()) {
                EmptyBehaviorsPrompt(onSetup = onSetupEdit)
            } else {
                // Behavior rows
                uiState.selectedBehaviors.forEach { behavior ->
                    BehaviorRow(
                        behavior = behavior,
                        value = uiState.responses[behavior.id]
                            ?: viewModel.defaultValue(behavior),
                        onValueChange = { viewModel.updateResponse(behavior.id, it) },
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                Spacer(Modifier.height(Spacing.md))

                // Save button
                Button(
                    onClick = { viewModel.saveEntry() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TextPrimary,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(Spacing.sm))
                    }
                    Text(
                        text = if (uiState.todayEntry != null) "Update Entry" else "Save Entry",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = Spacing.xs),
                    )
                }
            }
        }

        // Saved toast
        AnimatedVisibility(
            visible = uiState.showSavedConfirmation,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = Spacing.lg)
                    .clip(RoundedCornerShape(24.dp))
                    .background(BackgroundTertiary)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = RecoveryGreen,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(Spacing.sm))
                Text(
                    text = "Journal entry saved",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun DateHeader(todayEntry: Boolean) {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val formatted = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dayOfWeek.uppercase(),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Text(
                text = formatted,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
        }
        if (todayEntry) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = RecoveryGreen,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Logged",
                    style = MaterialTheme.typography.labelSmall,
                    color = RecoveryGreen,
                )
            }
        }
    }
}

@Composable
private fun EmptyBehaviorsPrompt(onSetup: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No Behaviors Selected",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Add behaviors to track how your daily habits affect your recovery and performance.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.height(Spacing.md))
        Button(
            onClick = onSetup,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Set Up Journal", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BehaviorRow(
    behavior: JournalBehavior,
    value: ResponseValue,
    onValueChange: (ResponseValue) -> Unit,
) {
    val categoryColor = when (behavior.category.lowercase()) {
        "nutrition" -> RecoveryGreen
        "lifestyle" -> RecoveryYellow
        "sleep hygiene" -> PrimaryBlue
        "recovery practices" -> Teal
        else -> PrimaryBlue
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = behavior.category.first().toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = categoryColor,
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        // Label
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = behavior.name,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Text(
                text = behavior.category,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp,
            )
        }

        // Response control
        when (behavior.responseType) {
            JournalResponseType.TOGGLE -> {
                val checked = (value as? ResponseValue.Toggle)?.value ?: false
                Switch(
                    checked = checked,
                    onCheckedChange = { onValueChange(ResponseValue.Toggle(it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = BackgroundTertiary,
                    ),
                )
            }
            JournalResponseType.NUMERIC -> {
                val amount = (value as? ResponseValue.Numeric)?.value ?: 0.0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (amount > 0) onValueChange(ResponseValue.Numeric(amount - 1))
                        },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Decrease",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Text(
                        text = "${amount.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.width(32.dp),
                    )
                    IconButton(
                        onClick = { onValueChange(ResponseValue.Numeric(amount + 1)) },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            JournalResponseType.SCALE -> {
                val current = (value as? ResponseValue.Scale)?.value
                    ?: behavior.options.firstOrNull() ?: ""
                if (behavior.options.size <= 4) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.width(200.dp),
                    ) {
                        behavior.options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = current == option,
                                onClick = { onValueChange(ResponseValue.Scale(option)) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = behavior.options.size,
                                ),
                                label = {
                                    Text(
                                        text = option,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                    )
                                },
                            )
                        }
                    }
                } else {
                    // Fallback for many options - show as row of chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        behavior.options.forEach { option ->
                            val selected = current == option
                            Text(
                                text = option,
                                fontSize = 10.sp,
                                color = if (selected) TextPrimary else TextTertiary,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) PrimaryBlue.copy(alpha = 0.3f) else BackgroundTertiary)
                                    .clickable { onValueChange(ResponseValue.Scale(option)) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
