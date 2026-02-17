package com.apexfit.shared.engine

import com.apexfit.shared.model.config.*
import kotlin.test.assertTrue

object TestConfig {
    val hrZoneConfig = HeartRateZoneConfig(
        boundaries = listOf(0.50, 0.60, 0.70, 0.80, 0.90, 1.00),
        multipliers = listOf(1.0, 2.0, 3.0, 4.0, 5.0),
        sampleMaxDurationSeconds = 600.0,
    )

    val strainConfig = StrainConfig(
        scalingFactor = 6.0,
        logOffsetConstant = 1.0,
        maxValue = 21.0,
        minValue = 0.0,
        zones = StrainZoneConfig(
            light = ValueRange(0.0, 8.0),
            moderate = ValueRange(8.0, 14.0),
            high = ValueRange(14.0, 18.0),
            overreaching = ValueRange(18.0, 21.0),
        ),
    )

    val recoveryConfig = RecoveryConfig(
        weights = RecoveryWeights(
            hrv = 0.40,
            restingHeartRate = 0.25,
            sleep = 0.20,
            respiratoryRate = 0.05,
            spo2 = 0.05,
            skinTemperature = 0.05,
        ),
        sigmoidSteepness = 1.5,
        scoreRange = ScoreRange(min = 1, max = 99),
        zones = RecoveryZoneConfig(
            green = ScoreRange(67, 99),
            yellow = ScoreRange(34, 66),
            red = ScoreRange(1, 33),
        ),
        strainTargets = RecoveryStrainTargets(
            green = ValueRange(14.0, 18.0),
            yellow = ValueRange(8.0, 13.9),
            red = ValueRange(2.0, 7.9),
        ),
        insightThresholds = RecoveryInsightThresholds(
            hrvPercentChange = 10.0,
            rhrDeltaBPM = 3.0,
            sleepPerformanceHigh = 95.0,
            sleepPerformanceLow = 70.0,
            skinTempDeviationCelsius = 0.5,
        ),
    )

    val sleepConfig = SleepConfig(
        compositeWeights = SleepCompositeWeights(
            sufficiency = 0.50,
            efficiency = 0.25,
            consistency = 0.15,
            disturbances = 0.10,
        ),
        consistencyWindowNights = 4,
        consistencyDecayTau = 60.0,
        disturbanceScaling = 20.0,
        strainSupplements = listOf(
            StrainSupplement(strainBelow = 8.0, addHours = 0.0),
            StrainSupplement(strainBelow = 14.0, addHours = 0.25),
            StrainSupplement(strainBelow = 18.0, addHours = 0.5),
            StrainSupplement(strainBelow = 999.0, addHours = 0.75),
        ),
        debtRepaymentRate = 0.20,
        defaults = SleepDefaults(
            baselineHours = 7.5,
            onsetLatencyMinutes = 15.0,
        ),
        sessionDetection = SleepSessionDetection(
            gapToleranceMinutes = 30.0,
            minimumDurationMinutes = 30.0,
            maximumNapDurationHours = 3.0,
            napCreditCapHours = 1.5,
        ),
    )

    val sleepPlannerConfig = SleepPlannerConfig(
        goalMultipliers = SleepGoalMultipliers(
            peak = 1.0,
            perform = 0.85,
            getBy = 0.70,
        ),
    )

    const val DEFAULT_MAX_HR = 200
}

fun assertApprox(expected: Double, actual: Double, tolerance: Double, message: String = "") {
    assertTrue(kotlin.math.abs(expected - actual) < tolerance, "$message expected $expected but got $actual")
}
