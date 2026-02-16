import Foundation
import ApexFitShared

// MARK: - Recovery Engine Adapter
//
// Provides a Swift-friendly API that wraps the KMP RecoveryEngine.
// iOS services can call these methods instead of the local Swift RecoveryEngine.

enum KMPRecoveryEngineAdapter {
    static func computeRecovery(
        input: ApexFitShared.RecoveryInput,
        baselines: ApexFitShared.RecoveryBaselines
    ) -> ApexFitShared.RecoveryResult {
        let config = KMPConfigProvider.scoringConfig.recovery
        let engine = ApexFitShared.RecoveryEngine(config: config)
        return engine.computeRecovery(input: input, baselines: baselines)
    }

    static func generateInsight(
        result: ApexFitShared.RecoveryResult,
        input: ApexFitShared.RecoveryInput,
        baselines: ApexFitShared.RecoveryBaselines
    ) -> String {
        let config = KMPConfigProvider.scoringConfig.recovery
        let engine = ApexFitShared.RecoveryEngine(config: config)
        return engine.generateInsight(result: result, input: input, baselines: baselines)
    }
}

// MARK: - Baseline Engine Adapter

enum KMPBaselineEngineAdapter {
    /// Compute baseline from an array of Swift Doubles.
    /// Handles conversion to KotlinDouble array internally.
    static func computeBaseline(
        values: [Double],
        windowDays: Int32 = 28,
        minimumSamples: Int32 = 3
    ) -> ApexFitShared.BaselineResult? {
        let kotlinValues = values.map { KotlinDouble(value: $0) }
        return ApexFitShared.BaselineEngine.shared.computeBaseline(
            values: kotlinValues,
            windowDays: windowDays,
            minimumSamples: minimumSamples
        )
    }

    static func zScore(value: Double, baseline: ApexFitShared.BaselineResult) -> Double {
        return ApexFitShared.BaselineEngine.shared.zScore(value: value, baseline: baseline)
    }
}

// MARK: - HRV Calculator Adapter

enum KMPHRVCalculatorAdapter {
    static func bestHRV(
        rmssdValue: Double? = nil,
        sdnnValue: Double? = nil
    ) -> ApexFitShared.HRVResult {
        return ApexFitShared.HRVCalculator.shared.bestHRV(
            rmssdValue: rmssdValue.map { KotlinDouble(value: $0) },
            sdnnValue: sdnnValue.map { KotlinDouble(value: $0) }
        )
    }

    static func computeRMSSD(rrIntervalsSeconds: [Double]) -> Double? {
        let kotlinValues = rrIntervalsSeconds.map { KotlinDouble(value: $0) }
        return ApexFitShared.HRVCalculator.shared.computeRMSSD(rrIntervalsSeconds: kotlinValues)?.doubleValue
    }

    static func effectiveHRV(from result: ApexFitShared.HRVResult) -> Double? {
        return ApexFitShared.HRVCalculator.shared.effectiveHRV(result: result)?.doubleValue
    }
}

// MARK: - Sleep Engine Adapter

enum KMPSleepEngineAdapter {
    static func analyze(
        sessions: [ApexFitShared.SleepSessionData],
        baselineSleepHours: Double,
        todayStrain: Double,
        pastWeekSleepHours: [Double],
        pastWeekSleepNeeds: [Double],
        consistencyInput: ApexFitShared.SleepConsistencyInput
    ) -> ApexFitShared.SleepAnalysisResult {
        let config = KMPConfigProvider.scoringConfig.sleep
        let engine = ApexFitShared.SleepEngine(config: config)
        return engine.analyze(
            sessions: sessions,
            baselineSleepHours: baselineSleepHours,
            todayStrain: todayStrain,
            pastWeekSleepHours: pastWeekSleepHours.map { KotlinDouble(value: $0) },
            pastWeekSleepNeeds: pastWeekSleepNeeds.map { KotlinDouble(value: $0) },
            consistencyInput: consistencyInput
        )
    }

    static func minutesSinceMidnight(epochMillis: Int64) -> Double {
        return ApexFitShared.SleepEngine.companion.minutesSinceMidnight(epochMillis: epochMillis)
    }
}

// MARK: - Strain Engine Adapter

enum KMPStrainEngineAdapter {
    static func computeWorkoutStrain(
        maxHeartRate: Int32,
        heartRateSamples: [(Int64, Double)]
    ) -> ApexFitShared.StrainResult {
        let config = KMPConfigProvider.scoringConfig
        let engine = ApexFitShared.StrainEngine(
            maxHeartRate: maxHeartRate,
            strainConfig: config.strain,
            hrZoneConfig: config.heartRateZones
        )
        let kotlinPairs = heartRateSamples.map { (ts, bpm) in
            KotlinPair<KotlinLong, KotlinDouble>(first: KotlinLong(value: ts), second: KotlinDouble(value: bpm))
        }
        return engine.computeWorkoutStrain(rawSamples: kotlinPairs)
    }
}
