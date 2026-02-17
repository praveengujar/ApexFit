import Foundation
import WatchKit

enum BreathworkPhase: String {
    case inhale = "Inhale"
    case sipInhale = "Sip In"
    case exhale = "Exhale"
    case idle = ""
}

@Observable
final class BreathworkSessionManager: NSObject, WKExtendedRuntimeSessionDelegate {
    var extendedSession: WKExtendedRuntimeSession?

    var isActive: Bool = false
    var currentPhase: BreathworkPhase = .idle
    var currentCycle: Int = 0
    var totalCycles: Int = 20
    var elapsedTime: TimeInterval = 0
    var circleScale: CGFloat = 0.4

    let inhaleDuration: TimeInterval = 4.0
    let sipDuration: TimeInterval = 2.0
    let exhaleDuration: TimeInterval = 8.0

    var startStress: Double = 0
    var endStress: Double = 0

    private var timer: Timer?
    private var phaseTimer: Timer?
    private var sessionStartDate: Date?

    func startSession(startingStress: Double) {
        extendedSession = WKExtendedRuntimeSession()
        extendedSession?.delegate = self
        extendedSession?.start()

        isActive = true
        currentCycle = 0
        elapsedTime = 0
        startStress = startingStress
        sessionStartDate = Date()

        startBreathingCycle()

        timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            guard let self, let start = self.sessionStartDate else { return }
            self.elapsedTime = Date().timeIntervalSince(start)
        }
    }

    func endSession() {
        timer?.invalidate()
        timer = nil
        phaseTimer?.invalidate()
        phaseTimer = nil
        extendedSession?.invalidate()
        extendedSession = nil
        isActive = false
        currentPhase = .idle
        circleScale = 0.4
    }

    private func startBreathingCycle() {
        guard isActive, currentCycle < totalCycles else {
            endSession()
            return
        }

        currentCycle += 1
        currentPhase = .inhale
        circleScale = 1.0
        HapticCoachingManager.shared.playInhale()

        phaseTimer = Timer.scheduledTimer(withTimeInterval: inhaleDuration, repeats: false) { [weak self] _ in
            guard let self, self.isActive else { return }
            self.currentPhase = .sipInhale
            HapticCoachingManager.shared.playSipInhale()

            self.phaseTimer = Timer.scheduledTimer(withTimeInterval: self.sipDuration, repeats: false) { [weak self] _ in
                guard let self, self.isActive else { return }
                self.currentPhase = .exhale
                self.circleScale = 0.4
                HapticCoachingManager.shared.playExhale()

                self.phaseTimer = Timer.scheduledTimer(withTimeInterval: self.exhaleDuration, repeats: false) { [weak self] _ in
                    guard let self, self.isActive else { return }
                    HapticCoachingManager.shared.playCycleComplete()
                    self.startBreathingCycle()
                }
            }
        }
    }

    var elapsedFormatted: String {
        let mins = Int(elapsedTime) / 60
        let secs = Int(elapsedTime) % 60
        return String(format: "%d:%02d", mins, secs)
    }

    // MARK: - WKExtendedRuntimeSessionDelegate

    func extendedRuntimeSessionDidStart(_ extendedRuntimeSession: WKExtendedRuntimeSession) {}

    func extendedRuntimeSessionWillExpire(_ extendedRuntimeSession: WKExtendedRuntimeSession) {
        endSession()
    }

    func extendedRuntimeSession(_ extendedRuntimeSession: WKExtendedRuntimeSession, didInvalidateWith reason: WKExtendedRuntimeSessionInvalidationReason, error: Error?) {
        DispatchQueue.main.async { self.isActive = false }
    }
}
