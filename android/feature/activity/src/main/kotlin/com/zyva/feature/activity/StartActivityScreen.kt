package com.zyva.feature.activity

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun StartActivityScreen(
    onFinished: () -> Unit = {},
    viewModel: ActivityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.saveSuccess) {
        onFinished()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        when {
            // Countdown overlay
            uiState.countdown != null -> {
                CountdownOverlay(count = uiState.countdown!!)
            }
            // Live workout
            uiState.isLiveActive -> {
                LiveWorkoutView(
                    uiState = uiState,
                    onPause = { viewModel.pauseWorkout() },
                    onResume = { viewModel.resumeWorkout() },
                    onEnd = { viewModel.endLiveWorkout() },
                    formatTime = { viewModel.formatElapsedTime(it) },
                )
            }
            // Type selection
            else -> {
                TypeSelectionGrid(
                    onTypeSelected = { viewModel.startCountdown(it) },
                )
            }
        }
    }
}

@Composable
private fun TypeSelectionGrid(
    onTypeSelected: (WorkoutTypeItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md),
    ) {
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = "Start Workout",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Select a workout type to begin",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.height(Spacing.lg))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            items(workoutTypes) { type ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundCard)
                        .clickable { onTypeSelected(type) }
                        .padding(Spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Teal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = type.displayName,
                            tint = Teal,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = type.displayName,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun CountdownOverlay(count: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$count",
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                color = Teal,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = "Get ready!",
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun LiveWorkoutView(
    uiState: ActivityUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onEnd: () -> Unit,
    formatTime: (Long) -> String,
) {
    var showEndConfirmation by remember { mutableStateOf(false) }

    if (showEndConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndConfirmation = false },
            title = { Text("End Workout?", color = TextPrimary) },
            text = { Text("Are you sure you want to end this workout?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showEndConfirmation = false
                    onEnd()
                }) {
                    Text("End", color = RecoveryRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndConfirmation = false }) {
                    Text("Continue", color = Teal)
                }
            },
            containerColor = BackgroundCard,
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = { 3 })

        // Pager for 3 workout pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            when (page) {
                0 -> HeartRatePage(uiState, formatTime)
                1 -> StrainPage(uiState, formatTime)
                2 -> ZoneBreakdownPage(uiState, formatTime)
            }
        }

        // Page indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.sm),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Teal else TextTertiary,
                        ),
                )
            }
        }

        // Controls bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundCard)
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pause/Resume button
            IconButton(
                onClick = { if (uiState.isPaused) onResume() else onPause() },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isPaused) RecoveryGreen else RecoveryYellow),
            ) {
                Icon(
                    imageVector = if (uiState.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    contentDescription = if (uiState.isPaused) "Resume" else "Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp),
                )
            }

            // End button
            IconButton(
                onClick = { showEndConfirmation = true },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(RecoveryRed),
            ) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "End",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun HeartRatePage(uiState: ActivityUiState, formatTime: (Long) -> String) {
    val zoneColor = zoneColor(uiState.liveCurrentZone)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(zoneColor.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = zoneName(uiState.liveCurrentZone),
                style = MaterialTheme.typography.labelLarge,
                color = zoneColor,
            )
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = if (uiState.liveCurrentHR > 0) "${uiState.liveCurrentHR}" else "--",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = "BPM",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = formatTime(uiState.liveElapsedSeconds),
                style = MaterialTheme.typography.headlineMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StrainPage(uiState: ActivityUiState, formatTime: (Long) -> String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "WORKOUT STRAIN",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "%.1f".format(uiState.liveStrain),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
            )
            Spacer(Modifier.height(Spacing.lg))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xl),
            ) {
                MetricColumn("Calories", "%.0f".format(uiState.liveCalories))
                MetricColumn("Distance", "%.2f km".format(uiState.liveDistance))
            }
            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = formatTime(uiState.liveElapsedSeconds),
                style = MaterialTheme.typography.headlineMedium,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ZoneBreakdownPage(uiState: ActivityUiState, formatTime: (Long) -> String) {
    val zones = listOf(
        5 to uiState.liveZone5Minutes,
        4 to uiState.liveZone4Minutes,
        3 to uiState.liveZone3Minutes,
        2 to uiState.liveZone2Minutes,
        1 to uiState.liveZone1Minutes,
    )
    val totalMinutes = zones.sumOf { it.second }.coerceAtLeast(0.01)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "ZONE BREAKDOWN",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.lg))

        zones.forEach { (zone, minutes) ->
            val fraction = (minutes / totalMinutes).toFloat().coerceIn(0f, 1f)
            val color = zoneColor(zone)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Z$zone",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    modifier = Modifier.width(28.dp),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BackgroundTertiary),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color),
                    )
                }
                Text(
                    text = "%.1fm".format(minutes),
                    fontSize = 11.sp,
                    color = TextTertiary,
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.End,
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            MetricColumn("Avg HR", "%.0f".format(uiState.liveAvgHR))
            MetricColumn("Max HR", "%.0f".format(uiState.liveMaxHR))
        }

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = formatTime(uiState.liveElapsedSeconds),
            style = MaterialTheme.typography.headlineMedium,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MetricColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 11.sp, color = TextTertiary)
    }
}

private fun zoneColor(zone: Int): Color = when (zone) {
    1 -> Color(0xFF4A90D9)
    2 -> RecoveryGreen
    3 -> RecoveryYellow
    4 -> Color(0xFFFF8C00)
    5 -> RecoveryRed
    else -> Color(0xFF4A90D9)
}

private fun zoneName(zone: Int): String = when (zone) {
    1 -> "ZONE 1 - WARM UP"
    2 -> "ZONE 2 - FAT BURN"
    3 -> "ZONE 3 - AEROBIC"
    4 -> "ZONE 4 - THRESHOLD"
    5 -> "ZONE 5 - ANAEROBIC"
    else -> "NO ZONE"
}
