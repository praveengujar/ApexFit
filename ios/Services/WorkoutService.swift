import Foundation
import SwiftData
import HealthKit

actor WorkoutService {
    private let queryService: HealthKitQueryService

    init(queryService: HealthKitQueryService = HealthKitQueryService()) {
        self.queryService = queryService
    }

    /// Process all workouts for a given date.
    func processWorkouts(for date: Date, dailyMetric: DailyMetric, maxHeartRate: Int, bodyWeightKG: Double?) async throws {
        let workouts = try await queryService.fetchWorkouts(from: date.startOfDay, to: date.endOfDay)

        let strainEngine = StrainEngine(maxHeartRate: maxHeartRate)

        for hkWorkout in workouts {
            // Fetch HR samples during workout
            let hrSamples = try await queryService.fetchHeartRateSamples(during: hkWorkout)

            // Compute workout strain
            let strainResult = strainEngine.computeWorkoutStrain(from: hrSamples)

            // Create workout record
            let record = WorkoutRecord(
                workoutType: hkWorkout.workoutActivityType.rawValue.description,
                workoutName: hkWorkout.workoutActivityType.displayName,
                startDate: hkWorkout.startDate,
                endDate: hkWorkout.endDate,
                strainScore: strainResult.strain
            )

            record.averageHeartRate = hrSamples.isEmpty ? nil : hrSamples.averageBPM()
            record.maxHeartRate = hrSamples.isEmpty ? nil : hrSamples.maxBPM()
            record.activeCalories = hkWorkout.totalEnergyBurned?.doubleValue(for: .kilocalorie()) ?? 0
            record.distanceMeters = hkWorkout.totalDistance?.doubleValue(for: .meter())
            record.zone1Minutes = strainResult.zone1Minutes
            record.zone2Minutes = strainResult.zone2Minutes
            record.zone3Minutes = strainResult.zone3Minutes
            record.zone4Minutes = strainResult.zone4Minutes
            record.zone5Minutes = strainResult.zone5Minutes
            record.healthKitWorkoutUUID = hkWorkout.uuid.uuidString

            // Muscular load for strength workouts
            if MuscularLoadEngine.isStrengthWorkout(hkWorkout.workoutActivityType) {
                if let avgHR = record.averageHeartRate, let maxHR = record.maxHeartRate {
                    let loadResult = MuscularLoadEngine.computeLoad(
                        workoutType: hkWorkout.workoutActivityType,
                        durationMinutes: record.durationMinutes,
                        averageHeartRate: avgHR,
                        maxHeartRateDuringWorkout: maxHR,
                        userMaxHeartRate: Double(maxHeartRate),
                        bodyWeightKG: bodyWeightKG
                    )
                    record.muscularLoad = loadResult.load
                    record.isStrengthWorkout = true
                }
            }

            dailyMetric.workouts.append(record)
        }

        dailyMetric.workoutCount = dailyMetric.workouts.count
        dailyMetric.peakStrain = dailyMetric.workouts.map(\.strainScore).max() ?? 0
    }
}
