"""Muscular load engine tests — mirrors MuscularLoadEngineTest.kt."""

from app.engines.muscular_load_engine import compute_load, is_strength_workout


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


def test_is_strength_workout_strength_type_returns_true():
    assert is_strength_workout("traditionalStrengthTraining")


def test_is_strength_workout_high_intensity_type_returns_true():
    assert is_strength_workout("crossTraining")


def test_is_strength_workout_running_returns_false():
    assert not is_strength_workout("running")


def test_is_strength_workout_yoga_returns_false():
    assert not is_strength_workout("yoga")


def test_compute_load_strength_workout_basic():
    # factor=1.0, dur=60, avgHR=140, maxHR=170, userMax=200
    # volume = 60 * 1.0 = 60
    # intensity = (140/200) * (170/200) = 0.7 * 0.85 = 0.595
    # load = 60 * 0.595 * 2.0 = 71.4
    result = compute_load(
        workout_type="traditionalStrengthTraining",
        duration_minutes=60.0,
        average_heart_rate=140.0,
        max_heart_rate_during_workout=170.0,
        user_max_heart_rate=200.0,
    )
    _approx(71.4, result.load, tolerance=0.1)
    _approx(60.0, result.volume_score)
    _approx(0.595, result.intensity_score, tolerance=0.001)


def test_compute_load_with_rpe_adjusts_load():
    # Same as above but RPE=8 -> rpeAdj = 1 + (8-5)*0.1 = 1.3
    # load = 71.4 * 1.3 = 92.82
    result = compute_load(
        workout_type="traditionalStrengthTraining",
        duration_minutes=60.0,
        average_heart_rate=140.0,
        max_heart_rate_during_workout=170.0,
        user_max_heart_rate=200.0,
        rpe=8,
    )
    _approx(92.82, result.load, tolerance=0.1)


def test_compute_load_clamped_at_100():
    result = compute_load(
        workout_type="traditionalStrengthTraining",
        duration_minutes=120.0,
        average_heart_rate=190.0,
        max_heart_rate_during_workout=200.0,
        user_max_heart_rate=200.0,
        rpe=10,
    )
    _approx(100.0, result.load)


def test_compute_load_unknown_type_uses_fallback_factor():
    # "running" not in map -> factor=0.5
    # volume = 30 * 0.5 = 15
    # intensity = (150/200) * (170/200) = 0.75 * 0.85 = 0.6375
    # load = 15 * 0.6375 * 2.0 = 19.125
    result = compute_load(
        workout_type="running",
        duration_minutes=30.0,
        average_heart_rate=150.0,
        max_heart_rate_during_workout=170.0,
        user_max_heart_rate=200.0,
    )
    _approx(19.13, result.load, tolerance=0.1)
