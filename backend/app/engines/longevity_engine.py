"""Longevity engine — mirrors shared/engine/LongevityEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass
from enum import StrEnum

# Gompertz slope parameter (mortality doubling rate per year).
GOMPERTZ_B: float = 0.09

# Overlap correction to avoid double-counting correlated metrics.
OVERLAP_CORRECTION: float = 0.85


# -- Metric Identification --


class MetricID(StrEnum):
    SLEEP_CONSISTENCY = "sleepConsistency"
    HOURS_OF_SLEEP = "hoursOfSleep"
    HR_ZONES_1_TO_3_WEEKLY = "hrZones1to3Weekly"
    HR_ZONES_4_TO_5_WEEKLY = "hrZones4to5Weekly"
    STRENGTH_ACTIVITY_WEEKLY = "strengthActivityWeekly"
    DAILY_STEPS = "dailySteps"
    VO2_MAX = "vo2Max"
    RESTING_HEART_RATE = "restingHeartRate"
    LEAN_BODY_MASS = "leanBodyMass"

    @property
    def category(self) -> LongevityCategory:
        _cat = {
            MetricID.SLEEP_CONSISTENCY: LongevityCategory.SLEEP,
            MetricID.HOURS_OF_SLEEP: LongevityCategory.SLEEP,
            MetricID.HR_ZONES_1_TO_3_WEEKLY: LongevityCategory.STRAIN,
            MetricID.HR_ZONES_4_TO_5_WEEKLY: LongevityCategory.STRAIN,
            MetricID.STRENGTH_ACTIVITY_WEEKLY: LongevityCategory.STRAIN,
            MetricID.DAILY_STEPS: LongevityCategory.STRAIN,
            MetricID.VO2_MAX: LongevityCategory.FITNESS,
            MetricID.RESTING_HEART_RATE: LongevityCategory.FITNESS,
            MetricID.LEAN_BODY_MASS: LongevityCategory.FITNESS,
        }
        return _cat[self]

    @property
    def display_name(self) -> str:
        _names = {
            MetricID.SLEEP_CONSISTENCY: "SLEEP CONSISTENCY",
            MetricID.HOURS_OF_SLEEP: "HOURS OF SLEEP",
            MetricID.HR_ZONES_1_TO_3_WEEKLY: "TIME IN HR ZONES 1-3 (WEEKLY)",
            MetricID.HR_ZONES_4_TO_5_WEEKLY: "TIME IN HR ZONES 4-5 (WEEKLY)",
            MetricID.STRENGTH_ACTIVITY_WEEKLY: "STRENGTH ACTIVITY TIME (WEEKLY)",
            MetricID.DAILY_STEPS: "STEPS",
            MetricID.VO2_MAX: "VO\u2082 MAX",
            MetricID.RESTING_HEART_RATE: "RHR",
            MetricID.LEAN_BODY_MASS: "LEAN BODY MASS",
        }
        return _names[self]

    @property
    def unit(self) -> str:
        _units = {
            MetricID.SLEEP_CONSISTENCY: "%",
            MetricID.HOURS_OF_SLEEP: "h",
            MetricID.HR_ZONES_1_TO_3_WEEKLY: "h",
            MetricID.HR_ZONES_4_TO_5_WEEKLY: "h",
            MetricID.STRENGTH_ACTIVITY_WEEKLY: "h",
            MetricID.DAILY_STEPS: "Steps",
            MetricID.VO2_MAX: "ml/kg/min",
            MetricID.RESTING_HEART_RATE: "bpm",
            MetricID.LEAN_BODY_MASS: "%",
        }
        return _units[self]

    @property
    def gradient_range(self) -> tuple[float, float]:
        _ranges: dict[MetricID, tuple[float, float]] = {
            MetricID.SLEEP_CONSISTENCY: (40.0, 100.0),
            MetricID.HOURS_OF_SLEEP: (5.0, 8.0),
            MetricID.HR_ZONES_1_TO_3_WEEKLY: (0.0, 5.0),
            MetricID.HR_ZONES_4_TO_5_WEEKLY: (0.0, 1.0),
            MetricID.STRENGTH_ACTIVITY_WEEKLY: (0.0, 2.0),
            MetricID.DAILY_STEPS: (0.0, 16000.0),
            MetricID.VO2_MAX: (15.0, 70.0),
            MetricID.RESTING_HEART_RATE: (40.0, 80.0),
            MetricID.LEAN_BODY_MASS: (60.0, 95.0),
        }
        return _ranges[self]

    @property
    def is_higher_better(self) -> bool:
        return self != MetricID.RESTING_HEART_RATE


class LongevityCategory(StrEnum):
    SLEEP = "Sleep"
    STRAIN = "Strain"
    FITNESS = "Fitness"


# -- Input / Output Types --


@dataclass
class MetricInput:
    id: MetricID
    six_month_avg: float | None
    thirty_day_avg: float | None


@dataclass
class MetricResult:
    id: MetricID
    six_month_avg: float
    thirty_day_avg: float
    hazard_ratio: float
    delta_years: float
    insight_title: str
    insight_body: str


@dataclass
class LongevityResult:
    chronological_age: float
    zyva_age: float
    years_younger_older: float  # negative = younger
    pace_of_aging: float  # -1.0 to 3.0
    metric_results: list[MetricResult]
    week_start_millis: int
    week_end_millis: int
    overall_insight_title: str
    overall_insight_body: str


# -- Core Computation --


def compute(
    chronological_age: float,
    inputs: list[MetricInput],
    week_start_millis: int = 0,
    week_end_millis: int = 0,
) -> LongevityResult:
    metric_results: list[MetricResult] = []
    total_delta_6mo = 0.0
    total_delta_30day = 0.0

    for inp in inputs:
        avg_6mo = inp.six_month_avg if inp.six_month_avg is not None else inp.thirty_day_avg
        avg_30day = inp.thirty_day_avg if inp.thirty_day_avg is not None else inp.six_month_avg

        if avg_6mo is None:
            continue
        val_30day = avg_30day if avg_30day is not None else avg_6mo

        hr_6mo = hazard_ratio(inp.id, avg_6mo)
        hr_30day = hazard_ratio(inp.id, val_30day)
        delta_6mo = delta_years(hr_6mo)
        delta_30day = delta_years(hr_30day)

        total_delta_6mo += delta_6mo
        total_delta_30day += delta_30day

        insight_title, insight_body = _generate_metric_insight(inp.id, avg_6mo, delta_6mo)

        metric_results.append(
            MetricResult(
                id=inp.id,
                six_month_avg=avg_6mo,
                thirty_day_avg=val_30day,
                hazard_ratio=hr_6mo,
                delta_years=delta_6mo,
                insight_title=insight_title,
                insight_body=insight_body,
            )
        )

    # Apply overlap correction
    total_delta_6mo *= OVERLAP_CORRECTION
    total_delta_30day *= OVERLAP_CORRECTION

    zyva_age = chronological_age + total_delta_6mo
    years_younger_older = total_delta_6mo
    projected_age_30 = chronological_age + total_delta_30day

    age_diff = projected_age_30 - zyva_age
    raw_pace = 1.0 + (age_diff / 2.5)
    pace_of_aging = max(-1.0, min(3.0, raw_pace))

    overall_title, overall_body = _generate_overall_insight(
        years_younger_older, pace_of_aging, metric_results
    )

    return LongevityResult(
        chronological_age=chronological_age,
        zyva_age=zyva_age,
        years_younger_older=years_younger_older,
        pace_of_aging=pace_of_aging,
        metric_results=metric_results,
        week_start_millis=week_start_millis,
        week_end_millis=week_end_millis,
        overall_insight_title=overall_title,
        overall_insight_body=overall_body,
    )


# -- Hazard Ratio Computation --


def delta_years(hr: float) -> float:
    if hr <= 0:
        return 0.0
    return math.log(hr) / GOMPERTZ_B


def hazard_ratio(metric_id: MetricID, value: float) -> float:
    _dispatch = {
        MetricID.SLEEP_CONSISTENCY: _hr_sleep_consistency,
        MetricID.HOURS_OF_SLEEP: _hr_hours_of_sleep,
        MetricID.HR_ZONES_1_TO_3_WEEKLY: _hr_zones_1_to_3,
        MetricID.HR_ZONES_4_TO_5_WEEKLY: _hr_zones_4_to_5,
        MetricID.STRENGTH_ACTIVITY_WEEKLY: _hr_strength_activity,
        MetricID.DAILY_STEPS: _hr_daily_steps,
        MetricID.VO2_MAX: _hr_vo2_max,
        MetricID.RESTING_HEART_RATE: _hr_resting_hr,
        MetricID.LEAN_BODY_MASS: _hr_lean_body_mass,
    }
    return _dispatch[metric_id](value)


# -- Dose-Response Curves --


def _hr_sleep_consistency(pct: float) -> float:
    points = [(40.0, 1.48), (50.0, 1.40), (60.0, 1.20), (70.0, 1.10), (85.0, 1.0), (100.0, 0.92)]
    return _interpolate(pct, points)


def _hr_hours_of_sleep(hours: float) -> float:
    points = [
        (4.0, 1.20), (5.0, 1.14), (6.0, 1.07), (7.0, 1.0),
        (8.0, 0.98), (9.0, 1.0), (10.0, 1.10),
    ]
    return _interpolate(hours, points)


def _hr_zones_1_to_3(hours: float) -> float:
    points = [(0.0, 1.0), (1.0, 0.90), (2.5, 0.79), (5.0, 0.78), (8.0, 0.78)]
    return _interpolate(hours, points)


def _hr_zones_4_to_5(hours: float) -> float:
    points = [(0.0, 1.0), (0.5, 0.88), (1.25, 0.77), (2.5, 0.77), (4.0, 0.80)]
    return _interpolate(hours, points)


def _hr_strength_activity(hours: float) -> float:
    points = [(0.0, 1.0), (0.5, 0.85), (1.0, 0.73), (1.5, 0.75), (3.0, 0.80)]
    return _interpolate(hours, points)


def _hr_daily_steps(steps: float) -> float:
    points = [
        (0.0, 1.30), (2000.0, 1.18), (4000.0, 1.06), (6000.0, 0.90),
        (8000.0, 0.78), (10000.0, 0.68), (12000.0, 0.65), (16000.0, 0.65),
    ]
    return _interpolate(steps, points)


def _hr_vo2_max(vo2: float) -> float:
    points = [
        (15.0, 2.00), (20.0, 1.70), (25.0, 1.40), (30.0, 1.15), (35.0, 1.0),
        (40.0, 0.86), (45.0, 0.74), (50.0, 0.64), (55.0, 0.55), (60.0, 0.50), (70.0, 0.45),
    ]
    return _interpolate(vo2, points)


def _hr_resting_hr(bpm: float) -> float:
    points = [
        (40.0, 0.82), (45.0, 0.85), (50.0, 0.90), (55.0, 0.95), (60.0, 1.0),
        (65.0, 1.05), (70.0, 1.09), (75.0, 1.20), (80.0, 1.45), (90.0, 1.65),
    ]
    return _interpolate(bpm, points)


def _hr_lean_body_mass(pct: float) -> float:
    points = [
        (55.0, 1.70), (60.0, 1.57), (65.0, 1.30), (70.0, 1.10), (75.0, 1.0),
        (80.0, 0.95), (85.0, 0.90), (90.0, 0.88), (95.0, 0.88),
    ]
    return _interpolate(pct, points)


# -- Interpolation --


def _interpolate(value: float, points: list[tuple[float, float]]) -> float:
    if not points:
        return 1.0
    if value <= points[0][0]:
        return points[0][1]
    if value >= points[-1][0]:
        return points[-1][1]

    for i in range(len(points) - 1):
        x0, y0 = points[i]
        x1, y1 = points[i + 1]
        if x0 <= value <= x1:
            t = (value - x0) / (x1 - x0)
            return y0 + t * (y1 - y0)

    return 1.0


# -- Insight Generation --


def _generate_metric_insight(
    metric_id: MetricID,
    value: float,
    delta: float,
) -> tuple[str, str]:
    is_good = delta < -0.3
    is_bad = delta > 0.3

    if is_good:
        _good = {
            MetricID.SLEEP_CONSISTENCY: (
                "Well Done",
                "Your sleep consistency is helping extend your healthspan."
                " Maintaining a regular schedule is one of the strongest"
                " longevity factors.",
            ),
            MetricID.HOURS_OF_SLEEP: (
                "Optimal Sleep",
                "You're getting enough sleep to support recovery and"
                " long-term health. Keep it up.",
            ),
            MetricID.HR_ZONES_1_TO_3_WEEKLY: (
                "Active Lifestyle",
                "Your weekly moderate activity is well within the range"
                " linked to reduced all-cause mortality.",
            ),
            MetricID.HR_ZONES_4_TO_5_WEEKLY: (
                "High Intensity Pay-Off",
                "Your vigorous exercise is contributing to"
                " cardiovascular fitness and longevity.",
            ),
            MetricID.STRENGTH_ACTIVITY_WEEKLY: (
                "Building Strength",
                "Resistance training is strongly linked to longevity."
                " Your weekly volume is in the optimal zone.",
            ),
            MetricID.DAILY_STEPS: (
                "Keep Moving",
                "Your daily step count is associated with significant"
                " mortality risk reduction.",
            ),
            MetricID.VO2_MAX: (
                "Elite Fitness",
                "Your cardiorespiratory fitness is a powerful predictor"
                " of longevity \u2014 stronger than smoking status.",
            ),
            MetricID.RESTING_HEART_RATE: (
                "Strong Heart",
                "A low resting heart rate reflects excellent"
                " cardiovascular efficiency.",
            ),
            MetricID.LEAN_BODY_MASS: (
                "Lean & Strong",
                "Maintaining lean body mass is crucial for metabolic"
                " health and longevity.",
            ),
        }
        return _good[metric_id]
    elif is_bad:
        _bad = {
            MetricID.SLEEP_CONSISTENCY: (
                "Time to Reassess",
                "Your sleep consistency is below the recommended range."
                " Irregular sleep patterns are associated with increased"
                " mortality risk.",
            ),
            MetricID.HOURS_OF_SLEEP: (
                "Sleep More",
                "Your sleep duration is below the 7-hour threshold"
                " linked to optimal health outcomes.",
            ),
            MetricID.HR_ZONES_1_TO_3_WEEKLY: (
                "Move More",
                "Increasing moderate activity to 150+ minutes per week"
                " could significantly reduce your mortality risk.",
            ),
            MetricID.HR_ZONES_4_TO_5_WEEKLY: (
                "Push Harder",
                "Adding vigorous exercise can provide additional"
                " cardiovascular benefits beyond moderate activity alone.",
            ),
            MetricID.STRENGTH_ACTIVITY_WEEKLY: (
                "Add Resistance",
                "Even 30 minutes of weekly strength training is"
                " associated with 15% lower mortality risk.",
            ),
            MetricID.DAILY_STEPS: (
                "Step It Up",
                "Increasing your daily steps toward 8,000 could"
                " meaningfully impact your long-term health.",
            ),
            MetricID.VO2_MAX: (
                "Build Fitness",
                "Improving cardiorespiratory fitness is one of the most"
                " impactful changes you can make for longevity.",
            ),
            MetricID.RESTING_HEART_RATE: (
                "Heart Health",
                "An elevated resting heart rate may indicate"
                " cardiovascular stress. Regular aerobic exercise"
                " can help lower it.",
            ),
            MetricID.LEAN_BODY_MASS: (
                "Build Muscle",
                "Low lean body mass is associated with increased"
                " mortality risk. Strength training can help.",
            ),
        }
        return _bad[metric_id]
    else:
        return (
            "On Track",
            f"Your {metric_id.display_name.lower()} is near the"
            " baseline. Small improvements can shift this toward"
            " positive impact.",
        )


def _generate_overall_insight(
    years_younger_older: float,
    pace_of_aging: float,
    metric_results: list[MetricResult],
) -> tuple[str, str]:
    is_younger = years_younger_older < -1.0
    pace_improving = pace_of_aging < 1.0

    sorted_results = sorted(metric_results, key=lambda r: r.delta_years)
    best_metric = sorted_results[0] if sorted_results else None
    worst_metric = sorted_results[-1] if sorted_results else None

    if is_younger and pace_improving:
        best_name = (
            best_metric.id.display_name.lower()
            if best_metric
            else "your habits"
        )
        return (
            "Crushing It",
            f"Your Zyva Age is improving and your Pace of Aging"
            f" is below 1.0x. Your {best_name} is a major contributor"
            " to your longevity gains.",
        )
    elif is_younger:
        return (
            "Solid Foundation",
            "You're biologically younger than your chronological age."
            " Keep your current habits consistent to maintain"
            " these gains.",
        )
    elif pace_improving:
        return (
            "Trending Better",
            "Your recent habits are pushing your Pace of Aging in"
            " the right direction. Keep the momentum going.",
        )
    else:
        worst_name = (
            worst_metric.id.display_name.lower()
            if worst_metric
            else "key metrics"
        )
        return (
            "Room for Growth",
            f"Focus on improving your {worst_name} \u2014 it has the"
            " largest impact on your longevity score right now.",
        )


# -- Value Formatting --


def format_value(value: float, metric_id: MetricID) -> str:
    if metric_id in (MetricID.SLEEP_CONSISTENCY, MetricID.LEAN_BODY_MASS):
        return f"{int(value)}%"
    elif metric_id == MetricID.HOURS_OF_SLEEP:
        hours = int(value)
        mins = int((value - hours) * 60)
        return f"{hours}:{mins:02d}"
    elif metric_id in (
        MetricID.HR_ZONES_1_TO_3_WEEKLY,
        MetricID.HR_ZONES_4_TO_5_WEEKLY,
        MetricID.STRENGTH_ACTIVITY_WEEKLY,
    ):
        hours = int(value)
        mins = int((value - hours) * 60)
        return f"{hours}:{mins:02d} h"
    elif metric_id == MetricID.DAILY_STEPS:
        return _format_with_commas(int(value))
    elif metric_id == MetricID.VO2_MAX:
        return f"{int(value)} ml/kg/min"
    elif metric_id == MetricID.RESTING_HEART_RATE:
        return f"{int(value)} bpm"
    return str(int(value))


def _format_with_commas(value: int) -> str:
    s = str(value)
    result: list[str] = []
    for i, ch in enumerate(s):
        if i > 0 and (len(s) - i) % 3 == 0:
            result.append(",")
        result.append(ch)
    return "".join(result)
