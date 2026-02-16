package com.apexfit.core.engine

import kotlin.math.sqrt

enum class HRVMethod {
    RMSSD_FROM_RR_INTERVALS,
    RMSSD_FROM_HEALTH_CONNECT,
    SDNN_FROM_HEALTH_CONNECT,
}

data class HRVResult(
    val rmssd: Double?,
    val sdnn: Double?,
    val method: HRVMethod,
)

object HRVCalculator {

    /**
     * Compute RMSSD from raw RR intervals (in seconds).
     * RMSSD = sqrt( (1/N) * Σ(RR[i+1] - RR[i])² )
     */
    fun computeRMSSD(rrIntervalsSeconds: List<Double>): Double? {
        if (rrIntervalsSeconds.size <= 1) return null

        // Convert timestamps to successive RR intervals in ms
        val intervals = mutableListOf<Double>()
        for (i in 1 until rrIntervalsSeconds.size) {
            val interval = (rrIntervalsSeconds[i] - rrIntervalsSeconds[i - 1]) * 1000
            if (interval in 200.0..2000.0) { // Filter physiological range (30-300 BPM)
                intervals.add(interval)
            }
        }

        if (intervals.size <= 1) return null

        // Compute successive differences
        val squaredDiffs = mutableListOf<Double>()
        for (i in 1 until intervals.size) {
            val diff = intervals[i] - intervals[i - 1]
            squaredDiffs.add(diff * diff)
        }

        if (squaredDiffs.isEmpty()) return null

        val meanSquaredDiff = squaredDiffs.sum() / squaredDiffs.size
        return sqrt(meanSquaredDiff)
    }

    /**
     * On Android with Health Connect, RMSSD is available directly.
     * This simplified version prefers the direct RMSSD value.
     */
    fun bestHRV(rmssdValue: Double? = null, sdnnValue: Double? = null): HRVResult {
        // Prefer direct RMSSD from Health Connect
        if (rmssdValue != null) {
            return HRVResult(
                rmssd = rmssdValue,
                sdnn = sdnnValue,
                method = HRVMethod.RMSSD_FROM_HEALTH_CONNECT,
            )
        }

        // Fall back to SDNN
        if (sdnnValue != null) {
            return HRVResult(
                rmssd = null,
                sdnn = sdnnValue,
                method = HRVMethod.SDNN_FROM_HEALTH_CONNECT,
            )
        }

        return HRVResult(null, null, HRVMethod.SDNN_FROM_HEALTH_CONNECT)
    }

    /**
     * The effective HRV value for use in recovery computation.
     * Returns RMSSD if available, otherwise SDNN.
     */
    fun effectiveHRV(result: HRVResult): Double? =
        result.rmssd ?: result.sdnn
}
