package com.apexfit.feature.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.repository.UserProfileRepository
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundPrimary
import com.apexfit.core.designsystem.theme.BackgroundSecondary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.Lavender
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.RecoveryGreen
import com.apexfit.core.designsystem.theme.RecoveryRed
import com.apexfit.core.designsystem.theme.RecoveryYellow
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.healthconnect.HealthConnectAvailability
import com.apexfit.core.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// -- ViewModel --

data class DeviceHealthUiState(
    val selectedDevice: String? = null,
    val healthConnectAvailability: HealthConnectAvailability = HealthConnectAvailability.NOT_SUPPORTED,
    val permissionsGrantedCount: Int = 0,
    val permissionsTotalCount: Int = 0,
    val profileId: String = "",
)

@HiltViewModel
class DeviceHealthViewModel @Inject constructor(
    private val userProfileRepo: UserProfileRepository,
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceHealthUiState())
    val uiState: StateFlow<DeviceHealthUiState> = _uiState.asStateFlow()

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            val profile = userProfileRepo.getProfile()
            val availability = healthConnectManager.availability
            var grantedCount = 0
            val totalCount = HealthConnectManager.REQUIRED_PERMISSIONS.size
            if (availability == HealthConnectAvailability.AVAILABLE) {
                grantedCount = try {
                    val granted = healthConnectManager.getGrantedPermissions()
                    HealthConnectManager.REQUIRED_PERMISSIONS.count { it in granted }
                } catch (_: Exception) {
                    0
                }
            }
            _uiState.update {
                it.copy(
                    selectedDevice = profile?.wearableDevice,
                    healthConnectAvailability = availability,
                    permissionsGrantedCount = grantedCount,
                    permissionsTotalCount = totalCount,
                    profileId = profile?.id ?: "",
                )
            }
        }
    }

    fun selectDevice(device: String) {
        _uiState.update { it.copy(selectedDevice = device) }
        viewModelScope.launch {
            val profileId = _uiState.value.profileId
            if (profileId.isNotEmpty()) {
                userProfileRepo.updateWearableDevice(profileId, device)
            }
        }
    }
}

// -- Device options --

private data class DeviceOption(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
)

private val deviceOptions = listOf(
    DeviceOption("PIXEL_WATCH", "Pixel Watch", Icons.Filled.Watch, PrimaryBlue),
    DeviceOption("GARMIN", "Garmin", Icons.Filled.Watch, RecoveryGreen),
    DeviceOption("AMAZFIT", "Amazfit", Icons.Filled.Watch, RecoveryYellow),
    DeviceOption("OURA_RING", "Oura Ring", Icons.Filled.Circle, Lavender),
    DeviceOption("OTHER", "Other", Icons.Filled.Devices, TextSecondary),
)

// -- Screen --

@Composable
fun DeviceHealthScreen(
    viewModel: DeviceHealthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
            text = "Device & Health",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.lg))

        // Section 1: Your Wearable
        Text(
            text = "Your Wearable",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.sm))

        deviceOptions.forEach { option ->
            val isSelected = uiState.selectedDevice == option.id
            DeviceOptionCard(
                option = option,
                isSelected = isSelected,
                onClick = { viewModel.selectDevice(option.id) },
            )
            Spacer(Modifier.height(Spacing.sm))
        }

        Spacer(Modifier.height(Spacing.lg))

        // Section 2: Health Data Access
        Text(
            text = "Health Data Access",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(Spacing.sm))

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
                imageVector = if (isAvailable) Icons.Filled.CheckCircle else Icons.Filled.Close,
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

            if (isAvailable) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "${uiState.permissionsGrantedCount} of ${uiState.permissionsTotalCount} permissions granted",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))

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

@Composable
private fun DeviceOptionCard(
    option: DeviceOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(CornerRadius.medium)
    val background = if (isSelected) PrimaryBlue.copy(alpha = 0.15f) else BackgroundSecondary
    val borderModifier = if (isSelected) {
        Modifier.border(width = 1.5.dp, color = PrimaryBlue, shape = shape)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier)
            .background(color = background, shape = shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = option.color,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(Spacing.md))
        Text(
            text = option.name,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
        )
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
