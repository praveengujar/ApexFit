package com.zyva.feature.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundSecondary
import com.zyva.core.designsystem.theme.BackgroundTertiary
import com.zyva.core.designsystem.theme.CornerRadius
import com.zyva.core.designsystem.theme.Lavender
import com.zyva.core.designsystem.theme.MinimumTapTarget
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary

@Composable
fun JournalSetupScreen(
    uiState: OnboardingUiState,
    onToggleBehavior: (String) -> Unit,
    onContinue: () -> Unit,
) {
    val categories = listOf("Lifestyle", "Nutrition", "Sleep Hygiene", "Recovery Practices")
    var expandedCategories by remember {
        mutableStateOf(categories.toSet())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.lg))

            Icon(
                imageVector = Icons.Filled.AutoStories,
                contentDescription = null,
                tint = Teal,
                modifier = Modifier.size(56.dp),
            )

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = "Daily Journal",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
            )

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = "Select behaviors to track daily. These help our AI find patterns that affect your recovery.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = "${uiState.selectedBehaviorIds.size} selected",
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryBlue,
            )

            Spacer(Modifier.height(Spacing.md))
        }

        // Category list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            categories.forEach { category ->
                val behaviorsInCategory = uiState.journalBehaviors.filter { it.category == category }
                if (behaviorsInCategory.isNotEmpty()) {
                    CategorySection(
                        category = category,
                        behaviors = behaviorsInCategory,
                        selectedIds = uiState.selectedBehaviorIds,
                        isExpanded = category in expandedCategories,
                        onToggleExpand = {
                            expandedCategories = if (category in expandedCategories) {
                                expandedCategories - category
                            } else {
                                expandedCategories + category
                            }
                        },
                        onToggleBehavior = onToggleBehavior,
                    )
                    Spacer(Modifier.height(Spacing.md))
                }
            }
            Spacer(Modifier.height(Spacing.xl))
        }

        // Bottom button
        HorizontalDivider(color = BackgroundTertiary)
        Column(
            modifier = Modifier
                .background(BackgroundPrimary)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MinimumTapTarget + 8.dp),
                shape = RoundedCornerShape(CornerRadius.medium),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                Text(
                    text = if (uiState.selectedBehaviorIds.isEmpty()) "Skip for now" else "Continue",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun CategorySection(
    category: String,
    behaviors: List<JournalBehaviorItem>,
    selectedIds: Set<String>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleBehavior: (String) -> Unit,
) {
    val categoryColor = categoryColor(category)
    val categoryIcon = categoryIcon(category)
    val selectedCount = behaviors.count { it.id in selectedIds }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundSecondary),
    ) {
        // Category header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(24.dp),
            )

            Spacer(Modifier.width(Spacing.md))

            Text(
                text = category,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
            )

            if (selectedCount > 0) {
                Text(
                    text = "$selectedCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = categoryColor,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
                Spacer(Modifier.width(Spacing.sm))
            }

            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp),
            )
        }

        // Behavior toggles
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.padding(
                    start = Spacing.md,
                    end = Spacing.md,
                    bottom = Spacing.sm,
                ),
            ) {
                behaviors.forEach { behavior ->
                    BehaviorToggleRow(
                        behavior = behavior,
                        isSelected = behavior.id in selectedIds,
                        accentColor = categoryColor,
                        onToggle = { onToggleBehavior(behavior.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BehaviorToggleRow(
    behavior: JournalBehaviorItem,
    isSelected: Boolean,
    accentColor: Color,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSelected) accentColor else Color.Transparent)
                .then(
                    if (!isSelected) {
                        Modifier.background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(6.dp),
                        )
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Transparent)
                        .padding(1.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Transparent),
                ) {
                    // Outlined checkbox
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(5.dp),
                            ),
                    )
                }
            }
        }

        Spacer(Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = behavior.name,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )
            if (!behavior.options.isNullOrEmpty()) {
                Text(
                    text = behavior.options.joinToString(" / "),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
        }

        Icon(
            imageVector = when (behavior.responseType) {
                "numeric" -> Icons.Filled.CheckCircle
                "scale" -> Icons.Filled.CheckCircle
                else -> Icons.Filled.CheckCircle
            },
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(16.dp),
        )
    }
}

private fun categoryColor(category: String): Color = when (category) {
    "Lifestyle" -> RecoveryYellow
    "Nutrition" -> RecoveryGreen
    "Sleep Hygiene" -> Lavender
    "Recovery Practices" -> Teal
    else -> PrimaryBlue
}

private fun categoryIcon(category: String): ImageVector = when (category) {
    "Lifestyle" -> Icons.Filled.SelfImprovement
    "Nutrition" -> Icons.Filled.Restaurant
    "Sleep Hygiene" -> Icons.Filled.DarkMode
    "Recovery Practices" -> Icons.Filled.FitnessCenter
    else -> Icons.Filled.CheckCircle
}
