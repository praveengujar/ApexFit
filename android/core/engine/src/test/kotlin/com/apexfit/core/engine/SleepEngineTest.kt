package com.apexfit.core.engine

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SleepEngineTest {

    private val engine = SleepEngine(TestConfig.sleepConfig)

    private fun session(
        totalMinutes: Double,
        deepMinutes: Double = 0.0,
        remMinutes: Double = 0.0,
        awakeMinutes: Double = 0.0,
        awakenings: Int = 0,
        efficiency: Double = 0.95,
    ) = SleepSessionData(
        startDateMillis = 0L,
        endDateMillis = (totalMinutes * 60 * 1000).toLong(),
        totalSleepMinutes = totalMinutes,
        timeInBedMinutes = totalMinutes / efficiency,
        lightMinutes = totalMinutes - deepMinutes - remMinutes - awakeMinutes,
        deepMinutes = deepMinutes,
        remMinutes = remMinutes,
        awakeMinutes = awakeMinutes,
        awakenings = awakenings,
        sleepOnsetLatencyMinutes = 10.0,
        sleepEfficiency = efficiency,
    )

    @Test
    fun classifySessions_empty_returnsNullAndEmpty() {
        val (main, naps) = engine.classifySessions(emptyList())
        assertNull(main)
        assertTrue(naps.isEmpty())
    }

    @Test
    fun classifySessions_singleSession_mainSleepNoNaps() {
        val s = session(480.0)
        val (main, naps) = engine.classifySessions(listOf(s))
        assertNotNull(main)
        assertEquals(480.0, main.totalSleepMinutes, 0.01)
        assertTrue(naps.isEmpty())
    }

    @Test
    fun classifySessions_mainPlusNap_classifiesCorrectly() {
        val mainSleep = session(480.0)
        val nap = session(45.0)
        val (main, naps) = engine.classifySessions(listOf(mainSleep, nap))
        assertNotNull(main)
        assertEquals(480.0, main.totalSleepMinutes, 0.01)
        assertEquals(1, naps.size)
        assertEquals(45.0, naps[0].totalSleepMinutes, 0.01)
    }

    @Test
    fun classifySessions_shortSessionFilteredOut() {
        val mainSleep = session(480.0)
        val tooShort = session(20.0) // below minimumDurationMinutes=30
        val (_, naps) = engine.classifySessions(listOf(mainSleep, tooShort))
        assertTrue(naps.isEmpty())
    }

    @Test
    fun computeSleepNeed_lowStrain_noSupplement() {
        // baseline=7.5, strain=5 (below 8 -> add 0.0), debt=0, naps=0
        val need = engine.computeSleepNeed(7.5, 5.0, 0.0, 0.0)
        assertEquals(7.5, need, 0.01)
    }

    @Test
    fun computeSleepNeed_highStrain_addsSupplement() {
        // baseline=7.5, strain=15 (below 18 -> add 0.5), debt=2.0 (repay 20%=0.4), naps=0
        val need = engine.computeSleepNeed(7.5, 15.0, 2.0, 0.0)
        assertEquals(8.4, need, 0.01)
    }

    @Test
    fun computeSleepPerformance_perfectSleep_returns100() {
        assertEquals(100.0, engine.computeSleepPerformance(8.0, 8.0), 0.01)
    }

    @Test
    fun computeSleepPerformance_halfSleep_returns50() {
        assertEquals(50.0, engine.computeSleepPerformance(4.0, 8.0), 0.01)
    }

    @Test
    fun computeSleepDebt_allDeficits_accumulatesDebt() {
        val actuals = listOf(7.0, 6.0, 7.0)
        val needs = listOf(8.0, 8.0, 8.0)
        // Deficits: 1, 2, 1 -> debt = 4.0
        assertEquals(4.0, engine.computeSleepDebt(actuals, needs), 0.01)
    }

    @Test
    fun computeSleepDebt_surplusIgnored() {
        val actuals = listOf(9.0, 10.0)
        val needs = listOf(8.0, 8.0)
        assertEquals(0.0, engine.computeSleepDebt(actuals, needs), 0.01)
    }

    @Test
    fun computeCompositeSleepScore_perfectInputs_returns100() {
        // disturbScore = max(0, 100 - 0*20) = 100
        // score = 0.5*100 + 0.25*100 + 0.15*100 + 0.10*100 = 100
        val score = engine.computeCompositeSleepScore(100.0, 100.0, 100.0, 0.0)
        assertEquals(100.0, score, 0.01)
    }

    @Test
    fun computeCompositeSleepScore_poorInputs_lowScore() {
        // disturbScore = max(0, 100 - 3.0*20) = 40
        // score = 0.5*50 + 0.25*70 + 0.15*40 + 0.10*40 = 25+17.5+6+4 = 52.5
        val score = engine.computeCompositeSleepScore(50.0, 70.0, 40.0, 3.0)
        assertEquals(52.5, score, 0.01)
    }

    @Test
    fun computeRestorativeSleepPct_correctRatio() {
        val s = session(480.0, deepMinutes = 90.0, remMinutes = 110.0)
        // (90 + 110) / 480 * 100 = 41.67
        assertEquals(41.67, engine.computeRestorativeSleepPct(s), 0.01)
    }

    @Test
    fun computeDisturbancesPerHour_correctRate() {
        val s = session(480.0, awakenings = 4)
        // 4 awakenings / 8 hours = 0.5
        assertEquals(0.5, engine.computeDisturbancesPerHour(s), 0.01)
    }

    @Test
    fun computeSleepConsistency_noPriorHistory_returns100() {
        val result = engine.computeSleepConsistency(
            currentBedtimeMinutes = -120.0, // 10 PM
            currentWakeTimeMinutes = 420.0,   // 7 AM
            recentBedtimeMinutes = emptyList(),
            recentWakeTimeMinutes = emptyList(),
        )
        assertEquals(100.0, result, 0.01)
    }
}
