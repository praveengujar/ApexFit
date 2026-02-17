import Foundation
import SwiftData

@Model
final class SleepSession {
    var id: UUID
    var startDate: Date
    var endDate: Date
    var isMainSleep: Bool
    var isNap: Bool
    var totalSleepMinutes: Double
    var timeInBedMinutes: Double
    var lightSleepMinutes: Double
    var deepSleepMinutes: Double
    var remSleepMinutes: Double
    var awakeMinutes: Double
    var awakenings: Int
    var sleepOnsetLatencyMinutes: Double?
    var sleepEfficiency: Double
    var sleepPerformance: Double?
    var sleepNeedHours: Double?
    var createdAt: Date

    var dailyMetric: DailyMetric?

    @Relationship(deleteRule: .cascade, inverse: \SleepStage.sleepSession)
    var stages: [SleepStage]

    init(startDate: Date, endDate: Date, isMainSleep: Bool = true) {
        self.id = UUID()
        self.startDate = startDate
        self.endDate = endDate
        self.isMainSleep = isMainSleep
        self.isNap = !isMainSleep
        self.totalSleepMinutes = 0
        self.timeInBedMinutes = endDate.timeIntervalSince(startDate) / 60.0
        self.lightSleepMinutes = 0
        self.deepSleepMinutes = 0
        self.remSleepMinutes = 0
        self.awakeMinutes = 0
        self.awakenings = 0
        self.sleepEfficiency = 0
        self.createdAt = Date()
        self.stages = []
    }

    var totalSleepHours: Double {
        totalSleepMinutes / 60.0
    }

    var deepSleepPercentage: Double {
        guard totalSleepMinutes > 0 else { return 0 }
        return (deepSleepMinutes / totalSleepMinutes) * 100.0
    }

    var remSleepPercentage: Double {
        guard totalSleepMinutes > 0 else { return 0 }
        return (remSleepMinutes / totalSleepMinutes) * 100.0
    }

    var lightSleepPercentage: Double {
        guard totalSleepMinutes > 0 else { return 0 }
        return (lightSleepMinutes / totalSleepMinutes) * 100.0
    }

    var formattedDuration: String {
        let hours = Int(totalSleepMinutes) / 60
        let mins = Int(totalSleepMinutes) % 60
        return "\(hours)h \(mins)m"
    }
}
