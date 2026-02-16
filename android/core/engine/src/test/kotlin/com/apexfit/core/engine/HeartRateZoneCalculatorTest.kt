package com.apexfit.core.engine

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HeartRateZoneCalculatorTest {

    private val calculator = HeartRateZoneCalculator(TestConfig.DEFAULT_MAX_HR, TestConfig.hrZoneConfig)

    @Test
    fun zone_belowThreshold_returnsNull() {
        // 90 bpm = 45% of 200, below 50% boundary
        assertNull(calculator.zone(90.0))
    }

    @Test
    fun zone_atLowerBoundary_returnsZone1() {
        // 100 bpm = exactly 50%
        val zone = calculator.zone(100.0)
        assertNotNull(zone)
        assertEquals(1, zone.zone)
        assertEquals("Warm-Up", zone.name)
    }

    @Test
    fun zone_inZone1_returnsCorrectMultiplier() {
        assertEquals(1.0, calculator.multiplier(110.0), 0.001)
        assertEquals(1, calculator.zoneNumber(110.0))
    }

    @Test
    fun zone_inZone2_returnsCorrectMultiplier() {
        assertEquals(2.0, calculator.multiplier(130.0), 0.001)
        assertEquals(2, calculator.zoneNumber(130.0))
    }

    @Test
    fun zone_inZone3_returnsCorrectMultiplier() {
        assertEquals(3.0, calculator.multiplier(150.0), 0.001)
        assertEquals(3, calculator.zoneNumber(150.0))
    }

    @Test
    fun zone_inZone4_returnsCorrectMultiplier() {
        assertEquals(4.0, calculator.multiplier(170.0), 0.001)
        assertEquals(4, calculator.zoneNumber(170.0))
    }

    @Test
    fun zone_inZone5_returnsCorrectMultiplier() {
        assertEquals(5.0, calculator.multiplier(185.0), 0.001)
        assertEquals(5, calculator.zoneNumber(185.0))
    }

    @Test
    fun zone_atMaxHR_returnsZone5() {
        val zone = calculator.zone(200.0)
        assertNotNull(zone)
        assertEquals(5, zone.zone)
    }

    @Test
    fun multiplier_belowThreshold_returnsZero() {
        assertEquals(0.0, calculator.multiplier(80.0), 0.001)
    }

    @Test
    fun zoneNumber_belowThreshold_returnsZero() {
        assertEquals(0, calculator.zoneNumber(80.0))
    }
}
