package com.zyva.core.domain.usecase

import com.zyva.core.data.repository.DailyMetricRepository
import com.zyva.core.data.repository.SleepRepository
import com.zyva.core.data.repository.UserProfileRepository
import com.zyva.core.engine.SleepEngine
import com.zyva.core.engine.SleepGoalType
import com.zyva.core.engine.SleepPlannerEngine
import com.zyva.core.model.SleepGoal
import com.zyva.core.model.config.ScoringConfig
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

data class SleepPlanResult(
    val recommendedBedtimeMillis: Long,
    val targetWakeTimeMillis: Long,
    val sleepNeedHours: Double,
    val sleepDebtHours: Double,
    val requiredSleepDuration: Double,
    val goal: SleepGoal,
)

@Singleton
class ComputeSleepPlanUseCase @Inject constructor(
    private val userProfileRepo: UserProfileRepository,
    private val dailyMetricRepo: DailyMetricRepository,
    private val sleepRepo: SleepRepository,
    private val config: ScoringConfig,
) {
    suspend fun compute(
        wakeTimeMillis: Long,
        goal: SleepGoal = SleepGoal.PERFORM,
    ): SleepPlanResult? {
        val profile = userProfileRepo.getProfile() ?: return null
        val baselineHours = profile.sleepBaselineHours ?: config.sleep.defaults.baselineHours

        val today = LocalDate.now()
        val todayMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val todayMetric = dailyMetricRepo.getByDate(todayMillis)

        val pastWeekSleepHours = dailyMetricRepo.getRecentSleepHours(7)
        val pastWeekSleepNeeds = dailyMetricRepo.getRecentSleepNeeds(7)
        val todayStrain = todayMetric?.strainScore ?: 0.0

        val sleepEngine = SleepEngine(config.sleep)
        val sleepDebt = sleepEngine.computeSleepDebt(pastWeekSleepHours, pastWeekSleepNeeds)
        val sleepNeed = sleepEngine.computeSleepNeed(
            baselineHours = baselineHours,
            todayStrain = todayStrain,
            sleepDebtHours = sleepDebt,
            napHoursToday = 0.0,
        )

        val goalType = when (goal) {
            SleepGoal.PEAK -> SleepGoalType.PEAK
            SleepGoal.PERFORM -> SleepGoalType.PERFORM
            SleepGoal.GET_BY -> SleepGoalType.GET_BY
        }

        val planner = SleepPlannerEngine(config.sleepPlanner)
        val planResult = planner.plan(
            sleepNeedHours = sleepNeed,
            goal = goalType,
            desiredWakeTimeMillis = wakeTimeMillis,
            estimatedOnsetLatencyMinutes = config.sleep.defaults.onsetLatencyMinutes,
            baselineNeed = baselineHours,
        )

        return SleepPlanResult(
            recommendedBedtimeMillis = planResult.recommendedBedtimeMillis,
            targetWakeTimeMillis = wakeTimeMillis,
            sleepNeedHours = sleepNeed,
            sleepDebtHours = sleepDebt,
            requiredSleepDuration = planResult.requiredSleepDuration,
            goal = goal,
        )
    }
}
