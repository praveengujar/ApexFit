import Foundation

enum SleepGoal: String, CaseIterable {
    case peak
    case perform
    case getBy

    var multiplier: Double {
        let goals = ConfigurationManager.shared.config.sleepPlanner.goalMultipliers
        switch self {
        case .peak: return goals.peak
        case .perform: return goals.perform
        case .getBy: return goals.getBy
        }
    }

    var label: String {
        switch self {
        case .peak: return "Peak"
        case .perform: return "Perform"
        case .getBy: return "Get By"
        }
    }

    var description: String {
        switch self {
        case .peak: return "Full sleep need for maximum recovery"
        case .perform: return "Solid sleep for a good recovery day"
        case .getBy: return "Minimum viable sleep — reduced recovery"
        }
    }
}

struct SleepPlannerResult {
    let sleepNeedHours: Double
    let requiredSleepDuration: Double
    let recommendedBedtime: Date
    let expectedWakeTime: Date
    let goal: SleepGoal
    let baselineNeed: Double
    let strainSupplement: Double
    let debtRepayment: Double
    let napCredit: Double
}

struct SleepPlannerEngine {
    /// Compute recommended bedtime and required sleep duration.
    static func plan(
        sleepNeedHours: Double,
        goal: SleepGoal,
        desiredWakeTime: Date,
        estimatedOnsetLatencyMinutes: Double = HealthKitConstants.defaultSleepOnsetLatencyMinutes,
        baselineNeed: Double = HealthKitConstants.defaultSleepBaselineHours,
        strainSupplement: Double = 0,
        debtRepayment: Double = 0,
        napCredit: Double = 0
    ) -> SleepPlannerResult {
        let requiredSleep = sleepNeedHours * goal.multiplier
        let totalTimeInBed = requiredSleep + (estimatedOnsetLatencyMinutes / 60.0)
        let bedtime = desiredWakeTime.addingTimeInterval(-totalTimeInBed * 3600)

        return SleepPlannerResult(
            sleepNeedHours: sleepNeedHours,
            requiredSleepDuration: requiredSleep,
            recommendedBedtime: bedtime,
            expectedWakeTime: desiredWakeTime,
            goal: goal,
            baselineNeed: baselineNeed,
            strainSupplement: strainSupplement,
            debtRepayment: debtRepayment,
            napCredit: napCredit
        )
    }

    /// Estimate wake time from user's historical data (14-day average).
    static func estimateWakeTime(from recentWakeTimes: [Date]) -> Date {
        guard !recentWakeTimes.isEmpty else {
            // Default to 7:00 AM tomorrow
            var components = Calendar.current.dateComponents([.year, .month, .day], from: Date().tomorrow)
            components.hour = 7
            components.minute = 0
            return Calendar.current.date(from: components) ?? Date().tomorrow
        }

        // Average the time-of-day component
        let totalMinutesFromMidnight = recentWakeTimes.reduce(0.0) { sum, date in
            let components = Calendar.current.dateComponents([.hour, .minute], from: date)
            return sum + Double((components.hour ?? 7) * 60 + (components.minute ?? 0))
        }
        let avgMinutes = Int(totalMinutesFromMidnight / Double(recentWakeTimes.count))

        var components = Calendar.current.dateComponents([.year, .month, .day], from: Date().tomorrow)
        components.hour = avgMinutes / 60
        components.minute = avgMinutes % 60
        return Calendar.current.date(from: components) ?? Date().tomorrow
    }

    /// Estimate sleep onset latency from historical data.
    static func estimateOnsetLatency(from historicalLatencies: [Double]) -> Double {
        guard !historicalLatencies.isEmpty else {
            return HealthKitConstants.defaultSleepOnsetLatencyMinutes
        }
        return historicalLatencies.reduce(0, +) / Double(historicalLatencies.count)
    }
}
