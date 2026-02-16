import Foundation

struct HRVResult {
    let rmssd: Double?
    let sdnn: Double?
    let method: HRVMethod
}

enum HRVMethod {
    case rmssdFromRRIntervals
    case sdnnFromHealthKit
}

struct HRVCalculator {
    /// Compute RMSSD from raw RR intervals (in seconds).
    /// RMSSD = sqrt( (1/N) * Σ(RR[i+1] - RR[i])² )
    static func computeRMSSD(from rrIntervals: [TimeInterval]) -> Double? {
        guard rrIntervals.count > 1 else { return nil }

        // Convert timestamps to successive RR intervals
        var successiveDiffs: [Double] = []
        for i in 1..<rrIntervals.count {
            let interval = (rrIntervals[i] - rrIntervals[i - 1]) * 1000 // Convert to ms
            if interval > 200 && interval < 2000 { // Filter physiological range (30-300 BPM)
                successiveDiffs.append(interval)
            }
        }

        guard successiveDiffs.count > 1 else { return nil }

        // Compute successive differences
        var squaredDiffs: [Double] = []
        for i in 1..<successiveDiffs.count {
            let diff = successiveDiffs[i] - successiveDiffs[i - 1]
            squaredDiffs.append(diff * diff)
        }

        guard !squaredDiffs.isEmpty else { return nil }

        let meanSquaredDiff = squaredDiffs.reduce(0, +) / Double(squaredDiffs.count)
        return sqrt(meanSquaredDiff)
    }

    /// Determine best available HRV value.
    /// Prefers RMSSD from beat-to-beat data; falls back to SDNN from HealthKit.
    static func bestHRV(rrIntervals: [TimeInterval]?, sdnnValue: Double?) -> HRVResult {
        // Try RMSSD first (preferred)
        if let intervals = rrIntervals, !intervals.isEmpty {
            if let rmssd = computeRMSSD(from: intervals) {
                return HRVResult(rmssd: rmssd, sdnn: sdnnValue, method: .rmssdFromRRIntervals)
            }
        }

        // Fall back to SDNN
        if let sdnn = sdnnValue {
            return HRVResult(rmssd: nil, sdnn: sdnn, method: .sdnnFromHealthKit)
        }

        return HRVResult(rmssd: nil, sdnn: nil, method: .sdnnFromHealthKit)
    }

    /// The effective HRV value for use in recovery computation.
    /// Returns RMSSD if available, otherwise SDNN.
    static func effectiveHRV(from result: HRVResult) -> Double? {
        result.rmssd ?? result.sdnn
    }
}
