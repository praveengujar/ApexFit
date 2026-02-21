package com.zyva.feature.journal

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.model.JournalResponseType

@Composable
fun JournalSetupEditScreen(
    onSave: () -> Unit = {},
    viewModel: JournalViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var localSelectedIds by remember(uiState.selectedBehaviorIds) {
        mutableStateOf(uiState.selectedBehaviorIds)
    }

    val categories = uiState.allBehaviors
        .groupBy { it.category }
        .toSortedMap()

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
            text = "Edit Behaviors",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Choose which behaviors to track daily. These will appear in your journal and be analyzed for their impact on your recovery.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.lg))

        // Category sections
        categories.forEach { (category, behaviors) ->
            CategorySection(
                category = category,
                behaviors = behaviors,
                selectedIds = localSelectedIds,
                onToggle = { id ->
                    localSelectedIds = if (id in localSelectedIds) {
                        localSelectedIds - id
                    } else {
                        localSelectedIds + id
                    }
                },
            )
            Spacer(Modifier.height(Spacing.lg))
        }

        // Save button
        Button(
            onClick = {
                viewModel.updateSelectedBehaviors(localSelectedIds)
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Save (${localSelectedIds.size} selected)",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }
    }
}

@Composable
private fun CategorySection(
    category: String,
    behaviors: List<JournalBehavior>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
) {
    val categoryColor = when (category.lowercase()) {
        "nutrition" -> RecoveryGreen
        "lifestyle" -> RecoveryYellow
        "sleep hygiene" -> PrimaryBlue
        "recovery practices" -> Teal
        else -> PrimaryBlue
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.first().toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = categoryColor,
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = category,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        behaviors.forEach { behavior ->
            val isSelected = behavior.id in selectedIds
            BehaviorToggleRow(
                behavior = behavior,
                isSelected = isSelected,
                categoryColor = categoryColor,
                onClick = { onToggle(behavior.id) },
            )
            Spacer(Modifier.height(Spacing.xs))
        }
    }
}

@Composable
private fun BehaviorToggleRow(
    behavior: JournalBehavior,
    isSelected: Boolean,
    categoryColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundCard)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(categoryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = behavior.name.first().toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = categoryColor,
            )
        }

        Spacer(Modifier.width(Spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = behavior.name,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Text(
                text = responseTypeLabel(behavior.responseType),
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                fontSize = 11.sp,
            )
        }

        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = if (isSelected) PrimaryBlue else TextTertiary,
            modifier = Modifier.size(24.dp),
        )
    }
}

private fun responseTypeLabel(type: JournalResponseType): String {
    return when (type) {
        JournalResponseType.TOGGLE -> "Yes / No"
        JournalResponseType.NUMERIC -> "Number"
        JournalResponseType.SCALE -> "Scale"
    }
}
