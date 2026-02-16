package com.apexfit.shared.platform

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Calendar

actual fun randomUUID(): String = java.util.UUID.randomUUID().toString()

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun minutesSinceMidnight(epochMillis: Long): Double {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = epochMillis
    }
    var minutes = calendar.get(Calendar.HOUR_OF_DAY) * 60.0 +
        calendar.get(Calendar.MINUTE)
    // Late-night bedtimes (after 6 PM) wrapped to negative for proximity to midnight
    if (minutes > 18 * 60) {
        minutes -= 24 * 60
    }
    return minutes
}

actual fun calculateAge(dobEpochMillis: Long): Int {
    val dob = Instant.ofEpochMilli(dobEpochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return Period.between(dob, LocalDate.now()).years
}
