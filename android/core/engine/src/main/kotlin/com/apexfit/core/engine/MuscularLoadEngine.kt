package com.apexfit.core.engine

data class MuscularLoadResult(
    val load: Double,
    val volumeScore: Double,
    val intensityScore: Double,
    val workoutType: String,
)

object MuscularLoadEngine {

    /**
     * Effective mass factor by workout type category.
     * Higher values for exercises that engage more muscle groups.
     */
    private val effectiveMassFactors = mapOf(
        "traditionalStrengthTraining" to 1.0,
        "functionalStrengthTraining" to 0.9,
        "crossTraining" to 0.85,
        "highIntensityIntervalTraining" to 0.8,
        "coreTraining" to 0.6,
        "yoga" to 0.4,
        "pilates" to 0.5,
        "flexibility" to 0.3,
        "wrestling" to 0.9,
        "boxing" to 0.8,
        "kickboxing" to 0.85,
        "martialArts" to 0.85,
        "climbing" to 0.85,
        "rowing" to 0.75,
    )

    private val strengthTypes = setOf(
        "traditionalStrengthTraining",
        "functionalStrengthTraining",
        "coreTraining",
    )

    private val highIntensityTypes = setOf(
        "crossTraining",
        "highIntensityIntervalTraining",
        "wrestling",
        "boxing",
        "kickboxing",
        "martialArts",
        "climbing",
    )

    fun computeLoad(
        workoutType: String,
        durationMinutes: Double,
        averageHeartRate: Double,
        maxHeartRateDuringWorkout: Double,
        userMaxHeartRate: Double,
        bodyWeightKG: Double? = null,
        rpe: Int? = null,
    ): MuscularLoadResult {
        val effectiveMassFactor = effectiveMassFactors[workoutType] ?: 0.5
        val volumeScore = durationMinutes * effectiveMassFactor

        val avgHRRatio = averageHeartRate / userMaxHeartRate
        val peakHRRatio = maxHeartRateDuringWorkout / userMaxHeartRate
        val intensityScore = (avgHRRatio * peakHRRatio).clamped(0.0..1.0)

        val calibrationFactor = 2.0
        var load = volumeScore * intensityScore * calibrationFactor

        if (rpe != null) {
            val rpeAdjustment = 1.0 + (rpe - 5.0) * 0.1
            load *= rpeAdjustment
        }

        load = load.clamped(0.0..100.0)

        return MuscularLoadResult(
            load = load,
            volumeScore = volumeScore,
            intensityScore = intensityScore,
            workoutType = workoutType,
        )
    }

    fun isStrengthWorkout(type: String): Boolean =
        type in strengthTypes || type in highIntensityTypes
}
