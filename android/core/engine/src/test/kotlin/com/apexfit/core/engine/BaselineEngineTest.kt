package com.apexfit.core.engine

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BaselineEngineTest {

    @Test
    fun computeBaseline_emptyValues_returnsNull() {
        assertNull(BaselineEngine.computeBaseline(emptyList()))
    }

    @Test
    fun computeBaseline_twoValues_returnsNull() {
        assertNull(BaselineEngine.computeBaseline(listOf(60.0, 70.0)))
    }

    @Test
    fun computeBaseline_threeValues_returnsValidBaseline() {
        val result = BaselineEngine.computeBaseline(listOf(60.0, 70.0, 80.0))
        assertNotNull(result)
        assertEquals(70.0, result.mean, 0.01)
        assertEquals(3, result.sampleCount)
        assertTrue(result.isValid)
    }

    @Test
    fun computeBaseline_identicalValues_floorsStdDev() {
        val result = BaselineEngine.computeBaseline(listOf(50.0, 50.0, 50.0))
        assertNotNull(result)
        assertEquals(50.0, result.mean, 0.01)
        assertEquals(0.001, result.standardDeviation, 0.0001)
        // isValid: sampleCount=3 >= 3 AND stdDev=0.001 > 0 -> true
        assertTrue(result.isValid)
    }

    @Test
    fun zScore_meanValue_returnsZero() {
        val baseline = BaselineResult(mean = 70.0, standardDeviation = 10.0, sampleCount = 10, windowDays = 28)
        assertEquals(0.0, BaselineEngine.zScore(70.0, baseline), 0.001)
    }

    @Test
    fun zScore_oneStdDevAbove_returnsOne() {
        val baseline = BaselineResult(mean = 70.0, standardDeviation = 10.0, sampleCount = 10, windowDays = 28)
        assertEquals(1.0, BaselineEngine.zScore(80.0, baseline), 0.001)
    }

    @Test
    fun updateBaseline_shiftsTowardNewValue() {
        val current = BaselineResult(mean = 60.0, standardDeviation = 5.0, sampleCount = 10, windowDays = 28)
        val updated = BaselineEngine.updateBaseline(current, 70.0, alpha = 0.1)
        // newMean = 60*0.9 + 70*0.1 = 61.0
        assertEquals(61.0, updated.mean, 0.01)
        assertEquals(11, updated.sampleCount)
        // newVariance = 25*0.9 + (70-61)^2*0.1 = 22.5 + 8.1 = 30.6
        // newStdDev = sqrt(30.6) = 5.532
        assertEquals(5.532, updated.standardDeviation, 0.01)
    }
}
