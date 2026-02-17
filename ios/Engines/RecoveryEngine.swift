import Foundation

/// Research-backed recovery computation engine.
///
/// **Algorithm:** Sigmoid-mapped z-scores with personalized 28-day baselines.
/// Each biometric is compared to its personal baseline, converted to a z-score,
/// then mapped through a sigmoid function to a 0-100 scale.
///
/// **Weights (research-backed):**
/// - HRV (40%): Most sensitive autonomic marker (PMC5900369 meta-analysis)
/// - RHR (25%): Lower = better parasympathetic tone
/// - Sleep (20%): Sleep performance directly impacts recovery
/// - Respiratory Rate (5%): Elevated during illness/overtraining
/// - SpO2 (5%): Low values indicate respiratory compromise
/// - Skin Temperature (5%): Elevated deviation suggests illness/poor recovery
///
/// **Zones (WHOOP-compatible):**
/// - Green (67-99%): Ready for peak performance
/// - Yellow (34-66%): Moderate readiness, adjust training
/// - Red (1-33%): Prioritize rest and recovery
///
/// **Sources:**
/// - WHOOP Recovery: 70% autonomic (HRV+RHR), 20% sleep, 10% secondary
/// - Kubios: PNS/SNS indexes from Mean RR + RMSSD + Baevsky SI
/// - Athlytic: 60-day rolling baseline with standard deviation bands
/// - Academic: PMC5900369 — RMSSD most sensitive to autonomic changes
struct RecoveryInput {
    let hrv: Double?
    let restingHeartRate: Double?
    let sleepPerformance: Double?
    let respiratoryRate: Double?
    let spo2: Double?
    /// Skin temperature deviation from baseline (e.g., from Apple Watch sleeping wrist temperature).
    /// Positive values = warmer than usual → may indicate illness or poor recovery.
    let skinTemperatureDeviation: Double?
}

struct RecoveryBaselines {
    let hrv: BaselineResult?
    let restingHeartRate: BaselineResult?
    let sleepPerformance: BaselineResult?
    let respiratoryRate: BaselineResult?
    let spo2: BaselineResult?
    let skinTemperature: BaselineResult?
}

struct RecoveryResult {
    let score: Double
    let zone: RecoveryZone
    let hrvScore: Double?
    let rhrScore: Double?
    let sleepScore: Double?
    let respRateScore: Double?
    let spo2Score: Double?
    let skinTempScore: Double?
    let contributorCount: Int
}

struct RecoveryEngine {
    /// Sigmoid function mapping z-score to 0-100.
    ///
    /// z = 0 → 50 (at baseline)
    /// z = +2 → ~95 (well above baseline)
    /// z = -2 → ~5 (well below baseline)
    ///
    /// Steepness of 1.5 provides good discrimination in the ±2 std dev range.
    static func sigmoid(_ z: Double, steepness: Double = ConfigurationManager.shared.config.recovery.sigmoidSteepness) -> Double {
        return 100.0 / (1.0 + exp(-steepness * z))
    }

    /// Compute recovery score from current metrics and personal baselines.
    static func computeRecovery(input: RecoveryInput, baselines: RecoveryBaselines) -> RecoveryResult {
        var totalWeight: Double = 0
        var weightedSum: Double = 0
        var contributorCount = 0

        // HRV: higher is better → positive z-score = good
        let hrvScore: Double? = computeContributor(
            value: input.hrv,
            baseline: baselines.hrv,
            invert: false,
            weight: HealthKitConstants.recoveryHRVWeight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // RHR: lower is better → invert (high RHR = negative recovery signal)
        let rhrScore: Double? = computeContributor(
            value: input.restingHeartRate,
            baseline: baselines.restingHeartRate,
            invert: true,
            weight: HealthKitConstants.recoveryRHRWeight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // Sleep Performance: higher is better
        let sleepScore: Double? = computeContributor(
            value: input.sleepPerformance,
            baseline: baselines.sleepPerformance,
            invert: false,
            weight: HealthKitConstants.recoverySleepWeight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // Respiratory Rate: lower is better at rest → invert
        let respRateScore: Double? = computeContributor(
            value: input.respiratoryRate,
            baseline: baselines.respiratoryRate,
            invert: true,
            weight: HealthKitConstants.recoveryRespRateWeight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // SpO2: higher is better
        let spo2Score: Double? = computeContributor(
            value: input.spo2,
            baseline: baselines.spo2,
            invert: false,
            weight: HealthKitConstants.recoverySpO2Weight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // Skin Temperature: warmer than usual = worse recovery → invert
        // Apple Watch's sleeping wrist temperature is already a deviation from personal baseline.
        // We compare this deviation against its own baseline of deviations.
        let skinTempScore: Double? = computeContributor(
            value: input.skinTemperatureDeviation,
            baseline: baselines.skinTemperature,
            invert: true,
            weight: HealthKitConstants.recoverySkinTempWeight,
            totalWeight: &totalWeight,
            weightedSum: &weightedSum,
            contributorCount: &contributorCount
        )

        // Normalize by actual weights used (handles missing contributors gracefully)
        let rawScore: Double
        if totalWeight > 0 {
            rawScore = weightedSum / totalWeight
        } else {
            rawScore = 50.0
        }

        let range = ConfigurationManager.shared.config.recovery.scoreRange
        let finalScore = rawScore.clamped(to: Double(range.min)...Double(range.max))
        let zone = RecoveryZone.from(score: finalScore)

        return RecoveryResult(
            score: finalScore,
            zone: zone,
            hrvScore: hrvScore,
            rhrScore: rhrScore,
            sleepScore: sleepScore,
            respRateScore: respRateScore,
            spo2Score: spo2Score,
            skinTempScore: skinTempScore,
            contributorCount: contributorCount
        )
    }

    private static func computeContributor(
        value: Double?,
        baseline: BaselineResult?,
        invert: Bool,
        weight: Double,
        totalWeight: inout Double,
        weightedSum: inout Double,
        contributorCount: inout Int
    ) -> Double? {
        guard let value, let baseline, baseline.isValid else { return nil }

        var z = BaselineEngine.zScore(value: value, baseline: baseline)
        if invert { z = -z }

        let score = sigmoid(z)
        totalWeight += weight
        weightedSum += score * weight
        contributorCount += 1

        return score
    }

    /// Generate strain target based on recovery zone.
    static func strainTarget(for zone: RecoveryZone) -> ClosedRange<Double> {
        let targets = ConfigurationManager.shared.config.recovery.strainTargets
        switch zone {
        case .green: return targets.green.min...targets.green.max
        case .yellow: return targets.yellow.min...targets.yellow.max
        case .red: return targets.red.min...targets.red.max
        }
    }

    /// Generate recovery insight text.
    static func generateInsight(result: RecoveryResult, input: RecoveryInput, baselines: RecoveryBaselines) -> String {
        let thresholds = ConfigurationManager.shared.config.recovery.insightThresholds
        var insights: [String] = []

        if let hrv = input.hrv, let baseline = baselines.hrv {
            let pctChange = ((hrv - baseline.mean) / baseline.mean) * 100
            if abs(pctChange) > thresholds.hrvPercentChange {
                let direction = pctChange > 0 ? "above" : "below"
                insights.append("HRV was \(abs(Int(pctChange)))% \(direction) your baseline")
            }
        }

        if let rhr = input.restingHeartRate, let baseline = baselines.restingHeartRate {
            let delta = rhr - baseline.mean
            if abs(delta) > thresholds.rhrDeltaBPM {
                let direction = delta > 0 ? "elevated by" : "lower by"
                insights.append("RHR was \(direction) \(abs(Int(delta))) BPM")
            }
        }

        if let sleepPerf = input.sleepPerformance {
            if sleepPerf >= thresholds.sleepPerformanceHigh {
                insights.append("you got \(Int(sleepPerf))% of your sleep need")
            } else if sleepPerf < thresholds.sleepPerformanceLow {
                insights.append("you only got \(Int(sleepPerf))% of your sleep need")
            }
        }

        if let skinTemp = input.skinTemperatureDeviation, abs(skinTemp) > thresholds.skinTempDeviationCelsius {
            let direction = skinTemp > 0 ? "elevated" : "lower"
            insights.append("skin temperature was \(direction) by \(String(format: "%.1f", abs(skinTemp)))°C")
        }

        let prefix = "Your Recovery is \(Int(result.score))% (\(result.zone.label)). "
        if insights.isEmpty {
            return prefix + "Your metrics are within normal range."
        }
        return prefix + insights.joined(separator: ", and ") + "."
    }
}

extension BaselineResult {
    var isValid: Bool {
        sampleCount >= HealthKitConstants.minimumBaselineSamples && standardDeviation > 0
    }
}
