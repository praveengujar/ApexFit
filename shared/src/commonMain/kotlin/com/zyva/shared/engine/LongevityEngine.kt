package com.zyva.shared.engine

import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

object LongevityEngine {

    // Gompertz slope parameter (mortality doubling rate per year).
    const val GOMPERTZ_B: Double = 0.09

    // Overlap correction to avoid double-counting correlated metrics.
    const val OVERLAP_CORRECTION: Double = 0.85

    // MARK: - Metric Identification

    enum class MetricID(val id: String) {
        SLEEP_CONSISTENCY("sleepConsistency"),
        HOURS_OF_SLEEP("hoursOfSleep"),
        HR_ZONES_1_TO_3_WEEKLY("hrZones1to3Weekly"),
        HR_ZONES_4_TO_5_WEEKLY("hrZones4to5Weekly"),
        STRENGTH_ACTIVITY_WEEKLY("strengthActivityWeekly"),
        DAILY_STEPS("dailySteps"),
        VO2_MAX("vo2Max"),
        RESTING_HEART_RATE("restingHeartRate"),
        LEAN_BODY_MASS("leanBodyMass");

        val category: LongevityCategory
            get() = when (this) {
                SLEEP_CONSISTENCY, HOURS_OF_SLEEP -> LongevityCategory.SLEEP
                HR_ZONES_1_TO_3_WEEKLY, HR_ZONES_4_TO_5_WEEKLY, STRENGTH_ACTIVITY_WEEKLY, DAILY_STEPS -> LongevityCategory.STRAIN
                VO2_MAX, RESTING_HEART_RATE, LEAN_BODY_MASS -> LongevityCategory.FITNESS
            }

        val displayName: String
            get() = when (this) {
                SLEEP_CONSISTENCY -> "SLEEP CONSISTENCY"
                HOURS_OF_SLEEP -> "HOURS OF SLEEP"
                HR_ZONES_1_TO_3_WEEKLY -> "TIME IN HR ZONES 1-3 (WEEKLY)"
                HR_ZONES_4_TO_5_WEEKLY -> "TIME IN HR ZONES 4-5 (WEEKLY)"
                STRENGTH_ACTIVITY_WEEKLY -> "STRENGTH ACTIVITY TIME (WEEKLY)"
                DAILY_STEPS -> "STEPS"
                VO2_MAX -> "VO\u2082 MAX"
                RESTING_HEART_RATE -> "RHR"
                LEAN_BODY_MASS -> "LEAN BODY MASS"
            }

        val unit: String
            get() = when (this) {
                SLEEP_CONSISTENCY -> "%"
                HOURS_OF_SLEEP, HR_ZONES_1_TO_3_WEEKLY, HR_ZONES_4_TO_5_WEEKLY, STRENGTH_ACTIVITY_WEEKLY -> "h"
                DAILY_STEPS -> "Steps"
                VO2_MAX -> "ml/kg/min"
                RESTING_HEART_RATE -> "bpm"
                LEAN_BODY_MASS -> "%"
            }

        val gradientRange: ClosedFloatingPointRange<Double>
            get() = when (this) {
                SLEEP_CONSISTENCY -> 40.0..100.0
                HOURS_OF_SLEEP -> 5.0..8.0
                HR_ZONES_1_TO_3_WEEKLY -> 0.0..5.0
                HR_ZONES_4_TO_5_WEEKLY -> 0.0..1.0
                STRENGTH_ACTIVITY_WEEKLY -> 0.0..2.0
                DAILY_STEPS -> 0.0..16000.0
                VO2_MAX -> 15.0..70.0
                RESTING_HEART_RATE -> 40.0..80.0
                LEAN_BODY_MASS -> 60.0..95.0
            }

        val isHigherBetter: Boolean
            get() = this != RESTING_HEART_RATE
    }

    enum class LongevityCategory(val displayName: String) {
        SLEEP("Sleep"),
        STRAIN("Strain"),
        FITNESS("Fitness"),
    }

    // MARK: - Input / Output Types

    data class MetricInput(
        val id: MetricID,
        val sixMonthAvg: Double?,
        val thirtyDayAvg: Double?,
    )

    data class MetricResult(
        val id: MetricID,
        val sixMonthAvg: Double,
        val thirtyDayAvg: Double,
        val hazardRatio: Double,
        val deltaYears: Double,
        val insightTitle: String,
        val insightBody: String,
    )

    data class Result(
        val chronologicalAge: Double,
        val zyvaAge: Double,
        val yearsYoungerOlder: Double,   // negative = younger
        val paceOfAging: Double,         // -1.0 to 3.0
        val metricResults: List<MetricResult>,
        val weekStartMillis: Long,
        val weekEndMillis: Long,
        val overallInsightTitle: String,
        val overallInsightBody: String,
    )

    // MARK: - Core Computation

    fun compute(
        chronologicalAge: Double,
        inputs: List<MetricInput>,
        weekStartMillis: Long = 0L,
        weekEndMillis: Long = 0L,
    ): Result {
        val metricResults = mutableListOf<MetricResult>()
        var totalDelta6Mo = 0.0
        var totalDelta30Day = 0.0

        for (input in inputs) {
            val avg6Mo = input.sixMonthAvg ?: input.thirtyDayAvg
            val avg30Day = input.thirtyDayAvg ?: input.sixMonthAvg

            val val6Mo = avg6Mo ?: continue
            val val30Day = avg30Day ?: val6Mo

            val hr6Mo = hazardRatio(input.id, val6Mo)
            val hr30Day = hazardRatio(input.id, val30Day)
            val delta6Mo = deltaYears(hr6Mo)
            val delta30Day = deltaYears(hr30Day)

            totalDelta6Mo += delta6Mo
            totalDelta30Day += delta30Day

            val insight = generateMetricInsight(input.id, val6Mo, delta6Mo)

            metricResults.add(
                MetricResult(
                    id = input.id,
                    sixMonthAvg = val6Mo,
                    thirtyDayAvg = val30Day,
                    hazardRatio = hr6Mo,
                    deltaYears = delta6Mo,
                    insightTitle = insight.first,
                    insightBody = insight.second,
                )
            )
        }

        // Apply overlap correction
        totalDelta6Mo *= OVERLAP_CORRECTION
        totalDelta30Day *= OVERLAP_CORRECTION

        val zyvaAge = chronologicalAge + totalDelta6Mo
        val yearsYoungerOlder = totalDelta6Mo
        val projectedAge30 = chronologicalAge + totalDelta30Day

        val ageDiff = projectedAge30 - zyvaAge
        val rawPace = 1.0 + (ageDiff / 2.5)
        val paceOfAging = max(-1.0, min(3.0, rawPace))

        val overallInsight = generateOverallInsight(yearsYoungerOlder, paceOfAging, metricResults)

        return Result(
            chronologicalAge = chronologicalAge,
            zyvaAge = zyvaAge,
            yearsYoungerOlder = yearsYoungerOlder,
            paceOfAging = paceOfAging,
            metricResults = metricResults,
            weekStartMillis = weekStartMillis,
            weekEndMillis = weekEndMillis,
            overallInsightTitle = overallInsight.first,
            overallInsightBody = overallInsight.second,
        )
    }

    // MARK: - Hazard Ratio Computation

    fun deltaYears(hr: Double): Double {
        if (hr <= 0) return 0.0
        return ln(hr) / GOMPERTZ_B
    }

    fun hazardRatio(id: MetricID, value: Double): Double = when (id) {
        MetricID.SLEEP_CONSISTENCY -> hrSleepConsistency(value)
        MetricID.HOURS_OF_SLEEP -> hrHoursOfSleep(value)
        MetricID.HR_ZONES_1_TO_3_WEEKLY -> hrZones1to3(value)
        MetricID.HR_ZONES_4_TO_5_WEEKLY -> hrZones4to5(value)
        MetricID.STRENGTH_ACTIVITY_WEEKLY -> hrStrengthActivity(value)
        MetricID.DAILY_STEPS -> hrDailySteps(value)
        MetricID.VO2_MAX -> hrVO2Max(value)
        MetricID.RESTING_HEART_RATE -> hrRestingHR(value)
        MetricID.LEAN_BODY_MASS -> hrLeanBodyMass(value)
    }

    // MARK: - Dose-Response Curves

    private fun hrSleepConsistency(pct: Double): Double {
        val points = listOf(
            40.0 to 1.48, 50.0 to 1.40, 60.0 to 1.20, 70.0 to 1.10, 85.0 to 1.0, 100.0 to 0.92
        )
        return interpolate(pct, points)
    }

    private fun hrHoursOfSleep(hours: Double): Double {
        val points = listOf(
            4.0 to 1.20, 5.0 to 1.14, 6.0 to 1.07, 7.0 to 1.0, 8.0 to 0.98, 9.0 to 1.0, 10.0 to 1.10
        )
        return interpolate(hours, points)
    }

    private fun hrZones1to3(hours: Double): Double {
        val points = listOf(
            0.0 to 1.0, 1.0 to 0.90, 2.5 to 0.79, 5.0 to 0.78, 8.0 to 0.78
        )
        return interpolate(hours, points)
    }

    private fun hrZones4to5(hours: Double): Double {
        val points = listOf(
            0.0 to 1.0, 0.5 to 0.88, 1.25 to 0.77, 2.5 to 0.77, 4.0 to 0.80
        )
        return interpolate(hours, points)
    }

    private fun hrStrengthActivity(hours: Double): Double {
        val points = listOf(
            0.0 to 1.0, 0.5 to 0.85, 1.0 to 0.73, 1.5 to 0.75, 3.0 to 0.80
        )
        return interpolate(hours, points)
    }

    private fun hrDailySteps(steps: Double): Double {
        val points = listOf(
            0.0 to 1.30, 2000.0 to 1.18, 4000.0 to 1.06, 6000.0 to 0.90,
            8000.0 to 0.78, 10000.0 to 0.68, 12000.0 to 0.65, 16000.0 to 0.65
        )
        return interpolate(steps, points)
    }

    private fun hrVO2Max(vo2: Double): Double {
        val points = listOf(
            15.0 to 2.00, 20.0 to 1.70, 25.0 to 1.40, 30.0 to 1.15, 35.0 to 1.0,
            40.0 to 0.86, 45.0 to 0.74, 50.0 to 0.64, 55.0 to 0.55, 60.0 to 0.50, 70.0 to 0.45
        )
        return interpolate(vo2, points)
    }

    private fun hrRestingHR(bpm: Double): Double {
        val points = listOf(
            40.0 to 0.82, 45.0 to 0.85, 50.0 to 0.90, 55.0 to 0.95, 60.0 to 1.0,
            65.0 to 1.05, 70.0 to 1.09, 75.0 to 1.20, 80.0 to 1.45, 90.0 to 1.65
        )
        return interpolate(bpm, points)
    }

    private fun hrLeanBodyMass(pct: Double): Double {
        val points = listOf(
            55.0 to 1.70, 60.0 to 1.57, 65.0 to 1.30, 70.0 to 1.10, 75.0 to 1.0,
            80.0 to 0.95, 85.0 to 0.90, 90.0 to 0.88, 95.0 to 0.88
        )
        return interpolate(pct, points)
    }

    // MARK: - Interpolation

    private fun interpolate(value: Double, points: List<Pair<Double, Double>>): Double {
        if (points.isEmpty()) return 1.0
        if (value <= points.first().first) return points.first().second
        if (value >= points.last().first) return points.last().second

        for (i in 0 until points.size - 1) {
            val (x0, y0) = points[i]
            val (x1, y1) = points[i + 1]
            if (value in x0..x1) {
                val t = (value - x0) / (x1 - x0)
                return y0 + t * (y1 - y0)
            }
        }
        return 1.0
    }

    // MARK: - Insight Generation

    private fun generateMetricInsight(
        id: MetricID,
        value: Double,
        deltaYears: Double,
    ): Pair<String, String> {
        val isGood = deltaYears < -0.3
        val isBad = deltaYears > 0.3

        if (isGood) {
            return when (id) {
                MetricID.SLEEP_CONSISTENCY -> "Well Done" to "Your sleep consistency is helping extend your healthspan. Maintaining a regular schedule is one of the strongest longevity factors."
                MetricID.HOURS_OF_SLEEP -> "Optimal Sleep" to "You're getting enough sleep to support recovery and long-term health. Keep it up."
                MetricID.HR_ZONES_1_TO_3_WEEKLY -> "Active Lifestyle" to "Your weekly moderate activity is well within the range linked to reduced all-cause mortality."
                MetricID.HR_ZONES_4_TO_5_WEEKLY -> "High Intensity Pay-Off" to "Your vigorous exercise is contributing to cardiovascular fitness and longevity."
                MetricID.STRENGTH_ACTIVITY_WEEKLY -> "Building Strength" to "Resistance training is strongly linked to longevity. Your weekly volume is in the optimal zone."
                MetricID.DAILY_STEPS -> "Keep Moving" to "Your daily step count is associated with significant mortality risk reduction."
                MetricID.VO2_MAX -> "Elite Fitness" to "Your cardiorespiratory fitness is a powerful predictor of longevity \u2014 stronger than smoking status."
                MetricID.RESTING_HEART_RATE -> "Strong Heart" to "A low resting heart rate reflects excellent cardiovascular efficiency."
                MetricID.LEAN_BODY_MASS -> "Lean & Strong" to "Maintaining lean body mass is crucial for metabolic health and longevity."
            }
        } else if (isBad) {
            return when (id) {
                MetricID.SLEEP_CONSISTENCY -> "Time to Reassess" to "Your sleep consistency is below the recommended range. Irregular sleep patterns are associated with increased mortality risk."
                MetricID.HOURS_OF_SLEEP -> "Sleep More" to "Your sleep duration is below the 7-hour threshold linked to optimal health outcomes."
                MetricID.HR_ZONES_1_TO_3_WEEKLY -> "Move More" to "Increasing moderate activity to 150+ minutes per week could significantly reduce your mortality risk."
                MetricID.HR_ZONES_4_TO_5_WEEKLY -> "Push Harder" to "Adding vigorous exercise can provide additional cardiovascular benefits beyond moderate activity alone."
                MetricID.STRENGTH_ACTIVITY_WEEKLY -> "Add Resistance" to "Even 30 minutes of weekly strength training is associated with 15% lower mortality risk."
                MetricID.DAILY_STEPS -> "Step It Up" to "Increasing your daily steps toward 8,000 could meaningfully impact your long-term health."
                MetricID.VO2_MAX -> "Build Fitness" to "Improving cardiorespiratory fitness is one of the most impactful changes you can make for longevity."
                MetricID.RESTING_HEART_RATE -> "Heart Health" to "An elevated resting heart rate may indicate cardiovascular stress. Regular aerobic exercise can help lower it."
                MetricID.LEAN_BODY_MASS -> "Build Muscle" to "Low lean body mass is associated with increased mortality risk. Strength training can help."
            }
        } else {
            return "On Track" to "Your ${id.displayName.lowercase()} is near the baseline. Small improvements can shift this toward positive impact."
        }
    }

    private fun generateOverallInsight(
        yearsYoungerOlder: Double,
        paceOfAging: Double,
        metricResults: List<MetricResult>,
    ): Pair<String, String> {
        val isYounger = yearsYoungerOlder < -1.0
        val paceImproving = paceOfAging < 1.0

        val sorted = metricResults.sortedBy { it.deltaYears }
        val bestMetric = sorted.firstOrNull()
        val worstMetric = sorted.lastOrNull()

        return if (isYounger && paceImproving) {
            val bestName = bestMetric?.id?.displayName?.lowercase() ?: "your habits"
            "Crushing It" to "Your Zyva Age is improving and your Pace of Aging is below 1.0x. Your $bestName is a major contributor to your longevity gains."
        } else if (isYounger) {
            "Solid Foundation" to "You're biologically younger than your chronological age. Keep your current habits consistent to maintain these gains."
        } else if (paceImproving) {
            "Trending Better" to "Your recent habits are pushing your Pace of Aging in the right direction. Keep the momentum going."
        } else {
            val worstName = worstMetric?.id?.displayName?.lowercase() ?: "key metrics"
            "Room for Growth" to "Focus on improving your $worstName \u2014 it has the largest impact on your longevity score right now."
        }
    }

    // MARK: - Value Formatting

    fun formatValue(value: Double, id: MetricID): String = when (id) {
        MetricID.SLEEP_CONSISTENCY, MetricID.LEAN_BODY_MASS ->
            "${value.toInt()}%"
        MetricID.HOURS_OF_SLEEP -> {
            val hours = value.toInt()
            val mins = ((value - hours) * 60).toInt()
            "$hours:${mins.toString().padStart(2, '0')}"
        }
        MetricID.HR_ZONES_1_TO_3_WEEKLY, MetricID.HR_ZONES_4_TO_5_WEEKLY, MetricID.STRENGTH_ACTIVITY_WEEKLY -> {
            val hours = value.toInt()
            val mins = ((value - hours) * 60).toInt()
            "$hours:${mins.toString().padStart(2, '0')} h"
        }
        MetricID.DAILY_STEPS ->
            formatWithCommas(value.toInt())
        MetricID.VO2_MAX ->
            "${value.toInt()} ml/kg/min"
        MetricID.RESTING_HEART_RATE ->
            "${value.toInt()} bpm"
    }

    private fun formatWithCommas(value: Int): String {
        val str = value.toString()
        val result = StringBuilder()
        for (i in str.indices) {
            if (i > 0 && (str.length - i) % 3 == 0) {
                result.append(',')
            }
            result.append(str[i])
        }
        return result.toString()
    }
}
