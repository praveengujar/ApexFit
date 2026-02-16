package com.apexfit.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.Spacing

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.onboardingComplete) {
        onOnboardingComplete()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
    ) {
        // Progress indicator for middle steps
        val progressSteps = listOf(
            OnboardingStep.SIGN_IN,
            OnboardingStep.HEALTH_CONNECT,
            OnboardingStep.PROFILE,
            OnboardingStep.JOURNAL_SETUP,
        )
        if (uiState.currentStep in progressSteps) {
            val currentIndex = progressSteps.indexOf(uiState.currentStep)
            ProgressIndicator(
                stepCount = progressSteps.size,
                currentStep = currentIndex,
                modifier = Modifier
                    .padding(horizontal = Spacing.xl, vertical = Spacing.md),
            )
        }

        // Step content with slide animation
        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            },
            label = "onboarding_step",
        ) { step ->
            when (step) {
                OnboardingStep.WELCOME -> WelcomeScreen(
                    onGetStarted = { viewModel.advanceTo(OnboardingStep.SIGN_IN) },
                )
                OnboardingStep.SIGN_IN -> SignInScreen(
                    onSignIn = { viewModel.advanceTo(OnboardingStep.HEALTH_CONNECT) },
                    onSkip = { viewModel.advanceTo(OnboardingStep.HEALTH_CONNECT) },
                )
                OnboardingStep.HEALTH_CONNECT -> HealthConnectPermissionScreen(
                    uiState = uiState,
                    onPermissionsResult = viewModel::onPermissionsResult,
                    onRequestPermissions = { viewModel.setRequestingPermissions(true) },
                    onContinue = { viewModel.advanceTo(OnboardingStep.PROFILE) },
                )
                OnboardingStep.PROFILE -> ProfileSetupScreen(
                    uiState = uiState,
                    onDisplayNameChanged = viewModel::updateDisplayName,
                    onBiologicalSexChanged = viewModel::updateBiologicalSex,
                    onPreferredUnitsChanged = viewModel::updatePreferredUnits,
                    onHeightChanged = viewModel::updateHeight,
                    onWeightChanged = viewModel::updateWeight,
                    onContinue = {
                        viewModel.saveProfile()
                        viewModel.advanceTo(OnboardingStep.JOURNAL_SETUP)
                    },
                )
                OnboardingStep.JOURNAL_SETUP -> JournalSetupScreen(
                    uiState = uiState,
                    onToggleBehavior = viewModel::toggleBehavior,
                    onContinue = {
                        viewModel.saveJournalBehaviors()
                        viewModel.advanceTo(OnboardingStep.COMPLETE)
                    },
                )
                OnboardingStep.COMPLETE -> OnboardingCompleteScreen(
                    uiState = uiState,
                    onStartApp = { viewModel.completeOnboarding() },
                )
            }
        }
    }
}

@Composable
private fun ProgressIndicator(
    stepCount: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        repeat(stepCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index <= currentStep) PrimaryBlue else BackgroundTertiary),
            )
        }
    }
}
