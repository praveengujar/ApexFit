import UserNotifications

enum NotificationContent {
    static func morningRecovery(score: Double, zone: RecoveryZone) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Morning Recovery"

        switch zone {
        case .green:
            content.body = "Your Recovery is \(Int(score))% (Green). Your body is primed for peak performance today."
            content.subtitle = "Push for high intensity"
        case .yellow:
            content.body = "Your Recovery is \(Int(score))% (Yellow). Train at moderate intensity today."
            content.subtitle = "Moderate effort recommended"
        case .red:
            content.body = "Your Recovery is \(Int(score))% (Red). Your body needs rest. Prioritize sleep and light movement."
            content.subtitle = "Focus on rest"
        }

        content.sound = .default
        content.categoryIdentifier = "RECOVERY"
        return content
    }

    static func bedtimeReminder(bedtime: Date) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Bedtime Reminder"
        content.body = "Your recommended bedtime is \(bedtime.hourMinuteString). Start winding down for optimal recovery."
        content.subtitle = "Sleep Planner"
        content.sound = .default
        content.categoryIdentifier = "SLEEP"
        return content
    }

    static func strainTargetReached(strain: Double, target: ClosedRange<Double>) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Strain Target Reached"
        content.body = "Your strain is \(strain.formattedOneDecimal), within your target of \(target.lowerBound.formattedOneDecimal)-\(target.upperBound.formattedOneDecimal). Consider wrapping up your activity."
        content.sound = .default
        content.categoryIdentifier = "STRAIN"
        return content
    }

    static func healthAlert(message: String) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Health Alert"
        content.body = message
        content.sound = .defaultCritical
        content.categoryIdentifier = "HEALTH_ALERT"
        return content
    }

    static func journalReminder() -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Journal Reminder"
        content.body = "Take a moment to log yesterday's behaviors. Your insights get better with every entry."
        content.sound = .default
        content.categoryIdentifier = "JOURNAL"
        return content
    }

    static func weeklyReport(recoveryAvg: Double, strainAvg: Double, sleepAvg: Double) -> UNMutableNotificationContent {
        let content = UNMutableNotificationContent()
        content.title = "Weekly Performance Report"
        content.body = "This week: Avg Recovery \(Int(recoveryAvg))%, Avg Strain \(strainAvg.formattedOneDecimal), Avg Sleep \(sleepAvg.formattedOneDecimal)h. Tap to see your full report."
        content.sound = .default
        content.categoryIdentifier = "WEEKLY_REPORT"
        return content
    }
}
