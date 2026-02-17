"""Recovery engine — mirrors shared/engine/RecoveryEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass
from enum import StrEnum

from app.engines.baseline_engine import BaselineResult, z_score
from app.engines.config import RecoveryConfig


class RecoveryZone(StrEnum):
    GREEN = "Green"
    YELLOW = "Yellow"
    RED = "Red"

    @staticmethod
    def from_score(score: float) -> RecoveryZone:
        if score >= 67.0:
            return RecoveryZone.GREEN
        elif score >= 34.0:
            return RecoveryZone.YELLOW
        else:
            return RecoveryZone.RED


@dataclass
class RecoveryInput:
    hrv: float | None = None
    resting_heart_rate: float | None = None
    sleep_performance: float | None = None
    respiratory_rate: float | None = None
    spo2: float | None = None
    skin_temperature_deviation: float | None = None


@dataclass
class RecoveryBaselines:
    hrv: BaselineResult | None = None
    resting_heart_rate: BaselineResult | None = None
    sleep_performance: BaselineResult | None = None
    respiratory_rate: BaselineResult | None = None
    spo2: BaselineResult | None = None
    skin_temperature: BaselineResult | None = None


@dataclass
class RecoveryResult:
    score: float
    zone: RecoveryZone
    hrv_score: float | None
    rhr_score: float | None
    sleep_score: float | None
    resp_rate_score: float | None
    spo2_score: float | None
    skin_temp_score: float | None
    contributor_count: int


class RecoveryEngine:
    def __init__(self, config: RecoveryConfig) -> None:
        self._config = config

    def _sigmoid(self, z: float) -> float:
        return 100.0 / (1.0 + math.exp(-self._config.sigmoidSteepness * z))

    def _compute_contributor(
        self,
        value: float | None,
        baseline: BaselineResult | None,
        invert: bool,
        weight: float,
        accum: _Accumulator,
    ) -> float | None:
        if value is None or baseline is None or not baseline.is_valid:
            return None

        z = z_score(value, baseline)
        if invert:
            z = -z

        score = self._sigmoid(z)
        accum.total_weight += weight
        accum.weighted_sum += score * weight
        accum.contributor_count += 1

        return score

    def compute_recovery(self, inp: RecoveryInput, baselines: RecoveryBaselines) -> RecoveryResult:
        accum = _Accumulator()
        w = self._config.weights

        hrv_score = self._compute_contributor(inp.hrv, baselines.hrv, False, w.hrv, accum)
        rhr_score = self._compute_contributor(
            inp.resting_heart_rate, baselines.resting_heart_rate, True, w.restingHeartRate, accum
        )
        sleep_score = self._compute_contributor(
            inp.sleep_performance, baselines.sleep_performance, False, w.sleep, accum
        )
        resp_rate_score = self._compute_contributor(
            inp.respiratory_rate, baselines.respiratory_rate, True, w.respiratoryRate, accum
        )
        spo2_score = self._compute_contributor(inp.spo2, baselines.spo2, False, w.spo2, accum)
        skin_temp_score = self._compute_contributor(
            inp.skin_temperature_deviation, baselines.skin_temperature,
            True, w.skinTemperature, accum
        )

        raw_score = accum.weighted_sum / accum.total_weight if accum.total_weight > 0 else 50.0
        score_min = float(self._config.scoreRange.min)
        score_max = float(self._config.scoreRange.max)
        final_score = max(score_min, min(score_max, raw_score))
        zone = RecoveryZone.from_score(final_score)

        return RecoveryResult(
            score=final_score,
            zone=zone,
            hrv_score=hrv_score,
            rhr_score=rhr_score,
            sleep_score=sleep_score,
            resp_rate_score=resp_rate_score,
            spo2_score=spo2_score,
            skin_temp_score=skin_temp_score,
            contributor_count=accum.contributor_count,
        )

    def strain_target(self, zone: RecoveryZone) -> tuple[float, float]:
        targets = self._config.strainTargets
        if zone == RecoveryZone.GREEN:
            return (targets.green.min, targets.green.max)
        elif zone == RecoveryZone.YELLOW:
            return (targets.yellow.min, targets.yellow.max)
        else:
            return (targets.red.min, targets.red.max)

    def generate_insight(
        self,
        result: RecoveryResult,
        inp: RecoveryInput,
        baselines: RecoveryBaselines,
    ) -> str:
        thresholds = self._config.insightThresholds
        insights: list[str] = []

        if inp.hrv is not None and baselines.hrv is not None:
            pct_change = ((inp.hrv - baselines.hrv.mean) / baselines.hrv.mean) * 100
            if abs(pct_change) > thresholds.hrvPercentChange:
                direction = "above" if pct_change > 0 else "below"
                insights.append(f"HRV was {abs(int(pct_change))}% {direction} your baseline")

        if inp.resting_heart_rate is not None and baselines.resting_heart_rate is not None:
            delta = inp.resting_heart_rate - baselines.resting_heart_rate.mean
            if abs(delta) > thresholds.rhrDeltaBPM:
                direction = "elevated by" if delta > 0 else "lower by"
                insights.append(f"RHR was {direction} {abs(int(delta))} BPM")

        if inp.sleep_performance is not None:
            if inp.sleep_performance >= thresholds.sleepPerformanceHigh:
                insights.append(f"you got {int(inp.sleep_performance)}% of your sleep need")
            elif inp.sleep_performance < thresholds.sleepPerformanceLow:
                insights.append(f"you only got {int(inp.sleep_performance)}% of your sleep need")

        if (
            inp.skin_temperature_deviation is not None
            and abs(inp.skin_temperature_deviation) > thresholds.skinTempDeviationCelsius
        ):
            direction = "elevated" if inp.skin_temperature_deviation > 0 else "lower"
            rounded = int(abs(inp.skin_temperature_deviation) * 10) / 10.0
            insights.append(f"skin temperature was {direction} by {rounded}\u00b0C")

        prefix = f"Your Recovery is {int(result.score)}% ({result.zone.value}). "
        if not insights:
            return prefix + "Your metrics are within normal range."
        return prefix + ", and ".join(insights) + "."


class _Accumulator:
    __slots__ = ("total_weight", "weighted_sum", "contributor_count")

    def __init__(self) -> None:
        self.total_weight = 0.0
        self.weighted_sum = 0.0
        self.contributor_count = 0
