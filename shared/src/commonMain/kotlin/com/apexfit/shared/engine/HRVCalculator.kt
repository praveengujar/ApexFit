package com.apexfit.shared.engine

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

    fun computeRMSSD(rrIntervalsSeconds: List<Double>): Double? {
        if (rrIntervalsSeconds.size <= 1) return null

        val intervals = mutableListOf<Double>()
        for (i in 1 until rrIntervalsSeconds.size) {
            val interval = (rrIntervalsSeconds[i] - rrIntervalsSeconds[i - 1]) * 1000
            if (interval in 200.0..2000.0) {
                intervals.add(interval)
            }
        }

        if (intervals.size <= 1) return null

        val squaredDiffs = mutableListOf<Double>()
        for (i in 1 until intervals.size) {
            val diff = intervals[i] - intervals[i - 1]
            squaredDiffs.add(diff * diff)
        }

        if (squaredDiffs.isEmpty()) return null

        val meanSquaredDiff = squaredDiffs.sum() / squaredDiffs.size
        return sqrt(meanSquaredDiff)
    }

    fun bestHRV(rmssdValue: Double? = null, sdnnValue: Double? = null): HRVResult {
        if (rmssdValue != null) {
            return HRVResult(
                rmssd = rmssdValue,
                sdnn = sdnnValue,
                method = HRVMethod.RMSSD_FROM_HEALTH_CONNECT,
            )
        }

        if (sdnnValue != null) {
            return HRVResult(
                rmssd = null,
                sdnn = sdnnValue,
                method = HRVMethod.SDNN_FROM_HEALTH_CONNECT,
            )
        }

        return HRVResult(null, null, HRVMethod.SDNN_FROM_HEALTH_CONNECT)
    }

    fun effectiveHRV(result: HRVResult): Double? =
        result.rmssd ?: result.sdnn
}
