package com.apexfit.shared.engine

import kotlin.test.Test
import kotlin.test.*

class MuscularLoadEngineTest {

    @Test
    fun isStrengthWorkout_strengthType_returnsTrue() {
        assertTrue(MuscularLoadEngine.isStrengthWorkout("traditionalStrengthTraining"))
    }

    @Test
    fun isStrengthWorkout_highIntensityType_returnsTrue() {
        assertTrue(MuscularLoadEngine.isStrengthWorkout("crossTraining"))
    }

    @Test
    fun isStrengthWorkout_running_returnsFalse() {
        assertFalse(MuscularLoadEngine.isStrengthWorkout("running"))
    }

    @Test
    fun isStrengthWorkout_yoga_returnsFalse() {
        assertFalse(MuscularLoadEngine.isStrengthWorkout("yoga"))
    }

    @Test
    fun computeLoad_strengthWorkout_basicComputation() {
        // factor=1.0, dur=60, avgHR=140, maxHR=170, userMax=200
        // volume = 60 * 1.0 = 60
        // intensity = (140/200) * (170/200) = 0.7 * 0.85 = 0.595
        // load = 60 * 0.595 * 2.0 = 71.4
        val result = MuscularLoadEngine.computeLoad(
            workoutType = "traditionalStrengthTraining",
            durationMinutes = 60.0,
            averageHeartRate = 140.0,
            maxHeartRateDuringWorkout = 170.0,
            userMaxHeartRate = 200.0,
        )
        assertApprox(71.4, result.load, 0.1)
        assertApprox(60.0, result.volumeScore, 0.01)
        assertApprox(0.595, result.intensityScore, 0.001)
    }

    @Test
    fun computeLoad_withRPE_adjustsLoad() {
        // Same as above but RPE=8 -> rpeAdj = 1 + (8-5)*0.1 = 1.3
        // load = 71.4 * 1.3 = 92.82
        val result = MuscularLoadEngine.computeLoad(
            workoutType = "traditionalStrengthTraining",
            durationMinutes = 60.0,
            averageHeartRate = 140.0,
            maxHeartRateDuringWorkout = 170.0,
            userMaxHeartRate = 200.0,
            rpe = 8,
        )
        assertApprox(92.82, result.load, 0.1)
    }

    @Test
    fun computeLoad_clampedAt100() {
        val result = MuscularLoadEngine.computeLoad(
            workoutType = "traditionalStrengthTraining",
            durationMinutes = 120.0,
            averageHeartRate = 190.0,
            maxHeartRateDuringWorkout = 200.0,
            userMaxHeartRate = 200.0,
            rpe = 10,
        )
        assertApprox(100.0, result.load, 0.01)
    }

    @Test
    fun computeLoad_unknownWorkoutType_usesFallbackFactor() {
        // "running" not in map -> factor=0.5
        // volume = 30 * 0.5 = 15
        // intensity = (150/200) * (170/200) = 0.75 * 0.85 = 0.6375
        // load = 15 * 0.6375 * 2.0 = 19.125
        val result = MuscularLoadEngine.computeLoad(
            workoutType = "running",
            durationMinutes = 30.0,
            averageHeartRate = 150.0,
            maxHeartRateDuringWorkout = 170.0,
            userMaxHeartRate = 200.0,
        )
        assertApprox(19.13, result.load, 0.1)
    }
}
