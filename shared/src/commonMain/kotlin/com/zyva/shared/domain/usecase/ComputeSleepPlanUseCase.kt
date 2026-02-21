package com.zyva.shared.domain.usecase

import com.zyva.shared.domain.repository.DailyMetricRepository
import com.zyva.shared.domain.repository.SleepRepository
import com.zyva.shared.domain.repository.UserProfileRepository
import com.zyva.shared.engine.SleepEngine
import com.zyva.shared.engine.SleepGoalType
import com.zyva.shared.engine.SleepPlannerEngine
import com.zyva.shared.model.SleepGoal
import com.zyva.shared.model.config.ScoringConfig

data class SleepPlanResult(
    val recommendedBedtimeMillis: Long,
    val targetWakeTimeMillis: Long,
    val sleepNeedHours: Double,
    val sleepDebtHours: Double,
    val requiredSleepDuration: Double,
    val goal: SleepGoal,
)

class ComputeSleepPlanUseCase(
    private val userProfileRepo: UserProfileRepository,
    private val dailyMetricRepo: DailyMetricRepository,
    private val sleepRepo: SleepRepository,
    private val config: ScoringConfig,
) {
    suspend fun compute(
        todayMillis: Long,
        wakeTimeMillis: Long,
        goal: SleepGoal = SleepGoal.PERFORM,
    ): SleepPlanResult? {
        val profile = userProfileRepo.getProfile() ?: return null
        val baselineHours = profile.sleepBaselineHours

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
