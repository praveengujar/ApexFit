package com.apexfit.feature.strain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.WorkoutRepository
import com.apexfit.core.model.RecoveryZone
import com.apexfit.core.model.StrainZone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class StrainUiState(
    val todayMetric: DailyMetricEntity? = null,
    val weekMetrics: List<DailyMetricEntity> = emptyList(),
    val todayWorkouts: List<WorkoutRecordEntity> = emptyList(),
)

@HiltViewModel
class StrainViewModel @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<WorkoutRecordEntity>>(emptyList())

    val uiState: StateFlow<StrainUiState> = dailyMetricRepo.observeRecent(30)
        .map { metrics ->
            val todayMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val yesterdayMillis = LocalDate.now().minusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val today = metrics.find { it.date == todayMillis }
                ?: metrics.find { it.date == yesterdayMillis }

            val weekCutoff = LocalDate.now().minusDays(7)
                .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val week = metrics.filter { it.date >= weekCutoff }.sortedBy { it.date }

            StrainUiState(
                todayMetric = today,
                weekMetrics = week,
                todayWorkouts = _workouts.value,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StrainUiState())

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            val todayMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
            val metric = dailyMetricRepo.getByDate(todayMillis)
            if (metric != null) {
                _workouts.value = workoutRepo.getByDailyMetric(metric.id)
            }
        }
    }

    fun strainInsight(strainScore: Double, recoveryZone: RecoveryZone): String {
        val target = strainTarget(recoveryZone)
        return when {
            strainScore < target.first -> "Your optimal activity level today is low. Consider taking a rest day for recovery, or try a light workout that will help you achieve a Day Strain under ${formatStrain(target.second)}."
            strainScore <= target.second -> "You're on track! Your current strain of ${formatStrain(strainScore)} is within your optimal range of ${formatStrain(target.first)}\u2013${formatStrain(target.second)} for today."
            else -> "Your strain is higher than recommended for your recovery level. Consider winding down to allow your body adequate recovery time."
        }
    }

    private fun strainTarget(zone: RecoveryZone): Pair<Double, Double> = when (zone) {
        RecoveryZone.GREEN -> 14.0 to 18.0
        RecoveryZone.YELLOW -> 8.0 to 14.0
        RecoveryZone.RED -> 0.0 to 8.0
    }

    private fun formatStrain(value: Double): String = "%.1f".format(value)
}
