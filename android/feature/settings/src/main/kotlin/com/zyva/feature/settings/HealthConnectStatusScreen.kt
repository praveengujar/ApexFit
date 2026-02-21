package com.zyva.feature.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zyva.core.designsystem.theme.BackgroundCard
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.PrimaryBlue
import com.zyva.core.designsystem.theme.RecoveryGreen
import com.zyva.core.designsystem.theme.RecoveryRed
import com.zyva.core.designsystem.theme.RecoveryYellow
import com.zyva.core.designsystem.theme.Spacing
import com.zyva.core.designsystem.theme.TextPrimary
import com.zyva.core.designsystem.theme.TextSecondary
import com.zyva.core.designsystem.theme.TextTertiary
import com.zyva.core.healthconnect.HealthConnectAvailability

@Composable
fun HealthConnectStatusScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadHealthConnectStatus()
    }

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
            text = "Health Connect",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.md))

        // Summary card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundCard)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val isAvailable = uiState.healthConnectAvailability == HealthConnectAvailability.AVAILABLE
            Icon(
                imageVector = if (isAvailable) Icons.Filled.FavoriteBorder else Icons.Filled.Close,
                contentDescription = null,
                tint = if (isAvailable) RecoveryGreen else RecoveryRed,
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = if (isAvailable) "Health Connect Available" else "Health Connect Unavailable",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )

            if (isAvailable && uiState.permissionStatuses.isNotEmpty()) {
                val granted = uiState.permissionStatuses.count { it.isGranted }
                val total = uiState.permissionStatuses.size
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "$granted of $total data types authorized",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            if (uiState.healthConnectAvailability == HealthConnectAvailability.NOT_INSTALLED) {
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = "Health Connect needs to be installed or updated.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RecoveryYellow,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // Permission list
        if (uiState.isLoadingPermissions) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.permissionStatuses.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundCard)
                    .padding(Spacing.md),
            ) {
                Text(
                    text = "Data Types",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(Spacing.sm))

                uiState.permissionStatuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if (status.isGranted) Icons.Filled.CheckCircle else Icons.Filled.Help,
                            contentDescription = null,
                            tint = if (status.isGranted) RecoveryGreen else RecoveryYellow,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = status.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = if (status.isGranted) "Authorized" else "Not Determined",
                            fontSize = 11.sp,
                            color = if (status.isGranted) RecoveryGreen else RecoveryYellow,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Spacing.lg))

        // Open Health Connect button
        Button(
            onClick = { openHealthConnect(context) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Open Health Connect",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }
    }
}

private fun openHealthConnect(context: Context) {
    try {
        val intent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Could not open Health Connect
        }
    }
}
