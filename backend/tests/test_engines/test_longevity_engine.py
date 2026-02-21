"""Longevity engine tests — covers dose-response curves, delta years, and compute."""

import math

from app.engines.longevity_engine import (
    GOMPERTZ_B,
    LongevityCategory,
    MetricID,
    MetricInput,
    compute,
    delta_years,
    format_value,
    hazard_ratio,
)


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


# -- Delta years --


def test_delta_years_hr_of_1_returns_zero():
    # ln(1.0) / 0.09 = 0
    _approx(0.0, delta_years(1.0))


def test_delta_years_hr_below_1_negative():
    # hr < 1.0 means protective -> negative delta (younger)
    dy = delta_years(0.8)
    assert dy < 0


def test_delta_years_hr_above_1_positive():
    # hr > 1.0 means harmful -> positive delta (older)
    dy = delta_years(1.5)
    assert dy > 0


def test_delta_years_zero_hr_returns_zero():
    _approx(0.0, delta_years(0.0))


# -- Hazard ratio dose-response curves --


def test_hr_sleep_consistency_low_value_high_risk():
    hr = hazard_ratio(MetricID.SLEEP_CONSISTENCY, 40.0)
    _approx(1.48, hr, tolerance=0.01)


def test_hr_sleep_consistency_high_value_low_risk():
    hr = hazard_ratio(MetricID.SLEEP_CONSISTENCY, 100.0)
    _approx(0.92, hr, tolerance=0.01)


def test_hr_hours_of_sleep_optimal_7h():
    hr = hazard_ratio(MetricID.HOURS_OF_SLEEP, 7.0)
    _approx(1.0, hr, tolerance=0.01)


def test_hr_hours_of_sleep_u_shape():
    # Both too little and too much sleep increases risk
    hr_short = hazard_ratio(MetricID.HOURS_OF_SLEEP, 4.0)
    hr_long = hazard_ratio(MetricID.HOURS_OF_SLEEP, 10.0)
    hr_optimal = hazard_ratio(MetricID.HOURS_OF_SLEEP, 8.0)
    assert hr_short > hr_optimal
    assert hr_long > hr_optimal


def test_hr_daily_steps_higher_is_better():
    hr_sedentary = hazard_ratio(MetricID.DAILY_STEPS, 2000.0)
    hr_active = hazard_ratio(MetricID.DAILY_STEPS, 10000.0)
    assert hr_sedentary > hr_active


def test_hr_daily_steps_plateau():
    hr_12k = hazard_ratio(MetricID.DAILY_STEPS, 12000.0)
    hr_16k = hazard_ratio(MetricID.DAILY_STEPS, 16000.0)
    _approx(hr_12k, hr_16k, tolerance=0.01)


def test_hr_vo2_max_low_is_risky():
    hr = hazard_ratio(MetricID.VO2_MAX, 15.0)
    _approx(2.0, hr, tolerance=0.01)


def test_hr_vo2_max_high_is_protective():
    hr = hazard_ratio(MetricID.VO2_MAX, 70.0)
    _approx(0.45, hr, tolerance=0.01)


def test_hr_resting_hr_inverse():
    # Lower RHR is better
    hr_low = hazard_ratio(MetricID.RESTING_HEART_RATE, 50.0)
    hr_high = hazard_ratio(MetricID.RESTING_HEART_RATE, 80.0)
    assert hr_low < hr_high


def test_hr_strength_activity_optimal_around_1h():
    hr_0 = hazard_ratio(MetricID.STRENGTH_ACTIVITY_WEEKLY, 0.0)
    hr_1 = hazard_ratio(MetricID.STRENGTH_ACTIVITY_WEEKLY, 1.0)
    assert hr_1 < hr_0  # 1h is protective vs none


# -- MetricID properties --


def test_metric_id_categories():
    assert MetricID.HOURS_OF_SLEEP.category == LongevityCategory.SLEEP
    assert MetricID.DAILY_STEPS.category == LongevityCategory.STRAIN
    assert MetricID.VO2_MAX.category == LongevityCategory.FITNESS


def test_metric_id_higher_is_better():
    assert MetricID.DAILY_STEPS.is_higher_better is True
    assert MetricID.VO2_MAX.is_higher_better is True
    assert MetricID.RESTING_HEART_RATE.is_higher_better is False


# -- Full compute --


def test_compute_empty_inputs():
    result = compute(chronological_age=40.0, inputs=[])
    _approx(40.0, result.zyva_age)
    _approx(0.0, result.years_younger_older)


def test_compute_single_protective_metric():
    inputs = [MetricInput(id=MetricID.VO2_MAX, six_month_avg=50.0, thirty_day_avg=50.0)]
    result = compute(chronological_age=40.0, inputs=inputs)
    # VO2 50 -> hr=0.64, delta=ln(0.64)/0.09 ≈ -4.96, corrected * 0.85 ≈ -4.22
    assert result.zyva_age < 40.0  # should be younger
    assert result.years_younger_older < 0


def test_compute_single_harmful_metric():
    inputs = [MetricInput(id=MetricID.VO2_MAX, six_month_avg=15.0, thirty_day_avg=15.0)]
    result = compute(chronological_age=40.0, inputs=inputs)
    # VO2 15 -> hr=2.0, delta=ln(2.0)/0.09 ≈ 7.70, corrected * 0.85 ≈ 6.55
    assert result.zyva_age > 40.0  # should be older
    assert result.years_younger_older > 0


def test_compute_multiple_metrics():
    inputs = [
        MetricInput(id=MetricID.HOURS_OF_SLEEP, six_month_avg=7.5, thirty_day_avg=7.5),
        MetricInput(id=MetricID.DAILY_STEPS, six_month_avg=10000.0, thirty_day_avg=10000.0),
        MetricInput(id=MetricID.VO2_MAX, six_month_avg=45.0, thirty_day_avg=45.0),
    ]
    result = compute(chronological_age=35.0, inputs=inputs)
    assert len(result.metric_results) == 3
    # All protective metrics -> should be younger
    assert result.zyva_age < 35.0


def test_compute_skips_none_inputs():
    inputs = [MetricInput(id=MetricID.VO2_MAX, six_month_avg=None, thirty_day_avg=None)]
    result = compute(chronological_age=40.0, inputs=inputs)
    assert len(result.metric_results) == 0
    _approx(40.0, result.zyva_age)


# -- Value formatting --


def test_format_value_steps():
    assert format_value(10000, MetricID.DAILY_STEPS) == "10,000"


def test_format_value_sleep_hours():
    assert format_value(7.5, MetricID.HOURS_OF_SLEEP) == "7:30"


def test_format_value_percentage():
    assert format_value(85, MetricID.SLEEP_CONSISTENCY) == "85%"


def test_format_value_vo2_max():
    assert format_value(45, MetricID.VO2_MAX) == "45 ml/kg/min"


def test_format_value_rhr():
    assert format_value(60, MetricID.RESTING_HEART_RATE) == "60 bpm"
