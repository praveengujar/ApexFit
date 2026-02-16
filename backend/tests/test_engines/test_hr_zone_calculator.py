"""Heart rate zone calculator tests — mirrors HeartRateZoneCalculatorTest.kt."""

from tests.test_engines.conftest import DEFAULT_MAX_HR, HR_ZONE_CONFIG

from app.engines.hr_zone_calculator import HeartRateZoneCalculator


def _approx(expected, actual, tolerance=0.001):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


calculator = HeartRateZoneCalculator(DEFAULT_MAX_HR, HR_ZONE_CONFIG)


def test_zone_below_threshold_returns_none():
    # 90 bpm = 45% of 200, below 50% boundary
    assert calculator.zone(90.0) is None


def test_zone_at_lower_boundary_returns_zone1():
    # 100 bpm = exactly 50%
    zone = calculator.zone(100.0)
    assert zone is not None
    assert zone.zone == 1
    assert zone.name == "Warm-Up"


def test_zone_in_zone1_returns_correct_multiplier():
    _approx(1.0, calculator.multiplier(110.0))
    assert calculator.zone_number(110.0) == 1


def test_zone_in_zone2_returns_correct_multiplier():
    _approx(2.0, calculator.multiplier(130.0))
    assert calculator.zone_number(130.0) == 2


def test_zone_in_zone3_returns_correct_multiplier():
    _approx(3.0, calculator.multiplier(150.0))
    assert calculator.zone_number(150.0) == 3


def test_zone_in_zone4_returns_correct_multiplier():
    _approx(4.0, calculator.multiplier(170.0))
    assert calculator.zone_number(170.0) == 4


def test_zone_in_zone5_returns_correct_multiplier():
    _approx(5.0, calculator.multiplier(185.0))
    assert calculator.zone_number(185.0) == 5


def test_zone_at_max_hr_returns_zone5():
    zone = calculator.zone(200.0)
    assert zone is not None
    assert zone.zone == 5


def test_multiplier_below_threshold_returns_zero():
    _approx(0.0, calculator.multiplier(80.0))


def test_zone_number_below_threshold_returns_zero():
    assert calculator.zone_number(80.0) == 0
