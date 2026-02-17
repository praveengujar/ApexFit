import Foundation
import SwiftData

@MainActor
@Observable
final class HealthMonitorService {
    var metricsInRange: Int = 0
    var totalMetrics: Int = 0

    var isWithinRange: Bool {
        metricsInRange == totalMetrics
    }

    // MARK: - Health Status Evaluation

    /// Evaluates today's vitals against 28-day baselines.
    /// Checks 5 metrics: HRV, Resting Heart Rate, SpO2, Respiratory Rate, Skin Temperature.
    /// A metric is "within range" if its absolute z-score is within the configured threshold.
    func evaluateHealthStatus(metric: DailyMetric, historicalMetrics: [DailyMetric]) {
        let threshold = ConfigurationManager.shared.config.healthMonitor.zScoreThreshold
        var inRange = 0
        var total = 0

        // 1. HRV (RMSSD)
        if let currentHRV = metric.hrvRMSSD {
            let historicalValues = historicalMetrics.compactMap { $0.hrvRMSSD }
            if let baseline = BaselineEngine.computeBaseline(
                values: historicalValues,
                windowDays: HealthKitConstants.baselineWindowDays,
                minimumSamples: HealthKitConstants.minimumBaselineSamples
            ) {
                total += 1
                let z = BaselineEngine.zScore(value: currentHRV, baseline: baseline)
                if abs(z) <= threshold {
                    inRange += 1
                }
            }
        }

        // 2. Resting Heart Rate
        if let currentRHR = metric.restingHeartRate {
            let historicalValues = historicalMetrics.compactMap { $0.restingHeartRate }
            if let baseline = BaselineEngine.computeBaseline(
                values: historicalValues,
                windowDays: HealthKitConstants.baselineWindowDays,
                minimumSamples: HealthKitConstants.minimumBaselineSamples
            ) {
                total += 1
                let z = BaselineEngine.zScore(value: currentRHR, baseline: baseline)
                if abs(z) <= threshold {
                    inRange += 1
                }
            }
        }

        // 3. SpO2
        if let currentSpO2 = metric.spo2 {
            let historicalValues = historicalMetrics.compactMap { $0.spo2 }
            if let baseline = BaselineEngine.computeBaseline(
                values: historicalValues,
                windowDays: HealthKitConstants.baselineWindowDays,
                minimumSamples: HealthKitConstants.minimumBaselineSamples
            ) {
                total += 1
                let z = BaselineEngine.zScore(value: currentSpO2, baseline: baseline)
                if abs(z) <= threshold {
                    inRange += 1
                }
            }
        }

        // 4. Respiratory Rate
        if let currentRespRate = metric.respiratoryRate {
            let historicalValues = historicalMetrics.compactMap { $0.respiratoryRate }
            if let baseline = BaselineEngine.computeBaseline(
                values: historicalValues,
                windowDays: HealthKitConstants.baselineWindowDays,
                minimumSamples: HealthKitConstants.minimumBaselineSamples
            ) {
                total += 1
                let z = BaselineEngine.zScore(value: currentRespRate, baseline: baseline)
                if abs(z) <= threshold {
                    inRange += 1
                }
            }
        }

        // 5. Skin Temperature
        if let currentSkinTemp = metric.skinTemperature {
            let historicalValues = historicalMetrics.compactMap { $0.skinTemperature }
            if let baseline = BaselineEngine.computeBaseline(
                values: historicalValues,
                windowDays: HealthKitConstants.baselineWindowDays,
                minimumSamples: HealthKitConstants.minimumBaselineSamples
            ) {
                total += 1
                let z = BaselineEngine.zScore(value: currentSkinTemp, baseline: baseline)
                if abs(z) <= threshold {
                    inRange += 1
                }
            }
        }

        metricsInRange = inRange
        totalMetrics = total
    }
}
