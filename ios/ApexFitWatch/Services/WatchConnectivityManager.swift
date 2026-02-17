import Foundation
import WatchConnectivity
import WidgetKit

@Observable
final class WatchConnectivityManager: NSObject, WCSessionDelegate {
    static let shared = WatchConnectivityManager()

    var applicationContext: WatchApplicationContext = .placeholder
    var isReachable: Bool = false
    var lastUpdated: Date?

    private let session = WCSession.default

    private override init() {
        super.init()
    }

    func activate() {
        guard WCSession.isSupported() else { return }
        session.delegate = self
        session.activate()
    }

    // MARK: - Send Data to iPhone

    func sendWorkoutSummary(_ summary: [String: Any]) {
        session.transferUserInfo(summary)
    }

    func sendQuickJournal(_ entries: [String: Bool]) {
        let payload: [String: Any] = [
            "type": "quick_journal",
            "entries": entries,
            "timestamp": Date().timeIntervalSince1970
        ]
        session.transferUserInfo(payload)
    }

    func requestLatestMetrics() {
        guard session.isReachable else { return }
        session.sendMessage(
            ["type": "request_metrics"],
            replyHandler: { reply in
                if let context = WatchApplicationContext.from(dictionary: reply) {
                    DispatchQueue.main.async {
                        self.applicationContext = context
                        self.lastUpdated = Date()
                    }
                }
            },
            errorHandler: nil
        )
    }

    // MARK: - Update Complication Data

    private func updateComplicationDefaults() {
        guard let defaults = UserDefaults(suiteName: "group.com.apexfit.shared") else { return }
        defaults.set(applicationContext.recoveryScore, forKey: "complication_recovery_score")
        defaults.set(applicationContext.recoveryZone, forKey: "complication_recovery_zone")
        defaults.set(applicationContext.currentDayStrain, forKey: "complication_strain_score")
        defaults.set(applicationContext.strainTargetHigh, forKey: "complication_strain_target")
        defaults.set(applicationContext.sleepPerformance, forKey: "complication_sleep_performance")
        let hours = Double(applicationContext.sleepDurationMinutes) / 60.0
        defaults.set(hours.formattedHoursMinutes, forKey: "complication_sleep_duration")
    }

    // MARK: - WCSessionDelegate

    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        if activationState == .activated {
            if let received = session.receivedApplicationContext as? [String: Any],
               let context = WatchApplicationContext.from(dictionary: received) {
                DispatchQueue.main.async {
                    self.applicationContext = context
                    self.lastUpdated = Date()
                    self.updateComplicationDefaults()
                }
            }
        }
    }

    func session(_ session: WCSession, didReceiveApplicationContext applicationContext: [String: Any]) {
        if let context = WatchApplicationContext.from(dictionary: applicationContext) {
            DispatchQueue.main.async {
                self.applicationContext = context
                self.lastUpdated = Date()
                self.updateComplicationDefaults()
                WidgetCenter.shared.reloadAllTimelines()
            }
        }
    }

    func session(_ session: WCSession, didReceiveUserInfo userInfo: [String: Any] = [:]) {
        if let context = WatchApplicationContext.from(dictionary: userInfo) {
            DispatchQueue.main.async {
                self.applicationContext = context
                self.lastUpdated = Date()
                self.updateComplicationDefaults()
            }
        }
    }

    func session(_ session: WCSession, didReceiveMessage message: [String: Any]) {
        if let context = WatchApplicationContext.from(dictionary: message) {
            DispatchQueue.main.async {
                self.applicationContext = context
                self.lastUpdated = Date()
            }
        }
    }

    func sessionReachabilityDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isReachable = session.isReachable
        }
    }
}
