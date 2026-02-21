package com.zyva.shared.domain.usecase

import com.zyva.shared.domain.repository.*
import com.zyva.shared.engine.*
import com.zyva.shared.model.DailyMetric
import com.zyva.shared.model.RecoveryZone
import com.zyva.shared.model.config.ScoringConfig
import com.zyva.shared.platform.currentTimeMillis
import com.zyva.shared.platform.randomUUID

class SyncHealthDataUseCase(
    private val healthDataProvider: HealthDataProvider,
    private val dailyMetricRepo: DailyMetricRepository,
    private val workoutRepo: WorkoutRepository,
    private val sleepRepo: SleepRepository,
    private val userProfileRepo: UserProfileRepository,
    private val baselineRepo: BaselineRepository,
    private val config: ScoringConfig,
) {
    suspend fun syncForDate(dateMillis: Long) {
        val profile = userProfileRepo.getProfile() ?: return

        // Assume dateMillis is start of day
        val endOfDayMillis = dateMillis + 86_400_000L

        // Get or create daily metric
        val existingMetric = dailyMetricRepo.getByDate(dateMillis)
        val metricId = existingMetric?.id ?: randomUUID()

        // --- Fetch all health data ---

        // 1. Vitals
        val restingHR = tryOrNull { healthDataProvider.fetchRestingHeartRate(dateMillis) }
        val respRate = tryOrNull { healthDataProvider.fetchRespiratoryRate(dateMillis) }
        val spo2 = tryOrNull { healthDataProvider.fetchSpO2(dateMillis) }

        // 2. HRV
        val hrvSamples = tryOrNull { healthDataProvider.fetchHRVSamples(dateMillis, endOfDayMillis) }
        val bestHRVValue = hrvSamples?.maxByOrNull { it.second }?.second
        val hrvResult = HRVCalculator.bestHRV(rmssdValue = bestHRVValue)
        val effectiveHRV = HRVCalculator.effectiveHRV(hrvResult)

        // 3. Activity
        val steps = tryOrNull { healthDataProvider.fetchSteps(dateMillis) } ?: 0L
        val activeCalories = tryOrNull { healthDataProvider.fetchActiveCalories(dateMillis) } ?: 0.0
        val vo2Max = tryOrNull { healthDataProvider.fetchVO2Max(dateMillis) }

        // 4. Sleep (look back to previous evening for main sleep)
        val sleepStartMillis = dateMillis - 6 * 3600_000L // 6 PM previous day
        val sleepSessions = tryOrNull { healthDataProvider.fetchSleepSessions(sleepStartMillis, endOfDayMillis) }

        // 5. Workouts
        val exerciseSessions = tryOrNull { healthDataProvider.fetchExerciseSessions(dateMillis, endOfDayMillis) }

        // 6. Heart rate for strain computation
        val heartRateSamples = tryOrNull { healthDataProvider.fetchHeartRateSamples(dateMillis, endOfDayMillis) }

        // --- Process Sleep ---
        val sleepEngine = SleepEngine(config.sleep)
        val baselineHours = profile.sleepBaselineHours
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

        // --- Process Strain ---
        var totalStrain = 0.0
        var peakWorkoutStrain = 0.0
        val maxHR = profile.estimatedMaxHR

        if (exerciseSessions != null) {
            val strainEngine = StrainEngine(maxHR, config.strain, config.heartRateZones)

            for (exercise in exerciseSessions) {
                val existing = workoutRepo.getByHealthConnectUUID(exercise.id)
                if (existing != null) {
                    totalStrain += existing.strainScore
                    peakWorkoutStrain = maxOf(peakWorkoutStrain, existing.strainScore)
                    continue
                }

                val workoutHR = heartRateSamples?.filter { (ts, _) ->
                    ts in exercise.startTimeMillis..exercise.endTimeMillis
                } ?: emptyList()

                val strainResult = strainEngine.computeWorkoutStrain(workoutHR)
                totalStrain += strainResult.strain
                peakWorkoutStrain = maxOf(peakWorkoutStrain, strainResult.strain)
            }
        }

        // --- Process Recovery ---
        val recentHRVValues = dailyMetricRepo.getRecentHRV(28)
        val recentRHRValues = dailyMetricRepo.getRecentRHR(28)

        val hrvBaseline = BaselineEngine.computeBaseline(recentHRVValues)
        val rhrBaseline = BaselineEngine.computeBaseline(recentRHRValues)

        val recentSleepPerf = dailyMetricRepo.getRange(
            startDate = dateMillis - 28L * 86_400_000L,
            endDate = dateMillis,
        ).mapNotNull { it.sleepPerformance }
        val sleepPerfBaseline = BaselineEngine.computeBaseline(recentSleepPerf)

        val respBaseline = baselineRepo.getByType("RESPIRATORY_RATE")?.let {
            BaselineResult(it.mean, it.standardDeviation, it.sampleCount, 28)
        }
        val spo2Baseline = baselineRepo.getByType("SPO2")?.let {
            BaselineResult(it.mean, it.standardDeviation, it.sampleCount, 28)
        }

        val recoveryEngine = RecoveryEngine(config.recovery)
        val recoveryResult = recoveryEngine.computeRecovery(
            input = RecoveryInput(
                hrv = effectiveHRV,
                restingHeartRate = restingHR,
                sleepPerformance = sleepAnalysis.sleepPerformance,
                respiratoryRate = respRate,
                spo2 = spo2,
                skinTemperatureDeviation = null,
            ),
            baselines = RecoveryBaselines(
                hrv = hrvBaseline,
                restingHeartRate = rhrBaseline,
                sleepPerformance = sleepPerfBaseline,
                respiratoryRate = respBaseline,
                spo2 = spo2Baseline,
                skinTemperature = null,
            ),
        )

        // --- Save Daily Metric ---
        val dailyMetric = DailyMetric(
            id = metricId,
            date = dateMillis,
            recoveryScore = recoveryResult.score,
            recoveryZone = recoveryResult.zone,
            strainScore = totalStrain,
            sleepPerformance = sleepAnalysis.sleepPerformance,
            hrvRMSSD = effectiveHRV,
            restingHeartRate = restingHR,
            respiratoryRate = respRate,
            spo2 = spo2,
            steps = steps.toInt(),
            activeCalories = activeCalories,
            vo2Max = vo2Max,
            peakStrain = peakWorkoutStrain,
            workoutCount = exerciseSessions?.size ?: 0,
            sleepDurationHours = sleepAnalysis.totalSleepHours,
            sleepNeedHours = sleepAnalysis.sleepNeedHours,
            sleepDebtHours = sleepAnalysis.sleepDebtHours,
            sleepScore = sleepAnalysis.sleepScore,
            sleepConsistency = sleepAnalysis.sleepConsistency,
            sleepEfficiencyPct = sleepAnalysis.sleepEfficiency,
            restorativeSleepPct = sleepAnalysis.restorativeSleepPct,
            deepSleepPct = sleepAnalysis.deepSleepPct,
            remSleepPct = sleepAnalysis.remSleepPct,
            isComputed = true,
            computedAt = currentTimeMillis(),
            createdAt = existingMetric?.createdAt ?: currentTimeMillis(),
            userProfileId = profile.id,
        )
        dailyMetricRepo.insertOrUpdate(dailyMetric)
    }

    private suspend fun <T> tryOrNull(block: suspend () -> T): T? {
        return try {
            block()
        } catch (_: Exception) {
            null
        }
    }
}
