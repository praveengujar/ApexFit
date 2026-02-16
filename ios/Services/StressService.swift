import Foundation
import HealthKit

/// Research-backed stress computation service using a hybrid baseline-deviation model.
///
/// **Algorithm** (based on publicly available research):
/// - WHOOP-like 0-3 scale comparing real-time HR + HRV against personalized baselines
/// - ln(RMSSD) for linear distribution (Elite HRV / industry standard)
/// - Z-score deviation from 14-day rolling baseline (academic consensus)
/// - Weighted combination: 70% HRV + 30% HR (meta-analysis: HRV more sensitive to autonomic changes)
/// - Motion filtering to distinguish exercise from stress
///
/// **Sources:**
/// - Baevsky SI: PMC10305391 — SI = AMo / (2 × Mo × MxDMn)
/// - ln(RMSSD): Elite HRV — natural log for linear distribution, range 0-6.5
/// - Z-score baseline: PMC10237460 — personalized deviation-based stress detection
/// - Meta-analysis: PMC5900369 — stress → parasympathetic withdrawal (RMSSD↓, HR↑)
/// - WHOOP: 0-3 scale, 14-day baseline, motion-filtered
@MainActor
@Observable
final class StressService {
    // MARK: - Published State

    var stressTimeline: [(timestamp: Date, score: Double)] = []
    var currentStressScore: Double?
    var currentStressLevel: String = "LOW"
    var lastUpdated: Date?

    // MARK: - Baseline State

    private(set) var baselineLnRMSSD: Double?
    private(set) var baselineLnRMSSDStd: Double?
    private(set) var baselineHR: Double?
    private(set) var baselineHRStd: Double?
    private(set) var baselineSampleCount: Int = 0

    // MARK: - Configuration

    private static var stressConfig: StressConfig { ConfigurationManager.shared.config.stress }
    private static var baselineWindowDays: Int { stressConfig.baselineWindowDays }
    private static var minimumBaselineSamples: Int { stressConfig.minimumBaselineSamples }
    private static var hrvWeight: Double { stressConfig.weights.hrv }
    private static var hrWeight: Double { stressConfig.weights.heartRate }
    private static var motionDampeningFactor: Double { stressConfig.motionDampeningFactor }
    private static var bucketInterval: TimeInterval { Double(stressConfig.bucketIntervalMinutes) * 60 }

    // MARK: - Dependencies

    private let queryService: HealthKitQueryService

    init(queryService: HealthKitQueryService = HealthKitQueryService()) {
        self.queryService = queryService
    }

    // MARK: - Baseline Computation

    /// Establishes personalized 14-day rolling baselines for ln(RMSSD) and resting HR.
    /// Uses nighttime/resting HRV readings for stability (circadian correction).
    func computeBaselines(using dailyMetrics: [DailyMetric]) {
        let sorted = dailyMetrics
            .sorted { $0.date < $1.date }
            .suffix(Self.baselineWindowDays)

        // ln(RMSSD) baseline from daily HRV SDNN values
        // SDNN correlates with RMSSD for short recordings; we use it as proxy
        let hrvValues = sorted.compactMap { metric -> Double? in
            guard let hrv = metric.hrvRMSSD, hrv > 0 else { return nil }
            return log(hrv) // ln(RMSSD) for linear distribution
        }

        // Resting HR baseline
        let hrValues = sorted.compactMap { $0.restingHeartRate }

        guard hrvValues.count >= Self.minimumBaselineSamples else {
            let defaults = Self.stressConfig.populationDefaults
            baselineLnRMSSD = defaults.lnRMSSDMean
            baselineLnRMSSDStd = defaults.lnRMSSDStd
            baselineHR = defaults.heartRateMean
            baselineHRStd = defaults.heartRateStd
            baselineSampleCount = 0
            return
        }

        let hrvMean = hrvValues.reduce(0, +) / Double(hrvValues.count)
        let hrvVariance = hrvValues.map { pow($0 - hrvMean, 2) }.reduce(0, +) / Double(hrvValues.count)
        let hrvStd = max(sqrt(hrvVariance), 0.1) // Prevent division by zero

        baselineLnRMSSD = hrvMean
        baselineLnRMSSDStd = hrvStd
        baselineSampleCount = hrvValues.count

        if hrValues.count >= Self.minimumBaselineSamples {
            let hrMean = hrValues.reduce(0, +) / Double(hrValues.count)
            let hrVariance = hrValues.map { pow($0 - hrMean, 2) }.reduce(0, +) / Double(hrValues.count)
            baselineHR = hrMean
            baselineHRStd = max(sqrt(hrVariance), 1.0)
        } else {
            let defaults = Self.stressConfig.populationDefaults
            baselineHR = defaults.heartRateMean
            baselineHRStd = defaults.heartRateStd
        }
    }

    // MARK: - Stress Timeline Computation

    /// Computes an intraday stress timeline using the hybrid baseline-deviation model.
    ///
    /// **Algorithm per data point:**
    /// 1. Compute ln(RMSSD) from HRV sample
    /// 2. Compute z-score deviation from personal baseline
    /// 3. Combine HRV z-score (70%) + HR z-score (30%)
    /// 4. Normalize to 0-3 scale
    /// 5. Group into 15-minute buckets
    func computeStressTimeline(for date: Date) async throws {
        // Fetch intraday HRV and HR samples
        let hrvSamples = try await queryService.fetchIntradayHRV(for: date)
        let hrSamples = try await queryService.fetchHeartRateSamples(
            from: date.startOfDay, to: date.endOfDay
        )

        guard !hrvSamples.isEmpty else {
            stressTimeline = []
            currentStressScore = nil
            currentStressLevel = "LOW"
            lastUpdated = Date()
            return
        }

        // Build HR lookup by rounding to nearest minute for matching
        var hrByMinute: [Int: Double] = [:]
        for sample in hrSamples {
            let minuteKey = Int(sample.date.timeIntervalSince(date.startOfDay) / 60)
            hrByMinute[minuteKey] = sample.bpm
        }

        // Detect active workout periods for motion filtering
        let workouts = try await queryService.fetchWorkouts(from: date.startOfDay, to: date.endOfDay)
        let workoutIntervals = workouts.map { ($0.startDate, $0.endDate) }

        let dayStart = date.startOfDay
        var buckets: [Date: [Double]] = [:]

        for sample in hrvSamples {
            let sdnn = sample.sdnn
            guard sdnn > 0 else { continue }

            // Step 1: ln(RMSSD) — using SDNN as proxy for short recordings
            let lnRMSSD = log(sdnn)

            // Step 2: Find corresponding HR (nearest minute)
            let minuteKey = Int(sample.date.timeIntervalSince(dayStart) / 60)
            let currentHR = hrByMinute[minuteKey]
                ?? hrByMinute[minuteKey - 1]
                ?? hrByMinute[minuteKey + 1]

            // Step 3: Compute z-scores against personal baselines
            let stressScore = computeStressScore(
                lnRMSSD: lnRMSSD,
                currentHR: currentHR,
                timestamp: sample.date,
                workoutIntervals: workoutIntervals
            )

            // Step 4: Bucket by 15-minute interval
            let offset = sample.date.timeIntervalSince(dayStart)
            let bucketIndex = Int(offset / Self.bucketInterval)
            let bucketStart = dayStart.addingTimeInterval(Double(bucketIndex) * Self.bucketInterval)

            buckets[bucketStart, default: []].append(stressScore)
        }

        // Average each bucket
        let timeline = buckets.map { (timestamp, scores) -> (timestamp: Date, score: Double) in
            let average = scores.reduce(0, +) / Double(scores.count)
            return (timestamp: timestamp, score: average)
        }.sorted { $0.timestamp < $1.timestamp }

        stressTimeline = timeline
        currentStressScore = timeline.last?.score
        currentStressLevel = Self.stressLevel(for: currentStressScore)
        lastUpdated = Date()
    }

    // MARK: - Daily Average

    func computeDailyStressAverage() -> Double? {
        guard !stressTimeline.isEmpty else { return nil }
        let total = stressTimeline.reduce(0.0) { $0 + $1.score }
        return total / Double(stressTimeline.count)
    }

    // MARK: - On-Demand Stress Reading (Baevsky SI)

    /// Computes a spot-check stress score from a set of RR intervals using
    /// Baevsky's Stress Index (SI = AMo / (2 × Mo × MxDMn)).
    ///
    /// **Reference:** PMC10305391, normal SI range: 80-150
    static func baevskySI(from rrIntervals: [TimeInterval]) -> Double? {
        guard rrIntervals.count >= 10 else { return nil }

        // Mo: Mode (most frequent RR interval)
        let binWidth = stressConfig.baevsky.binWidthSeconds
        var histogram: [Int: Int] = [:]

        for rr in rrIntervals {
            let bin = Int(rr / binWidth)
            histogram[bin, default: 0] += 1
        }

        guard let modeBin = histogram.max(by: { $0.value < $1.value }) else { return nil }

        let mo = Double(modeBin.key) * binWidth + binWidth / 2.0 // Mode in seconds
        let amo = Double(modeBin.value) / Double(rrIntervals.count) * 100.0 // Mode amplitude as %

        // MxDMn: Variation range (max RR - min RR)
        guard let maxRR = rrIntervals.max(), let minRR = rrIntervals.min() else { return nil }
        let mxDMn = maxRR - minRR

        guard mo > 0, mxDMn > 0 else { return nil }

        // SI = AMo / (2 × Mo × MxDMn)
        let si = amo / (2.0 * mo * mxDMn)

        // Normalize SI to 0-3 scale
        let baevsky = stressConfig.baevsky
        let normalized = (si / baevsky.normalizationDivisor) * baevsky.normalizationMultiplier
        return normalized.clamped(to: 0...stressConfig.normalization.outputMax)
    }

    // MARK: - Private Helpers

    /// Core stress computation combining HRV and HR z-scores.
    private func computeStressScore(
        lnRMSSD: Double,
        currentHR: Double?,
        timestamp: Date,
        workoutIntervals: [(start: Date, end: Date)]
    ) -> Double {
        guard let baseLnRMSSD = baselineLnRMSSD,
              let baseLnRMSSDStd = baselineLnRMSSDStd else {
            // No baseline available — fall back to absolute scale
            return absoluteStressFromLnRMSSD(lnRMSSD)
        }

        // Z-score for HRV (inverted: lower HRV = higher stress)
        let zHRV = (baseLnRMSSD - lnRMSSD) / baseLnRMSSDStd

        // Z-score for HR (direct: higher HR = higher stress)
        var zHR = 0.0
        if let hr = currentHR, let baseHR = baselineHR, let baseHRStd = baselineHRStd {
            zHR = (hr - baseHR) / baseHRStd
        }

        // Weighted combination
        let rawStress = Self.hrvWeight * zHRV + Self.hrWeight * zHR

        // Normalize to 0-3 scale
        let norm = Self.stressConfig.normalization
        var score = (rawStress + norm.zOffset) * (norm.outputMax / norm.zRange)

        // Motion filter: dampen during active workouts
        let isDuringWorkout = workoutIntervals.contains { interval in
            timestamp >= interval.start && timestamp <= interval.end
        }
        if isDuringWorkout {
            score *= Self.motionDampeningFactor
        }

        return score.clamped(to: 0...Self.stressConfig.normalization.outputMax)
    }

    /// Fallback stress computation when no personal baseline exists.
    /// Uses population-average ln(RMSSD) ≈ 3.5 (roughly 33ms RMSSD).
    private func absoluteStressFromLnRMSSD(_ lnRMSSD: Double) -> Double {
        let defaults = Self.stressConfig.populationDefaults
        let norm = Self.stressConfig.normalization
        let z = (defaults.lnRMSSDMean - lnRMSSD) / defaults.lnRMSSDStd
        let score = (z + norm.zOffset) * (norm.outputMax / norm.zRange)
        return score.clamped(to: 0...norm.outputMax)
    }

    /// Maps a numeric stress score to a human-readable level.
    static func stressLevel(for score: Double?) -> String {
        guard let score else { return "LOW" }
        let levels = stressConfig.levels
        for level in levels {
            if score < level.below {
                return level.label
            }
        }
        return levels.last?.label ?? "VERY HIGH"
    }
}
