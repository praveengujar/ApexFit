package com.zyva.shared.engine

import kotlin.test.Test
import kotlin.test.*

class StrainEngineTest {

    private val engine = StrainEngine(TestConfig.DEFAULT_MAX_HR, TestConfig.strainConfig, TestConfig.hrZoneConfig)

    @Test
    fun computeStrain_emptySamples_returnsZeroStrain() {
        val result = engine.computeStrain(emptyList())
        // strain = 6 * log10(0 + 1) = 6 * 0 = 0
        assertApprox(0.0, result.strain, 0.01)
        assertApprox(0.0, result.zone1Minutes, 0.001)
        assertApprox(0.0, result.zone5Minutes, 0.001)
    }

    @Test
    fun computeStrain_belowZone1_noContribution() {
        // HR=90 at maxHR=200 is 45%, below 50% threshold -> multiplier=0
        val samples = listOf(HeartRateSample(0L, 90.0, 600.0)) // 10 min
        val result = engine.computeStrain(samples)
        assertApprox(0.0, result.weightedHRArea, 0.001)
        assertApprox(0.0, result.strain, 0.01)
    }

    @Test
    fun computeStrain_zone1Only_lowStrain() {
        // 10 min at 110bpm (Zone1, mult=1.0) -> weighted = 10 * 1.0 = 10
        // strain = 6 * log10(10 + 1) = 6 * 1.04139 = 6.248
        val samples = listOf(HeartRateSample(0L, 110.0, 600.0))
        val result = engine.computeStrain(samples)
        assertApprox(10.0, result.weightedHRArea, 0.01)
        assertApprox(6.248, result.strain, 0.01)
        assertApprox(10.0, result.zone1Minutes, 0.01)
    }

    @Test
    fun computeStrain_zone5Only_highStrain() {
        // 30 min at 185bpm (Zone5, mult=5.0) -> weighted = 30 * 5.0 = 150
        // strain = 6 * log10(150 + 1) = 6 * 2.17900 = 13.074
        val samples = listOf(HeartRateSample(0L, 185.0, 1800.0))
        val result = engine.computeStrain(samples)
        assertApprox(150.0, result.weightedHRArea, 0.01)
        assertApprox(13.07, result.strain, 0.02)
        assertApprox(30.0, result.zone5Minutes, 0.01)
    }

    @Test
    fun computeStrain_mixedZones_correctZoneTracking() {
        // 5min@110(Z1) + 5min@130(Z2) + 5min@150(Z3)
        // weighted = 5*1 + 5*2 + 5*3 = 30
        // strain = 6 * log10(31) = 6 * 1.4914 = 8.949
        val samples = listOf(
            HeartRateSample(0L, 110.0, 300.0),
            HeartRateSample(300_000L, 130.0, 300.0),
            HeartRateSample(600_000L, 150.0, 300.0),
        )
        val result = engine.computeStrain(samples)
        assertApprox(30.0, result.weightedHRArea, 0.01)
        assertApprox(8.95, result.strain, 0.02)
        assertApprox(5.0, result.zone1Minutes, 0.01)
        assertApprox(5.0, result.zone2Minutes, 0.01)
        assertApprox(5.0, result.zone3Minutes, 0.01)
    }

    @Test
    fun computeStrain_clampedAtMax21() {
        // Very long Z5 session: 10000 min at 185bpm -> weighted = 50000
        // strain = 6 * log10(50001) = 6 * 4.699 = 28.19, clamped to 21
        val samples = listOf(HeartRateSample(0L, 185.0, 600000.0))
        val result = engine.computeStrain(samples)
        assertApprox(21.0, result.strain, 0.01)
    }

    @Test
    fun estimateDurations_singleSample_defaults5seconds() {
        val raw = listOf(0L to 150.0)
        val result = StrainEngine.estimateDurations(raw)
        assertEquals(1, result.size)
        assertApprox(5.0, result[0].durationSeconds, 0.001)
    }

    @Test
    fun estimateDurations_multipleSamples_computesDiffs() {
        // 10s apart
        val raw = listOf(0L to 150.0, 10000L to 155.0, 20000L to 160.0)
        val result = StrainEngine.estimateDurations(raw)
        assertEquals(3, result.size)
        assertApprox(10.0, result[0].durationSeconds, 0.001)
        assertApprox(10.0, result[1].durationSeconds, 0.001)
        assertApprox(10.0, result[2].durationSeconds, 0.001) // last copies prev
    }

    @Test
    fun estimateDurations_capsAtMaxDuration() {
        // 120s apart, capped at 60s
        val raw = listOf(0L to 150.0, 120000L to 155.0)
        val result = StrainEngine.estimateDurations(raw)
        assertApprox(60.0, result[0].durationSeconds, 0.001)
    }
}
