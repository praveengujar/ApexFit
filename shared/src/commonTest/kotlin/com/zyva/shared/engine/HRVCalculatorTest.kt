package com.zyva.shared.engine

import kotlin.test.Test
import kotlin.test.*

class HRVCalculatorTest {

    @Test
    fun computeRMSSD_emptyList_returnsNull() {
        assertNull(HRVCalculator.computeRMSSD(emptyList()))
    }

    @Test
    fun computeRMSSD_singleValue_returnsNull() {
        assertNull(HRVCalculator.computeRMSSD(listOf(0.8)))
    }

    @Test
    fun computeRMSSD_constantIntervals_returnsZero() {
        // Timestamps: 0.0, 0.8, 1.6, 2.4 -> RR intervals: 800, 800, 800ms
        // Successive diffs: 0, 0 -> RMSSD = 0
        val result = HRVCalculator.computeRMSSD(listOf(0.0, 0.8, 1.6, 2.4))
        assertNotNull(result)
        assertApprox(0.0, result, 0.01)
    }

    @Test
    fun computeRMSSD_variableIntervals_returnsCorrectValue() {
        // Timestamps: 0.0, 0.8, 1.7, 2.4
        // RR intervals: 800ms, 900ms, 700ms
        // Successive diffs: 100, -200 -> squared: 10000, 40000
        // RMSSD = sqrt(25000) = 158.11
        val result = HRVCalculator.computeRMSSD(listOf(0.0, 0.8, 1.7, 2.4))
        assertNotNull(result)
        assertApprox(158.11, result, 0.5)
    }

    @Test
    fun bestHRV_prefersRMSSD() {
        val result = HRVCalculator.bestHRV(rmssdValue = 45.0, sdnnValue = 50.0)
        assertEquals(HRVMethod.RMSSD_FROM_HEALTH_CONNECT, result.method)
        assertEquals(45.0, result.rmssd)
    }

    @Test
    fun bestHRV_fallsBackToSDNN() {
        val result = HRVCalculator.bestHRV(rmssdValue = null, sdnnValue = 50.0)
        assertEquals(HRVMethod.SDNN_FROM_HEALTH_CONNECT, result.method)
        assertEquals(50.0, result.sdnn)
    }

    @Test
    fun effectiveHRV_prefersRMSSD() {
        val result = HRVResult(rmssd = 45.0, sdnn = 50.0, method = HRVMethod.RMSSD_FROM_HEALTH_CONNECT)
        assertEquals(45.0, HRVCalculator.effectiveHRV(result))
    }

    @Test
    fun effectiveHRV_fallsBackToSDNN() {
        val result = HRVResult(rmssd = null, sdnn = 50.0, method = HRVMethod.SDNN_FROM_HEALTH_CONNECT)
        assertEquals(50.0, HRVCalculator.effectiveHRV(result))
    }

    @Test
    fun effectiveHRV_noData_returnsNull() {
        val result = HRVResult(rmssd = null, sdnn = null, method = HRVMethod.SDNN_FROM_HEALTH_CONNECT)
        assertNull(HRVCalculator.effectiveHRV(result))
    }
}
