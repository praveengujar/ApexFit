import Foundation

struct BaselineResult {
    let mean: Double
    let standardDeviation: Double
    let sampleCount: Int
    let windowDays: Int
}

struct BaselineEngine {
    /// Compute a 28-day rolling baseline for a set of daily values.
    /// Falls back to shorter windows (minimum 3 samples).
    static func computeBaseline(
        values: [Double],
        windowDays: Int = HealthKitConstants.baselineWindowDays,
        minimumSamples: Int = HealthKitConstants.minimumBaselineSamples
    ) -> BaselineResult? {
        guard !values.isEmpty else { return nil }

        // Use up to windowDays most recent values
        let recentValues = Array(values.suffix(windowDays))

        guard recentValues.count >= minimumSamples else {
            // Not enough data; try with what we have if at least minimum
            if values.count >= minimumSamples {
                let fallbackValues = Array(values.suffix(minimumSamples))
                return BaselineResult(
                    mean: fallbackValues.map { $0 }.mean,
                    standardDeviation: fallbackValues.map { $0 }.standardDeviation,
                    sampleCount: fallbackValues.count,
                    windowDays: fallbackValues.count
                )
            }
            return nil
        }

        let mean = recentValues.mean
        let stddev = recentValues.standardDeviation

        return BaselineResult(
            mean: mean,
            standardDeviation: max(stddev, 0.001), // Prevent division by zero
            sampleCount: recentValues.count,
            windowDays: windowDays
        )
    }

    /// Compute z-score for a value against a baseline.
    static func zScore(value: Double, baseline: BaselineResult) -> Double {
        guard baseline.standardDeviation > 0 else { return 0 }
        return (value - baseline.mean) / baseline.standardDeviation
    }

    /// Update a running baseline with a new value.
    /// Returns the updated baseline using exponential weighting.
    static func updateBaseline(
        current: BaselineResult,
        newValue: Double,
        alpha: Double = 0.1
    ) -> BaselineResult {
        let newMean = current.mean * (1 - alpha) + newValue * alpha
        let newVariance = pow(current.standardDeviation, 2) * (1 - alpha) + pow(newValue - newMean, 2) * alpha
        let newStdDev = sqrt(max(newVariance, 0.001))

        return BaselineResult(
            mean: newMean,
            standardDeviation: newStdDev,
            sampleCount: current.sampleCount + 1,
            windowDays: current.windowDays
        )
    }
}
