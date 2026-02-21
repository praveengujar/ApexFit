package com.zyva.feature.journal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.entity.JournalEntryEntity
import com.zyva.core.data.entity.JournalResponseEntity
import com.zyva.core.data.repository.DailyMetricRepository
import com.zyva.core.data.repository.JournalRepository
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.engine.CorrelationResult
import com.zyva.core.engine.StatisticalEngine
import com.zyva.core.model.JournalResponseType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

data class JournalBehavior(
    val id: String,
    val name: String,
    val category: String,
    val responseType: JournalResponseType,
    val options: List<String>,
)

sealed class ResponseValue {
    data class Toggle(val value: Boolean) : ResponseValue()
    data class Numeric(val value: Double) : ResponseValue()
    data class Scale(val value: String) : ResponseValue()
}

enum class TargetMetric(val displayName: String) {
    RECOVERY("Recovery"),
    STRAIN("Strain"),
    SLEEP("Sleep"),
}

data class JournalUiState(
    val selectedBehaviors: List<JournalBehavior> = emptyList(),
    val allBehaviors: List<JournalBehavior> = emptyList(),
    val selectedBehaviorIds: Set<String> = emptySet(),
    val responses: Map<String, ResponseValue> = emptyMap(),
    val todayEntry: JournalEntryEntity? = null,
    val recentEntries: List<JournalEntryEntity> = emptyList(),
    val isSaving: Boolean = false,
    val showSavedConfirmation: Boolean = false,
    // Impact analysis
    val impactResults: List<CorrelationResult> = emptyList(),
    val selectedTargetMetric: TargetMetric = TargetMetric.RECOVERY,
    val isAnalyzing: Boolean = false,
    val completedCount: Int = 0,
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journalRepo: JournalRepository,
    private val userProfileRepo: UserProfileRepository,
    private val dailyMetricRepo: DailyMetricRepository,
) : ViewModel() {

    private val _responses = MutableStateFlow<Map<String, ResponseValue>>(emptyMap())
    private val _isSaving = MutableStateFlow(false)
    private val _showSaved = MutableStateFlow(false)
    private val _impactResults = MutableStateFlow<List<CorrelationResult>>(emptyList())
    private val _selectedMetric = MutableStateFlow(TargetMetric.RECOVERY)
    private val _isAnalyzing = MutableStateFlow(false)
    private val _completedCount = MutableStateFlow(0)
    private val _selectedBehaviorIds = MutableStateFlow<Set<String>>(emptySet())

    private val allBehaviors: List<JournalBehavior> = loadBehaviors()

    val uiState: StateFlow<JournalUiState> = combine(
        journalRepo.observeRecentEntries(60),
        _responses,
        _isSaving,
        _showSaved,
    ) { entries, responses, saving, saved ->
        val todayMillis = todayMillis()
        val todayEntry = entries.find { it.date == todayMillis }
        val selectedIds = _selectedBehaviorIds.value
        val selected = allBehaviors.filter { it.id in selectedIds }

        JournalUiState(
            selectedBehaviors = selected,
            allBehaviors = allBehaviors,
            selectedBehaviorIds = selectedIds,
            responses = responses,
            todayEntry = todayEntry,
            recentEntries = entries,
            isSaving = saving,
            showSavedConfirmation = saved,
            impactResults = _impactResults.value,
            selectedTargetMetric = _selectedMetric.value,
            isAnalyzing = _isAnalyzing.value,
            completedCount = _completedCount.value,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JournalUiState())

    init {
        loadProfile()
        loadTodayResponses()
        loadCompletedCount()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val profile = userProfileRepo.getProfile()
            if (profile != null) {
                val ids = try {
                    val json = JSONArray(profile.selectedJournalBehaviorIDs ?: "[]")
                    (0 until json.length()).map { json.getString(it) }.toSet()
                } catch (_: Exception) {
                    emptySet()
                }
                _selectedBehaviorIds.value = ids
            }
        }
    }

    private fun loadTodayResponses() {
        viewModelScope.launch {
            val entry = journalRepo.getEntryByDate(todayMillis()) ?: return@launch
            val responses = journalRepo.getResponses(entry.id)
            val map = mutableMapOf<String, ResponseValue>()
            for (r in responses) {
                val type = try {
                    JournalResponseType.valueOf(r.responseType)
                } catch (_: Exception) {
                    continue
                }
                map[r.behaviorID] = when (type) {
                    JournalResponseType.TOGGLE -> ResponseValue.Toggle(r.toggleValue ?: false)
                    JournalResponseType.NUMERIC -> ResponseValue.Numeric(r.numericValue ?: 0.0)
                    JournalResponseType.SCALE -> ResponseValue.Scale(
                        r.scaleValue?.let { allBehaviors.find { b -> b.id == r.behaviorID }?.options?.getOrNull(it) }
                            ?: allBehaviors.find { b -> b.id == r.behaviorID }?.options?.firstOrNull()
                            ?: ""
                    )
                }
            }
            _responses.value = map
        }
    }

    private fun loadCompletedCount() {
        viewModelScope.launch {
            _completedCount.value = journalRepo.getCompletedCount()
        }
    }

    fun updateResponse(behaviorId: String, value: ResponseValue) {
        _responses.value = _responses.value + (behaviorId to value)
    }

    fun saveEntry() {
        viewModelScope.launch {
            _isSaving.value = true
            val todayMillis = todayMillis()
            val existingEntry = journalRepo.getEntryByDate(todayMillis)
            val profile = userProfileRepo.getProfile()

            val entryId = existingEntry?.id ?: UUID.randomUUID().toString()
            val entry = JournalEntryEntity(
                id = entryId,
                userProfileId = profile?.id ?: "",
                date = todayMillis,
                completedAt = System.currentTimeMillis(),
                isComplete = true,
            )

            val responseEntities = _selectedBehaviorIds.value.map { behaviorId ->
                val behavior = allBehaviors.find { it.id == behaviorId }
                val value = _responses.value[behaviorId]
                JournalResponseEntity(
                    id = UUID.randomUUID().toString(),
                    journalEntryId = entryId,
                    behaviorID = behaviorId,
                    behaviorName = behavior?.name ?: behaviorId,
                    category = behavior?.category ?: "",
                    responseType = (behavior?.responseType ?: JournalResponseType.TOGGLE).name,
                    toggleValue = (value as? ResponseValue.Toggle)?.value,
                    numericValue = (value as? ResponseValue.Numeric)?.value,
                    scaleValue = (value as? ResponseValue.Scale)?.let { sv ->
                        behavior?.options?.indexOf(sv.value)?.takeIf { it >= 0 }
                    },
                )
            }

            journalRepo.insertEntryWithResponses(entry, responseEntities)
            _isSaving.value = false
            _showSaved.value = true
            loadCompletedCount()

            kotlinx.coroutines.delay(2000)
            _showSaved.value = false
        }
    }

    fun updateSelectedBehaviors(ids: Set<String>) {
        _selectedBehaviorIds.value = ids
        viewModelScope.launch {
            val profile = userProfileRepo.getProfile() ?: return@launch
            userProfileRepo.updateJournalBehaviors(
                profile.id,
                ids.toList(),
            )
        }
    }

    fun setTargetMetric(metric: TargetMetric) {
        _selectedMetric.value = metric
        computeCorrelations()
    }

    fun computeCorrelations() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            val entries = journalRepo.getCompletedEntries()
            if (entries.size < 14) {
                _impactResults.value = emptyList()
                _isAnalyzing.value = false
                return@launch
            }

            val metrics = dailyMetricRepo.getRange(
                entries.minOf { it.date },
                entries.maxOf { it.date },
            )
            val metricsByDate = metrics.associateBy { it.date }
            val metric = _selectedMetric.value
            val higherIsBetter = metric != TargetMetric.STRAIN

            val results = mutableListOf<CorrelationResult>()

            for (behavior in allBehaviors.filter { it.id in _selectedBehaviorIds.value }) {
                val withBehavior = mutableListOf<Double>()
                val withoutBehavior = mutableListOf<Double>()

                for (entry in entries) {
                    val dailyMetric = metricsByDate[entry.date] ?: continue
                    val metricValue = extractMetricValue(dailyMetric, metric) ?: continue
                    val responses = journalRepo.getResponses(entry.id)
                    val response = responses.find { it.behaviorID == behavior.id }

                    val hasBehavior = when (behavior.responseType) {
                        JournalResponseType.TOGGLE -> response?.toggleValue == true
                        JournalResponseType.NUMERIC -> (response?.numericValue ?: 0.0) > 0
                        JournalResponseType.SCALE -> (response?.scaleValue ?: 0) > 0
                    }

                    if (hasBehavior) {
                        withBehavior.add(metricValue)
                    } else {
                        withoutBehavior.add(metricValue)
                    }
                }

                val result = StatisticalEngine.analyzeCorrelation(
                    behaviorName = behavior.id,
                    metricName = metric.displayName,
                    withBehavior = withBehavior,
                    withoutBehavior = withoutBehavior,
                    higherIsBetter = higherIsBetter,
                )
                if (result != null) results.add(result)
            }

            _impactResults.value = results.sortedByDescending { kotlin.math.abs(it.effectSize) }
            _isAnalyzing.value = false
        }
    }

    private fun extractMetricValue(metric: DailyMetricEntity, target: TargetMetric): Double? {
        return when (target) {
            TargetMetric.RECOVERY -> metric.recoveryScore
            TargetMetric.STRAIN -> metric.strainScore
            TargetMetric.SLEEP -> metric.sleepPerformance
        }
    }

    fun defaultValue(behavior: JournalBehavior): ResponseValue {
        return when (behavior.responseType) {
            JournalResponseType.TOGGLE -> ResponseValue.Toggle(false)
            JournalResponseType.NUMERIC -> ResponseValue.Numeric(0.0)
            JournalResponseType.SCALE -> ResponseValue.Scale(behavior.options.firstOrNull() ?: "")
        }
    }

    fun behaviorDisplayName(id: String): String {
        return allBehaviors.find { it.id == id }?.name ?: id
    }

    fun categoryColor(category: String): String {
        return when (category.lowercase()) {
            "nutrition" -> "green"
            "lifestyle" -> "yellow"
            "sleep hygiene" -> "purple"
            "recovery practices" -> "teal"
            else -> "blue"
        }
    }

    private fun loadBehaviors(): List<JournalBehavior> {
        return try {
            val json = context.assets.open("JournalBehaviors.json").bufferedReader().readText()
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                val options = obj.getJSONArray("options")
                JournalBehavior(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    responseType = try {
                        JournalResponseType.valueOf(obj.getString("responseType").uppercase())
                    } catch (_: Exception) {
                        JournalResponseType.TOGGLE
                    },
                    options = (0 until options.length()).map { options.getString(it) },
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun todayMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
