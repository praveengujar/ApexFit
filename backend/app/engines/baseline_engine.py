"""Baseline engine — mirrors shared/engine/BaselineEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass


@dataclass
class BaselineResult:
    mean: float
    standard_deviation: float
    sample_count: int
    window_days: int

    @property
    def is_valid(self) -> bool:
        return self.sample_count >= 3 and self.standard_deviation > 0


def _mean(values: list[float]) -> float:
    if not values:
        return 0.0
    return sum(values) / len(values)


def _std_dev(values: list[float]) -> float:
    if len(values) <= 1:
        return 0.0
    avg = _mean(values)
    variance = sum((v - avg) ** 2 for v in values) / len(values)
    return math.sqrt(variance)


def compute_baseline(
    values: list[float],
    window_days: int = 28,
    minimum_samples: int = 3,
) -> BaselineResult | None:
    if not values:
        return None

    recent = values[-window_days:]

    if len(recent) >= minimum_samples:
        return BaselineResult(
            mean=_mean(recent),
            standard_deviation=max(_std_dev(recent), 0.001),
            sample_count=len(recent),
            window_days=window_days,
        )

    if len(values) >= minimum_samples:
        fallback = values[-minimum_samples:]
        return BaselineResult(
            mean=_mean(fallback),
            standard_deviation=max(_std_dev(fallback), 0.001),
            sample_count=len(fallback),
            window_days=len(fallback),
        )

    return None


def z_score(value: float, baseline: BaselineResult) -> float:
    if baseline.standard_deviation <= 0:
        return 0.0
    return (value - baseline.mean) / baseline.standard_deviation


def update_baseline(
    current: BaselineResult,
    new_value: float,
    alpha: float = 0.1,
) -> BaselineResult:
    new_mean = current.mean * (1 - alpha) + new_value * alpha
    new_variance = current.standard_deviation**2 * (1 - alpha) + (new_value - new_mean) ** 2 * alpha
    new_std_dev = math.sqrt(max(new_variance, 0.001))

    return BaselineResult(
        mean=new_mean,
        standard_deviation=new_std_dev,
        sample_count=current.sample_count + 1,
        window_days=current.window_days,
    )
