import ZyvaShared
import Foundation

enum HealthKitConstants {
    private static var c: ZyvaShared.ScoringConfig { KMPConfigProvider.scoringConfig }

    // MARK: - Data Staleness Thresholds
    static var heartRateStalenessHours: Int { Int(c.staleness.heartRateHours) }
    static var sleepDataStalenessHours: Int { Int(c.staleness.sleepDataHours) }
    static var workoutStalenessHours: Int { Int(c.staleness.workoutHours) }
    static var restingHRStalenessHours: Int { Int(c.staleness.restingHRHours) }
    static var hrvStalenessHours: Int { Int(c.staleness.hrvHours) }
    static var spo2StalenessHours: Int { Int(c.staleness.spo2Hours) }

    // MARK: - Background Task Identifiers
    static let backgroundRefreshTaskID = "com.zyva.background.refresh"
    static let backgroundProcessingTaskID = "com.zyva.background.processing"

    // MARK: - Baseline Computation
    static var baselineWindowDays: Int { Int(c.baselines.windowDays) }
    static var minimumBaselineSamples: Int { Int(c.baselines.minimumSamples) }
    static var baselineFallbackDays: Int { Int(c.baselines.fallbackDays) }

    // MARK: - Sleep Session Detection
    static var sleepSessionGapToleranceMinutes: Double { c.sleep.sessionDetection.gapToleranceMinutes }
    static var minimumSleepDurationMinutes: Double { c.sleep.sessionDetection.minimumDurationMinutes }
    static var maximumNapDurationHours: Double { c.sleep.sessionDetection.maximumNapDurationHours }
    static var napCreditCapHours: Double { c.sleep.sessionDetection.napCreditCapHours }

    // MARK: - Recovery Timing
    static let recoveryRecalculationThresholdPoints = 3.0

    // MARK: - Strain Computation
    static var strainScalingFactor: Double { c.strain.scalingFactor }
    static var strainMaxValue: Double { c.strain.maxValue }
    static var strainMinValue: Double { c.strain.minValue }

    // MARK: - Heart Rate Zones (% of Max HR)
    static var zone1LowerBound: Double { c.heartRateZones.boundaries[0].doubleValue }
    static var zone1UpperBound: Double { c.heartRateZones.boundaries[1].doubleValue }
    static var zone2UpperBound: Double { c.heartRateZones.boundaries[2].doubleValue }
    static var zone3UpperBound: Double { c.heartRateZones.boundaries[3].doubleValue }
    static var zone4UpperBound: Double { c.heartRateZones.boundaries[4].doubleValue }
    static var zone5UpperBound: Double { c.heartRateZones.boundaries[5].doubleValue }

    // MARK: - Zone Multipliers
    static var zone1Multiplier: Double { c.heartRateZones.multipliers[0].doubleValue }
    static var zone2Multiplier: Double { c.heartRateZones.multipliers[1].doubleValue }
    static var zone3Multiplier: Double { c.heartRateZones.multipliers[2].doubleValue }
    static var zone4Multiplier: Double { c.heartRateZones.multipliers[3].doubleValue }
    static var zone5Multiplier: Double { c.heartRateZones.multipliers[4].doubleValue }

    // MARK: - Recovery Weights (research-backed)
    // Meta-analysis PMC5900369: HRV is the most sensitive autonomic marker.
    // WHOOP uses ~70% autonomic (HRV+RHR), ~20% sleep, ~10% secondary.
    static var recoveryHRVWeight: Double { c.recovery.weights.hrv }
    static var recoveryRHRWeight: Double { c.recovery.weights.restingHeartRate }
    static var recoverySleepWeight: Double { c.recovery.weights.sleep }
    static var recoveryRespRateWeight: Double { c.recovery.weights.respiratoryRate }
    static var recoverySpO2Weight: Double { c.recovery.weights.spo2 }
    static var recoverySkinTempWeight: Double { c.recovery.weights.skinTemperature }

    // MARK: - Sleep Need Defaults
    static var defaultSleepBaselineHours: Double { c.sleep.defaults.baselineHours }
    static var defaultSleepOnsetLatencyMinutes: Double { c.sleep.defaults.onsetLatencyMinutes }
}
