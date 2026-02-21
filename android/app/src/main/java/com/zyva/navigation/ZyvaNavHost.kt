package com.zyva.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.BackgroundSecondary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.feature.activity.AddActivityScreen
import com.zyva.feature.activity.StartActivityScreen
import com.zyva.feature.coach.CoachScreen
import com.zyva.feature.home.DeviceHealthScreen
import com.zyva.feature.home.HomeScreen
import com.zyva.feature.longevity.LongevityDashboardScreen
import com.zyva.feature.myplan.MyPlanScreen
import com.zyva.feature.journal.JournalEntryScreen
import com.zyva.feature.journal.JournalHistoryScreen
import com.zyva.feature.journal.JournalImpactScreen
import com.zyva.feature.journal.JournalSetupEditScreen
import com.zyva.feature.onboarding.OnboardingScreen
import com.zyva.feature.profile.ProfileScreen
import com.zyva.feature.recovery.RecoveryDashboardScreen
import com.zyva.feature.settings.AboutScreen
import com.zyva.feature.settings.HealthConnectStatusScreen
import com.zyva.feature.settings.MaxHRSettingsScreen
import com.zyva.feature.settings.NotificationSettingsScreen
import com.zyva.feature.settings.SettingsScreen
import com.zyva.feature.settings.SleepGoalSettingsScreen
import com.zyva.feature.settings.UnitSettingsScreen
import com.zyva.feature.sleep.SleepDashboardScreen
import com.zyva.feature.strain.StrainDashboardScreen
import com.zyva.feature.trends.TrendChartScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userProfileRepo: UserProfileRepository,
) : ViewModel() {
    val hasCompletedOnboarding = userProfileRepo.observeProfile()
        .map { it?.hasCompletedOnboarding == true }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home),
    BottomNavItem("Longevity", Icons.Outlined.FavoriteBorder),
    BottomNavItem("Community", Icons.Filled.Person),
    BottomNavItem("My Plan", Icons.Filled.Star),
    BottomNavItem("Coach", Icons.Outlined.School),
)

@Composable
fun ZyvaNavHost(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState(initial = null)

    when (hasCompletedOnboarding) {
        null -> {
            // Loading state - show nothing while checking DB
            Box(
                modifier = Modifier.fillMaxSize(),
            )
        }
        false -> {
            OnboardingScreen(
                onOnboardingComplete = {
                    // State will automatically update via Flow observation
                },
            )
        }
        true -> {
            MainTabScaffold()
        }
    }
}

@Composable
private fun MainTabScaffold() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    // Track detail screen navigation from Home tab gauge taps
    var detailScreen by rememberSaveable { mutableStateOf<String?>(null) }

    // Handle back navigation from detail screens
    if (detailScreen != null) {
        BackHandler { detailScreen = null }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        bottomBar = {
            NavigationBar(
                containerColor = BackgroundSecondary,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            detailScreen = null
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = BackgroundSecondary,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Detail screen takes priority when set
            when (detailScreen) {
                "recovery" -> RecoveryDashboardScreen()
                "sleep" -> SleepDashboardScreen()
                "strain" -> StrainDashboardScreen()
                "journal_entry" -> JournalEntryScreen(
                    onSetupEdit = { detailScreen = "journal_setup" },
                )
                "journal_history" -> JournalHistoryScreen()
                "journal_setup" -> JournalSetupEditScreen(
                    onSave = { detailScreen = "journal_entry" },
                )
                "journal_impact" -> JournalImpactScreen()
                "trends" -> TrendChartScreen()
                "add_activity" -> AddActivityScreen(
                    onSaved = { detailScreen = null },
                )
                "start_activity" -> StartActivityScreen(
                    onFinished = { detailScreen = null },
                )
                "settings" -> SettingsScreen(
                    onMaxHRTap = { detailScreen = "settings_maxhr" },
                    onSleepGoalTap = { detailScreen = "settings_sleep_goal" },
                    onNotificationsTap = { detailScreen = "settings_notifications" },
                    onUnitsTap = { detailScreen = "settings_units" },
                    onHealthConnectTap = { detailScreen = "settings_health_connect" },
                    onAboutTap = { detailScreen = "settings_about" },
                )
                "settings_maxhr" -> MaxHRSettingsScreen()
                "settings_sleep_goal" -> SleepGoalSettingsScreen()
                "settings_notifications" -> NotificationSettingsScreen()
                "settings_units" -> UnitSettingsScreen()
                "settings_health_connect" -> HealthConnectStatusScreen()
                "settings_about" -> AboutScreen()
                "profile" -> ProfileScreen(
                    onBack = { detailScreen = null },
                )
                "device_health" -> DeviceHealthScreen()
                else -> {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            onSleepTap = { detailScreen = "sleep" },
                            onRecoveryTap = { detailScreen = "recovery" },
                            onStrainTap = { detailScreen = "strain" },
                            onJournalTap = { detailScreen = "journal_entry" },
                            onTrendsTap = { detailScreen = "trends" },
                            onAddActivityTap = { detailScreen = "add_activity" },
                            onStartActivityTap = { detailScreen = "start_activity" },
                            onSettingsTap = { detailScreen = "settings" },
                            onProfileTap = { detailScreen = "profile" },
                            onWatchTap = { detailScreen = "device_health" },
                        )
                        1 -> LongevityDashboardScreen()
                        2 -> {
                            // Community placeholder
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Community",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                        3 -> MyPlanScreen()
                        4 -> CoachScreen()
                    }
                }
            }
        }
    }
}

