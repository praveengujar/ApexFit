import Foundation
import WatchKit
import UserNotifications

@Observable
final class HapticCoachingManager {
    static let shared = HapticCoachingManager()

    private init() {}

    // MARK: - Recovery

    func playRecoveryHaptic(zone: RecoveryZone) {
        switch zone {
        case .green: WKInterfaceDevice.current().play(.success)
        case .yellow: WKInterfaceDevice.current().play(.retry)
        case .red: WKInterfaceDevice.current().play(.failure)
        }
    }

    // MARK: - Strain

    func playStrainTargetReached() {
        WKInterfaceDevice.current().play(.success)
    }

    func playStrainOverreach() {
        WKInterfaceDevice.current().play(.retry)
    }

    // MARK: - Breathwork

    func playInhale() { WKInterfaceDevice.current().play(.start) }
    func playSipInhale() { WKInterfaceDevice.current().play(.click) }
    func playExhale() { WKInterfaceDevice.current().play(.directionDown) }
    func playCycleComplete() { WKInterfaceDevice.current().play(.success) }

    // MARK: - General

    func playClick() { WKInterfaceDevice.current().play(.click) }
    func playNotification() { WKInterfaceDevice.current().play(.notification) }

    // MARK: - Scheduled Notifications

    func scheduleBedtimeReminder(bedtime: Date) {
        let center = UNUserNotificationCenter.current()

        // 30 min before
        let reminderDate = bedtime.addingTimeInterval(-30 * 60)
        let content = UNMutableNotificationContent()
        content.title = "Bedtime Reminder"
        content.body = "Time to start winding down. Bed by \(bedtime.formatted(date: .omitted, time: .shortened))."
        content.sound = .default

        let components = Calendar.current.dateComponents([.hour, .minute], from: reminderDate)
        let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
        center.add(UNNotificationRequest(identifier: "bedtime_reminder", content: content, trigger: trigger))

        // At bedtime
        let bedtimeContent = UNMutableNotificationContent()
        bedtimeContent.title = "Time for Bed"
        bedtimeContent.body = "Sleep Performance goal: 100%."
        bedtimeContent.sound = .default

        let bedtimeComponents = Calendar.current.dateComponents([.hour, .minute], from: bedtime)
        let bedtimeTrigger = UNCalendarNotificationTrigger(dateMatching: bedtimeComponents, repeats: false)
        center.add(UNNotificationRequest(identifier: "bedtime_final", content: bedtimeContent, trigger: bedtimeTrigger))
    }
}
