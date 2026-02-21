package com.zyva.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.NotificationPreferenceEntity
import com.zyva.core.data.repository.NotificationPreferenceRepository
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.engine.HeartRateZoneCalculator
import com.zyva.core.healthconnect.HealthConnectAvailability
import com.zyva.core.healthconnect.HealthConnectManager
import com.zyva.core.model.MaxHRSource
import com.zyva.core.model.NotificationType
import com.zyva.core.model.UnitSystem
import com.zyva.core.model.config.ScoringConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthConnectPermissionStatus(
    val permissionName: String,
    val displayName: String,
    val isGranted: Boolean,
)

data class SettingsUiState(
    // Profile
    val maxHeartRate: Int = 190,
    val maxHeartRateSource: String = "AGE_ESTIMATE",
    val sleepBaselineHours: Double = 7.5,
    val preferredUnits: String = "METRIC",
    val dateOfBirth: Long? = null,
    val profileId: String = "",

    // Notifications
    val notificationPrefs: List<NotificationPreferenceEntity> = emptyList(),
    val bedtimeHour: Int = 22,
    val bedtimeMinute: Int = 0,

    // Health Connect
    val healthConnectAvailability: HealthConnectAvailability = HealthConnectAvailability.AVAILABLE,
    val permissionStatuses: List<HealthConnectPermissionStatus> = emptyList(),
    val isLoadingPermissions: Boolean = false,

    // Max HR settings
    val manualHRInput: String = "",
    val hrZones: List<Triple<Int, Int, Int>> = emptyList(), // zone, lower, upper
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userProfileRepo: UserProfileRepository,
    private val notifRepo: NotificationPreferenceRepository,
    private val healthConnectManager: HealthConnectManager,
    private val config: ScoringConfig,
) : ViewModel() {

    private val _extraState = MutableStateFlow(
        SettingsUiState(),
    )

    val uiState: StateFlow<SettingsUiState> = combine(
        userProfileRepo.observeProfile(),
        notifRepo.observeAll(),
        _extraState,
    ) { profile, notifPrefs, extra ->
        val maxHR = profile?.maxHeartRate ?: 190
        val zoneCalc = HeartRateZoneCalculator(maxHR, config.heartRateZones)

        extra.copy(
            maxHeartRate = maxHR,
            maxHeartRateSource = profile?.maxHeartRateSource ?: "AGE_ESTIMATE",
            sleepBaselineHours = profile?.sleepBaselineHours ?: 7.5,
            preferredUnits = profile?.preferredUnits ?: "METRIC",
            dateOfBirth = profile?.dateOfBirth,
            profileId = profile?.id ?: "",
            notificationPrefs = notifPrefs,
            hrZones = zoneCalc.zoneBoundaries(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    init {
        initializeNotificationPrefs()
    }

    private fun initializeNotificationPrefs() {
        viewModelScope.launch {
            NotificationType.entries.forEach { type ->
                val existing = notifRepo.getByType(type.name)
                if (existing == null) {
                    notifRepo.insert(
                        NotificationPreferenceEntity(
                            notificationType = type.name,
                            isEnabled = true,
                            customTimeHour = if (type == NotificationType.BEDTIME_REMINDER) 22 else null,
                            customTimeMinute = if (type == NotificationType.BEDTIME_REMINDER) 0 else null,
                        ),
                    )
                }
            }
        }
    }

    fun updateMaxHR(maxHR: Int) {
        viewModelScope.launch {
            val profileId = uiState.value.profileId
            if (profileId.isNotEmpty()) {
                userProfileRepo.updateMaxHeartRate(profileId, maxHR, MaxHRSource.USER_INPUT.name)
            }
        }
    }

    fun resetMaxHRToAge() {
        viewModelScope.launch {
            val state = uiState.value
            if (state.profileId.isNotEmpty() && state.dateOfBirth != null) {
                val age = ((System.currentTimeMillis() - state.dateOfBirth) / (365.25 * 24 * 3600 * 1000)).toInt()
                val estimatedMaxHR = (220 - age).coerceIn(120, 220)
                userProfileRepo.updateMaxHeartRate(state.profileId, estimatedMaxHR, MaxHRSource.AGE_ESTIMATE.name)
            }
        }
    }

    fun setManualHRInput(text: String) {
        _extraState.update { it.copy(manualHRInput = text) }
    }

    fun updateSleepGoal(hours: Double) {
        viewModelScope.launch {
            val profileId = uiState.value.profileId
            if (profileId.isNotEmpty()) {
                userProfileRepo.updateSleepBaseline(profileId, hours)
            }
        }
    }

    fun toggleNotification(type: NotificationType, enabled: Boolean) {
        viewModelScope.launch {
            notifRepo.setEnabled(type.name, enabled)
        }
    }

    fun updateBedtimeTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val existing = notifRepo.getByType(NotificationType.BEDTIME_REMINDER.name)
            if (existing != null) {
                notifRepo.insert(existing.copy(customTimeHour = hour, customTimeMinute = minute))
            }
            _extraState.update { it.copy(bedtimeHour = hour, bedtimeMinute = minute) }
        }
    }

    fun updateUnits(units: UnitSystem) {
        viewModelScope.launch {
            val profileId = uiState.value.profileId
            if (profileId.isNotEmpty()) {
                val profile = userProfileRepo.getProfile()
                if (profile != null) {
                    userProfileRepo.updateProfile(profile.copy(preferredUnits = units.name))
                }
            }
        }
    }

    fun loadHealthConnectStatus() {
        viewModelScope.launch {
            _extraState.update { it.copy(isLoadingPermissions = true) }
            val availability = healthConnectManager.availability
            val statuses = if (availability == HealthConnectAvailability.AVAILABLE) {
                val granted = try {
                    healthConnectManager.getGrantedPermissions()
                } catch (_: Exception) {
                    emptySet()
                }
                HealthConnectManager.REQUIRED_PERMISSIONS.map { perm ->
                    val displayName = perm
                        .removePrefix("android.permission.health.READ_")
                        .replace("_", " ")
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                    HealthConnectPermissionStatus(
                        permissionName = perm,
                        displayName = displayName,
                        isGranted = perm in granted,
                    )
                }.distinctBy { it.displayName }
            } else {
                emptyList()
            }
            _extraState.update {
                it.copy(
                    healthConnectAvailability = availability,
                    permissionStatuses = statuses,
                    isLoadingPermissions = false,
                )
            }
        }
    }
}
