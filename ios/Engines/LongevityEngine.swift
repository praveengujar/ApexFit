import Foundation

/// Computes biological age ("ApexFit Age") and Pace of Aging using the Gompertz
/// mortality law and 9 health metrics mapped through epidemiological hazard ratios.
///
/// The Gompertz law models mortality risk as h(t) = a·exp(b·t), where mortality
/// doubles every ~8 years (b ≈ 0.085–0.095). Converting hazard ratios to effective
/// age delta: delta_years = ln(HR) / b.
///
/// Research basis:
/// - Gompertz-Makeham law of mortality (1825)
/// - Sleep regularity & mortality: Windred et al. 2024 (SLEEP)
/// - Sleep duration & mortality: Cappuccio et al. 2010 (meta-analysis)
/// - Steps & mortality: Paluch et al. 2022 (Lancet Public Health)
/// - Exercise & mortality: Arem et al. 2015 (JAMA Internal Medicine)
/// - Strength training: Momma et al. 2022 (BJSM)
/// - VO2 Max: Mandsager et al. 2018 (JAMA Network Open)
/// - RHR: Zhang et al. 2016 (CMAJ)
/// - Lean body mass: Liu et al. 2023 (PLOS ONE)
struct LongevityEngine {

    // MARK: - Constants

    /// Gompertz slope parameter (mortality doubling rate per year).
    static let gompertzB: Double = 0.09

    /// Overlap correction to avoid double-counting correlated metrics.
    static let overlapCorrection: Double = 0.85

    // MARK: - Metric Identification

    enum MetricID: String, CaseIterable, Identifiable {
        case sleepConsistency
        case hoursOfSleep
        case hrZones1to3Weekly
        case hrZones4to5Weekly
        case strengthActivityWeekly
        case dailySteps
        case vo2Max
        case restingHeartRate
        case leanBodyMass

        var id: String { rawValue }

        var category: Category {
            switch self {
            case .sleepConsistency, .hoursOfSleep: return .sleep
            case .hrZones1to3Weekly, .hrZones4to5Weekly, .strengthActivityWeekly, .dailySteps: return .strain
            case .vo2Max, .restingHeartRate, .leanBodyMass: return .fitness
            }
        }

        var displayName: String {
            switch self {
            case .sleepConsistency: return "SLEEP CONSISTENCY"
            case .hoursOfSleep: return "HOURS OF SLEEP"
            case .hrZones1to3Weekly: return "TIME IN HR ZONES 1-3 (WEEKLY)"
            case .hrZones4to5Weekly: return "TIME IN HR ZONES 4-5 (WEEKLY)"
            case .strengthActivityWeekly: return "STRENGTH ACTIVITY TIME (WEEKLY)"
            case .dailySteps: return "STEPS"
            case .vo2Max: return "VO\u{2082} MAX"
            case .restingHeartRate: return "RHR"
            case .leanBodyMass: return "LEAN BODY MASS"
            }
        }

        var unit: String {
            switch self {
            case .sleepConsistency: return "%"
            case .hoursOfSleep: return "h"
            case .hrZones1to3Weekly, .hrZones4to5Weekly, .strengthActivityWeekly: return "h"
            case .dailySteps: return "Steps"
            case .vo2Max: return "ml/kg/min"
            case .restingHeartRate: return "bpm"
            case .leanBodyMass: return "%"
            }
        }

        /// Range for the gradient bar display (min...max).
        var gradientRange: ClosedRange<Double> {
            switch self {
            case .sleepConsistency: return 40...100
            case .hoursOfSleep: return 5...8
            case .hrZones1to3Weekly: return 0...5     // hours
            case .hrZones4to5Weekly: return 0...1     // hours
            case .strengthActivityWeekly: return 0...2 // hours
            case .dailySteps: return 0...16000
            case .vo2Max: return 15...70
            case .restingHeartRate: return 40...80
            case .leanBodyMass: return 60...95
            }
        }

        /// Whether higher values are beneficial (lower mortality risk).
        var isHigherBetter: Bool {
            switch self {
            case .restingHeartRate: return false // Lower RHR is better
            default: return true
            }
        }
    }

    enum Category: String, CaseIterable {
        case sleep, strain, fitness

        var displayName: String {
            switch self {
            case .sleep: return "Sleep"
            case .strain: return "Strain"
            case .fitness: return "Fitness"
            }
        }
    }

    // MARK: - Input / Output Types

    struct MetricInput {
        let id: MetricID
        let sixMonthAvg: Double?
        let thirtyDayAvg: Double?
    }

    struct MetricResult {
        let id: MetricID
        let sixMonthAvg: Double
        let thirtyDayAvg: Double
        let hazardRatio: Double
        let deltaYears: Double
        let insightTitle: String
        let insightBody: String
    }

    struct Result {
        let chronologicalAge: Double
        let apexFitAge: Double
        let yearsYoungerOlder: Double   // negative = younger
        let paceOfAging: Double         // -1.0 to 3.0
        let metricResults: [MetricResult]
        let weekStart: Date
        let weekEnd: Date
        let overallInsightTitle: String
        let overallInsightBody: String
    }

    // MARK: - Core Computation

    /// Compute the full longevity result from 9 metric inputs.
    static func compute(
        chronologicalAge: Double,
        inputs: [MetricInput],
        weekStart: Date = Date().daysAgo(7),
        weekEnd: Date = Date()
    ) -> Result {
        var metricResults: [MetricResult] = []
        var totalDelta6Mo: Double = 0
        var totalDelta30Day: Double = 0
        var valid6MoCount = 0
        var valid30DayCount = 0

        for input in inputs {
            let avg6Mo = input.sixMonthAvg ?? input.thirtyDayAvg
            let avg30Day = input.thirtyDayAvg ?? input.sixMonthAvg

            guard let val6Mo = avg6Mo else { continue }
            let val30Day = avg30Day ?? val6Mo

            let hr6Mo = hazardRatio(for: input.id, value: val6Mo)
            let hr30Day = hazardRatio(for: input.id, value: val30Day)
            let delta6Mo = deltaYears(from: hr6Mo)
            let delta30Day = deltaYears(from: hr30Day)

            totalDelta6Mo += delta6Mo
            totalDelta30Day += delta30Day
            valid6MoCount += 1
            valid30DayCount += 1

            let insight = generateMetricInsight(id: input.id, value: val6Mo, deltaYears: delta6Mo)

            metricResults.append(MetricResult(
                id: input.id,
                sixMonthAvg: val6Mo,
                thirtyDayAvg: val30Day,
                hazardRatio: hr6Mo,
                deltaYears: delta6Mo,
                insightTitle: insight.title,
                insightBody: insight.body
            ))
        }

        // Apply overlap correction
        totalDelta6Mo *= overlapCorrection
        totalDelta30Day *= overlapCorrection

        let apexFitAge = chronologicalAge + totalDelta6Mo
        let yearsYoungerOlder = totalDelta6Mo // negative = younger
        let projectedAge30 = chronologicalAge + totalDelta30Day

        // Pace of Aging: map difference between 30-day projection and 6-month age
        // If 30-day is better (lower age) than 6-month → pace < 1.0
        // If same → pace = 1.0, if worse → pace > 1.0
        let ageDiff = projectedAge30 - apexFitAge
        // Map: -5 years diff → -1.0x, 0 diff → 1.0x, +5 years diff → 3.0x
        let rawPace = 1.0 + (ageDiff / 2.5)
        let paceOfAging = max(-1.0, min(3.0, rawPace))

        let overallInsight = generateOverallInsight(
            yearsYoungerOlder: yearsYoungerOlder,
            paceOfAging: paceOfAging,
            metricResults: metricResults
        )

        return Result(
            chronologicalAge: chronologicalAge,
            apexFitAge: apexFitAge,
            yearsYoungerOlder: yearsYoungerOlder,
            paceOfAging: paceOfAging,
            metricResults: metricResults,
            weekStart: weekStart,
            weekEnd: weekEnd,
            overallInsightTitle: overallInsight.title,
            overallInsightBody: overallInsight.body
        )
    }

    // MARK: - Hazard Ratio Computation

    /// Convert hazard ratio to delta years via Gompertz formula.
    /// HR < 1 → negative delta (younger), HR > 1 → positive delta (older).
    static func deltaYears(from hr: Double) -> Double {
        guard hr > 0 else { return 0 }
        return log(hr) / gompertzB
    }

    /// Compute hazard ratio for a given metric value using piecewise-linear dose-response curves.
    static func hazardRatio(for id: MetricID, value: Double) -> Double {
        switch id {
        case .sleepConsistency: return hrSleepConsistency(value)
        case .hoursOfSleep: return hrHoursOfSleep(value)
        case .hrZones1to3Weekly: return hrZones1to3(value)
        case .hrZones4to5Weekly: return hrZones4to5(value)
        case .strengthActivityWeekly: return hrStrengthActivity(value)
        case .dailySteps: return hrDailySteps(value)
        case .vo2Max: return hrVO2Max(value)
        case .restingHeartRate: return hrRestingHR(value)
        case .leanBodyMass: return hrLeanBodyMass(value)
        }
    }

    // MARK: - Dose-Response Curves

    /// Sleep Consistency (0-100%). Referent: 85%.
    /// Windred et al. 2024: irregular sleep → 20-48% higher mortality.
    private static func hrSleepConsistency(_ pct: Double) -> Double {
        // Breakpoints: [40%, 50%, 60%, 70%, 85%, 100%] → [1.48, 1.40, 1.20, 1.10, 1.0, 0.92]
        let points: [(Double, Double)] = [
            (40, 1.48), (50, 1.40), (60, 1.20), (70, 1.10), (85, 1.0), (100, 0.92)
        ]
        return interpolate(value: pct, points: points)
    }

    /// Hours of Sleep (daily). Referent: 7h. U-shaped curve.
    /// Cappuccio et al. 2010: short sleep HR ~1.12, long sleep HR ~1.30.
    private static func hrHoursOfSleep(_ hours: Double) -> Double {
        let points: [(Double, Double)] = [
            (4, 1.20), (5, 1.14), (6, 1.07), (7, 1.0), (8, 0.98), (9, 1.0), (10, 1.10)
        ]
        return interpolate(value: hours, points: points)
    }

    /// HR Zones 1-3 Weekly (hours of moderate activity). Referent: 2.5h (150min/wk).
    /// Arem et al. 2015: 150-300min/wk → HR 0.79.
    private static func hrZones1to3(_ hours: Double) -> Double {
        let points: [(Double, Double)] = [
            (0, 1.0), (1.0, 0.90), (2.5, 0.79), (5.0, 0.78), (8.0, 0.78)
        ]
        return interpolate(value: hours, points: points)
    }

    /// HR Zones 4-5 Weekly (hours of vigorous activity). Referent: 1.25h (75min/wk).
    /// Lee et al. 2022: 75-150min vigorous → 20-23% reduction.
    private static func hrZones4to5(_ hours: Double) -> Double {
        let points: [(Double, Double)] = [
            (0, 1.0), (0.5, 0.88), (1.25, 0.77), (2.5, 0.77), (4.0, 0.80)
        ]
        return interpolate(value: hours, points: points)
    }

    /// Strength Activity Weekly (hours). Referent: 0.5h (30min/wk).
    /// Momma et al. 2022: 30-60min/wk max benefit HR 0.73, any → HR 0.85.
    private static func hrStrengthActivity(_ hours: Double) -> Double {
        let points: [(Double, Double)] = [
            (0, 1.0), (0.5, 0.85), (1.0, 0.73), (1.5, 0.75), (3.0, 0.80)
        ]
        return interpolate(value: hours, points: points)
    }

    /// Daily Steps. Referent: 8000.
    /// Paluch et al. 2022: each 1000 steps → 15% lower mortality up to ~10K plateau.
    private static func hrDailySteps(_ steps: Double) -> Double {
        let points: [(Double, Double)] = [
            (0, 1.30), (2000, 1.18), (4000, 1.06), (6000, 0.90),
            (8000, 0.78), (10000, 0.68), (12000, 0.65), (16000, 0.65)
        ]
        return interpolate(value: steps, points: points)
    }

    /// VO2 Max (ml/kg/min). Age/sex adjusted referent ~40.
    /// Mandsager et al. 2018: each 3.5 ml/kg/min (1 MET) → ~14% lower mortality.
    /// Kodama et al. 2009: each MET → 13% lower all-cause.
    private static func hrVO2Max(_ vo2: Double) -> Double {
        let points: [(Double, Double)] = [
            (15, 2.00), (20, 1.70), (25, 1.40), (30, 1.15), (35, 1.0),
            (40, 0.86), (45, 0.74), (50, 0.64), (55, 0.55), (60, 0.50), (70, 0.45)
        ]
        return interpolate(value: vo2, points: points)
    }

    /// Resting Heart Rate (bpm). Referent: 60 bpm. Lower is better.
    /// Zhang et al. 2016: each 10 bpm increase → HR 1.09, >80 → HR 1.45.
    private static func hrRestingHR(_ bpm: Double) -> Double {
        // Note: lower BPM = lower HR (better)
        let points: [(Double, Double)] = [
            (40, 0.82), (45, 0.85), (50, 0.90), (55, 0.95), (60, 1.0),
            (65, 1.05), (70, 1.09), (75, 1.20), (80, 1.45), (90, 1.65)
        ]
        return interpolate(value: bpm, points: points)
    }

    /// Lean Body Mass (%). Referent: 80%.
    /// Liu et al. 2023: low muscle (<70%) → HR 1.57.
    private static func hrLeanBodyMass(_ pct: Double) -> Double {
        let points: [(Double, Double)] = [
            (55, 1.70), (60, 1.57), (65, 1.30), (70, 1.10), (75, 1.0),
            (80, 0.95), (85, 0.90), (90, 0.88), (95, 0.88)
        ]
        return interpolate(value: pct, points: points)
    }

    // MARK: - Interpolation

    /// Piecewise linear interpolation between breakpoints.
    private static func interpolate(value: Double, points: [(Double, Double)]) -> Double {
        guard !points.isEmpty else { return 1.0 }
        if value <= points.first!.0 { return points.first!.1 }
        if value >= points.last!.0 { return points.last!.1 }

        for i in 0..<(points.count - 1) {
            let (x0, y0) = points[i]
            let (x1, y1) = points[i + 1]
            if value >= x0 && value <= x1 {
                let t = (value - x0) / (x1 - x0)
                return y0 + t * (y1 - y0)
            }
        }
        return 1.0
    }

    // MARK: - Insight Generation

    private static func generateMetricInsight(
        id: MetricID,
        value: Double,
        deltaYears: Double
    ) -> (title: String, body: String) {
        let isGood = deltaYears < -0.3
        let isBad = deltaYears > 0.3

        if isGood {
            switch id {
            case .sleepConsistency:
                return ("Well Done", "Your sleep consistency is helping extend your healthspan. Maintaining a regular schedule is one of the strongest longevity factors.")
            case .hoursOfSleep:
                return ("Optimal Sleep", "You're getting enough sleep to support recovery and long-term health. Keep it up.")
            case .hrZones1to3Weekly:
                return ("Active Lifestyle", "Your weekly moderate activity is well within the range linked to reduced all-cause mortality.")
            case .hrZones4to5Weekly:
                return ("High Intensity Pay-Off", "Your vigorous exercise is contributing to cardiovascular fitness and longevity.")
            case .strengthActivityWeekly:
                return ("Building Strength", "Resistance training is strongly linked to longevity. Your weekly volume is in the optimal zone.")
            case .dailySteps:
                return ("Keep Moving", "Your daily step count is associated with significant mortality risk reduction.")
            case .vo2Max:
                return ("Elite Fitness", "Your cardiorespiratory fitness is a powerful predictor of longevity — stronger than smoking status.")
            case .restingHeartRate:
                return ("Strong Heart", "A low resting heart rate reflects excellent cardiovascular efficiency.")
            case .leanBodyMass:
                return ("Lean & Strong", "Maintaining lean body mass is crucial for metabolic health and longevity.")
            }
        } else if isBad {
            switch id {
            case .sleepConsistency:
                return ("Time to Reassess", "Your sleep consistency is below the recommended range. Irregular sleep patterns are associated with increased mortality risk.")
            case .hoursOfSleep:
                return ("Sleep More", "Your sleep duration is below the 7-hour threshold linked to optimal health outcomes.")
            case .hrZones1to3Weekly:
                return ("Move More", "Increasing moderate activity to 150+ minutes per week could significantly reduce your mortality risk.")
            case .hrZones4to5Weekly:
                return ("Push Harder", "Adding vigorous exercise can provide additional cardiovascular benefits beyond moderate activity alone.")
            case .strengthActivityWeekly:
                return ("Add Resistance", "Even 30 minutes of weekly strength training is associated with 15% lower mortality risk.")
            case .dailySteps:
                return ("Step It Up", "Increasing your daily steps toward 8,000 could meaningfully impact your long-term health.")
            case .vo2Max:
                return ("Build Fitness", "Improving cardiorespiratory fitness is one of the most impactful changes you can make for longevity.")
            case .restingHeartRate:
                return ("Heart Health", "An elevated resting heart rate may indicate cardiovascular stress. Regular aerobic exercise can help lower it.")
            case .leanBodyMass:
                return ("Build Muscle", "Low lean body mass is associated with increased mortality risk. Strength training can help.")
            }
        } else {
            return ("On Track", "Your \(id.displayName.lowercased()) is near the baseline. Small improvements can shift this toward positive impact.")
        }
    }

    private static func generateOverallInsight(
        yearsYoungerOlder: Double,
        paceOfAging: Double,
        metricResults: [MetricResult]
    ) -> (title: String, body: String) {
        let isYounger = yearsYoungerOlder < -1.0
        let paceImproving = paceOfAging < 1.0

        // Find biggest positive and negative contributors
        let sorted = metricResults.sorted { $0.deltaYears < $1.deltaYears }
        let bestMetric = sorted.first
        let worstMetric = sorted.last

        if isYounger && paceImproving {
            let bestName = bestMetric?.id.displayName.lowercased() ?? "your habits"
            return ("Crushing It", "Your ApexFit Age is improving and your Pace of Aging is below 1.0x. Your \(bestName) is a major contributor to your longevity gains.")
        } else if isYounger {
            return ("Solid Foundation", "You're biologically younger than your chronological age. Keep your current habits consistent to maintain these gains.")
        } else if paceImproving {
            return ("Trending Better", "Your recent habits are pushing your Pace of Aging in the right direction. Keep the momentum going.")
        } else {
            let worstName = worstMetric?.id.displayName.lowercased() ?? "key metrics"
            return ("Room for Growth", "Focus on improving your \(worstName) — it has the largest impact on your longevity score right now.")
        }
    }

    // MARK: - Value Formatting

    /// Format a metric value for display.
    static func formatValue(_ value: Double, for id: MetricID) -> String {
        switch id {
        case .sleepConsistency, .leanBodyMass:
            return "\(Int(value))%"
        case .hoursOfSleep:
            let hours = Int(value)
            let mins = Int((value - Double(hours)) * 60)
            return "\(hours):\(String(format: "%02d", mins))"
        case .hrZones1to3Weekly, .hrZones4to5Weekly, .strengthActivityWeekly:
            let hours = Int(value)
            let mins = Int((value - Double(hours)) * 60)
            return "\(hours):\(String(format: "%02d", mins)) h"
        case .dailySteps:
            return "\(Int(value).formattedWithComma)"
        case .vo2Max:
            return "\(Int(value)) ml/kg/min"
        case .restingHeartRate:
            return "\(Int(value)) bpm"
        }
    }
}
