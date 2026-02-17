import Foundation
import HealthKit

/// Research-backed sleep analysis engine.
///
/// **Composite Sleep Score** (WHOOP-inspired, 0-100 scale):
/// - Sufficiency (50%): actual sleep / sleep need × 100
/// - Efficiency (25%): total sleep time / time in bed × 100
/// - Consistency (15%): bedtime/wake regularity over 4 nights
/// - Disturbances (10%): awakenings per hour (fewer = better)
///
/// **Sleep Need** (WHOOP model):
/// `need = baseline + strainSupplement + debtRepayment - napCredit`
///
/// **Sources:**
/// - WHOOP Sleep Performance: sufficiency + consistency + efficiency + sleep stress
/// - Athlytic: efficiency-weighted performance with 60-day baselines
/// - Academic: Sleep Regularity Index (SRI) for consistency, PMC9491430
/// - NSF: restorative sleep (deep + REM) target 40-50% of total
struct SleepAnalysisResult {
    let mainSleep: SleepSessionData?
    let naps: [SleepSessionData]
    let totalSleepHours: Double
    let sleepNeedHours: Double
    let sleepPerformance: Double      // Sufficiency only (0-100)
    let sleepDebtHours: Double

    // Research-backed composite scoring
    let sleepScore: Double            // Composite (0-100): sufficiency + efficiency + consistency + disturbances
    let sleepEfficiency: Double       // TST / TIB × 100
    let sleepConsistency: Double      // Bedtime/wake regularity (0-100)
    let restorativeSleepPct: Double   // (deep + REM) / total × 100
    let disturbancesPerHour: Double   // Awakenings per hour
    let deepSleepPct: Double          // Deep / total × 100
    let remSleepPct: Double           // REM / total × 100
}

struct SleepSessionData {
    let startDate: Date
    let endDate: Date
    let totalSleepMinutes: Double
    let timeInBedMinutes: Double
    let lightMinutes: Double
    let deepMinutes: Double
    let remMinutes: Double
    let awakeMinutes: Double
    let awakenings: Int
    let sleepOnsetLatencyMinutes: Double?
    let sleepEfficiency: Double
    let stages: [SleepStageData]
}

struct SleepStageData {
    let type: SleepStageType
    let startDate: Date
    let endDate: Date
    let durationMinutes: Double
}

/// Recent sleep timing data for consistency computation.
struct SleepConsistencyInput {
    let recentBedtimes: [Date]   // Past 3-4 nights
    let recentWakeTimes: [Date]  // Past 3-4 nights
}

struct SleepEngine {
    // MARK: - Composite Sleep Score Weights (config-driven)
    private static var sufficiencyWeight: Double { ConfigurationManager.shared.config.sleep.compositeWeights.sufficiency }
    private static var efficiencyWeight: Double { ConfigurationManager.shared.config.sleep.compositeWeights.efficiency }
    private static var consistencyWeight: Double { ConfigurationManager.shared.config.sleep.compositeWeights.consistency }
    private static var disturbanceWeight: Double { ConfigurationManager.shared.config.sleep.compositeWeights.disturbances }

    /// Consistency window: number of recent nights to compare against.
    private static var consistencyWindowNights: Int { ConfigurationManager.shared.config.sleep.consistencyWindowNights }

    /// Parse HealthKit sleep category samples into structured sleep sessions.
    static func parseSleepSamples(_ samples: [HKCategorySample]) -> [SleepSessionData] {
        guard !samples.isEmpty else { return [] }

        let sorted = samples.sorted { $0.startDate < $1.startDate }

        var sessions: [[HKCategorySample]] = []
        var currentSession: [HKCategorySample] = [sorted[0]]

        for i in 1..<sorted.count {
            let gap = sorted[i].startDate.timeIntervalSince(sorted[i - 1].endDate)
            if gap < HealthKitConstants.sleepSessionGapToleranceMinutes * 60 {
                currentSession.append(sorted[i])
            } else {
                sessions.append(currentSession)
                currentSession = [sorted[i]]
            }
        }
        sessions.append(currentSession)

        return sessions.compactMap { buildSessionData(from: $0) }
    }

    /// Identify main sleep and naps from parsed sessions.
    static func classifySessions(_ sessions: [SleepSessionData]) -> (main: SleepSessionData?, naps: [SleepSessionData]) {
        guard !sessions.isEmpty else { return (nil, []) }

        let sorted = sessions.sorted { $0.totalSleepMinutes > $1.totalSleepMinutes }
        let main = sorted.first
        let naps = Array(sorted.dropFirst()).filter {
            $0.totalSleepMinutes >= HealthKitConstants.minimumSleepDurationMinutes &&
            $0.totalSleepMinutes <= HealthKitConstants.maximumNapDurationHours * 60
        }

        return (main, naps)
    }

    // MARK: - Sleep Need (WHOOP Model)

    /// Compute dynamic sleep need.
    ///
    /// `need = baseline + strainSupplement + debtRepayment - napCredit`
    ///
    /// **Strain supplements** (research-backed escalation):
    /// - Strain < 8: +0h (light day)
    /// - Strain 8-14: +0.25h (moderate)
    /// - Strain 14-18: +0.5h (high)
    /// - Strain 18+: +0.75h (overreaching)
    ///
    /// **Debt repayment:** 20% of accumulated debt per night (gradual payoff).
    /// **Nap credit:** Capped at 1.5h to prevent "nap replaces sleep" behavior.
    static func computeSleepNeed(
        baselineHours: Double,
        todayStrain: Double,
        sleepDebtHours: Double,
        napHoursToday: Double
    ) -> Double {
        let supplements = ConfigurationManager.shared.config.sleep.strainSupplements
        let strainSupplement = supplements.first(where: { todayStrain < $0.strainBelow })?.addHours ?? 0

        let debtRepayment = sleepDebtHours * ConfigurationManager.shared.config.sleep.debtRepaymentRate
        let napCredit = Swift.min(napHoursToday, HealthKitConstants.napCreditCapHours)

        return baselineHours + strainSupplement + debtRepayment - napCredit
    }

    /// Sleep Sufficiency (Performance): actual / need × 100.
    static func computeSleepPerformance(actualSleepHours: Double, sleepNeedHours: Double) -> Double {
        guard sleepNeedHours > 0 else { return 0 }
        return ((actualSleepHours / sleepNeedHours) * 100).clamped(to: 0...100)
    }

    /// Accumulated sleep debt over past 7 days (rolling sum of deficits).
    static func computeSleepDebt(pastWeekSleepHours: [Double], pastWeekSleepNeeds: [Double]) -> Double {
        var debt: Double = 0
        for i in 0..<Swift.min(pastWeekSleepHours.count, pastWeekSleepNeeds.count) {
            let deficit = pastWeekSleepNeeds[i] - pastWeekSleepHours[i]
            debt += Swift.max(0, deficit)
        }
        return debt
    }

    // MARK: - Composite Sleep Score Components

    /// Bedtime/wake time consistency over recent nights.
    ///
    /// Uses standard deviation of sleep/wake times across a 4-night window.
    /// Lower variability = more consistent circadian rhythm = higher score.
    ///
    /// **Scoring:** `100 × exp(-avgStdDev / 60)`
    /// - 0 min std → 100 (perfectly consistent)
    /// - 30 min std → ~61 (moderate variability)
    /// - 60 min std → ~37 (high variability)
    /// - 120+ min std → ~14 (very irregular)
    ///
    /// **Source:** Sleep Regularity Index (SRI), PMC9491430. Regular sleep timing
    /// correlates with better cardiovascular health, mood, and cognitive performance.
    static func computeSleepConsistency(
        currentBedtime: Date,
        currentWakeTime: Date,
        recentBedtimes: [Date],
        recentWakeTimes: [Date]
    ) -> Double {
        guard !recentBedtimes.isEmpty else { return 100.0 }

        let allBedtimes = recentBedtimes + [currentBedtime]
        let allWakeTimes = recentWakeTimes + [currentWakeTime]

        let bedtimeMinutes = allBedtimes.map { minutesSinceMidnight($0) }
        let wakeTimeMinutes = allWakeTimes.map { minutesSinceMidnight($0) }

        let bedtimeStd = standardDeviationOf(bedtimeMinutes)
        let wakeTimeStd = standardDeviationOf(wakeTimeMinutes)

        let avgStd = (bedtimeStd + wakeTimeStd) / 2.0

        // Exponential decay: score = 100 × exp(-std / 60)
        let score = 100.0 * exp(-avgStd / ConfigurationManager.shared.config.sleep.consistencyDecayTau)
        return score.clamped(to: 0...100)
    }

    /// Percentage of sleep in restorative stages (Deep + REM).
    ///
    /// **Target:** 40-50% for optimal recovery.
    /// - Deep sleep: tissue repair, immune function, memory consolidation
    /// - REM sleep: emotional processing, learning, creativity
    ///
    /// **Source:** National Sleep Foundation; academic consensus on restorative sleep.
    static func computeRestorativeSleepPct(session: SleepSessionData) -> Double {
        guard session.totalSleepMinutes > 0 else { return 0 }
        return ((session.deepMinutes + session.remMinutes) / session.totalSleepMinutes) * 100
    }

    /// Disturbances per hour of sleep.
    ///
    /// **Normal:** 0-2 awakenings/hr. **Elevated:** 3-4/hr. **High:** 5+/hr.
    static func computeDisturbancesPerHour(session: SleepSessionData) -> Double {
        let hours = session.totalSleepMinutes / 60.0
        guard hours > 0 else { return 0 }
        return Double(session.awakenings) / hours
    }

    /// Composite Sleep Score combining four research-backed components (0-100).
    ///
    /// **Weights (WHOOP-inspired):**
    /// - Sufficiency (50%): Did you get enough sleep?
    /// - Efficiency (25%): How much of time in bed was sleep?
    /// - Consistency (15%): Regular bedtime/wake times?
    /// - Disturbances (10%): How often were you disturbed?
    static func computeCompositeSleepScore(
        sufficiency: Double,
        efficiency: Double,
        consistency: Double,
        disturbancesPerHour: Double
    ) -> Double {
        // Disturbance score: 0 awakenings/hr → 100, 5+/hr → 0
        let disturbanceScore = Swift.max(0, Swift.min(100, 100 - disturbancesPerHour * ConfigurationManager.shared.config.sleep.disturbanceScaling))

        let score = sufficiencyWeight * sufficiency
                  + efficiencyWeight * efficiency
                  + consistencyWeight * consistency
                  + disturbanceWeight * disturbanceScore

        return score.clamped(to: 0...100)
    }

    // MARK: - Full Analysis

    /// Complete sleep analysis with composite scoring.
    static func analyze(
        samples: [HKCategorySample],
        baselineSleepHours: Double,
        todayStrain: Double,
        pastWeekSleepHours: [Double],
        pastWeekSleepNeeds: [Double],
        consistencyInput: SleepConsistencyInput = SleepConsistencyInput(recentBedtimes: [], recentWakeTimes: [])
    ) -> SleepAnalysisResult {
        let sessions = parseSleepSamples(samples)
        let (main, naps) = classifySessions(sessions)

        let totalMainSleep = main?.totalSleepMinutes ?? 0
        let napHours = naps.reduce(0) { $0 + $1.totalSleepMinutes } / 60.0
        let totalSleepHours = (totalMainSleep / 60.0) + napHours

        let sleepDebt = computeSleepDebt(pastWeekSleepHours: pastWeekSleepHours, pastWeekSleepNeeds: pastWeekSleepNeeds)
        let sleepNeed = computeSleepNeed(
            baselineHours: baselineSleepHours,
            todayStrain: todayStrain,
            sleepDebtHours: sleepDebt,
            napHoursToday: napHours
        )
        let performance = computeSleepPerformance(actualSleepHours: totalSleepHours, sleepNeedHours: sleepNeed)

        // Compute composite components from main sleep session
        let efficiency = main?.sleepEfficiency ?? 0
        let restorativePct = main.map { computeRestorativeSleepPct(session: $0) } ?? 0
        let disturbances = main.map { computeDisturbancesPerHour(session: $0) } ?? 0
        let deepPct = main.map { session -> Double in
            guard session.totalSleepMinutes > 0 else { return 0 }
            return (session.deepMinutes / session.totalSleepMinutes) * 100
        } ?? 0
        let remPct = main.map { session -> Double in
            guard session.totalSleepMinutes > 0 else { return 0 }
            return (session.remMinutes / session.totalSleepMinutes) * 100
        } ?? 0

        // Consistency: compare current bedtime/wake against recent nights
        let consistency: Double
        if let mainSleep = main {
            consistency = computeSleepConsistency(
                currentBedtime: mainSleep.startDate,
                currentWakeTime: mainSleep.endDate,
                recentBedtimes: consistencyInput.recentBedtimes,
                recentWakeTimes: consistencyInput.recentWakeTimes
            )
        } else {
            consistency = 100.0
        }

        // Composite score
        let sleepScore = computeCompositeSleepScore(
            sufficiency: performance,
            efficiency: efficiency,
            consistency: consistency,
            disturbancesPerHour: disturbances
        )

        return SleepAnalysisResult(
            mainSleep: main,
            naps: naps,
            totalSleepHours: totalSleepHours,
            sleepNeedHours: sleepNeed,
            sleepPerformance: performance,
            sleepDebtHours: sleepDebt,
            sleepScore: sleepScore,
            sleepEfficiency: efficiency,
            sleepConsistency: consistency,
            restorativeSleepPct: restorativePct,
            disturbancesPerHour: disturbances,
            deepSleepPct: deepPct,
            remSleepPct: remPct
        )
    }

    // MARK: - Private Helpers

    private static func buildSessionData(from samples: [HKCategorySample]) -> SleepSessionData? {
        guard let first = samples.first, let last = samples.last else { return nil }

        let startDate = first.startDate
        let endDate = last.endDate
        let timeInBed = endDate.timeIntervalSince(startDate) / 60.0

        var lightMinutes = 0.0
        var deepMinutes = 0.0
        var remMinutes = 0.0
        var awakeMinutes = 0.0
        var awakenings = 0
        var stages: [SleepStageData] = []
        var firstSleepDate: Date?

        for sample in samples {
            let duration = sample.endDate.timeIntervalSince(sample.startDate) / 60.0
            let stageType: SleepStageType

            switch sample.value {
            case HKCategoryValueSleepAnalysis.asleepCore.rawValue:
                lightMinutes += duration
                stageType = .light
                if firstSleepDate == nil { firstSleepDate = sample.startDate }
            case HKCategoryValueSleepAnalysis.asleepDeep.rawValue:
                deepMinutes += duration
                stageType = .deep
                if firstSleepDate == nil { firstSleepDate = sample.startDate }
            case HKCategoryValueSleepAnalysis.asleepREM.rawValue:
                remMinutes += duration
                stageType = .rem
                if firstSleepDate == nil { firstSleepDate = sample.startDate }
            case HKCategoryValueSleepAnalysis.awake.rawValue:
                awakeMinutes += duration
                awakenings += 1
                stageType = .awake
            case HKCategoryValueSleepAnalysis.inBed.rawValue:
                stageType = .inBed
            default:
                lightMinutes += duration
                stageType = .light
                if firstSleepDate == nil { firstSleepDate = sample.startDate }
            }

            stages.append(SleepStageData(
                type: stageType,
                startDate: sample.startDate,
                endDate: sample.endDate,
                durationMinutes: duration
            ))
        }

        let totalSleep = lightMinutes + deepMinutes + remMinutes
        guard totalSleep >= HealthKitConstants.minimumSleepDurationMinutes else { return nil }

        let onsetLatency: Double?
        if let inBedSample = samples.first(where: { $0.value == HKCategoryValueSleepAnalysis.inBed.rawValue }),
           let firstSleep = firstSleepDate {
            onsetLatency = firstSleep.timeIntervalSince(inBedSample.startDate) / 60.0
        } else {
            onsetLatency = nil
        }

        let efficiency = timeInBed > 0 ? (totalSleep / timeInBed) * 100 : 0

        return SleepSessionData(
            startDate: startDate,
            endDate: endDate,
            totalSleepMinutes: totalSleep,
            timeInBedMinutes: timeInBed,
            lightMinutes: lightMinutes,
            deepMinutes: deepMinutes,
            remMinutes: remMinutes,
            awakeMinutes: awakeMinutes,
            awakenings: awakenings,
            sleepOnsetLatencyMinutes: onsetLatency,
            sleepEfficiency: efficiency,
            stages: stages
        )
    }

    /// Convert a date to minutes since midnight, handling late-night times.
    /// Bedtimes after 6 PM are treated as negative (before midnight) for consistency math.
    private static func minutesSinceMidnight(_ date: Date) -> Double {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.hour, .minute], from: date)
        var minutes = Double(components.hour ?? 0) * 60 + Double(components.minute ?? 0)
        // Late-night bedtimes (after 6 PM) wrapped to negative for proximity to midnight
        if minutes > 18 * 60 {
            minutes -= 24 * 60
        }
        return minutes
    }

    private static func standardDeviationOf(_ values: [Double]) -> Double {
        guard values.count > 1 else { return 0 }
        let mean = values.reduce(0, +) / Double(values.count)
        let variance = values.map { pow($0 - mean, 2) }.reduce(0, +) / Double(values.count)
        return sqrt(variance)
    }
}
