package com.apexfit.core.engine

import com.apexfit.core.model.RecoveryZone
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RecoveryEngineTest {

    private val engine = RecoveryEngine(TestConfig.recoveryConfig)

    private fun validBaseline(mean: Double, sd: Double) =
        BaselineResult(mean = mean, standardDeviation = sd, sampleCount = 10, windowDays = 28)

    private fun invalidBaseline() =
        BaselineResult(mean = 60.0, standardDeviation = 10.0, sampleCount = 2, windowDays = 28)

    @Test
    fun computeRecovery_noData_returnsDefault50() {
        val result = engine.computeRecovery(RecoveryInput(), RecoveryBaselines())
        assertEquals(50.0, result.score, 0.01)
        assertEquals(0, result.contributorCount)
    }

    @Test
    fun computeRecovery_hrvAboveBaseline_highScore() {
        // HRV=80, baseline(mean=60, sd=10): z=2.0
        // sigmoid = 100 / (1 + exp(-1.5*2.0)) = 100 / (1 + exp(-3.0)) = 95.26
        val input = RecoveryInput(hrv = 80.0)
        val baselines = RecoveryBaselines(hrv = validBaseline(60.0, 10.0))
        val result = engine.computeRecovery(input, baselines)
        assertEquals(95.26, result.score, 0.5)
        assertEquals(RecoveryZone.GREEN, result.zone)
        assertEquals(1, result.contributorCount)
    }

    @Test
    fun computeRecovery_rhrElevated_lowScore() {
        // RHR=75, baseline(mean=60, sd=5): z=3.0, inverted -> z=-3.0
        // sigmoid = 100 / (1 + exp(-1.5*(-3.0))) = 100 / (1 + exp(4.5)) = ~1.10
        // Clamped to scoreRange [1, 99]
        val input = RecoveryInput(restingHeartRate = 75.0)
        val baselines = RecoveryBaselines(restingHeartRate = validBaseline(60.0, 5.0))
        val result = engine.computeRecovery(input, baselines)
        assertTrue(result.score < 5.0)
        assertEquals(RecoveryZone.RED, result.zone)
    }

    @Test
    fun computeRecovery_allContributors_properWeighting() {
        val input = RecoveryInput(
            hrv = 70.0,
            restingHeartRate = 58.0,
            sleepPerformance = 85.0,
            respiratoryRate = 15.0,
            spo2 = 98.0,
            skinTemperatureDeviation = 0.1,
        )
        val baselines = RecoveryBaselines(
            hrv = validBaseline(60.0, 10.0),
            restingHeartRate = validBaseline(60.0, 5.0),
            sleepPerformance = validBaseline(80.0, 10.0),
            respiratoryRate = validBaseline(16.0, 1.0),
            spo2 = validBaseline(97.0, 1.0),
            skinTemperature = validBaseline(0.0, 0.5),
        )
        val result = engine.computeRecovery(input, baselines)
        assertEquals(6, result.contributorCount)
        assertTrue(result.score in 1.0..99.0)
    }

    @Test
    fun computeRecovery_invalidBaseline_excluded() {
        val input = RecoveryInput(hrv = 80.0)
        val baselines = RecoveryBaselines(hrv = invalidBaseline())
        val result = engine.computeRecovery(input, baselines)
        assertNull(result.hrvScore)
        assertEquals(0, result.contributorCount)
        assertEquals(50.0, result.score, 0.01)
    }

    @Test
    fun computeRecovery_zoneClassification_green() {
        assertEquals(RecoveryZone.GREEN, RecoveryZone.from(85.0))
        assertEquals(RecoveryZone.GREEN, RecoveryZone.from(67.0))
    }

    @Test
    fun computeRecovery_zoneClassification_yellow() {
        assertEquals(RecoveryZone.YELLOW, RecoveryZone.from(50.0))
        assertEquals(RecoveryZone.YELLOW, RecoveryZone.from(34.0))
    }

    @Test
    fun computeRecovery_zoneClassification_red() {
        assertEquals(RecoveryZone.RED, RecoveryZone.from(20.0))
        assertEquals(RecoveryZone.RED, RecoveryZone.from(1.0))
    }

    @Test
    fun strainTarget_green_returns14to18() {
        val range = engine.strainTarget(RecoveryZone.GREEN)
        assertEquals(14.0, range.start, 0.01)
        assertEquals(18.0, range.endInclusive, 0.01)
    }

    @Test
    fun generateInsight_noSignificantChanges_normalRange() {
        val result = RecoveryResult(
            score = 65.0, zone = RecoveryZone.YELLOW,
            hrvScore = 50.0, rhrScore = 50.0, sleepScore = 50.0,
            respRateScore = null, spo2Score = null, skinTempScore = null,
            contributorCount = 3,
        )
        val input = RecoveryInput(hrv = 61.0, restingHeartRate = 60.5, sleepPerformance = 80.0)
        val baselines = RecoveryBaselines(
            hrv = validBaseline(60.0, 10.0),
            restingHeartRate = validBaseline(60.0, 5.0),
            sleepPerformance = validBaseline(80.0, 10.0),
        )
        val insight = engine.generateInsight(result, input, baselines)
        assertTrue(insight.contains("within normal range"))
    }
}
