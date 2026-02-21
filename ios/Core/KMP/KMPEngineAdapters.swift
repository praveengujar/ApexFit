import Foundation
import ZyvaShared

// MARK: - Recovery Engine Adapter
//
// Provides a Swift-friendly API that wraps the KMP RecoveryEngine.
// iOS services can call these methods instead of the local Swift RecoveryEngine.

enum KMPRecoveryEngineAdapter {
    static func computeRecovery(
        input: ZyvaShared.RecoveryInput,
        baselines: ZyvaShared.RecoveryBaselines
    ) -> ZyvaShared.RecoveryResult {
        let config = KMPConfigProvider.scoringConfig.recovery
        let engine = ZyvaShared.RecoveryEngine(config: config)
        return engine.computeRecovery(input: input, baselines: baselines)
    }

    static func generateInsight(
        result: ZyvaShared.RecoveryResult,
        input: ZyvaShared.RecoveryInput,
        baselines: ZyvaShared.RecoveryBaselines
    ) -> String {
        let config = KMPConfigProvider.scoringConfig.recovery
        let engine = ZyvaShared.RecoveryEngine(config: config)
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
    ) -> ZyvaShared.BaselineResult? {
        let kotlinValues = values.map { KotlinDouble(value: $0) }
        return ZyvaShared.BaselineEngine.shared.computeBaseline(
            values: kotlinValues,
            windowDays: windowDays,
            minimumSamples: minimumSamples
        )
    }

    static func zScore(value: Double, baseline: ZyvaShared.BaselineResult) -> Double {
        return ZyvaShared.BaselineEngine.shared.zScore(value: value, baseline: baseline)
    }
}

// MARK: - HRV Calculator Adapter

enum KMPHRVCalculatorAdapter {
    static func bestHRV(
        rmssdValue: Double? = nil,
        sdnnValue: Double? = nil
    ) -> ZyvaShared.HRVResult {
        return ZyvaShared.HRVCalculator.shared.bestHRV(
            rmssdValue: rmssdValue.map { KotlinDouble(value: $0) },
            sdnnValue: sdnnValue.map { KotlinDouble(value: $0) }
        )
    }

    static func computeRMSSD(rrIntervalsSeconds: [Double]) -> Double? {
        let kotlinValues = rrIntervalsSeconds.map { KotlinDouble(value: $0) }
        return ZyvaShared.HRVCalculator.shared.computeRMSSD(rrIntervalsSeconds: kotlinValues)?.doubleValue
    }

    static func effectiveHRV(from result: ZyvaShared.HRVResult) -> Double? {
        return ZyvaShared.HRVCalculator.shared.effectiveHRV(result: result)?.doubleValue
    }
}

// MARK: - Sleep Engine Adapter

enum KMPSleepEngineAdapter {
    static func analyze(
        sessions: [ZyvaShared.SleepSessionData],
        baselineSleepHours: Double,
        todayStrain: Double,
        pastWeekSleepHours: [Double],
        pastWeekSleepNeeds: [Double],
        consistencyInput: ZyvaShared.SleepConsistencyInput
    ) -> ZyvaShared.SleepAnalysisResult {
        let config = KMPConfigProvider.scoringConfig.sleep
        let engine = ZyvaShared.SleepEngine(config: config)
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
        return ZyvaShared.SleepEngine.companion.minutesSinceMidnight(epochMillis: epochMillis)
    }
}

// MARK: - Strain Engine Adapter

enum KMPStrainEngineAdapter {
    static func computeWorkoutStrain(
        maxHeartRate: Int32,
        heartRateSamples: [(Int64, Double)]
    ) -> ZyvaShared.StrainResult {
        let config = KMPConfigProvider.scoringConfig
        let engine = ZyvaShared.StrainEngine(
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
