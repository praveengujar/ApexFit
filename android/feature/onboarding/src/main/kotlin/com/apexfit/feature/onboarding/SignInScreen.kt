package com.apexfit.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.MinimumTapTarget
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary

@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(64.dp),
        )

        Spacer(Modifier.height(Spacing.md))

        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Sign in to sync your data across devices and enable cloud backup.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Spacing.lg),
        )

        Spacer(Modifier.weight(1f))

        // Sign in with Google (placeholder)
        Button(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(MinimumTapTarget + 8.dp),
            shape = RoundedCornerShape(CornerRadius.medium),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        ) {
            Text(
                text = "Sign In with Google",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
        }

        Spacer(Modifier.height(Spacing.md))

        TextButton(onClick = onSkip) {
            Text(
                text = "Skip for now",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Spacer(Modifier.height(Spacing.xl))
    }
}
