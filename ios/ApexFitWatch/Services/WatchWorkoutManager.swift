import ApexFitShared
import Foundation
import HealthKit
import WatchKit

@Observable
final class WatchWorkoutManager: NSObject {
    let healthStore = HKHealthStore()
    var workoutSession: HKWorkoutSession?
    var liveBuilder: HKLiveWorkoutBuilder?

    // State
    var isWorkoutActive: Bool = false
    var isPaused: Bool = false
    var currentHeartRate: Double = 0
    var averageHeartRate: Double = 0
    var maxHeartRateReading: Double = 0
    var currentCalories: Double = 0
    var currentDistance: Double = 0
    var elapsedTime: TimeInterval = 0
    var currentStrain: Double = 0
    var currentZone: Int = 0
    var selectedWorkoutType: HKWorkoutActivityType = .running

    // Zone time
    var zone1Minutes: Double = 0
    var zone2Minutes: Double = 0
    var zone3Minutes: Double = 0
    var zone4Minutes: Double = 0
    var zone5Minutes: Double = 0

    // Internal
    private var hrSamples: [ApexFitShared.HeartRateSample] = []
    private var strainEngine: ApexFitShared.StrainEngine?
    private var zoneCalculator: ApexFitShared.HeartRateZoneCalculator?
    private var userMaxHR: Int = 190
    private var workoutStartDate: Date?
    private var lastHRSampleDate: Date?
    private var dayStrainBefore: Double = 0

    func configure(maxHeartRate: Int, dayStrainBefore: Double) {
        self.userMaxHR = maxHeartRate
        self.dayStrainBefore = dayStrainBefore
        let config = KMPConfigProvider.scoringConfig
        self.strainEngine = ApexFitShared.StrainEngine(
            maxHeartRate: Int32(maxHeartRate),
            strainConfig: config.strain,
            hrZoneConfig: config.heartRateZones
        )
        self.zoneCalculator = ApexFitShared.HeartRateZoneCalculator(
            maxHeartRate: Int32(maxHeartRate),
            hrConfig: config.heartRateZones
        )
    }

    // MARK: - Workout Lifecycle

    func startWorkout(type: HKWorkoutActivityType) async throws {
        let config = HKWorkoutConfiguration()
        config.activityType = type
        config.locationType = type == .swimming ? .indoor : .outdoor
        selectedWorkoutType = type

        workoutSession = try HKWorkoutSession(healthStore: healthStore, configuration: config)
        liveBuilder = workoutSession?.associatedWorkoutBuilder()
        liveBuilder?.dataSource = HKLiveWorkoutDataSource(healthStore: healthStore, workoutConfiguration: config)

        workoutSession?.delegate = self
        liveBuilder?.delegate = self

        let startDate = Date()
        workoutStartDate = startDate
        workoutSession?.startActivity(with: startDate)
        try await liveBuilder?.beginCollection(at: startDate)

        isWorkoutActive = true
        isPaused = false
        resetMetrics()

        if type == .swimming {
            WKInterfaceDevice.current().enableWaterLock()
        }
    }

    func pauseWorkout() {
        workoutSession?.pause()
        isPaused = true
    }

    func resumeWorkout() {
        workoutSession?.resume()
        isPaused = false
    }

    func endWorkout() async {
        workoutSession?.end()
        if let builder = liveBuilder {
            try? await builder.endCollection(at: Date())
            try? await builder.finishWorkout()
        }
        isWorkoutActive = false
        isPaused = false
    }

    // MARK: - Helpers

    private func resetMetrics() {
        currentHeartRate = 0
        averageHeartRate = 0
        maxHeartRateReading = 0
        currentCalories = 0
        currentDistance = 0
        elapsedTime = 0
        currentStrain = 0
        currentZone = 0
        zone1Minutes = 0
        zone2Minutes = 0
        zone3Minutes = 0
        zone4Minutes = 0
        zone5Minutes = 0
        hrSamples = []
        lastHRSampleDate = nil
    }

    private func recalculateStrain() {
        guard let engine = strainEngine, !hrSamples.isEmpty else { return }
        let result = engine.computeStrain(samples: hrSamples)
        currentStrain = result.strain
        zone1Minutes = result.zone1Minutes
        zone2Minutes = result.zone2Minutes
        zone3Minutes = result.zone3Minutes
        zone4Minutes = result.zone4Minutes
        zone5Minutes = result.zone5Minutes
    }

    var totalDayStrain: Double {
        dayStrainBefore + currentStrain
    }

    var workoutDurationFormatted: String {
        let minutes = Int(elapsedTime) / 60
        let seconds = Int(elapsedTime) % 60
        return String(format: "%d:%02d", minutes, seconds)
    }

    var zoneBoundaries: [(zone: Int, lower: Int, upper: Int)] {
        guard let calc = zoneCalculator else { return [] }
        return calc.zoneBoundaries().map { triple in
            (zone: triple.first!.intValue, lower: triple.second!.intValue, upper: triple.third!.intValue)
        }
    }
}

// MARK: - HKWorkoutSessionDelegate

extension WatchWorkoutManager: HKWorkoutSessionDelegate {
    func workoutSession(_ workoutSession: HKWorkoutSession, didChangeTo toState: HKWorkoutSessionState, from fromState: HKWorkoutSessionState, date: Date) {
        DispatchQueue.main.async {
            switch toState {
            case .running:
                self.isPaused = false
            case .paused:
                self.isPaused = true
            case .ended:
                self.isWorkoutActive = false
            default:
                break
            }
        }
    }

    func workoutSession(_ workoutSession: HKWorkoutSession, didFailWithError error: Error) {}
}

// MARK: - HKLiveWorkoutBuilderDelegate

extension WatchWorkoutManager: HKLiveWorkoutBuilderDelegate {
    func workoutBuilder(_ workoutBuilder: HKLiveWorkoutBuilder, didCollectDataOf collectedTypes: Set<HKSampleType>) {
        for type in collectedTypes {
            guard let quantityType = type as? HKQuantityType else { continue }

            switch quantityType {
            case HKQuantityType(.heartRate):
                let stats = workoutBuilder.statistics(for: quantityType)
                let hr = stats?.mostRecentQuantity()?.doubleValue(for: .count().unitDivided(by: .minute())) ?? 0
                let avgHR = stats?.averageQuantity()?.doubleValue(for: .count().unitDivided(by: .minute())) ?? 0
                let maxHRVal = stats?.maximumQuantity()?.doubleValue(for: .count().unitDivided(by: .minute())) ?? 0

                let now = Date()
                let duration = lastHRSampleDate.map { now.timeIntervalSince($0) } ?? 5.0
                lastHRSampleDate = now

                let sample = ApexFitShared.HeartRateSample(
                    timestampMillis: Int64(now.timeIntervalSince1970 * 1000),
                    bpm: hr,
                    durationSeconds: duration
                )

                DispatchQueue.main.async {
                    self.currentHeartRate = hr
                    self.averageHeartRate = avgHR
                    self.maxHeartRateReading = maxHRVal
                    self.currentZone = Int(self.zoneCalculator?.zoneNumber(heartRate: hr) ?? 0)
                    self.hrSamples.append(sample)
                    self.recalculateStrain()

                    if let start = self.workoutStartDate {
                        self.elapsedTime = now.timeIntervalSince(start)
                    }

                    // Haptic on zone change
                    let previousZone = self.hrSamples.dropLast().last.map { Int(self.zoneCalculator?.zoneNumber(heartRate: $0.bpm) ?? 0) } ?? 0
                    if self.currentZone != previousZone && previousZone > 0 {
                        if self.currentZone > previousZone {
                            WKInterfaceDevice.current().play(.directionUp)
                        } else {
                            WKInterfaceDevice.current().play(.directionDown)
                        }
                    }
                }

            case HKQuantityType(.activeEnergyBurned):
                let stats = workoutBuilder.statistics(for: quantityType)
                let cal = stats?.sumQuantity()?.doubleValue(for: .kilocalorie()) ?? 0
                DispatchQueue.main.async { self.currentCalories = cal }

            case HKQuantityType(.distanceWalkingRunning), HKQuantityType(.distanceCycling), HKQuantityType(.distanceSwimming):
                let stats = workoutBuilder.statistics(for: quantityType)
                let dist = stats?.sumQuantity()?.doubleValue(for: .meter()) ?? 0
                DispatchQueue.main.async { self.currentDistance = dist / 1000.0 }

            default:
                break
            }
        }
    }

    func workoutBuilderDidCollectEvent(_ workoutBuilder: HKLiveWorkoutBuilder) {}
}
