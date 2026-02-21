package com.zyva.shared.engine

import kotlin.test.Test
import kotlin.test.*

class StatisticalEngineTest {

    @Test
    fun tTest_smallSamples_returnsNull() {
        assertNull(StatisticalEngine.tTest(listOf(1.0, 2.0), listOf(3.0, 4.0)))
    }

    @Test
    fun tTest_identicalDistributions_returnsNull() {
        // stdDev = 0, pooledSE = 0 -> returns null
        assertNull(StatisticalEngine.tTest(listOf(5.0, 5.0, 5.0), listOf(5.0, 5.0, 5.0)))
    }

    @Test
    fun tTest_clearlyDifferentGroups_significantResult() {
        val with = listOf(10.0, 11.0, 12.0, 10.0, 11.0)
        val without = listOf(5.0, 6.0, 5.0, 6.0, 5.0)
        val result = StatisticalEngine.tTest(with, without)
        assertNotNull(result)
        assertTrue(result.first > 0) // positive t (with > without)
        assertTrue(result.second < 0.05) // significant
    }

    @Test
    fun cohensD_smallSamples_returnsNull() {
        assertNull(StatisticalEngine.cohensD(listOf(1.0, 2.0), listOf(3.0, 4.0)))
    }

    @Test
    fun cohensD_clearlyDifferent_largeEffectSize() {
        val with = listOf(10.0, 11.0, 12.0, 10.0, 11.0)
        val without = listOf(5.0, 6.0, 5.0, 6.0, 5.0)
        val d = StatisticalEngine.cohensD(with, without)
        assertNotNull(d)
        assertTrue(d > 0.8) // large effect
    }

    @Test
    fun cohensD_identicalMeans_returnsZero() {
        val d = StatisticalEngine.cohensD(listOf(5.0, 6.0, 7.0), listOf(5.0, 6.0, 7.0))
        assertNotNull(d)
        assertApprox(0.0, d, 0.01)
    }

    @Test
    fun interpretEffectSize_correctLabels() {
        assertEquals("Negligible", StatisticalEngine.interpretEffectSize(0.1))
        assertEquals("Small", StatisticalEngine.interpretEffectSize(0.3))
        assertEquals("Medium", StatisticalEngine.interpretEffectSize(0.6))
        assertEquals("Large", StatisticalEngine.interpretEffectSize(1.0))
    }

    @Test
    fun analyzeCorrelation_significant_returnsPositiveDirection() {
        val with = listOf(10.0, 11.0, 12.0, 10.0, 11.0)
        val without = listOf(5.0, 6.0, 5.0, 6.0, 5.0)
        val result = StatisticalEngine.analyzeCorrelation(
            behaviorName = "Meditation",
            metricName = "Recovery",
            withBehavior = with,
            withoutBehavior = without,
            higherIsBetter = true,
        )
        assertNotNull(result)
        assertTrue(result.isSignificant)
        assertEquals(CorrelationDirection.POSITIVE, result.direction)
        assertEquals(5, result.sampleSizeWith)
        assertEquals(5, result.sampleSizeWithout)
    }
}
