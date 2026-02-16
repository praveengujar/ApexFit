package com.apexfit.core.engine

import kotlin.math.pow
import kotlin.math.sqrt

val List<Double>.mean: Double
    get() = if (isEmpty()) 0.0 else sum() / size

val List<Double>.standardDeviation: Double
    get() {
        if (size <= 1) return 0.0
        val avg = mean
        val variance = map { (it - avg).pow(2) }.sum() / size
        return sqrt(variance)
    }

val List<Double>.median: Double
    get() {
        if (isEmpty()) return 0.0
        val sorted = sorted()
        val mid = size / 2
        return if (size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2.0
        } else {
            sorted[mid]
        }
    }

fun Double.clamped(range: ClosedFloatingPointRange<Double>): Double =
    coerceIn(range.start, range.endInclusive)
