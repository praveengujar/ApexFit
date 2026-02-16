package com.apexfit.feature.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundSecondary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.MinimumTapTarget
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.Teal
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary

@Composable
fun OnboardingCompleteScreen(
    uiState: OnboardingUiState,
    onStartApp: () -> Unit,
) {
    var checkmarkAppeared by remember { mutableStateOf(false) }
    var contentAppeared by remember { mutableStateOf(false) }
    var buttonAppeared by remember { mutableStateOf(false) }

    val checkmarkScale by animateFloatAsState(
        targetValue = if (checkmarkAppeared) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "checkmark_scale",
    )
    val checkmarkAlpha by animateFloatAsState(
        targetValue = if (checkmarkAppeared) 1f else 0f,
        animationSpec = tween(600),
        label = "checkmark_alpha",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentAppeared) 1f else 0f,
        animationSpec = tween(500),
        label = "content_alpha",
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (contentAppeared) 0f else 20f,
        animationSpec = tween(500),
        label = "content_offset",
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonAppeared) 1f else 0f,
        animationSpec = tween(500),
        label = "button_alpha",
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        checkmarkAppeared = true
        kotlinx.coroutines.delay(400)
        contentAppeared = true
        kotlinx.coroutines.delay(300)
        buttonAppeared = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        // Animated checkmark
        Box(
            modifier = Modifier
                .scale(checkmarkScale)
                .alpha(checkmarkAlpha),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        color = RecoveryGreen.copy(alpha = 0.15f),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(
                        color = RecoveryGreen.copy(alpha = 0.3f),
                        shape = CircleShape,
                    ),
            )
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = RecoveryGreen,
                modifier = Modifier.size(80.dp),
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        // Title
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha),
        ) {
            Text(
                text = "You're All Set!",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
            )

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = "ApexFit is ready to help you optimize your performance.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Spacing.xl),
            )
        }

        Spacer(Modifier.height(Spacing.xxl))

        // Configuration summary
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(contentAlpha),
        ) {
            Text(
                text = "What's configured",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = Spacing.md),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = BackgroundSecondary,
                        shape = RoundedCornerShape(CornerRadius.medium),
                    )
                    .padding(Spacing.md),
            ) {
                if (uiState.displayName.isNotEmpty()) {
                    SummaryRow(
                        icon = Icons.Filled.Person,
                        text = "Profile: ${uiState.displayName}",
                    )
                    Spacer(Modifier.height(Spacing.sm))
                }

                SummaryRow(
                    icon = Icons.Filled.PersonOutline,
                    text = "Signed in as guest",
                )
                Spacer(Modifier.height(Spacing.sm))

                SummaryRow(
                    icon = Icons.Filled.Favorite,
                    text = if (uiState.healthConnectPermissionsGranted) "Health Connect: Connected" else "Health Connect: Not connected",
                )

                if (uiState.selectedBehaviorIds.isNotEmpty()) {
                    Spacer(Modifier.height(Spacing.sm))
                    SummaryRow(
                        icon = Icons.Filled.AutoStories,
                        text = "Journal: ${uiState.selectedBehaviorIds.size} behaviors tracked",
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Start button
        Button(
            onClick = onStartApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(MinimumTapTarget + 8.dp)
                .alpha(buttonAlpha),
            shape = RoundedCornerShape(CornerRadius.medium),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, Teal),
                        ),
                        shape = RoundedCornerShape(CornerRadius.medium),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Start Using ApexFit",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                    )
                    Spacer(Modifier.width(Spacing.sm))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun SummaryRow(
    icon: ImageVector,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = RecoveryGreen,
            modifier = Modifier.size(16.dp),
        )
    }
}
