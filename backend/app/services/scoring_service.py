"""Scoring service — compute recovery, strain, sleep scores from raw vitals."""

from __future__ import annotations

from app.engines.baseline_engine import BaselineResult, compute_baseline, z_score
from app.engines.config import get_scoring_config
from app.engines.recovery_engine import (
    RecoveryBaselines,
    RecoveryEngine,
    RecoveryInput,
    RecoveryResult,
)
from app.engines.strain_engine import StrainEngine, StrainResult


def build_baseline(values: list[float], window_days: int = 28) -> BaselineResult | None:
    """Build a baseline from a list of recent values."""
    return compute_baseline(values, window_days=window_days)


def compute_recovery(
    hrv: float | None,
    resting_heart_rate: float | None,
    sleep_performance: float | None,
    respiratory_rate: float | None,
    spo2: float | None,
    skin_temperature_deviation: float | None,
    historical_hrv: list[float] | None = None,
    historical_rhr: list[float] | None = None,
    historical_sleep: list[float] | None = None,
    historical_resp: list[float] | None = None,
    historical_spo2: list[float] | None = None,
    historical_skin_temp: list[float] | None = None,
) -> RecoveryResult | None:
    """Compute recovery score from vitals and historical baselines."""
    config = get_scoring_config()
    engine = RecoveryEngine(config.recovery)

    baselines = RecoveryBaselines(
        hrv=build_baseline(historical_hrv) if historical_hrv else None,
        resting_heart_rate=build_baseline(historical_rhr) if historical_rhr else None,
        sleep_performance=build_baseline(historical_sleep) if historical_sleep else None,
        respiratory_rate=build_baseline(historical_resp) if historical_resp else None,
        spo2=build_baseline(historical_spo2) if historical_spo2 else None,
        skin_temperature=build_baseline(historical_skin_temp) if historical_skin_temp else None,
    )

    inp = RecoveryInput(
        hrv=hrv,
        resting_heart_rate=resting_heart_rate,
        sleep_performance=sleep_performance,
        respiratory_rate=respiratory_rate,
        spo2=spo2,
        skin_temperature_deviation=skin_temperature_deviation,
    )

    return engine.compute_recovery(inp, baselines)


def compute_strain(
    max_heart_rate: int,
    hr_samples: list[tuple[int, float]],
) -> StrainResult | None:
    """Compute strain from heart rate samples."""
    if not hr_samples or max_heart_rate <= 0:
        return None

    config = get_scoring_config()
    engine = StrainEngine(max_heart_rate, config.strain, config.heartRateZones)
    return engine.compute_workout_strain(hr_samples)
