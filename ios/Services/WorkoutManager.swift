import Foundation
import HealthKit

/// iOS live workout manager using HKAnchoredObjectQuery for real-time HR streaming.
/// HKLiveWorkoutBuilder is only available on iOS 26+ / watchOS, so this uses
/// anchored queries to monitor HR samples in real-time during a workout.
@Observable
final class WorkoutManager: NSObject {
    let healthStore = HKHealthStore()

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
    private var hrSamples: [HeartRateSample] = []
    private var strainEngine: StrainEngine?
    private var zoneCalculator: HeartRateZoneCalculator?
    private var userMaxHR: Int = 190
    private var workoutStartDate: Date?
    private var lastHRSampleDate: Date?
    private var dayStrainBefore: Double = 0
    private var elapsedTimer: Timer?
    private var hrQuery: HKAnchoredObjectQuery?
    private var caloriesQuery: HKAnchoredObjectQuery?
    private var distanceQuery: HKAnchoredObjectQuery?
    private var hrAnchor: HKQueryAnchor?
    private var caloriesAnchor: HKQueryAnchor?
    private var distanceAnchor: HKQueryAnchor?
    private var allHRValues: [Double] = []

    func configure(maxHeartRate: Int, dayStrainBefore: Double) {
        self.userMaxHR = maxHeartRate
        self.dayStrainBefore = dayStrainBefore
        self.strainEngine = StrainEngine(maxHeartRate: maxHeartRate)
        self.zoneCalculator = HeartRateZoneCalculator(maxHeartRate: maxHeartRate)
    }

    // MARK: - Workout Lifecycle

    func startWorkout(type: HKWorkoutActivityType) {
        selectedWorkoutType = type
        let startDate = Date()
        workoutStartDate = startDate
        isWorkoutActive = true
        isPaused = false
        resetMetrics()
        startElapsedTimer()
        startHRMonitoring(from: startDate)
        startCaloriesMonitoring(from: startDate)
        startDistanceMonitoring(from: startDate)
    }

    func pauseWorkout() {
        isPaused = true
        elapsedTimer?.invalidate()
    }

    func resumeWorkout() {
        isPaused = false
        startElapsedTimer()
    }

    func endWorkout() {
        elapsedTimer?.invalidate()
        elapsedTimer = nil
        stopAllQueries()
        isWorkoutActive = false
        isPaused = false
    }

    // MARK: - HR Monitoring via HKAnchoredObjectQuery

    private func startHRMonitoring(from startDate: Date) {
        let hrType = HKQuantityType(.heartRate)
        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: nil, options: .strictStartDate)

        let query = HKAnchoredObjectQuery(
            type: hrType,
            predicate: predicate,
            anchor: hrAnchor,
            limit: HKObjectQueryNoLimit
        ) { [weak self] _, samples, _, newAnchor, _ in
            self?.hrAnchor = newAnchor
            self?.processHRSamples(samples)
        }

        query.updateHandler = { [weak self] _, samples, _, newAnchor, _ in
            self?.hrAnchor = newAnchor
            self?.processHRSamples(samples)
        }

        healthStore.execute(query)
        hrQuery = query
    }

    private func processHRSamples(_ samples: [HKSample]?) {
        guard let quantitySamples = samples as? [HKQuantitySample], !quantitySamples.isEmpty else { return }

        for sample in quantitySamples {
            let hr = sample.quantity.doubleValue(for: HKUnit.count().unitDivided(by: .minute()))
            let now = sample.startDate
            let duration = lastHRSampleDate.map { now.timeIntervalSince($0) } ?? 5.0
            lastHRSampleDate = now

            let hrSample = HeartRateSample(timestamp: now, bpm: hr, durationSeconds: min(duration, 600))
            hrSamples.append(hrSample)
            allHRValues.append(hr)
        }

        let latestHR = quantitySamples.last.map {
            $0.quantity.doubleValue(for: HKUnit.count().unitDivided(by: .minute()))
        } ?? 0

        DispatchQueue.main.async { [self] in
            currentHeartRate = latestHR
            if !allHRValues.isEmpty {
                averageHeartRate = allHRValues.reduce(0, +) / Double(allHRValues.count)
                maxHeartRateReading = allHRValues.max() ?? 0
            }
            currentZone = zoneCalculator?.zoneNumber(for: latestHR) ?? 0
            recalculateStrain()
        }
    }

    // MARK: - Calories Monitoring

    private func startCaloriesMonitoring(from startDate: Date) {
        let calType = HKQuantityType(.activeEnergyBurned)
        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: nil, options: .strictStartDate)

        let query = HKAnchoredObjectQuery(
            type: calType,
            predicate: predicate,
            anchor: caloriesAnchor,
            limit: HKObjectQueryNoLimit
        ) { [weak self] _, samples, _, newAnchor, _ in
            self?.caloriesAnchor = newAnchor
            self?.processCaloriesSamples(samples)
        }

        query.updateHandler = { [weak self] _, samples, _, newAnchor, _ in
            self?.caloriesAnchor = newAnchor
            self?.processCaloriesSamples(samples)
        }

        healthStore.execute(query)
        caloriesQuery = query
    }

    private func processCaloriesSamples(_ samples: [HKSample]?) {
        guard let quantitySamples = samples as? [HKQuantitySample] else { return }
        let total = quantitySamples.reduce(0.0) {
            $0 + $1.quantity.doubleValue(for: .kilocalorie())
        }
        DispatchQueue.main.async { [self] in
            currentCalories += total
        }
    }

    // MARK: - Distance Monitoring

    private func startDistanceMonitoring(from startDate: Date) {
        let distType: HKQuantityType
        switch selectedWorkoutType {
        case .cycling:
            distType = HKQuantityType(.distanceCycling)
        case .swimming:
            distType = HKQuantityType(.distanceSwimming)
        default:
            distType = HKQuantityType(.distanceWalkingRunning)
        }

        let predicate = HKQuery.predicateForSamples(withStart: startDate, end: nil, options: .strictStartDate)

        let query = HKAnchoredObjectQuery(
            type: distType,
            predicate: predicate,
            anchor: distanceAnchor,
            limit: HKObjectQueryNoLimit
        ) { [weak self] _, samples, _, newAnchor, _ in
            self?.distanceAnchor = newAnchor
            self?.processDistanceSamples(samples)
        }

        query.updateHandler = { [weak self] _, samples, _, newAnchor, _ in
            self?.distanceAnchor = newAnchor
            self?.processDistanceSamples(samples)
        }

        healthStore.execute(query)
        distanceQuery = query
    }

    private func processDistanceSamples(_ samples: [HKSample]?) {
        guard let quantitySamples = samples as? [HKQuantitySample] else { return }
        let total = quantitySamples.reduce(0.0) {
            $0 + $1.quantity.doubleValue(for: .meter())
        }
        DispatchQueue.main.async { [self] in
            currentDistance += total / 1000.0
        }
    }

    // MARK: - Stop Queries

    private func stopAllQueries() {
        if let q = hrQuery { healthStore.stop(q) }
        if let q = caloriesQuery { healthStore.stop(q) }
        if let q = distanceQuery { healthStore.stop(q) }
        hrQuery = nil
        caloriesQuery = nil
        distanceQuery = nil
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
        allHRValues = []
        lastHRSampleDate = nil
        hrAnchor = nil
        caloriesAnchor = nil
        distanceAnchor = nil
    }

    private func recalculateStrain() {
        guard let engine = strainEngine, !hrSamples.isEmpty else { return }
        let result = engine.computeStrain(from: hrSamples)
        currentStrain = result.strain
        zone1Minutes = result.zone1Minutes
        zone2Minutes = result.zone2Minutes
        zone3Minutes = result.zone3Minutes
        zone4Minutes = result.zone4Minutes
        zone5Minutes = result.zone5Minutes
    }

    private func startElapsedTimer() {
        elapsedTimer?.invalidate()
        elapsedTimer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
            guard let self, let start = self.workoutStartDate, !self.isPaused else { return }
            DispatchQueue.main.async {
                self.elapsedTime = Date().timeIntervalSince(start)
            }
        }
    }

    var totalDayStrain: Double {
        dayStrainBefore + currentStrain
    }

    var workoutDurationFormatted: String {
        let totalSeconds = Int(elapsedTime)
        let hours = totalSeconds / 3600
        let minutes = (totalSeconds % 3600) / 60
        let seconds = totalSeconds % 60
        if hours > 0 {
            return String(format: "%d:%02d:%02d", hours, minutes, seconds)
        }
        return String(format: "%d:%02d", minutes, seconds)
    }

    var zoneBoundaries: [(zone: Int, lower: Int, upper: Int)] {
        zoneCalculator?.zoneBoundaries() ?? []
    }
}
