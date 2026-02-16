import Foundation
import SwiftData

@Model
final class JournalEntry {
    var id: UUID
    var date: Date
    var completedAt: Date?
    var isComplete: Bool
    var streakDays: Int
    var createdAt: Date

    var userProfile: UserProfile?

    @Relationship(deleteRule: .cascade, inverse: \JournalResponse.journalEntry)
    var responses: [JournalResponse]

    init(date: Date) {
        self.id = UUID()
        self.date = Calendar.current.startOfDay(for: date)
        self.isComplete = false
        self.streakDays = 0
        self.createdAt = Date()
        self.responses = []
    }

    var responseCount: Int {
        responses.count
    }

    var formattedDate: String {
        date.formatted(date: .abbreviated, time: .omitted)
    }
}
