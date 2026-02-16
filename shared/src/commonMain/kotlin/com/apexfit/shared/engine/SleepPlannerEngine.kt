package com.apexfit.shared.engine

import com.apexfit.shared.model.config.SleepPlannerConfig

data class SleepPlannerResult(
    val sleepNeedHours: Double,
    val requiredSleepDuration: Double,
    val recommendedBedtimeMillis: Long,
    val expectedWakeTimeMillis: Long,
    val goal: SleepGoalType,
    val baselineNeed: Double,
    val strainSupplement: Double,
    val debtRepayment: Double,
    val napCredit: Double,
)

enum class SleepGoalType(val label: String, val description: String) {
    PEAK("Peak", "Full sleep need for maximum recovery"),
    PERFORM("Perform", "Solid sleep for a good recovery day"),
    GET_BY("Get By", "Minimum viable sleep — reduced recovery");

    fun multiplier(config: SleepPlannerConfig): Double = when (this) {
        PEAK -> config.goalMultipliers.peak
        PERFORM -> config.goalMultipliers.perform
        GET_BY -> config.goalMultipliers.getBy
    }
}

class SleepPlannerEngine(private val config: SleepPlannerConfig) {

    fun plan(
        sleepNeedHours: Double,
        goal: SleepGoalType,
        desiredWakeTimeMillis: Long,
        estimatedOnsetLatencyMinutes: Double = 15.0,
        baselineNeed: Double = 7.5,
        strainSupplement: Double = 0.0,
        debtRepayment: Double = 0.0,
        napCredit: Double = 0.0,
    ): SleepPlannerResult {
        val requiredSleep = sleepNeedHours * goal.multiplier(config)
        val totalTimeInBedHours = requiredSleep + (estimatedOnsetLatencyMinutes / 60.0)
        val bedtimeMillis = desiredWakeTimeMillis - (totalTimeInBedHours * 3600 * 1000).toLong()

        return SleepPlannerResult(
            sleepNeedHours = sleepNeedHours,
            requiredSleepDuration = requiredSleep,
            recommendedBedtimeMillis = bedtimeMillis,
            expectedWakeTimeMillis = desiredWakeTimeMillis,
            goal = goal,
            baselineNeed = baselineNeed,
            strainSupplement = strainSupplement,
            debtRepayment = debtRepayment,
            napCredit = napCredit,
        )
    }

    fun estimateWakeTime(recentWakeTimeMinutesFromMidnight: List<Int>): Int {
        if (recentWakeTimeMinutesFromMidnight.isEmpty()) {
            return 7 * 60 // Default to 7:00 AM
        }
        return recentWakeTimeMinutesFromMidnight.sum() / recentWakeTimeMinutesFromMidnight.size
    }

    fun estimateOnsetLatency(historicalLatencies: List<Double>): Double {
        if (historicalLatencies.isEmpty()) return 15.0
        return historicalLatencies.sum() / historicalLatencies.size
    }
}
