package com.apexfit.shared.engine

import kotlin.test.Test
import kotlin.test.*

class SleepPlannerEngineTest {

    private val engine = SleepPlannerEngine(TestConfig.sleepPlannerConfig)

    // Use a reference wake time (7 AM as millis offset)
    private val wakeTime7AM = 7L * 3600 * 1000

    @Test
    fun plan_peakGoal_fullSleepDuration() {
        // need=8.0, PEAK (mult=1.0) -> requiredSleep = 8.0
        val result = engine.plan(
            sleepNeedHours = 8.0,
            goal = SleepGoalType.PEAK,
            desiredWakeTimeMillis = wakeTime7AM,
        )
        assertApprox(8.0, result.requiredSleepDuration, 0.01)
    }

    @Test
    fun plan_performGoal_reducedDuration() {
        // need=8.0, PERFORM (mult=0.85) -> requiredSleep = 6.8
        val result = engine.plan(
            sleepNeedHours = 8.0,
            goal = SleepGoalType.PERFORM,
            desiredWakeTimeMillis = wakeTime7AM,
        )
        assertApprox(6.8, result.requiredSleepDuration, 0.01)
    }

    @Test
    fun plan_getByGoal_minimumDuration() {
        // need=8.0, GET_BY (mult=0.70) -> requiredSleep = 5.6
        val result = engine.plan(
            sleepNeedHours = 8.0,
            goal = SleepGoalType.GET_BY,
            desiredWakeTimeMillis = wakeTime7AM,
        )
        assertApprox(5.6, result.requiredSleepDuration, 0.01)
    }

    @Test
    fun plan_bedtimeIsCorrectOffset() {
        // requiredSleep=8.0, latency=15min -> totalInBed = 8.25h
        // bedtime = wake - 8.25 * 3600000 = wake - 29700000
        val result = engine.plan(
            sleepNeedHours = 8.0,
            goal = SleepGoalType.PEAK,
            desiredWakeTimeMillis = wakeTime7AM,
            estimatedOnsetLatencyMinutes = 15.0,
        )
        val expectedBedtime = wakeTime7AM - (8.25 * 3600 * 1000).toLong()
        assertEquals(expectedBedtime, result.recommendedBedtimeMillis)
    }

    @Test
    fun estimateWakeTime_empty_returns7AM() {
        assertEquals(420, engine.estimateWakeTime(emptyList()))
    }

    @Test
    fun estimateWakeTime_withHistory_returnsAverage() {
        // [360(6AM), 420(7AM), 480(8AM)] -> avg = 420 (7AM)
        assertEquals(420, engine.estimateWakeTime(listOf(360, 420, 480)))
    }

    @Test
    fun estimateOnsetLatency_empty_returns15() {
        assertApprox(15.0, engine.estimateOnsetLatency(emptyList()), 0.01)
    }

    @Test
    fun estimateOnsetLatency_withHistory_returnsAverage() {
        assertApprox(15.0, engine.estimateOnsetLatency(listOf(10.0, 20.0, 15.0)), 0.01)
    }
}
