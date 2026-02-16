import Foundation

// MARK: - Root Configuration

struct ScoringConfig: Codable {
    let version: Int
    let recovery: RecoveryConfig
    let sleep: SleepConfig
    let stress: StressConfig
    let strain: StrainConfig
    let heartRateZones: HeartRateZoneConfig
    let baselines: BaselineConfig
    let healthMonitor: HealthMonitorConfig
    let sleepPlanner: SleepPlannerConfig
    let staleness: StalenessConfig
}

// MARK: - Recovery

struct RecoveryConfig: Codable {
    let weights: RecoveryWeights
    let sigmoidSteepness: Double
    let scoreRange: ScoreRange
    let zones: RecoveryZoneConfig
    let strainTargets: RecoveryStrainTargets
    let insightThresholds: RecoveryInsightThresholds
}

struct RecoveryWeights: Codable {
    let hrv: Double
    let restingHeartRate: Double
    let sleep: Double
    let respiratoryRate: Double
    let spo2: Double
    let skinTemperature: Double
}

struct RecoveryZoneConfig: Codable {
    let green: ScoreRange
    let yellow: ScoreRange
    let red: ScoreRange
}

struct RecoveryStrainTargets: Codable {
    let green: ValueRange
    let yellow: ValueRange
    let red: ValueRange
}

struct RecoveryInsightThresholds: Codable {
    let hrvPercentChange: Double
    let rhrDeltaBPM: Double
    let sleepPerformanceHigh: Double
    let sleepPerformanceLow: Double
    let skinTempDeviationCelsius: Double
}

// MARK: - Sleep

struct SleepConfig: Codable {
    let compositeWeights: SleepCompositeWeights
    let consistencyWindowNights: Int
    let consistencyDecayTau: Double
    let disturbanceScaling: Double
    let strainSupplements: [StrainSupplement]
    let debtRepaymentRate: Double
    let defaults: SleepDefaults
    let sessionDetection: SleepSessionDetection
}

struct SleepCompositeWeights: Codable {
    let sufficiency: Double
    let efficiency: Double
    let consistency: Double
    let disturbances: Double
}

struct StrainSupplement: Codable {
    let strainBelow: Double
    let addHours: Double
}

struct SleepDefaults: Codable {
    let baselineHours: Double
    let onsetLatencyMinutes: Double
}

struct SleepSessionDetection: Codable {
    let gapToleranceMinutes: Double
    let minimumDurationMinutes: Double
    let maximumNapDurationHours: Double
    let napCreditCapHours: Double
}

// MARK: - Stress

struct StressConfig: Codable {
    let weights: StressWeights
    let baselineWindowDays: Int
    let minimumBaselineSamples: Int
    let motionDampeningFactor: Double
    let bucketIntervalMinutes: Int
    let normalization: StressNormalization
    let levels: [StressLevelThreshold]
    let populationDefaults: StressPopulationDefaults
    let baevsky: BaevskyConfig
}

struct StressWeights: Codable {
    let hrv: Double
    let heartRate: Double
}

struct StressNormalization: Codable {
    let zOffset: Double
    let zRange: Double
    let outputMax: Double
}

struct StressLevelThreshold: Codable {
    let below: Double
    let label: String
}

struct StressPopulationDefaults: Codable {
    let lnRMSSDMean: Double
    let lnRMSSDStd: Double
    let heartRateMean: Double
    let heartRateStd: Double
}

struct BaevskyConfig: Codable {
    let binWidthSeconds: Double
    let normalizationDivisor: Double
    let normalizationMultiplier: Double
}

// MARK: - Strain

struct StrainConfig: Codable {
    let scalingFactor: Double
    let logOffsetConstant: Double
    let maxValue: Double
    let minValue: Double
    let zones: StrainZoneConfig
}

struct StrainZoneConfig: Codable {
    let light: ValueRange
    let moderate: ValueRange
    let high: ValueRange
    let overreaching: ValueRange
}

// MARK: - Heart Rate Zones

struct HeartRateZoneConfig: Codable {
    let boundaries: [Double]
    let multipliers: [Double]
    let sampleMaxDurationSeconds: Double
}

// MARK: - Baselines

struct BaselineConfig: Codable {
    let windowDays: Int
    let minimumSamples: Int
    let fallbackDays: Int
    let exponentialAlpha: Double
}

// MARK: - Health Monitor

struct HealthMonitorConfig: Codable {
    let zScoreThreshold: Double
}

// MARK: - Sleep Planner

struct SleepPlannerConfig: Codable {
    let goalMultipliers: SleepGoalMultipliers
}

struct SleepGoalMultipliers: Codable {
    let peak: Double
    let perform: Double
    let getBy: Double
}

// MARK: - Staleness

struct StalenessConfig: Codable {
    let heartRateHours: Int
    let sleepDataHours: Int
    let workoutHours: Int
    let restingHRHours: Int
    let hrvHours: Int
    let spo2Hours: Int
}

// MARK: - Shared Types

struct ScoreRange: Codable {
    let min: Int
    let max: Int
}

struct ValueRange: Codable {
    let min: Double
    let max: Double
}
