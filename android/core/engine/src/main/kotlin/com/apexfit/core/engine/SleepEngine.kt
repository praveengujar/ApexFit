package com.apexfit.core.engine

import com.apexfit.core.model.config.SleepConfig
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

data class SleepSessionData(
    val startDateMillis: Long,
    val endDateMillis: Long,
    val totalSleepMinutes: Double,
    val timeInBedMinutes: Double,
    val lightMinutes: Double,
    val deepMinutes: Double,
    val remMinutes: Double,
    val awakeMinutes: Double,
    val awakenings: Int,
    val sleepOnsetLatencyMinutes: Double?,
    val sleepEfficiency: Double,
    val stages: List<SleepStageData> = emptyList(),
)

data class SleepStageData(
    val type: String, // "light", "deep", "rem", "awake", "inBed"
    val startDateMillis: Long,
    val endDateMillis: Long,
    val durationMinutes: Double,
)

data class SleepConsistencyInput(
    val recentBedtimeMinutes: List<Double> = emptyList(),
    val recentWakeTimeMinutes: List<Double> = emptyList(),
)

data class SleepAnalysisResult(
    val mainSleep: SleepSessionData?,
    val naps: List<SleepSessionData>,
    val totalSleepHours: Double,
    val sleepNeedHours: Double,
    val sleepPerformance: Double,
    val sleepDebtHours: Double,
    val sleepScore: Double,
    val sleepEfficiency: Double,
    val sleepConsistency: Double,
    val restorativeSleepPct: Double,
    val disturbancesPerHour: Double,
    val deepSleepPct: Double,
    val remSleepPct: Double,
)

class SleepEngine(private val config: SleepConfig) {

    fun classifySessions(sessions: List<SleepSessionData>): Pair<SleepSessionData?, List<SleepSessionData>> {
        if (sessions.isEmpty()) return null to emptyList()

        val sorted = sessions.sortedByDescending { it.totalSleepMinutes }
        val main = sorted.first()
        val maxNapMinutes = config.sessionDetection.maximumNapDurationHours * 60
        val minDurationMinutes = config.sessionDetection.minimumDurationMinutes
        val naps = sorted.drop(1).filter {
            it.totalSleepMinutes >= minDurationMinutes && it.totalSleepMinutes <= maxNapMinutes
        }

        return main to naps
    }

    fun computeSleepNeed(
        baselineHours: Double,
        todayStrain: Double,
        sleepDebtHours: Double,
        napHoursToday: Double,
    ): Double {
        val strainSupplement = config.strainSupplements
            .firstOrNull { todayStrain < it.strainBelow }
            ?.addHours ?: 0.0

        val debtRepayment = sleepDebtHours * config.debtRepaymentRate
        val napCredit = min(napHoursToday, config.sessionDetection.napCreditCapHours)

        return baselineHours + strainSupplement + debtRepayment - napCredit
    }

    fun computeSleepPerformance(actualSleepHours: Double, sleepNeedHours: Double): Double {
        if (sleepNeedHours <= 0) return 0.0
        return ((actualSleepHours / sleepNeedHours) * 100).clamped(0.0..100.0)
    }

    fun computeSleepDebt(pastWeekSleepHours: List<Double>, pastWeekSleepNeeds: List<Double>): Double {
        var debt = 0.0
        val count = min(pastWeekSleepHours.size, pastWeekSleepNeeds.size)
        for (i in 0 until count) {
            val deficit = pastWeekSleepNeeds[i] - pastWeekSleepHours[i]
            debt += max(0.0, deficit)
        }
        return debt
    }

    fun computeSleepConsistency(
        currentBedtimeMinutes: Double,
        currentWakeTimeMinutes: Double,
        recentBedtimeMinutes: List<Double>,
        recentWakeTimeMinutes: List<Double>,
    ): Double {
        if (recentBedtimeMinutes.isEmpty()) return 100.0

        val allBedtimes = recentBedtimeMinutes + currentBedtimeMinutes
        val allWakeTimes = recentWakeTimeMinutes + currentWakeTimeMinutes

        val bedtimeStd = standardDeviationOf(allBedtimes)
        val wakeTimeStd = standardDeviationOf(allWakeTimes)
        val avgStd = (bedtimeStd + wakeTimeStd) / 2.0

        val score = 100.0 * exp(-avgStd / config.consistencyDecayTau)
        return score.clamped(0.0..100.0)
    }

    fun computeRestorativeSleepPct(session: SleepSessionData): Double {
        if (session.totalSleepMinutes <= 0) return 0.0
        return ((session.deepMinutes + session.remMinutes) / session.totalSleepMinutes) * 100
    }

    fun computeDisturbancesPerHour(session: SleepSessionData): Double {
        val hours = session.totalSleepMinutes / 60.0
        if (hours <= 0) return 0.0
        return session.awakenings.toDouble() / hours
    }

    fun computeCompositeSleepScore(
        sufficiency: Double,
        efficiency: Double,
        consistency: Double,
        disturbancesPerHour: Double,
    ): Double {
        val disturbanceScore = max(
            0.0,
            min(100.0, 100 - disturbancesPerHour * config.disturbanceScaling),
        )

        val score = config.compositeWeights.sufficiency * sufficiency +
            config.compositeWeights.efficiency * efficiency +
            config.compositeWeights.consistency * consistency +
            config.compositeWeights.disturbances * disturbanceScore

        return score.clamped(0.0..100.0)
    }

    fun analyze(
        sessions: List<SleepSessionData>,
        baselineSleepHours: Double,
        todayStrain: Double,
        pastWeekSleepHours: List<Double>,
        pastWeekSleepNeeds: List<Double>,
        consistencyInput: SleepConsistencyInput = SleepConsistencyInput(),
    ): SleepAnalysisResult {
        val (main, naps) = classifySessions(sessions)

        val totalMainSleep = main?.totalSleepMinutes ?: 0.0
        val napHours = naps.sumOf { it.totalSleepMinutes } / 60.0
        val totalSleepHours = (totalMainSleep / 60.0) + napHours

        val sleepDebt = computeSleepDebt(pastWeekSleepHours, pastWeekSleepNeeds)
        val sleepNeed = computeSleepNeed(baselineSleepHours, todayStrain, sleepDebt, napHours)
        val performance = computeSleepPerformance(totalSleepHours, sleepNeed)

        val efficiency = main?.sleepEfficiency ?: 0.0
        val restorativePct = main?.let { computeRestorativeSleepPct(it) } ?: 0.0
        val disturbances = main?.let { computeDisturbancesPerHour(it) } ?: 0.0
        val deepPct = main?.let { s ->
            if (s.totalSleepMinutes > 0) (s.deepMinutes / s.totalSleepMinutes) * 100 else 0.0
        } ?: 0.0
        val remPct = main?.let { s ->
            if (s.totalSleepMinutes > 0) (s.remMinutes / s.totalSleepMinutes) * 100 else 0.0
        } ?: 0.0

        val consistency = if (main != null) {
            val bedtimeMin = minutesSinceMidnight(main.startDateMillis)
            val wakeMin = minutesSinceMidnight(main.endDateMillis)
            computeSleepConsistency(
                bedtimeMin, wakeMin,
                consistencyInput.recentBedtimeMinutes,
                consistencyInput.recentWakeTimeMinutes,
            )
        } else {
            100.0
        }

        val sleepScore = computeCompositeSleepScore(performance, efficiency, consistency, disturbances)

        return SleepAnalysisResult(
            mainSleep = main,
            naps = naps,
            totalSleepHours = totalSleepHours,
            sleepNeedHours = sleepNeed,
            sleepPerformance = performance,
            sleepDebtHours = sleepDebt,
            sleepScore = sleepScore,
            sleepEfficiency = efficiency,
            sleepConsistency = consistency,
            restorativeSleepPct = restorativePct,
            disturbancesPerHour = disturbances,
            deepSleepPct = deepPct,
            remSleepPct = remPct,
        )
    }

    companion object {
        fun minutesSinceMidnight(epochMillis: Long): Double {
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = epochMillis
            }
            var minutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60.0 +
                calendar.get(java.util.Calendar.MINUTE)
            // Late-night bedtimes (after 6 PM) wrapped to negative for proximity to midnight
            if (minutes > 18 * 60) {
                minutes -= 24 * 60
            }
            return minutes
        }

        private fun standardDeviationOf(values: List<Double>): Double {
            if (values.size <= 1) return 0.0
            val mean = values.sum() / values.size
            val variance = values.map { (it - mean).pow(2) }.sum() / values.size
            return sqrt(variance)
        }
    }
}
