import Foundation
import SwiftData

@Model
final class JournalResponse {
    var id: UUID
    var behaviorID: String
    var behaviorName: String
    var category: String
    var responseType: JournalResponseType
    var toggleValue: Bool?
    var numericValue: Double?
    var scaleValue: String?
    var createdAt: Date

    var journalEntry: JournalEntry?

    init(
        behaviorID: String,
        behaviorName: String,
        category: String,
        responseType: JournalResponseType
    ) {
        self.id = UUID()
        self.behaviorID = behaviorID
        self.behaviorName = behaviorName
        self.category = category
        self.responseType = responseType
        self.createdAt = Date()
    }

    var displayValue: String {
        switch responseType {
        case .toggle:
            return (toggleValue ?? false) ? "Yes" : "No"
        case .numeric:
            if let value = numericValue {
                return String(format: "%.0f", value)
            }
            return "-"
        case .scale:
            return scaleValue ?? "-"
        }
    }
}

enum JournalResponseType: String, Codable {
    case toggle
    case numeric
    case scale
}
