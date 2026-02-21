package com.zyva.shared.model.config

import kotlinx.serialization.Serializable

@Serializable
data class ScoringConfig(
    val version: Int,
    val recovery: RecoveryConfig,
    val sleep: SleepConfig,
    val stress: StressConfig,
    val strain: StrainConfig,
    val heartRateZones: HeartRateZoneConfig,
    val baselines: BaselineConfig,
    val healthMonitor: HealthMonitorConfig,
    val sleepPlanner: SleepPlannerConfig,
    val staleness: StalenessConfig,
)

// Recovery

@Serializable
data class RecoveryConfig(
    val weights: RecoveryWeights,
    val sigmoidSteepness: Double,
    val scoreRange: ScoreRange,
    val zones: RecoveryZoneConfig,
    val strainTargets: RecoveryStrainTargets,
    val insightThresholds: RecoveryInsightThresholds,
)

@Serializable
data class RecoveryWeights(
    val hrv: Double,
    val restingHeartRate: Double,
    val sleep: Double,
    val respiratoryRate: Double,
    val spo2: Double,
    val skinTemperature: Double,
)

@Serializable
data class RecoveryZoneConfig(
    val green: ScoreRange,
    val yellow: ScoreRange,
    val red: ScoreRange,
)

@Serializable
data class RecoveryStrainTargets(
    val green: ValueRange,
    val yellow: ValueRange,
    val red: ValueRange,
)

@Serializable
data class RecoveryInsightThresholds(
    val hrvPercentChange: Double,
    val rhrDeltaBPM: Double,
    val sleepPerformanceHigh: Double,
    val sleepPerformanceLow: Double,
    val skinTempDeviationCelsius: Double,
)

// Sleep

@Serializable
data class SleepConfig(
    val compositeWeights: SleepCompositeWeights,
    val consistencyWindowNights: Int,
    val consistencyDecayTau: Double,
    val disturbanceScaling: Double,
    val strainSupplements: List<StrainSupplement>,
    val debtRepaymentRate: Double,
    val defaults: SleepDefaults,
    val sessionDetection: SleepSessionDetection,
)

@Serializable
data class SleepCompositeWeights(
    val sufficiency: Double,
    val efficiency: Double,
    val consistency: Double,
    val disturbances: Double,
)

@Serializable
data class StrainSupplement(
    val strainBelow: Double,
    val addHours: Double,
)

@Serializable
data class SleepDefaults(
    val baselineHours: Double,
    val onsetLatencyMinutes: Double,
)

@Serializable
data class SleepSessionDetection(
    val gapToleranceMinutes: Double,
    val minimumDurationMinutes: Double,
    val maximumNapDurationHours: Double,
    val napCreditCapHours: Double,
)

// Stress

@Serializable
data class StressConfig(
    val weights: StressWeights,
    val baselineWindowDays: Int,
    val minimumBaselineSamples: Int,
    val motionDampeningFactor: Double,
    val bucketIntervalMinutes: Int,
    val normalization: StressNormalization,
    val levels: List<StressLevelThreshold>,
    val populationDefaults: StressPopulationDefaults,
    val baevsky: BaevskyConfig,
)

@Serializable
data class StressWeights(
    val hrv: Double,
    val heartRate: Double,
)

@Serializable
data class StressNormalization(
    val zOffset: Double,
    val zRange: Double,
    val outputMax: Double,
)

@Serializable
data class StressLevelThreshold(
    val below: Double,
    val label: String,
)

@Serializable
data class StressPopulationDefaults(
    val lnRMSSDMean: Double,
    val lnRMSSDStd: Double,
    val heartRateMean: Double,
    val heartRateStd: Double,
)

@Serializable
data class BaevskyConfig(
    val binWidthSeconds: Double,
    val normalizationDivisor: Double,
    val normalizationMultiplier: Double,
)

// Strain

@Serializable
data class StrainConfig(
    val scalingFactor: Double,
    val logOffsetConstant: Double,
    val maxValue: Double,
    val minValue: Double,
    val zones: StrainZoneConfig,
)

@Serializable
data class StrainZoneConfig(
    val light: ValueRange,
    val moderate: ValueRange,
    val high: ValueRange,
    val overreaching: ValueRange,
)

// Heart Rate Zones

@Serializable
data class HeartRateZoneConfig(
    val boundaries: List<Double>,
    val multipliers: List<Double>,
    val sampleMaxDurationSeconds: Double,
)

// Baselines

@Serializable
data class BaselineConfig(
    val windowDays: Int,
    val minimumSamples: Int,
    val fallbackDays: Int,
    val exponentialAlpha: Double,
)

// Health Monitor

@Serializable
data class HealthMonitorConfig(
    val zScoreThreshold: Double,
)

// Sleep Planner

@Serializable
data class SleepPlannerConfig(
    val goalMultipliers: SleepGoalMultipliers,
)

@Serializable
data class SleepGoalMultipliers(
    val peak: Double,
    val perform: Double,
    val getBy: Double,
)

// Staleness

@Serializable
data class StalenessConfig(
    val heartRateHours: Int,
    val sleepDataHours: Int,
    val workoutHours: Int,
    val restingHRHours: Int,
    val hrvHours: Int,
    val spo2Hours: Int,
)

// Shared Types

@Serializable
data class ScoreRange(
    val min: Int,
    val max: Int,
)

@Serializable
data class ValueRange(
    val min: Double,
    val max: Double,
)
