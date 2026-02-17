import Foundation
import SwiftData

@Model
final class NotificationPreference {
    var id: UUID
    var notificationType: NotificationType
    var isEnabled: Bool
    var customTime: Date?
    var updatedAt: Date

    init(notificationType: NotificationType, isEnabled: Bool = true) {
        self.id = UUID()
        self.notificationType = notificationType
        self.isEnabled = isEnabled
        self.updatedAt = Date()
    }
}

enum NotificationType: String, Codable, CaseIterable {
    case morningRecovery
    case bedtimeReminder
    case strainTarget
    case weeklyReport
    case healthAlert
    case journalReminder
    case coachInsight

    var label: String {
        switch self {
        case .morningRecovery: return "Morning Recovery"
        case .bedtimeReminder: return "Bedtime Reminder"
        case .strainTarget: return "Strain Target"
        case .weeklyReport: return "Weekly Report"
        case .healthAlert: return "Health Alert"
        case .journalReminder: return "Journal Reminder"
        case .coachInsight: return "Coach Insight"
        }
    }

    var description: String {
        switch self {
        case .morningRecovery: return "Get your recovery score when you wake up"
        case .bedtimeReminder: return "Reminder to go to bed based on sleep planner"
        case .strainTarget: return "Alert when you hit your strain target"
        case .weeklyReport: return "Weekly performance summary"
        case .healthAlert: return "Alerts for unusual health metrics"
        case .journalReminder: return "Daily reminder to complete your journal"
        case .coachInsight: return "AI coach insights and tips"
        }
    }
}
