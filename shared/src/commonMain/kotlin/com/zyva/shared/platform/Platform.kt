package com.zyva.shared.platform

expect fun randomUUID(): String

expect fun currentTimeMillis(): Long

/**
 * Returns minutes since midnight for the given epoch millis.
 * Late-night bedtimes (after 6 PM) are wrapped to negative values
 * for proximity-to-midnight calculations.
 */
expect fun minutesSinceMidnight(epochMillis: Long): Double

/**
 * Calculates age in years from a date-of-birth given as epoch millis.
 */
expect fun calculateAge(dobEpochMillis: Long): Int
