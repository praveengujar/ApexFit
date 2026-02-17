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

        val intervals = rrIntervalsSeconds.zipWithNext { a, b -> (b - a) * 1000 }
            .filter { it in 200.0..2000.0 }

        if (intervals.size <= 1) return null

        val squaredDiffs = intervals.zipWithNext { a, b -> (b - a).let { it * it } }

        if (squaredDiffs.isEmpty()) return null

        return sqrt(squaredDiffs.average())
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
