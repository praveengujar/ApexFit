package com.apexfit.feature.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.feature.profile.components.AchievementCardsSection
import com.apexfit.feature.profile.components.ActivitySummarySection
import com.apexfit.feature.profile.components.DataHighlightsSection
import com.apexfit.feature.profile.components.DayStreakSection
import com.apexfit.feature.profile.components.NotableStatsSection
import com.apexfit.feature.profile.components.ProfileHeaderSection
import com.apexfit.feature.profile.components.StreaksSection

@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBack() }
                    .padding(4.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "PROFILE",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            // Placeholder for symmetry
            Spacer(modifier = Modifier.size(32.dp))
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                // Section 1: Header
                ProfileHeaderSection(
                    displayName = uiState.displayName,
                    initials = uiState.initials,
                    age = uiState.age,
                    memberSince = uiState.memberSince,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Achievement Cards
                AchievementCardsSection(
                    level = uiState.level,
                    greenRecoveryCount = uiState.greenRecoveryCount,
                    apexFitAge = uiState.apexFitAge,
                    yearsYoungerOlder = uiState.yearsYoungerOlder,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Day Streak
                DayStreakSection(dayStreak = uiState.dayStreak)

                Spacer(modifier = Modifier.height(20.dp))

                // Section 4: Data Highlights
                DataHighlightsSection(
                    period = uiState.highlightsPeriod,
                    onPeriodChange = { viewModel.setHighlightsPeriod(it) },
                    bestSleepPct = uiState.bestSleepPct,
                    peakRecoveryPct = uiState.peakRecoveryPct,
                    maxStrain = uiState.maxStrain,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 5: Streaks
                StreaksSection(
                    sleepStreak = uiState.sleepStreak,
                    greenRecoveryStreak = uiState.greenRecoveryStreak,
                    strainStreak = uiState.strainStreak,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 6: Notable Stats
                NotableStatsSection(
                    lowestRHR = uiState.lowestRHR,
                    highestRHR = uiState.highestRHR,
                    lowestHRV = uiState.lowestHRV,
                    highestHRV = uiState.highestHRV,
                    maxHeartRate = uiState.maxHeartRate,
                    longestSleepHours = uiState.longestSleepHours,
                    lowestRecovery = uiState.lowestRecovery,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 7: Activity Summary
                ActivitySummarySection(
                    period = uiState.activityPeriod,
                    onPeriodChange = { viewModel.setActivityPeriod(it) },
                    totalActivities = uiState.totalActivities,
                    breakdown = uiState.activityBreakdown,
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
