import Foundation
import SwiftData

/// Schedules notifications based on computed metrics and user preferences.
struct NotificationScheduler {
    private let manager = NotificationManager.shared

    /// Schedule all relevant notifications after metrics are computed.
    func schedulePostComputation(dailyMetric: DailyMetric, preferences: [NotificationPreference]) async {
        guard manager.isAuthorized else { return }

        let prefMap = Dictionary(uniqueKeysWithValues: preferences.map { ($0.notificationType, $0) })

        // Morning Recovery
        if let pref = prefMap[.morningRecovery], pref.isEnabled,
           let score = dailyMetric.recoveryScore,
           let zone = dailyMetric.recoveryZone {
            let time = timeComponents(from: pref.customTime, defaultHour: 7, defaultMinute: 0)
            manager.scheduleMorningRecovery(score: score, zone: zone, at: time)
        }

        // Strain Target - check if target reached
        if let pref = prefMap[.strainTarget], pref.isEnabled,
           let zone = dailyMetric.recoveryZone {
            let target = RecoveryEngine.strainTarget(for: zone)
            if dailyMetric.strainScore >= target.lowerBound && dailyMetric.strainScore <= target.upperBound {
                manager.scheduleStrainTargetReached(currentStrain: dailyMetric.strainScore, targetRange: target)
            }
        }

        // Health Alerts
        if let pref = prefMap[.healthAlert], pref.isEnabled {
            await checkHealthAlerts(metric: dailyMetric)
        }

        // Journal Reminder
        if let pref = prefMap[.journalReminder], pref.isEnabled {
            let time = timeComponents(from: pref.customTime, defaultHour: 9, defaultMinute: 0)
            manager.scheduleJournalReminder(at: time)
        }
    }

    /// Schedule bedtime reminder based on sleep planner.
    func scheduleBedtime(recommendedBedtime: Date, preferences: [NotificationPreference]) {
        guard manager.isAuthorized else { return }

        let prefMap = Dictionary(uniqueKeysWithValues: preferences.map { ($0.notificationType, $0) })

        if let pref = prefMap[.bedtimeReminder], pref.isEnabled {
            manager.scheduleBedtimeReminder(bedtime: recommendedBedtime)
        }
    }

    // MARK: - Private

    private func checkHealthAlerts(metric: DailyMetric) async {
        // Elevated RHR
        if let rhr = metric.restingHeartRate, rhr > 100 {
            manager.scheduleHealthAlert(
                message: "Your resting heart rate is elevated at \(Int(rhr)) BPM. This could indicate stress, illness, or dehydration."
            )
        }

        // Low SpO2
        if let spo2 = metric.spo2, spo2 < 92 {
            manager.scheduleHealthAlert(
                message: "Your blood oxygen level is \(Int(spo2))%, which is below normal. Consider consulting a healthcare provider."
            )
        }

        // Very low recovery
        if let recovery = metric.recoveryScore, recovery < 15 {
            manager.scheduleHealthAlert(
                message: "Your recovery is very low at \(Int(recovery))%. Your body needs significant rest today."
            )
        }
    }

    private func timeComponents(from customTime: Date?, defaultHour: Int, defaultMinute: Int) -> DateComponents {
        var components = DateComponents()
        if let time = customTime {
            let calendar = Calendar.current
            components.hour = calendar.component(.hour, from: time)
            components.minute = calendar.component(.minute, from: time)
        } else {
            components.hour = defaultHour
            components.minute = defaultMinute
        }
        return components
    }
}
