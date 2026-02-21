package com.zyva.shared.platform

import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970

actual fun randomUUID(): String = NSUUID().UUIDString()

actual fun currentTimeMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun minutesSinceMidnight(epochMillis: Long): Double {
    val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(
        NSCalendarUnitHour or NSCalendarUnitMinute,
        fromDate = date
    )
    var minutes = components.hour * 60.0 + components.minute
    // Late-night bedtimes (after 6 PM) wrapped to negative for proximity to midnight
    if (minutes > 18 * 60) {
        minutes -= 24 * 60
    }
    return minutes
}

actual fun calculateAge(dobEpochMillis: Long): Int {
    val dobDate = NSDate.dateWithTimeIntervalSince1970(dobEpochMillis / 1000.0)
    val now = NSDate()
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(
        NSCalendarUnitYear,
        fromDate = dobDate,
        toDate = now,
        options = 0u
    )
    return components.year.toInt()
}
