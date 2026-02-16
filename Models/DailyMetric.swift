import Foundation
import SwiftData

@Model
final class DailyMetric {
    var id: UUID
    var date: Date
    var recoveryScore: Double?
    var recoveryZone: RecoveryZone?
    var strainScore: Double
    var sleepPerformance: Double?
    var hrvRMSSD: Double?
    var hrvSDNN: Double?
    var restingHeartRate: Double?
    var respiratoryRate: Double?
    var spo2: Double?
    var skinTemperature: Double?
    var steps: Int
    var activeCalories: Double
    var vo2Max: Double?
    var peakStrain: Double
    var workoutCount: Int
    var sleepDurationHours: Double?
    var sleepNeedHours: Double?
    var sleepDebtHours: Double?
    var stressAverage: Double?

    // MARK: - Composite Sleep Score (research-backed)
    /// Composite sleep score (0-100) combining sufficiency, efficiency, consistency, disturbances.
    /// Distinct from `sleepPerformance` which is sufficiency-only (actual/need × 100).
    var sleepScore: Double?
    /// Bedtime/wake time consistency score (0-100). Based on 4-night std dev.
    var sleepConsistency: Double?
    /// Sleep efficiency: total sleep time / time in bed × 100.
    var sleepEfficiencyPct: Double?
    /// Restorative sleep: (deep + REM) / total sleep × 100. Target: 40-50%.
    var restorativeSleepPct: Double?
    /// Deep sleep as percentage of total sleep.
    var deepSleepPct: Double?
    /// REM sleep as percentage of total sleep.
    var remSleepPct: Double?
    /// Lean body mass percentage (100 - body fat %). Used for longevity computation.
    var leanBodyMassPct: Double?

    var isComputed: Bool
    var computedAt: Date?
    var syncedToCloud: Bool
    var createdAt: Date

    var userProfile: UserProfile?

    @Relationship(deleteRule: .cascade, inverse: \WorkoutRecord.dailyMetric)
    var workouts: [WorkoutRecord]

    @Relationship(deleteRule: .cascade, inverse: \SleepSession.dailyMetric)
    var sleepSessions: [SleepSession]

    init(date: Date) {
        self.id = UUID()
        self.date = Calendar.current.startOfDay(for: date)
        self.strainScore = 0
        self.steps = 0
        self.activeCalories = 0
        self.peakStrain = 0
        self.workoutCount = 0
        self.isComputed = false
        self.syncedToCloud = false
        self.createdAt = Date()
        self.workouts = []
        self.sleepSessions = []
    }

    var strainZone: StrainZone {
        switch strainScore {
        case 0..<8: return .light
        case 8..<14: return .moderate
        case 14..<18: return .high
        default: return .overreaching
        }
    }

    var formattedDate: String {
        date.formatted(date: .abbreviated, time: .omitted)
    }
}

enum RecoveryZone: String, Codable {
    case green, yellow, red

    var label: String {
        switch self {
        case .green: return "Green"
        case .yellow: return "Yellow"
        case .red: return "Red"
        }
    }

    static func from(score: Double) -> RecoveryZone {
        switch score {
        case 67...99: return .green
        case 34..<67: return .yellow
        default: return .red
        }
    }
}

enum StrainZone: String, Codable {
    case light, moderate, high, overreaching

    var label: String {
        switch self {
        case .light: return "Light"
        case .moderate: return "Moderate"
        case .high: return "High"
        case .overreaching: return "Overreaching"
        }
    }
}
