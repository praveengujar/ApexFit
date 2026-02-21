package com.zyva.feature.onboarding

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.CornerRadius
import com.zyva.core.designsystem.theme.Lavender
import com.zyva.core.designsystem.theme.MetricLarge
import com.zyva.core.designsystem.theme.MinimumTapTarget
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.Teal
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    var logoVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.5f,
        animationSpec = tween(600),
        label = "logo_scale",
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "logo_alpha",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "content_alpha",
    )

    LaunchedEffect(Unit) {
        logoVisible = true
        kotlinx.coroutines.delay(300)
        contentVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(logoScale)
                .alpha(logoAlpha)
                .background(
                    brush = Brush.linearGradient(colors = listOf(PrimaryBlue, Teal)),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Bolt,
                contentDescription = "Zyva",
                tint = Color.White,
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Zyva",
            style = MetricLarge,
            color = TextPrimary,
            modifier = Modifier.alpha(logoAlpha),
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Your Personal Health Performance Platform",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = Spacing.xl)
                .alpha(logoAlpha),
        )

        Spacer(Modifier.height(Spacing.xxl))

        // Feature bullets
        Column(modifier = Modifier.alpha(contentAlpha)) {
            FeatureBullet(
                icon = Icons.Filled.Favorite,
                color = RecoveryGreen,
                title = "Recovery Tracking",
                description = "Know when your body is ready to perform",
            )
            Spacer(Modifier.height(Spacing.lg))
            FeatureBullet(
                icon = Icons.Filled.FitnessCenter,
                color = RecoveryRed,
                title = "Strain Monitoring",
                description = "Optimize your training load day by day",
            )
            Spacer(Modifier.height(Spacing.lg))
            FeatureBullet(
                icon = Icons.Filled.Nightlight,
                color = Lavender,
                title = "Sleep Analysis",
                description = "Deep insights into your sleep quality",
            )
            Spacer(Modifier.height(Spacing.lg))
            FeatureBullet(
                icon = Icons.Filled.Psychology,
                color = Teal,
                title = "AI Coach",
                description = "Personalized guidance based on your data",
            )
        }

        Spacer(Modifier.weight(1f))

        // Get Started button
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(MinimumTapTarget + 8.dp)
                .alpha(contentAlpha),
            shape = RoundedCornerShape(CornerRadius.medium),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        ) {
            Text(
                text = "Get Started",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}

@Composable
private fun FeatureBullet(
    icon: ImageVector,
    color: Color,
    title: String,
    description: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Column {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
                color = TextPrimary,
            )
            Text(
                text = description,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}
