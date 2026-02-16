"""Strain engine tests — mirrors StrainEngineTest.kt."""

from tests.test_engines.conftest import DEFAULT_MAX_HR, HR_ZONE_CONFIG, STRAIN_CONFIG

from app.engines.strain_engine import HeartRateSample, StrainEngine, estimate_durations


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


engine = StrainEngine(DEFAULT_MAX_HR, STRAIN_CONFIG, HR_ZONE_CONFIG)


def test_compute_strain_empty_samples_returns_zero():
    result = engine.compute_strain([])
    # strain = 6 * log10(0 + 1) = 6 * 0 = 0
    _approx(0.0, result.strain)
    _approx(0.0, result.zone1_minutes, tolerance=0.001)
    _approx(0.0, result.zone5_minutes, tolerance=0.001)


def test_compute_strain_below_zone1_no_contribution():
    # HR=90 at maxHR=200 is 45%, below 50% threshold -> multiplier=0
    samples = [HeartRateSample(0, 90.0, 600.0)]  # 10 min
    result = engine.compute_strain(samples)
    _approx(0.0, result.weighted_hr_area, tolerance=0.001)
    _approx(0.0, result.strain)


def test_compute_strain_zone1_only_low_strain():
    # 10 min at 110bpm (Zone1, mult=1.0) -> weighted = 10 * 1.0 = 10
    # strain = 6 * log10(10 + 1) = 6 * 1.04139 = 6.248
    samples = [HeartRateSample(0, 110.0, 600.0)]
    result = engine.compute_strain(samples)
    _approx(10.0, result.weighted_hr_area)
    _approx(6.248, result.strain)
    _approx(10.0, result.zone1_minutes)


def test_compute_strain_zone5_only_high_strain():
    # 30 min at 185bpm (Zone5, mult=5.0) -> weighted = 30 * 5.0 = 150
    # strain = 6 * log10(150 + 1) = 6 * 2.17900 = 13.074
    samples = [HeartRateSample(0, 185.0, 1800.0)]
    result = engine.compute_strain(samples)
    _approx(150.0, result.weighted_hr_area)
    _approx(13.07, result.strain, tolerance=0.02)
    _approx(30.0, result.zone5_minutes)


def test_compute_strain_mixed_zones_correct_zone_tracking():
    # 5min@110(Z1) + 5min@130(Z2) + 5min@150(Z3)
    # weighted = 5*1 + 5*2 + 5*3 = 30
    # strain = 6 * log10(31) = 6 * 1.4914 = 8.949
    samples = [
        HeartRateSample(0, 110.0, 300.0),
        HeartRateSample(300_000, 130.0, 300.0),
        HeartRateSample(600_000, 150.0, 300.0),
    ]
    result = engine.compute_strain(samples)
    _approx(30.0, result.weighted_hr_area)
    _approx(8.95, result.strain, tolerance=0.02)
    _approx(5.0, result.zone1_minutes)
    _approx(5.0, result.zone2_minutes)
    _approx(5.0, result.zone3_minutes)


def test_compute_strain_clamped_at_max_21():
    # Very long Z5 session: 10000 min at 185bpm -> weighted = 50000
    # strain = 6 * log10(50001) = 6 * 4.699 = 28.19, clamped to 21
    samples = [HeartRateSample(0, 185.0, 600000.0)]
    result = engine.compute_strain(samples)
    _approx(21.0, result.strain)


def test_estimate_durations_single_sample_defaults_5_seconds():
    raw = [(0, 150.0)]
    result = estimate_durations(raw)
    assert len(result) == 1
    _approx(5.0, result[0].duration_seconds, tolerance=0.001)


def test_estimate_durations_multiple_samples_computes_diffs():
    # 10s apart
    raw = [(0, 150.0), (10000, 155.0), (20000, 160.0)]
    result = estimate_durations(raw)
    assert len(result) == 3
    _approx(10.0, result[0].duration_seconds, tolerance=0.001)
    _approx(10.0, result[1].duration_seconds, tolerance=0.001)
    _approx(10.0, result[2].duration_seconds, tolerance=0.001)  # last copies prev


def test_estimate_durations_caps_at_max_duration():
    # 120s apart, capped at 60s
    raw = [(0, 150.0), (120000, 155.0)]
    result = estimate_durations(raw)
    _approx(60.0, result[0].duration_seconds, tolerance=0.001)
