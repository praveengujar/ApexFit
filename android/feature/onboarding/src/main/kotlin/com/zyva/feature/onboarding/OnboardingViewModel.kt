package com.zyva.feature.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.UserProfileEntity
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.background.SyncScheduler
import com.zyva.core.healthconnect.HealthConnectAvailability
import com.zyva.core.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

enum class OnboardingStep {
    WELCOME, SIGN_IN, HEALTH_CONNECT, WEARABLE_SELECTION, PROFILE, JOURNAL_SETUP, COMPLETE
}

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    // Profile fields
    val displayName: String = "",
    val biologicalSex: String = "NOT_SET",
    val preferredUnits: String = "IMPERIAL",
    val heightValue: String = "",
    val weightValue: String = "",
    val dateOfBirthMillis: Long? = null,
    // Health Connect
    val healthConnectAvailability: HealthConnectAvailability = HealthConnectAvailability.NOT_SUPPORTED,
    val healthConnectPermissionsGranted: Boolean = false,
    val isRequestingPermissions: Boolean = false,
    // Journal
    val selectedBehaviorIds: Set<String> = emptySet(),
    val journalBehaviors: List<JournalBehaviorItem> = emptyList(),
    // Wearable
    val selectedWearableDevice: String? = null,
    // Navigation
    val onboardingComplete: Boolean = false,
)

data class JournalBehaviorItem(
    val id: String,
    val name: String,
    val category: String,
    val responseType: String,
    val options: List<String>?,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userProfileRepo: UserProfileRepository,
    private val healthConnectManager: HealthConnectManager,
    private val syncScheduler: SyncScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        checkHealthConnectAvailability()
        loadJournalBehaviors()
    }

    fun advanceTo(step: OnboardingStep) {
        _uiState.update { it.copy(currentStep = step) }
    }

    // --- Profile ---

    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name) }
    }

    fun updateBiologicalSex(sex: String) {
        _uiState.update { it.copy(biologicalSex = sex) }
    }

    fun updatePreferredUnits(units: String) {
        _uiState.update { it.copy(preferredUnits = units) }
    }

    fun updateHeight(value: String) {
        _uiState.update { it.copy(heightValue = value) }
    }

    fun updateWeight(value: String) {
        _uiState.update { it.copy(weightValue = value) }
    }

    fun updateDateOfBirth(millis: Long?) {
        _uiState.update { it.copy(dateOfBirthMillis = millis) }
    }

    // --- Wearable ---

    fun updateWearableDevice(device: String) {
        _uiState.update { it.copy(selectedWearableDevice = device) }
    }

    fun saveWearableDevice() {
        viewModelScope.launch {
            val profile = userProfileRepo.getOrCreateProfile()
            userProfileRepo.updateWearableDevice(profile.id, _uiState.value.selectedWearableDevice)
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            val profile = userProfileRepo.getOrCreateProfile()

            val heightCM = state.heightValue.toDoubleOrNull()?.let { h ->
                if (state.preferredUnits == "IMPERIAL") h * 2.54 else h
            }
            val weightKG = state.weightValue.toDoubleOrNull()?.let { w ->
                if (state.preferredUnits == "IMPERIAL") w / 2.20462 else w
            }

            userProfileRepo.updateProfile(
                profile.copy(
                    displayName = state.displayName.trim(),
                    biologicalSex = state.biologicalSex,
                    preferredUnits = state.preferredUnits,
                    heightCM = heightCM,
                    weightKG = weightKG,
                    dateOfBirth = state.dateOfBirthMillis,
                ),
            )
        }
    }

    // --- Health Connect ---

    private fun checkHealthConnectAvailability() {
        val availability = healthConnectManager.availability
        _uiState.update { it.copy(healthConnectAvailability = availability) }

        if (availability == HealthConnectAvailability.AVAILABLE) {
            viewModelScope.launch {
                val hasPerms = healthConnectManager.hasAllPermissions()
                _uiState.update { it.copy(healthConnectPermissionsGranted = hasPerms) }
            }
        }
    }

    fun onPermissionsResult(granted: Set<String>) {
        val allGranted = HealthConnectManager.REQUIRED_PERMISSIONS.all { it in granted }
        _uiState.update {
            it.copy(
                healthConnectPermissionsGranted = allGranted,
                isRequestingPermissions = false,
            )
        }
    }

    fun setRequestingPermissions(requesting: Boolean) {
        _uiState.update { it.copy(isRequestingPermissions = requesting) }
    }

    fun getPermissionRequestContract() = healthConnectManager.createPermissionRequestContract()

    // --- Journal ---

    private fun loadJournalBehaviors() {
        try {
            val jsonString = context.assets.open("JournalBehaviors.json")
                .bufferedReader().use { reader -> reader.readText() }
            val jsonArray = JSONArray(jsonString)
            val items = (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                val options = if (obj.has("options") && !obj.isNull("options")) {
                    val arr = obj.getJSONArray("options")
                    (0 until arr.length()).map { j -> arr.getString(j) }
                } else {
                    null
                }
                JournalBehaviorItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    responseType = obj.getString("responseType"),
                    options = options,
                )
            }
            _uiState.update { state -> state.copy(journalBehaviors = items) }
        } catch (_: Exception) {
            _uiState.update { state -> state.copy(journalBehaviors = defaultBehaviors()) }
        }
    }

    fun toggleBehavior(id: String) {
        _uiState.update { state ->
            val newSet = state.selectedBehaviorIds.toMutableSet()
            if (id in newSet) newSet.remove(id) else newSet.add(id)
            state.copy(selectedBehaviorIds = newSet)
        }
    }

    fun saveJournalBehaviors() {
        viewModelScope.launch {
            val profile = userProfileRepo.getOrCreateProfile()
            userProfileRepo.updateJournalBehaviors(
                profile.id,
                _uiState.value.selectedBehaviorIds.toList(),
            )
        }
    }

    // --- Complete ---

    fun completeOnboarding() {
        viewModelScope.launch {
            val profile = userProfileRepo.getOrCreateProfile()
            userProfileRepo.markOnboardingComplete(profile.id)
            // Trigger immediate data sync so HomeScreen has data right away
            syncScheduler.scheduleOneTimeSync()
            _uiState.update { it.copy(onboardingComplete = true) }
        }
    }

    private fun defaultBehaviors(): List<JournalBehaviorItem> = listOf(
        JournalBehaviorItem("alcohol", "Alcohol", "Lifestyle", "toggle", null),
        JournalBehaviorItem("caffeine", "Caffeine", "Lifestyle", "numeric", null),
        JournalBehaviorItem("stress_level", "Stress Level", "Lifestyle", "scale", listOf("Low", "Moderate", "High", "Very High")),
        JournalBehaviorItem("hydration", "Hydration", "Nutrition", "numeric", null),
        JournalBehaviorItem("late_meal", "Late Meal", "Nutrition", "toggle", null),
        JournalBehaviorItem("sleep_mask", "Sleep Mask", "Sleep Hygiene", "toggle", null),
        JournalBehaviorItem("ice_bath", "Ice Bath / Cold Plunge", "Recovery Practices", "toggle", null),
        JournalBehaviorItem("stretching", "Stretching / Mobility", "Recovery Practices", "toggle", null),
    )
}
