import Foundation
import SwiftData

@Model
final class WorkoutRecord {
    var id: UUID
    var workoutType: String
    var workoutName: String
    var startDate: Date
    var endDate: Date
    var durationMinutes: Double
    var strainScore: Double
    var averageHeartRate: Double?
    var maxHeartRate: Double?
    var activeCalories: Double
    var distanceMeters: Double?
    var zone1Minutes: Double
    var zone2Minutes: Double
    var zone3Minutes: Double
    var zone4Minutes: Double
    var zone5Minutes: Double
    var muscularLoad: Double?
    var isStrengthWorkout: Bool
    var healthKitWorkoutUUID: String?
    var createdAt: Date

    var dailyMetric: DailyMetric?

    init(
        workoutType: String,
        workoutName: String,
        startDate: Date,
        endDate: Date,
        strainScore: Double = 0
    ) {
        self.id = UUID()
        self.workoutType = workoutType
        self.workoutName = workoutName
        self.startDate = startDate
        self.endDate = endDate
        self.durationMinutes = endDate.timeIntervalSince(startDate) / 60.0
        self.strainScore = strainScore
        self.activeCalories = 0
        self.zone1Minutes = 0
        self.zone2Minutes = 0
        self.zone3Minutes = 0
        self.zone4Minutes = 0
        self.zone5Minutes = 0
        self.isStrengthWorkout = false
        self.createdAt = Date()
    }

    var totalZoneMinutes: Double {
        zone1Minutes + zone2Minutes + zone3Minutes + zone4Minutes + zone5Minutes
    }

    var primaryZone: Int {
        let zones = [zone1Minutes, zone2Minutes, zone3Minutes, zone4Minutes, zone5Minutes]
        guard let maxIndex = zones.enumerated().max(by: { $0.element < $1.element })?.offset else { return 1 }
        return maxIndex + 1
    }

    var formattedDuration: String {
        let hours = Int(durationMinutes) / 60
        let mins = Int(durationMinutes) % 60
        if hours > 0 {
            return "\(hours)h \(mins)m"
        }
        return "\(mins)m"
    }
}
