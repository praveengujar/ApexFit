package com.apexfit.core.engine

import org.junit.Test
import kotlin.test.assertEquals

class CollectionExtensionsTest {

    @Test
    fun mean_emptyList_returnsZero() {
        assertEquals(0.0, emptyList<Double>().mean, 0.001)
    }

    @Test
    fun mean_validList_returnsAverage() {
        assertEquals(4.0, listOf(2.0, 4.0, 6.0).mean, 0.001)
    }

    @Test
    fun standardDeviation_singleElement_returnsZero() {
        assertEquals(0.0, listOf(5.0).standardDeviation, 0.001)
    }

    @Test
    fun standardDeviation_uniformValues_returnsZero() {
        assertEquals(0.0, listOf(3.0, 3.0, 3.0).standardDeviation, 0.001)
    }

    @Test
    fun standardDeviation_validList_returnsPopulationStdDev() {
        // [2, 4, 4, 4, 5, 5, 7, 9] -> mean=5.0, variance=4.0, stddev=2.0
        val values = listOf(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
        assertEquals(2.0, values.standardDeviation, 0.001)
    }

    @Test
    fun median_emptyList_returnsZero() {
        assertEquals(0.0, emptyList<Double>().median, 0.001)
    }

    @Test
    fun median_oddCount_returnsMiddle() {
        assertEquals(3.0, listOf(1.0, 3.0, 5.0).median, 0.001)
    }

    @Test
    fun median_evenCount_returnsAverageOfMiddleTwo() {
        assertEquals(2.5, listOf(1.0, 2.0, 3.0, 4.0).median, 0.001)
    }

    @Test
    fun clamped_withinRange_returnsSameValue() {
        assertEquals(5.0, 5.0.clamped(0.0..10.0), 0.001)
    }

    @Test
    fun clamped_belowRange_returnsMin() {
        assertEquals(0.0, (-3.0).clamped(0.0..10.0), 0.001)
    }

    @Test
    fun clamped_aboveRange_returnsMax() {
        assertEquals(10.0, 15.0.clamped(0.0..10.0), 0.001)
    }
}
