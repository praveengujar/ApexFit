import Foundation
import SwiftData

@Model
final class UserProfile {
    var id: UUID
    var firebaseUID: String?
    var displayName: String
    var email: String?
    var dateOfBirth: Date?
    var biologicalSex: BiologicalSex
    var heightCM: Double?
    var weightKG: Double?
    var maxHeartRate: Int?
    var maxHeartRateSource: MaxHRSource
    var sleepBaselineHours: Double
    var preferredUnits: UnitSystem
    var selectedJournalBehaviorIDs: [String]
    var hasCompletedOnboarding: Bool
    var createdAt: Date
    var updatedAt: Date
    var deviceToken: String?
    var lastSyncedAt: Date?

    @Relationship(deleteRule: .cascade, inverse: \DailyMetric.userProfile)
    var dailyMetrics: [DailyMetric]

    @Relationship(deleteRule: .cascade, inverse: \JournalEntry.userProfile)
    var journalEntries: [JournalEntry]

    init(
        displayName: String = "",
        biologicalSex: BiologicalSex = .notSet,
        sleepBaselineHours: Double = 7.5,
        preferredUnits: UnitSystem = .metric
    ) {
        self.id = UUID()
        self.displayName = displayName
        self.biologicalSex = biologicalSex
        self.maxHeartRateSource = .ageEstimate
        self.sleepBaselineHours = sleepBaselineHours
        self.preferredUnits = preferredUnits
        self.selectedJournalBehaviorIDs = []
        self.hasCompletedOnboarding = false
        self.createdAt = Date()
        self.updatedAt = Date()
        self.dailyMetrics = []
        self.journalEntries = []
    }

    var age: Int? {
        guard let dob = dateOfBirth else { return nil }
        return Calendar.current.dateComponents([.year], from: dob, to: Date()).year
    }

    var estimatedMaxHR: Int {
        if let maxHeartRate { return maxHeartRate }
        guard let age else { return 190 }
        return 220 - age
    }
}

enum BiologicalSex: String, Codable, CaseIterable {
    case male, female, other, notSet
}

enum MaxHRSource: String, Codable {
    case userInput, observed, ageEstimate
}

enum UnitSystem: String, Codable, CaseIterable {
    case metric, imperial
}
