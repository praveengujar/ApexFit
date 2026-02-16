import Foundation
import HealthKit

struct MuscularLoadResult {
    let load: Double  // 0-100 scale
    let volumeScore: Double
    let intensityScore: Double
    let workoutType: String
}

struct MuscularLoadEngine {
    /// Compute muscular load for a strength workout.
    ///
    /// Muscular Load = Volume Score × Intensity Score × calibration
    ///
    /// Volume Score = duration_minutes × effective_mass_factor
    /// Intensity Score = (avgHR/maxHR) × (peakHR/maxHR)
    static func computeLoad(
        workoutType: HKWorkoutActivityType,
        durationMinutes: Double,
        averageHeartRate: Double,
        maxHeartRateDuringWorkout: Double,
        userMaxHeartRate: Double,
        bodyWeightKG: Double? = nil,
        rpe: Int? = nil
    ) -> MuscularLoadResult {
        let effectiveMassFactor = workoutType.effectiveMassFactor
        let volumeScore = durationMinutes * effectiveMassFactor

        let avgHRRatio = averageHeartRate / userMaxHeartRate
        let peakHRRatio = maxHeartRateDuringWorkout / userMaxHeartRate
        let intensityScore = (avgHRRatio * peakHRRatio).clamped(to: 0...1)

        let calibrationFactor = 2.0 // Calibrated so a hard 60-min session ≈ 80-90
        var load = volumeScore * intensityScore * calibrationFactor

        // RPE adjustment if provided
        if let rpe {
            let rpeAdjustment = 1.0 + (Double(rpe) - 5.0) * 0.1
            load *= rpeAdjustment
        }

        load = load.clamped(to: 0...100)

        return MuscularLoadResult(
            load: load,
            volumeScore: volumeScore,
            intensityScore: intensityScore,
            workoutType: workoutType.displayName
        )
    }

    /// Check if a workout type qualifies for muscular load computation.
    static func isStrengthWorkout(_ type: HKWorkoutActivityType) -> Bool {
        type.isStrength || type.isHighIntensity
    }
}
