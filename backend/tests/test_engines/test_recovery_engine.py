"""Recovery engine tests — mirrors RecoveryEngineTest.kt."""

from tests.test_engines.conftest import RECOVERY_CONFIG

from app.engines.baseline_engine import BaselineResult
from app.engines.recovery_engine import (
    RecoveryBaselines,
    RecoveryEngine,
    RecoveryInput,
    RecoveryResult,
    RecoveryZone,
)


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


engine = RecoveryEngine(RECOVERY_CONFIG)


def _valid_baseline(mean: float, sd: float) -> BaselineResult:
    return BaselineResult(mean=mean, standard_deviation=sd, sample_count=10, window_days=28)


def _invalid_baseline() -> BaselineResult:
    return BaselineResult(mean=60.0, standard_deviation=10.0, sample_count=2, window_days=28)


def test_compute_recovery_no_data_returns_default_50():
    result = engine.compute_recovery(RecoveryInput(), RecoveryBaselines())
    _approx(50.0, result.score)
    assert result.contributor_count == 0


def test_compute_recovery_hrv_above_baseline_high_score():
    # HRV=80, baseline(mean=60, sd=10): z=2.0
    # sigmoid = 100 / (1 + exp(-1.5*2.0)) = 100 / (1 + exp(-3.0)) = 95.26
    inp = RecoveryInput(hrv=80.0)
    baselines = RecoveryBaselines(hrv=_valid_baseline(60.0, 10.0))
    result = engine.compute_recovery(inp, baselines)
    _approx(95.26, result.score, tolerance=0.5)
    assert result.zone == RecoveryZone.GREEN
    assert result.contributor_count == 1


def test_compute_recovery_rhr_elevated_low_score():
    # RHR=75, baseline(mean=60, sd=5): z=3.0, inverted -> z=-3.0
    # sigmoid = 100 / (1 + exp(-1.5*(-3.0))) = 100 / (1 + exp(4.5)) = ~1.10
    inp = RecoveryInput(resting_heart_rate=75.0)
    baselines = RecoveryBaselines(resting_heart_rate=_valid_baseline(60.0, 5.0))
    result = engine.compute_recovery(inp, baselines)
    assert result.score < 5.0
    assert result.zone == RecoveryZone.RED


def test_compute_recovery_all_contributors_proper_weighting():
    inp = RecoveryInput(
        hrv=70.0,
        resting_heart_rate=58.0,
        sleep_performance=85.0,
        respiratory_rate=15.0,
        spo2=98.0,
        skin_temperature_deviation=0.1,
    )
    baselines = RecoveryBaselines(
        hrv=_valid_baseline(60.0, 10.0),
        resting_heart_rate=_valid_baseline(60.0, 5.0),
        sleep_performance=_valid_baseline(80.0, 10.0),
        respiratory_rate=_valid_baseline(16.0, 1.0),
        spo2=_valid_baseline(97.0, 1.0),
        skin_temperature=_valid_baseline(0.0, 0.5),
    )
    result = engine.compute_recovery(inp, baselines)
    assert result.contributor_count == 6
    assert 1.0 <= result.score <= 99.0


def test_compute_recovery_invalid_baseline_excluded():
    inp = RecoveryInput(hrv=80.0)
    baselines = RecoveryBaselines(hrv=_invalid_baseline())
    result = engine.compute_recovery(inp, baselines)
    assert result.hrv_score is None
    assert result.contributor_count == 0
    _approx(50.0, result.score)


def test_zone_classification_green():
    assert RecoveryZone.from_score(85.0) == RecoveryZone.GREEN
    assert RecoveryZone.from_score(67.0) == RecoveryZone.GREEN


def test_zone_classification_yellow():
    assert RecoveryZone.from_score(50.0) == RecoveryZone.YELLOW
    assert RecoveryZone.from_score(34.0) == RecoveryZone.YELLOW


def test_zone_classification_red():
    assert RecoveryZone.from_score(20.0) == RecoveryZone.RED
    assert RecoveryZone.from_score(1.0) == RecoveryZone.RED


def test_strain_target_green_returns_14_to_18():
    target = engine.strain_target(RecoveryZone.GREEN)
    _approx(14.0, target[0])
    _approx(18.0, target[1])


def test_generate_insight_no_significant_changes():
    result = RecoveryResult(
        score=65.0,
        zone=RecoveryZone.YELLOW,
        hrv_score=50.0,
        rhr_score=50.0,
        sleep_score=50.0,
        resp_rate_score=None,
        spo2_score=None,
        skin_temp_score=None,
        contributor_count=3,
    )
    inp = RecoveryInput(hrv=61.0, resting_heart_rate=60.5, sleep_performance=80.0)
    baselines = RecoveryBaselines(
        hrv=_valid_baseline(60.0, 10.0),
        resting_heart_rate=_valid_baseline(60.0, 5.0),
        sleep_performance=_valid_baseline(80.0, 10.0),
    )
    insight = engine.generate_insight(result, inp, baselines)
    assert "within normal range" in insight
