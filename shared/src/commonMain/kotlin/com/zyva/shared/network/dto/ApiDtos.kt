package com.zyva.shared.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// MARK: - User

@Serializable
data class UserProfileUpdateDto(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("biological_sex") val biologicalSex: String? = null,
    @SerialName("height_cm") val heightCM: Double? = null,
    @SerialName("weight_kg") val weightKG: Double? = null,
    @SerialName("max_heart_rate") val maxHeartRate: Int? = null,
    @SerialName("sleep_baseline_hours") val sleepBaselineHours: Double? = null,
    @SerialName("preferred_units") val preferredUnits: String? = null,
)

@Serializable
data class UserProfileResponseDto(
    val id: String,
    @SerialName("firebase_uid") val firebaseUid: String,
    @SerialName("display_name") val displayName: String,
    val email: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("biological_sex") val biologicalSex: String,
    @SerialName("created_at") val createdAt: String,
)

// MARK: - Metrics Sync

@Serializable
data class MetricsSyncRequestDto(
    val date: String,
    @SerialName("recovery_score") val recoveryScore: Double? = null,
    @SerialName("recovery_zone") val recoveryZone: String? = null,
    @SerialName("strain_score") val strainScore: Double,
    @SerialName("sleep_performance") val sleepPerformance: Double? = null,
    @SerialName("hrv_rmssd") val hrvRmssd: Double? = null,
    @SerialName("hrv_sdnn") val hrvSdnn: Double? = null,
    @SerialName("resting_heart_rate") val restingHeartRate: Double? = null,
    @SerialName("respiratory_rate") val respiratoryRate: Double? = null,
    val spo2: Double? = null,
    val steps: Int,
    @SerialName("active_calories") val activeCalories: Double,
    @SerialName("vo2_max") val vo2Max: Double? = null,
    @SerialName("sleep_duration_hours") val sleepDurationHours: Double? = null,
    @SerialName("sleep_need_hours") val sleepNeedHours: Double? = null,
    val workouts: List<WorkoutSyncDataDto>,
)

@Serializable
data class WorkoutSyncDataDto(
    @SerialName("workout_type") val workoutType: String,
    @SerialName("workout_name") val workoutName: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("strain_score") val strainScore: Double,
    @SerialName("average_heart_rate") val averageHeartRate: Double? = null,
    @SerialName("max_heart_rate") val maxHeartRate: Double? = null,
    @SerialName("active_calories") val activeCalories: Double,
    @SerialName("zone1_minutes") val zone1Minutes: Double,
    @SerialName("zone2_minutes") val zone2Minutes: Double,
    @SerialName("zone3_minutes") val zone3Minutes: Double,
    @SerialName("zone4_minutes") val zone4Minutes: Double,
    @SerialName("zone5_minutes") val zone5Minutes: Double,
)

@Serializable
data class WorkoutSyncRequestDto(
    val workouts: List<WorkoutSyncDataDto>,
)

@Serializable
data class DailyMetricResponseDto(
    val date: String,
    @SerialName("recovery_score") val recoveryScore: Double? = null,
    @SerialName("strain_score") val strainScore: Double,
    @SerialName("sleep_performance") val sleepPerformance: Double? = null,
    @SerialName("hrv_rmssd") val hrvRmssd: Double? = null,
    @SerialName("resting_heart_rate") val restingHeartRate: Double? = null,
    val steps: Int,
    @SerialName("active_calories") val activeCalories: Double,
)

@Serializable
data class MetricsListResponseDto(
    val metrics: List<DailyMetricResponseDto>,
    val pagination: PaginationInfoDto,
)

// MARK: - Journal

@Serializable
data class JournalSubmissionDto(
    val date: String,
    val responses: List<JournalResponseDataDto>,
)

@Serializable
data class JournalResponseDataDto(
    @SerialName("behavior_id") val behaviorId: String,
    @SerialName("behavior_name") val behaviorName: String,
    val category: String,
    @SerialName("response_type") val responseType: String,
    @SerialName("toggle_value") val toggleValue: Boolean? = null,
    @SerialName("numeric_value") val numericValue: Double? = null,
    @SerialName("scale_value") val scaleValue: String? = null,
)

@Serializable
data class JournalImpactResponseDto(
    val impacts: List<BehaviorImpactDto>,
)

@Serializable
data class BehaviorImpactDto(
    @SerialName("behavior_name") val behaviorName: String,
    @SerialName("metric_name") val metricName: String,
    @SerialName("effect_size") val effectSize: Double,
    @SerialName("is_significant") val isSignificant: Boolean,
    val direction: String,
    @SerialName("sample_size") val sampleSize: Int,
)

// MARK: - Coach

@Serializable
data class CoachMessageRequestDto(
    val message: String,
    @SerialName("screen_context") val screenContext: String? = null,
    @SerialName("conversation_id") val conversationId: String? = null,
    @SerialName("include_data_window") val includeDataWindow: String? = null,
)

@Serializable
data class CoachMessageResponseDto(
    val response: String,
    @SerialName("data_citations") val dataCitations: List<DataCitationDto>? = null,
    @SerialName("follow_up_suggestions") val followUpSuggestions: List<String>? = null,
    val confidence: Double? = null,
)

@Serializable
data class DataCitationDto(
    val metric: String,
    val value: Double? = null,
    val baseline: Double? = null,
    val delta: String? = null,
)

// MARK: - Teams

@Serializable
data class LeaderboardResponseDto(
    @SerialName("team_id") val teamId: String,
    @SerialName("team_name") val teamName: String,
    val entries: List<LeaderboardEntryDto>,
)

@Serializable
data class LeaderboardEntryDto(
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("recovery_score") val recoveryScore: Double? = null,
    @SerialName("strain_score") val strainScore: Double? = null,
    val rank: Int,
)

// MARK: - Notifications

@Serializable
data class DeviceTokenRegistrationDto(
    val token: String,
    val platform: String,
)

// MARK: - Common

@Serializable
data class PaginationInfoDto(
    val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("has_more") val hasMore: Boolean,
)

@Serializable
data class ErrorResponseDto(
    val error: String,
    val message: String,
    @SerialName("status_code") val statusCode: Int,
)
