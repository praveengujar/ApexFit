package com.apexfit.core.domain.usecase

import com.apexfit.core.data.entity.DailyMetricEntity
import com.apexfit.core.data.entity.SleepSessionEntity
import com.apexfit.core.data.entity.SleepStageEntity
import com.apexfit.core.data.entity.WorkoutRecordEntity
import com.apexfit.core.data.repository.BaselineRepository
import com.apexfit.core.data.repository.DailyMetricRepository
import com.apexfit.core.data.repository.SleepRepository
import com.apexfit.core.data.repository.UserProfileRepository
import com.apexfit.core.data.repository.WorkoutRepository
import com.apexfit.core.engine.BaselineEngine
import com.apexfit.core.engine.BaselineResult
import com.apexfit.core.engine.HRVCalculator
import com.apexfit.core.engine.RecoveryBaselines
import com.apexfit.core.engine.RecoveryEngine
import com.apexfit.core.engine.RecoveryInput
import com.apexfit.core.engine.SleepConsistencyInput
import com.apexfit.core.engine.SleepEngine
import com.apexfit.core.engine.SleepSessionData
import com.apexfit.core.engine.SleepStageData
import com.apexfit.core.engine.StrainEngine
import com.apexfit.core.healthconnect.HealthConnectQueryService
import com.apexfit.core.model.config.ScoringConfig
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncHealthDataUseCase @Inject constructor(
    private val queryService: HealthConnectQueryService,
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
    private val sleepRepo: SleepRepository,
    private val userProfileRepo: UserProfileRepository,
    private val baselineRepo: BaselineRepository,
    private val config: ScoringConfig,
) {
    suspend fun syncForDate(date: LocalDate = LocalDate.now()) {
        val profile = userProfileRepo.getProfile() ?: return
        val zone = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zone).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()
        val dateEpochMillis = startOfDay.toEpochMilli()

        // Get or create daily metric
        val existingMetric = dailyMetricRepo.getByDate(dateEpochMillis)
        val metricId = existingMetric?.id ?: UUID.randomUUID().toString()

        // --- Fetch all health data in parallel-safe order ---

        // 1. Vitals
        val restingHR = tryOrNull { queryService.fetchRestingHeartRate(date) }
        val respRate = tryOrNull { queryService.fetchRespiratoryRate(date) }
        val spo2 = tryOrNull { queryService.fetchSpO2(date) }

        // 2. HRV
        val hrvSamples = tryOrNull { queryService.fetchHRVSamples(startOfDay, endOfDay) }
        val bestHRVValue = hrvSamples?.maxByOrNull { it.second }?.second
        val hrvResult = HRVCalculator.bestHRV(rmssdValue = bestHRVValue)
        val effectiveHRV = HRVCalculator.effectiveHRV(hrvResult)

        // 3. Activity
        val steps = tryOrNull { queryService.fetchSteps(date) } ?: 0L
        val activeCalories = tryOrNull { queryService.fetchActiveCalories(date) } ?: 0.0
        val vo2Max = tryOrNull { queryService.fetchVO2Max(date) }

        // 4. Sleep (look back to previous evening for main sleep)
        val sleepStart = date.minusDays(1).atTime(18, 0).atZone(zone).toInstant()
        val sleepSessions = tryOrNull { queryService.fetchSleepSessions(sleepStart, endOfDay) }

        // 5. Workouts
        val exerciseSessions = tryOrNull { queryService.fetchExerciseSessions(startOfDay, endOfDay) }

        // 6. Heart rate for strain computation
        val heartRateSamples = tryOrNull { queryService.fetchHeartRateSamples(startOfDay, endOfDay) }

        // --- Process Sleep ---
        val sleepEngine = SleepEngine(config.sleep)
        val baselineHours = profile.sleepBaselineHours ?: config.sleep.defaults.baselineHours
        val pastWeekSleepHours = dailyMetricRepo.getRecentSleepHours(7)
        val pastWeekSleepNeeds = dailyMetricRepo.getRecentSleepNeeds(7)

        val sleepSessionDataList = sleepSessions?.map { session ->
            SleepSessionData(
                startDateMillis = session.startTimeMillis,
                endDateMillis = session.endTimeMillis,
                totalSleepMinutes = session.totalSleepMinutes,
                timeInBedMinutes = session.timeInBedMinutes,
                lightMinutes = session.lightMinutes,
                deepMinutes = session.deepMinutes,
                remMinutes = session.remMinutes,
                awakeMinutes = session.awakeMinutes,
                awakenings = session.awakenings,
                sleepOnsetLatencyMinutes = null,
                sleepEfficiency = session.sleepEfficiency,
                stages = session.stages.map { stage ->
                    SleepStageData(
                        type = stage.type,
                        startDateMillis = stage.startTimeMillis,
                        endDateMillis = stage.endTimeMillis,
                        durationMinutes = stage.durationMinutes,
                    )
                },
            )
        } ?: emptyList()

        val recentBedtimes = sleepRepo.getRecentBedtimes(4).map { millis ->
            SleepEngine.minutesSinceMidnight(millis)
        }
        val recentWakeTimes = sleepRepo.getRecentWakeTimes(4).map { millis ->
            SleepEngine.minutesSinceMidnight(millis)
        }

        val sleepAnalysis = sleepEngine.analyze(
            sessions = sleepSessionDataList,
            baselineSleepHours = baselineHours,
            todayStrain = existingMetric?.strainScore ?: 0.0,
            pastWeekSleepHours = pastWeekSleepHours,
            pastWeekSleepNeeds = pastWeekSleepNeeds,
            consistencyInput = SleepConsistencyInput(
                recentBedtimeMinutes = recentBedtimes,
                recentWakeTimeMinutes = recentWakeTimes,
            ),
        )

        // Store sleep sessions
        sleepSessions?.forEach { session ->
            val isMain = sleepAnalysis.mainSleep?.startDateMillis == session.startTimeMillis
            val sessionEntity = SleepSessionEntity(
                id = session.id,
                dailyMetricId = metricId,
                startDate = session.startTimeMillis,
                endDate = session.endTimeMillis,
                isMainSleep = isMain,
                isNap = !isMain,
                totalSleepMinutes = session.totalSleepMinutes,
                timeInBedMinutes = session.timeInBedMinutes,
                lightMinutes = session.lightMinutes,
                deepMinutes = session.deepMinutes,
                remMinutes = session.remMinutes,
                awakeMinutes = session.awakeMinutes,
                awakenings = session.awakenings,
                sleepEfficiency = session.sleepEfficiency,
                sleepPerformance = if (isMain) sleepAnalysis.sleepPerformance else null,
                sleepNeedHours = if (isMain) sleepAnalysis.sleepNeedHours else null,
                healthConnectUUID = session.id,
            )
            val stageEntities = session.stages.map { stage ->
                SleepStageEntity(
                    id = UUID.randomUUID().toString(),
                    sleepSessionId = session.id,
                    stageType = stage.type.uppercase(),
                    startDate = stage.startTimeMillis,
                    endDate = stage.endTimeMillis,
                    durationMinutes = stage.durationMinutes,
                )
            }
            tryOrNull { sleepRepo.insertSessionWithStages(sessionEntity, stageEntities) }
        }

        // --- Process Strain ---
        var totalStrain = 0.0
        var peakWorkoutStrain = 0.0
        val maxHR = profile.maxHeartRate ?: 190

        if (exerciseSessions != null) {
            val strainEngine = StrainEngine(maxHR, config.strain, config.heartRateZones)

            for (exercise in exerciseSessions) {
                // Skip if already stored
                val existing = workoutRepo.getByHealthConnectUUID(exercise.id)
                if (existing != null) {
                    totalStrain += existing.strainScore ?: 0.0
                    peakWorkoutStrain = maxOf(peakWorkoutStrain, existing.strainScore ?: 0.0)
                    continue
                }

                // Get HR samples during this workout
                val workoutHR = heartRateSamples?.filter { (ts, _) ->
                    ts in exercise.startTimeMillis..exercise.endTimeMillis
                } ?: emptyList()

                val strainResult = strainEngine.computeWorkoutStrain(workoutHR)
                val workoutType = HealthConnectQueryService.mapExerciseType(exercise.exerciseType)

                val workoutEntity = WorkoutRecordEntity(
                    id = UUID.randomUUID().toString(),
                    dailyMetricId = metricId,
                    workoutType = workoutType,
                    workoutName = exercise.title,
                    startDate = exercise.startTimeMillis,
                    endDate = exercise.endTimeMillis,
                    durationMinutes = exercise.durationMinutes,
                    strainScore = strainResult.strain,
                    averageHeartRate = if (workoutHR.isNotEmpty()) {
                        workoutHR.map { it.second }.average()
                    } else null,
                    maxHeartRate = workoutHR.maxByOrNull { it.second }?.second,
                    zone1Minutes = strainResult.zone1Minutes,
                    zone2Minutes = strainResult.zone2Minutes,
                    zone3Minutes = strainResult.zone3Minutes,
                    zone4Minutes = strainResult.zone4Minutes,
                    zone5Minutes = strainResult.zone5Minutes,
                    healthConnectUUID = exercise.id,
                )
                tryOrNull { workoutRepo.insert(workoutEntity) }

                totalStrain += strainResult.strain
                peakWorkoutStrain = maxOf(peakWorkoutStrain, strainResult.strain)
            }
        }

        // --- Process Recovery ---
        val recentHRVValues = dailyMetricRepo.getRecentHRV(28)
        val recentRHRValues = dailyMetricRepo.getRecentRHR(28)

        val hrvBaseline = BaselineEngine.computeBaseline(recentHRVValues)
        val rhrBaseline = BaselineEngine.computeBaseline(recentRHRValues)

        val recoveryEngine = RecoveryEngine(config.recovery)
        val recoveryResult = recoveryEngine.computeRecovery(
            input = RecoveryInput(
                hrv = effectiveHRV,
                restingHeartRate = restingHR,
                sleepPerformance = sleepAnalysis.sleepPerformance,
                respiratoryRate = respRate,
                spo2 = spo2,
                skinTemperatureDeviation = null, // Not available on Android
            ),
            baselines = RecoveryBaselines(
                hrv = hrvBaseline,
                restingHeartRate = rhrBaseline,
                sleepPerformance = buildSleepPerformanceBaseline(),
                respiratoryRate = buildVitalBaseline("respiratoryRate"),
                spo2 = buildVitalBaseline("spo2"),
                skinTemperature = null,
            ),
        )

        // --- Save Daily Metric ---
        val dailyMetric = DailyMetricEntity(
            id = metricId,
            userProfileId = profile.id,
            date = dateEpochMillis,
            recoveryScore = recoveryResult.score,
            recoveryZone = recoveryResult.zone.name,
            strainScore = totalStrain,
            sleepPerformance = sleepAnalysis.sleepPerformance,
            sleepScore = sleepAnalysis.sleepScore,
            sleepConsistency = sleepAnalysis.sleepConsistency,
            sleepEfficiency = sleepAnalysis.sleepEfficiency,
            restorativeSleepPercentage = sleepAnalysis.restorativeSleepPct,
            deepSleepPercentage = sleepAnalysis.deepSleepPct,
            remSleepPercentage = sleepAnalysis.remSleepPct,
            hrvRMSSD = effectiveHRV,
            restingHeartRate = restingHR,
            respiratoryRate = respRate,
            spo2 = spo2,
            steps = steps.toInt(),
            activeCalories = activeCalories,
            vo2Max = vo2Max,
            peakWorkoutStrain = peakWorkoutStrain,
            workoutCount = exerciseSessions?.size ?: 0,
            totalSleepHours = sleepAnalysis.totalSleepHours,
            sleepDebtHours = sleepAnalysis.sleepDebtHours,
            sleepNeedHours = sleepAnalysis.sleepNeedHours,
            createdAt = existingMetric?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        dailyMetricRepo.insertOrUpdate(dailyMetric)
    }

    private suspend fun buildSleepPerformanceBaseline(): BaselineResult? {
        val recentValues = dailyMetricRepo.getRange(
            startDate = LocalDate.now().minusDays(28).atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
            endDate = Instant.now().toEpochMilli(),
        ).mapNotNull { it.sleepPerformance }
        return BaselineEngine.computeBaseline(recentValues)
    }

    private suspend fun buildVitalBaseline(metricType: String): BaselineResult? {
        val entity = baselineRepo.getByType(metricType) ?: return null
        return BaselineResult(
            mean = entity.mean,
            standardDeviation = entity.standardDeviation,
            sampleCount = entity.sampleCount,
            windowDays = 28,
        )
    }

    private suspend fun <T> tryOrNull(block: suspend () -> T): T? {
        return try {
            block()
        } catch (_: Exception) {
            null
        }
    }
}
