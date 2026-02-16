import Foundation
import UserNotifications

@Observable
final class NotificationManager {
    static let shared = NotificationManager()

    var isAuthorized = false
    var pendingNotifications: [UNNotificationRequest] = []

    private let center = UNUserNotificationCenter.current()

    private init() {}

    func requestAuthorization() async throws {
        let granted = try await center.requestAuthorization(options: [.alert, .sound, .badge])
        isAuthorized = granted
    }

    func checkAuthorizationStatus() async -> UNAuthorizationStatus {
        let settings = await center.notificationSettings()
        isAuthorized = settings.authorizationStatus == .authorized
        return settings.authorizationStatus
    }

    // MARK: - Schedule Notifications

    func scheduleMorningRecovery(score: Double, zone: RecoveryZone, at time: DateComponents? = nil) {
        var triggerTime = time ?? DateComponents()
        if time == nil {
            triggerTime.hour = 7
            triggerTime.minute = 0
        }

        let content = NotificationContent.morningRecovery(score: score, zone: zone)
        let trigger = UNCalendarNotificationTrigger(dateMatching: triggerTime, repeats: false)
        let request = UNNotificationRequest(
            identifier: "morning-recovery-\(Date().shortDateString)",
            content: content,
            trigger: trigger
        )

        center.add(request)
    }

    func scheduleBedtimeReminder(bedtime: Date) {
        let reminderTime = bedtime.addingTimeInterval(-30 * 60) // 30 min before
        let components = Calendar.current.dateComponents([.hour, .minute], from: reminderTime)

        let content = NotificationContent.bedtimeReminder(bedtime: bedtime)
        let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
        let request = UNNotificationRequest(
            identifier: "bedtime-\(Date().shortDateString)",
            content: content,
            trigger: trigger
        )

        center.add(request)
    }

    func scheduleStrainTargetReached(currentStrain: Double, targetRange: ClosedRange<Double>) {
        let content = NotificationContent.strainTargetReached(strain: currentStrain, target: targetRange)
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: "strain-target-\(Date().shortDateString)",
            content: content,
            trigger: trigger
        )

        center.add(request)
    }

    func scheduleHealthAlert(message: String) {
        let content = NotificationContent.healthAlert(message: message)
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: "health-alert-\(UUID().uuidString)",
            content: content,
            trigger: trigger
        )

        center.add(request)
    }

    func scheduleJournalReminder(at time: DateComponents? = nil) {
        var triggerTime = time ?? DateComponents()
        if time == nil {
            triggerTime.hour = 9
            triggerTime.minute = 0
        }

        let content = NotificationContent.journalReminder()
        let trigger = UNCalendarNotificationTrigger(dateMatching: triggerTime, repeats: true)
        let request = UNNotificationRequest(
            identifier: "journal-reminder",
            content: content,
            trigger: trigger
        )

        center.add(request)
    }

    // MARK: - Management

    func removeAllPendingNotifications() {
        center.removeAllPendingNotificationRequests()
    }

    func removePendingNotification(identifier: String) {
        center.removePendingNotificationRequests(withIdentifiers: [identifier])
    }

    func getPendingNotifications() async -> [UNNotificationRequest] {
        let requests = await center.pendingNotificationRequests()
        pendingNotifications = requests
        return requests
    }
}
