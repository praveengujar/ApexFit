package com.apexfit.feature.activity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.UserProfileRepository
import com.apexfit.core.data.repository.WorkoutRepository
import com.apexfit.core.engine.HeartRateZoneCalculator
import com.apexfit.core.engine.MuscularLoadEngine
import com.apexfit.core.engine.StrainEngine
import com.apexfit.core.healthconnect.HealthConnectQueryService
import com.apexfit.core.model.config.ScoringConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

data class WorkoutTypeItem(
    val id: String,
    val displayName: String,
    val icon: ImageVector,
    val engineType: String, // maps to HealthConnectQueryService type strings
    val supportsDistance: Boolean = false,
)

val workoutTypes = listOf(
    WorkoutTypeItem("run", "Run", Icons.AutoMirrored.Filled.DirectionsRun, "running", supportsDistance = true),
    WorkoutTypeItem("cycle", "Cycle", Icons.AutoMirrored.Filled.DirectionsBike, "cycling", supportsDistance = true),
    WorkoutTypeItem("swim", "Swim", Icons.Filled.Pool, "swimming", supportsDistance = true),
    WorkoutTypeItem("strength", "Strength", Icons.Filled.FitnessCenter, "traditionalStrengthTraining"),
    WorkoutTypeItem("hiit", "HIIT", Icons.Filled.LocalFireDepartment, "highIntensityIntervalTraining"),
    WorkoutTypeItem("yoga", "Yoga", Icons.Filled.SelfImprovement, "yoga"),
    WorkoutTypeItem("walk", "Walk", Icons.AutoMirrored.Filled.DirectionsWalk, "walking", supportsDistance = true),
    WorkoutTypeItem("hike", "Hike", Icons.Filled.Hiking, "hiking", supportsDistance = true),
    WorkoutTypeItem("other", "Other", Icons.Filled.SportsScore, "other"),
)

data class ActivityUiState(
    val selectedType: WorkoutTypeItem = workoutTypes[0],
    val startDate: Long = System.currentTimeMillis() - 3600_000, // 1 hour ago
    val endDate: Long = System.currentTimeMillis(),
    val rpe: Int = 5,
    val activityName: String = "",
    val caloriesText: String = "",
    val distanceText: String = "",
    val showOptionalFields: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,

    // Live workout state
    val isLiveActive: Boolean = false,
    val isPaused: Boolean = false,
    val countdown: Int? = null,
    val liveElapsedSeconds: Long = 0,
    val liveCurrentHR: Int = 0,
    val liveStrain: Double = 0.0,
    val liveCalories: Double = 0.0,
    val liveDistance: Double = 0.0,
    val liveZone1Minutes: Double = 0.0,
    val liveZone2Minutes: Double = 0.0,
    val liveZone3Minutes: Double = 0.0,
    val liveZone4Minutes: Double = 0.0,
    val liveZone5Minutes: Double = 0.0,
    val liveCurrentZone: Int = 0,
    val liveAvgHR: Double = 0.0,
    val liveMaxHR: Double = 0.0,
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val workoutRepo: WorkoutRepository,
    private val dailyMetricRepo: DailyMetricRepository,
    private val userProfileRepo: UserProfileRepository,
    private val queryService: HealthConnectQueryService,
    private val config: ScoringConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private var liveTimerJob: Job? = null
    private var livePollingJob: Job? = null
    private var liveStartTimeMillis: Long = 0L
    private var liveHRSamples: MutableList<Pair<Long, Double>> = mutableListOf()
    private var strainEngine: StrainEngine? = null
    private var zoneCalculator: HeartRateZoneCalculator? = null

    fun selectType(type: WorkoutTypeItem) {
        _uiState.update {
            it.copy(selectedType = type, activityName = type.displayName)
        }
    }

    fun setStartDate(millis: Long) {
        _uiState.update { it.copy(startDate = millis) }
    }

    fun setEndDate(millis: Long) {
        _uiState.update { it.copy(endDate = millis) }
    }

    fun setRpe(value: Int) {
        _uiState.update { it.copy(rpe = value.coerceIn(1, 10)) }
    }

    fun setActivityName(name: String) {
        _uiState.update { it.copy(activityName = name) }
    }

    fun setCalories(text: String) {
        _uiState.update { it.copy(caloriesText = text) }
    }

    fun setDistance(text: String) {
        _uiState.update { it.copy(distanceText = text) }
    }

    fun toggleOptionalFields() {
        _uiState.update { it.copy(showOptionalFields = !it.showOptionalFields) }
    }

    fun saveManualActivity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val state = _uiState.value
                val profile = userProfileRepo.getProfile() ?: return@launch
                val maxHR = profile.maxHeartRate

                val startInstant = Instant.ofEpochMilli(state.startDate)
                val endInstant = Instant.ofEpochMilli(state.endDate)
                val durationMinutes = (state.endDate - state.startDate) / 60_000.0

                if (durationMinutes <= 0) return@launch

                val engine = StrainEngine(maxHR, config.strain, config.heartRateZones)

                // Try to get real HR data from Health Connect
                val realHR = try {
                    queryService.fetchHeartRateSamples(startInstant, endInstant)
                } catch (_: Exception) {
                    emptyList()
                }

                val strainResult = if (realHR.isNotEmpty()) {
                    engine.computeWorkoutStrain(realHR)
                } else {
                    // Synthesize from RPE
                    val hrPercent = estimatedHRPercent(state.rpe)
                    val estimatedBPM = hrPercent * maxHR
                    val sampleCount = (durationMinutes * 2).toInt().coerceAtLeast(1)
                    val intervalMs = (state.endDate - state.startDate) / sampleCount
                    val syntheticSamples = (0 until sampleCount).map { i ->
                        (state.startDate + i * intervalMs) to estimatedBPM
                    }
                    engine.computeWorkoutStrain(syntheticSamples)
                }

                // Compute muscular load for strength workouts
                val muscularLoad = if (MuscularLoadEngine.isStrengthWorkout(state.selectedType.engineType)) {
                    val avgHR = if (realHR.isNotEmpty()) {
                        realHR.map { it.second }.average()
                    } else {
                        estimatedHRPercent(state.rpe) * maxHR
                    }
                    val peakHR = if (realHR.isNotEmpty()) {
                        realHR.maxOf { it.second }
                    } else {
                        avgHR * 1.1
                    }
                    MuscularLoadEngine.computeLoad(
                        workoutType = state.selectedType.engineType,
                        durationMinutes = durationMinutes,
                        averageHeartRate = avgHR,
                        maxHeartRateDuringWorkout = peakHR,
                        userMaxHeartRate = maxHR.toDouble(),
                        bodyWeightKG = profile.weightKG,
                        rpe = state.rpe,
                    ).load
                } else null

                val distanceMeters = state.distanceText.toDoubleOrNull()?.let { it * 1609.34 }
                val calories = state.caloriesText.toDoubleOrNull()

                // Create workout record
                val workoutId = UUID.randomUUID().toString()
                val workoutDate = Instant.ofEpochMilli(state.startDate)
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                val dateEpochMillis = workoutDate.atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()

                val existingMetric = dailyMetricRepo.getByDate(dateEpochMillis)
                val metricId = existingMetric?.id ?: UUID.randomUUID().toString()

                if (existingMetric == null) {
                    dailyMetricRepo.insertOrUpdate(
                        DailyMetricEntity(
                            id = metricId,
                            userProfileId = profile.id,
                            date = dateEpochMillis,
                        ),
                    )
                }

                val avgHRForRecord = if (realHR.isNotEmpty()) realHR.map { it.second }.average() else null
                val maxHRForRecord = if (realHR.isNotEmpty()) realHR.maxOf { it.second } else null

                val workout = WorkoutRecordEntity(
                    id = workoutId,
                    dailyMetricId = metricId,
                    workoutType = state.selectedType.engineType,
                    workoutName = state.activityName.ifBlank { state.selectedType.displayName },
                    startDate = state.startDate,
                    endDate = state.endDate,
                    durationMinutes = durationMinutes,
                    strainScore = strainResult.strain,
                    averageHeartRate = avgHRForRecord,
                    maxHeartRate = maxHRForRecord,
                    caloriesBurned = calories,
                    distanceMeters = distanceMeters,
                    zone1Minutes = strainResult.zone1Minutes,
                    zone2Minutes = strainResult.zone2Minutes,
                    zone3Minutes = strainResult.zone3Minutes,
                    zone4Minutes = strainResult.zone4Minutes,
                    zone5Minutes = strainResult.zone5Minutes,
                    muscularLoad = muscularLoad,
                    isStrengthWorkout = MuscularLoadEngine.isStrengthWorkout(state.selectedType.engineType),
                )
                workoutRepo.insert(workout)

                // Update daily metric strain totals
                val allWorkouts = workoutRepo.getByDailyMetric(metricId)
                val totalStrain = allWorkouts.sumOf { it.strainScore ?: 0.0 }
                val peakStrain = allWorkouts.maxOfOrNull { it.strainScore ?: 0.0 } ?: 0.0

                val updatedMetric = (existingMetric ?: dailyMetricRepo.getByDate(dateEpochMillis))
                if (updatedMetric != null) {
                    dailyMetricRepo.insertOrUpdate(
                        updatedMetric.copy(
                            strainScore = totalStrain,
                            peakWorkoutStrain = peakStrain,
                            workoutCount = allWorkouts.size,
                            updatedAt = System.currentTimeMillis(),
                        ),
                    )
                }

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun startCountdown(type: WorkoutTypeItem) {
        _uiState.update { it.copy(selectedType = type, countdown = 3) }
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.update { it.copy(countdown = i) }
                delay(1000)
            }
            _uiState.update { it.copy(countdown = null) }
            beginLiveWorkout()
        }
    }

    private fun beginLiveWorkout() {
        viewModelScope.launch {
            val profile = userProfileRepo.getProfile() ?: return@launch
            val maxHR = profile.maxHeartRate
            strainEngine = StrainEngine(maxHR, config.strain, config.heartRateZones)
            zoneCalculator = HeartRateZoneCalculator(maxHR, config.heartRateZones)
            liveStartTimeMillis = System.currentTimeMillis()
            liveHRSamples.clear()

            _uiState.update {
                it.copy(
                    isLiveActive = true,
                    isPaused = false,
                    liveElapsedSeconds = 0,
                    liveCurrentHR = 0,
                    liveStrain = 0.0,
                    liveCalories = 0.0,
                    liveDistance = 0.0,
                    liveZone1Minutes = 0.0,
                    liveZone2Minutes = 0.0,
                    liveZone3Minutes = 0.0,
                    liveZone4Minutes = 0.0,
                    liveZone5Minutes = 0.0,
                    liveCurrentZone = 0,
                    liveAvgHR = 0.0,
                    liveMaxHR = 0.0,
                )
            }

            // Start elapsed timer
            liveTimerJob = viewModelScope.launch {
                while (isActive) {
                    delay(1000)
                    if (!_uiState.value.isPaused) {
                        val elapsed = (System.currentTimeMillis() - liveStartTimeMillis) / 1000
                        _uiState.update { it.copy(liveElapsedSeconds = elapsed) }
                    }
                }
            }

            // Start HR polling
            livePollingJob = viewModelScope.launch {
                while (isActive) {
                    delay(5000) // Poll every 5 seconds
                    if (!_uiState.value.isPaused) {
                        pollHeartRate()
                    }
                }
            }
        }
    }

    private suspend fun pollHeartRate() {
        try {
            val now = Instant.now()
            val fiveSecsAgo = now.minusSeconds(10)
            val samples = queryService.fetchHeartRateSamples(fiveSecsAgo, now)
            if (samples.isNotEmpty()) {
                liveHRSamples.addAll(samples)
                val currentHR = samples.last().second.toInt()
                val allBPMs = liveHRSamples.map { it.second }
                val avgHR = allBPMs.average()
                val maxHR = allBPMs.max()
                val currentZone = zoneCalculator?.zoneNumber(currentHR.toDouble()) ?: 0

                // Recompute strain from all accumulated samples
                val strainResult = strainEngine?.computeWorkoutStrain(liveHRSamples)

                _uiState.update {
                    it.copy(
                        liveCurrentHR = currentHR,
                        liveAvgHR = avgHR,
                        liveMaxHR = maxHR,
                        liveCurrentZone = currentZone,
                        liveStrain = strainResult?.strain ?: 0.0,
                        liveZone1Minutes = strainResult?.zone1Minutes ?: 0.0,
                        liveZone2Minutes = strainResult?.zone2Minutes ?: 0.0,
                        liveZone3Minutes = strainResult?.zone3Minutes ?: 0.0,
                        liveZone4Minutes = strainResult?.zone4Minutes ?: 0.0,
                        liveZone5Minutes = strainResult?.zone5Minutes ?: 0.0,
                    )
                }
            }
        } catch (_: Exception) {
            // Health Connect may not be responsive, continue polling
        }
    }

    fun pauseWorkout() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeWorkout() {
        _uiState.update { it.copy(isPaused = false) }
    }

    fun endLiveWorkout() {
        liveTimerJob?.cancel()
        livePollingJob?.cancel()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val state = _uiState.value
                val profile = userProfileRepo.getProfile() ?: return@launch
                val endTime = System.currentTimeMillis()
                val durationMinutes = (endTime - liveStartTimeMillis) / 60_000.0

                val strainResult = strainEngine?.computeWorkoutStrain(liveHRSamples)

                val muscularLoad = if (MuscularLoadEngine.isStrengthWorkout(state.selectedType.engineType) && state.liveAvgHR > 0) {
                    MuscularLoadEngine.computeLoad(
                        workoutType = state.selectedType.engineType,
                        durationMinutes = durationMinutes,
                        averageHeartRate = state.liveAvgHR,
                        maxHeartRateDuringWorkout = state.liveMaxHR,
                        userMaxHeartRate = profile.maxHeartRate.toDouble(),
                        bodyWeightKG = profile.weightKG,
                    ).load
                } else null

                val workoutId = UUID.randomUUID().toString()
                val workoutDate = LocalDate.now()
                val dateEpochMillis = workoutDate.atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()

                val existingMetric = dailyMetricRepo.getByDate(dateEpochMillis)
                val metricId = existingMetric?.id ?: UUID.randomUUID().toString()

                if (existingMetric == null) {
                    dailyMetricRepo.insertOrUpdate(
                        DailyMetricEntity(
                            id = metricId,
                            userProfileId = profile.id,
                            date = dateEpochMillis,
                        ),
                    )
                }

                val workout = WorkoutRecordEntity(
                    id = workoutId,
                    dailyMetricId = metricId,
                    workoutType = state.selectedType.engineType,
                    workoutName = state.selectedType.displayName,
                    startDate = liveStartTimeMillis,
                    endDate = endTime,
                    durationMinutes = durationMinutes,
                    strainScore = strainResult?.strain,
                    averageHeartRate = if (state.liveAvgHR > 0) state.liveAvgHR else null,
                    maxHeartRate = if (state.liveMaxHR > 0) state.liveMaxHR else null,
                    zone1Minutes = strainResult?.zone1Minutes ?: 0.0,
                    zone2Minutes = strainResult?.zone2Minutes ?: 0.0,
                    zone3Minutes = strainResult?.zone3Minutes ?: 0.0,
                    zone4Minutes = strainResult?.zone4Minutes ?: 0.0,
                    zone5Minutes = strainResult?.zone5Minutes ?: 0.0,
                    muscularLoad = muscularLoad,
                    isStrengthWorkout = MuscularLoadEngine.isStrengthWorkout(state.selectedType.engineType),
                )
                workoutRepo.insert(workout)

                // Update daily metric
                val allWorkouts = workoutRepo.getByDailyMetric(metricId)
                val totalStrain = allWorkouts.sumOf { it.strainScore ?: 0.0 }
                val peakStrain = allWorkouts.maxOfOrNull { it.strainScore ?: 0.0 } ?: 0.0
                val updatedMetric = dailyMetricRepo.getByDate(dateEpochMillis)
                if (updatedMetric != null) {
                    dailyMetricRepo.insertOrUpdate(
                        updatedMetric.copy(
                            strainScore = totalStrain,
                            peakWorkoutStrain = peakStrain,
                            workoutCount = allWorkouts.size,
                            updatedAt = System.currentTimeMillis(),
                        ),
                    )
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isLiveActive = false,
                        saveSuccess = true,
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, isLiveActive = false) }
            }
        }
    }

    fun resetState() {
        _uiState.value = ActivityUiState()
    }

    fun formatElapsedTime(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }

    companion object {
        fun estimatedHRPercent(rpe: Int): Double = when (rpe) {
            in 1..3 -> 0.55
            in 4..5 -> 0.65
            in 6..7 -> 0.75
            in 8..9 -> 0.85
            10 -> 0.95
            else -> 0.65
        }
    }
}
