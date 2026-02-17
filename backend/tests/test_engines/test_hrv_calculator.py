"""HRV calculator tests — mirrors HRVCalculatorTest.kt."""

from app.engines.hrv_calculator import HRVMethod, HRVResult, best_hrv, compute_rmssd, effective_hrv


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


def test_compute_rmssd_empty_returns_none():
    assert compute_rmssd([]) is None


def test_compute_rmssd_single_value_returns_none():
    assert compute_rmssd([0.8]) is None


def test_compute_rmssd_constant_intervals_returns_zero():
    # Timestamps: 0.0, 0.8, 1.6, 2.4 -> RR intervals: 800, 800, 800ms
    # Successive diffs: 0, 0 -> RMSSD = 0
    result = compute_rmssd([0.0, 0.8, 1.6, 2.4])
    assert result is not None
    _approx(0.0, result)


def test_compute_rmssd_variable_intervals_returns_correct_value():
    # Timestamps: 0.0, 0.8, 1.7, 2.4
    # RR intervals: 800ms, 900ms, 700ms
    # Successive diffs: 100, -200 -> squared: 10000, 40000
    # RMSSD = sqrt(25000) = 158.11
    result = compute_rmssd([0.0, 0.8, 1.7, 2.4])
    assert result is not None
    _approx(158.11, result, tolerance=0.5)


def test_best_hrv_prefers_rmssd():
    result = best_hrv(rmssd_value=45.0, sdnn_value=50.0)
    assert result.method == HRVMethod.RMSSD_FROM_HEALTH_CONNECT
    assert result.rmssd == 45.0


def test_best_hrv_falls_back_to_sdnn():
    result = best_hrv(rmssd_value=None, sdnn_value=50.0)
    assert result.method == HRVMethod.SDNN_FROM_HEALTH_CONNECT
    assert result.sdnn == 50.0


def test_effective_hrv_prefers_rmssd():
    result = HRVResult(rmssd=45.0, sdnn=50.0, method=HRVMethod.RMSSD_FROM_HEALTH_CONNECT)
    assert effective_hrv(result) == 45.0


def test_effective_hrv_falls_back_to_sdnn():
    result = HRVResult(rmssd=None, sdnn=50.0, method=HRVMethod.SDNN_FROM_HEALTH_CONNECT)
    assert effective_hrv(result) == 50.0


def test_effective_hrv_no_data_returns_none():
    result = HRVResult(rmssd=None, sdnn=None, method=HRVMethod.SDNN_FROM_HEALTH_CONNECT)
    assert effective_hrv(result) is None
