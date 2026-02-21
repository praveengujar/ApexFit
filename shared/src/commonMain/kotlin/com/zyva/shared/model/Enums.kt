package com.zyva.shared.model

enum class BiologicalSex {
    MALE, FEMALE, OTHER, NOT_SET;

    companion object {
        fun fromString(value: String): BiologicalSex =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: NOT_SET
    }
}

enum class MaxHRSource {
    USER_INPUT, OBSERVED, AGE_ESTIMATE
}

enum class UnitSystem {
    METRIC, IMPERIAL
}

enum class RecoveryZone {
    GREEN, YELLOW, RED;

    val label: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun from(score: Double): RecoveryZone = when {
            score >= 67.0 -> GREEN
            score >= 34.0 -> YELLOW
            else -> RED
        }
    }
}

enum class StrainZone {
    LIGHT, MODERATE, HIGH, OVERREACHING;

    val label: String get() = name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun from(score: Double): StrainZone = when {
            score < 8.0 -> LIGHT
            score < 14.0 -> MODERATE
            score < 18.0 -> HIGH
            else -> OVERREACHING
        }
    }
}

enum class SleepStageType(val label: String, val sortOrder: Int) {
    AWAKE("Awake", 0),
    LIGHT("Light", 1),
    DEEP("Deep (SWS)", 2),
    REM("REM", 3),
    IN_BED("In Bed", 4)
}

enum class JournalResponseType {
    TOGGLE, NUMERIC, SCALE
}

enum class BaselineMetricType {
    HRV,
    RESTING_HEART_RATE,
    RESPIRATORY_RATE,
    SPO2,
    SKIN_TEMPERATURE,
    SLEEP_DURATION,
    SLEEP_PERFORMANCE,
    STRAIN,
    STEPS,
    DEEP_SLEEP_PERCENTAGE,
    REM_SLEEP_PERCENTAGE
}

enum class NotificationType(val label: String, val description: String) {
    MORNING_RECOVERY(
        "Morning Recovery",
        "Get your recovery score when you wake up"
    ),
    BEDTIME_REMINDER(
        "Bedtime Reminder",
        "Reminder to go to bed based on sleep planner"
    ),
    STRAIN_TARGET(
        "Strain Target",
        "Alert when you hit your strain target"
    ),
    WEEKLY_REPORT(
        "Weekly Report",
        "Weekly performance summary"
    ),
    HEALTH_ALERT(
        "Health Alert",
        "Alerts for unusual health metrics"
    ),
    JOURNAL_REMINDER(
        "Journal Reminder",
        "Daily reminder to complete your journal"
    ),
    COACH_INSIGHT(
        "Coach Insight",
        "AI coach insights and tips"
    )
}

enum class SleepGoal {
    PEAK, PERFORM, GET_BY
}

enum class WearableDevice {
    APPLE_WATCH, PIXEL_WATCH, GARMIN, AMAZFIT, OURA_RING, OTHER, NONE;

    companion object {
        fun fromString(value: String): WearableDevice =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: NONE
    }
}
