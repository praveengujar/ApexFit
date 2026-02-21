package com.zyva.core.healthconnect

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectQueryService @Inject constructor(
    private val manager: HealthConnectManager,
) {
    private val client: HealthConnectClient get() = manager.healthConnectClient

    private fun dayTimeRange(date: LocalDate): Pair<Instant, Instant> {
        val zone = ZoneId.systemDefault()
        return date.atStartOfDay(zone).toInstant() to date.plusDays(1).atStartOfDay(zone).toInstant()
    }

    // MARK: - Heart Rate Samples

    suspend fun fetchHeartRateSamples(
        startTime: Instant,
        endTime: Instant,
    ): List<Pair<Long, Double>> {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.flatMap { record ->
            record.samples.map { sample ->
                sample.time.toEpochMilli() to sample.beatsPerMinute.toDouble()
            }
        }
    }

    // MARK: - HRV (RMSSD direct)

    suspend fun fetchHRVSamples(
        startTime: Instant,
        endTime: Instant,
    ): List<Pair<Long, Double>> {
        val request = ReadRecordsRequest(
            recordType = HeartRateVariabilityRmssdRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.map { record ->
            record.time.toEpochMilli() to record.heartRateVariabilityMillis
        }
    }

    // MARK: - Resting Heart Rate

    suspend fun fetchRestingHeartRate(date: LocalDate): Double? {
        val (startTime, endTime) = dayTimeRange(date)

        val request = ReadRecordsRequest(
            recordType = RestingHeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.lastOrNull()?.beatsPerMinute?.toDouble()
    }

    // MARK: - Respiratory Rate

    suspend fun fetchRespiratoryRate(date: LocalDate): Double? {
        val (startTime, endTime) = dayTimeRange(date)

        val request = ReadRecordsRequest(
            recordType = RespiratoryRateRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.lastOrNull()?.rate
    }

    // MARK: - SpO2

    suspend fun fetchSpO2(date: LocalDate): Double? {
        val (startTime, endTime) = dayTimeRange(date)

        val request = ReadRecordsRequest(
            recordType = OxygenSaturationRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.lastOrNull()?.percentage?.value
    }

    // MARK: - Sleep Sessions

    suspend fun fetchSleepSessions(
        startTime: Instant,
        endTime: Instant,
    ): List<HealthConnectSleepSession> {
        val request = ReadRecordsRequest(
            recordType = SleepSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.map { record ->
            parseSleepSession(record)
        }
    }

    private fun parseSleepSession(record: SleepSessionRecord): HealthConnectSleepSession {
        var lightMinutes = 0.0
        var deepMinutes = 0.0
        var remMinutes = 0.0
        var awakeMinutes = 0.0
        var awakenings = 0
        val stages = mutableListOf<HealthConnectSleepStage>()

        for (stage in record.stages) {
            val durationMinutes = java.time.Duration.between(
                stage.startTime, stage.endTime,
            ).toMillis() / 60_000.0

            val stageType = when (stage.stage) {
                SleepSessionRecord.STAGE_TYPE_LIGHT -> "light"
                SleepSessionRecord.STAGE_TYPE_DEEP -> "deep"
                SleepSessionRecord.STAGE_TYPE_REM -> "rem"
                SleepSessionRecord.STAGE_TYPE_AWAKE -> {
                    awakenings++
                    "awake"
                }
                SleepSessionRecord.STAGE_TYPE_SLEEPING -> "light" // generic sleep -> light
                SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> "awake"
                SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED -> {
                    awakenings++
                    "awake"
                }
                else -> "light"
            }

            when (stageType) {
                "light" -> lightMinutes += durationMinutes
                "deep" -> deepMinutes += durationMinutes
                "rem" -> remMinutes += durationMinutes
                "awake" -> awakeMinutes += durationMinutes
            }

            stages.add(
                HealthConnectSleepStage(
                    type = stageType,
                    startTimeMillis = stage.startTime.toEpochMilli(),
                    endTimeMillis = stage.endTime.toEpochMilli(),
                    durationMinutes = durationMinutes,
                ),
            )
        }

        val totalSleepMinutes = lightMinutes + deepMinutes + remMinutes
        val timeInBedMinutes = totalSleepMinutes + awakeMinutes
        val sleepEfficiency = if (timeInBedMinutes > 0) {
            (totalSleepMinutes / timeInBedMinutes) * 100.0
        } else {
            0.0
        }

        return HealthConnectSleepSession(
            id = record.metadata.id,
            startTimeMillis = record.startTime.toEpochMilli(),
            endTimeMillis = record.endTime.toEpochMilli(),
            totalSleepMinutes = totalSleepMinutes,
            timeInBedMinutes = timeInBedMinutes,
            lightMinutes = lightMinutes,
            deepMinutes = deepMinutes,
            remMinutes = remMinutes,
            awakeMinutes = awakeMinutes,
            awakenings = awakenings,
            sleepEfficiency = sleepEfficiency,
            stages = stages,
        )
    }

    // MARK: - Exercise Sessions (Workouts)

    suspend fun fetchExerciseSessions(
        startTime: Instant,
        endTime: Instant,
    ): List<HealthConnectExerciseSession> {
        val request = ReadRecordsRequest(
            recordType = ExerciseSessionRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.map { record ->
            HealthConnectExerciseSession(
                id = record.metadata.id,
                exerciseType = record.exerciseType,
                title = record.title?.toString(),
                startTimeMillis = record.startTime.toEpochMilli(),
                endTimeMillis = record.endTime.toEpochMilli(),
                durationMinutes = java.time.Duration.between(
                    record.startTime, record.endTime,
                ).toMillis() / 60_000.0,
            )
        }
    }

    // MARK: - Steps (Daily Aggregate)

    suspend fun fetchSteps(date: LocalDate): Long {
        val (startTime, endTime) = dayTimeRange(date)

        val request = AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.aggregate(request)
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    // MARK: - Active Calories (Daily Aggregate)

    suspend fun fetchActiveCalories(date: LocalDate): Double {
        val (startTime, endTime) = dayTimeRange(date)

        val request = AggregateRequest(
            metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.aggregate(request)
        return response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]
            ?.inKilocalories ?: 0.0
    }

    // MARK: - VO2 Max (Most Recent in Last 30 Days)

    suspend fun fetchVO2Max(date: LocalDate): Double? {
        val zone = ZoneId.systemDefault()
        val startTime = date.minusDays(30).atStartOfDay(zone).toInstant()
        val endTime = date.plusDays(1).atStartOfDay(zone).toInstant()

        val request = ReadRecordsRequest(
            recordType = Vo2MaxRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
        )
        val response = client.readRecords(request)
        return response.records.maxByOrNull { it.time }
            ?.vo2MillilitersPerMinuteKilogram
    }

    // MARK: - Changes Token

    suspend fun getChangesToken(recordTypes: Set<kotlin.reflect.KClass<out androidx.health.connect.client.records.Record>>): String {
        val request = ChangesTokenRequest(recordTypes)
        return client.getChangesToken(request)
    }

    // MARK: - Workout Type Mapping

    companion object {
        fun mapExerciseType(exerciseType: Int): String {
            return when (exerciseType) {
                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "running"
                ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL -> "running"
                ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "cycling"
                ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY -> "cycling"
                ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER -> "swimming"
                ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL -> "swimming"
                ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "walking"
                ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "hiking"
                ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "yoga"
                ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING -> "strengthTraining"
                ExerciseSessionRecord.EXERCISE_TYPE_CALISTHENICS -> "strengthTraining"
                ExerciseSessionRecord.EXERCISE_TYPE_ROWING -> "rowing"
                ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> "rowing"
                ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "elliptical"
                ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING -> "stairClimbing"
                ExerciseSessionRecord.EXERCISE_TYPE_STAIR_CLIMBING_MACHINE -> "stairClimbing"
                ExerciseSessionRecord.EXERCISE_TYPE_DANCING -> "dance"
                ExerciseSessionRecord.EXERCISE_TYPE_PILATES -> "pilates"
                ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING -> "hiit"
                ExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS -> "martialArts"
                ExerciseSessionRecord.EXERCISE_TYPE_BOXING -> "boxing"
                ExerciseSessionRecord.EXERCISE_TYPE_SOCCER -> "soccer"
                ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> "basketball"
                ExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> "tennis"
                ExerciseSessionRecord.EXERCISE_TYPE_GOLF -> "golf"
                ExerciseSessionRecord.EXERCISE_TYPE_SKIING -> "downhillSkiing"
                ExerciseSessionRecord.EXERCISE_TYPE_SNOWBOARDING -> "snowboarding"
                ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING -> "climbing"
                ExerciseSessionRecord.EXERCISE_TYPE_SURFING -> "surfing"
                else -> "other"
            }
        }
    }
}
