"""Statistical engine — mirrors shared/engine/StatisticalEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass
from enum import StrEnum


class CorrelationDirection(StrEnum):
    POSITIVE = "POSITIVE"
    NEGATIVE = "NEGATIVE"
    NEUTRAL = "NEUTRAL"


@dataclass
class CorrelationResult:
    behavior_name: str
    metric_name: str
    effect_size: float
    p_value: float
    is_significant: bool
    direction: CorrelationDirection
    sample_size_with: int
    sample_size_without: int
    mean_with: float
    mean_without: float


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


def _erfc(x: float) -> float:
    t = 1.0 / (1.0 + 0.3275911 * abs(x))
    poly = t * (
        0.254829592
        + t * (-0.284496736 + t * (1.421413741 + t * (-1.453152027 + t * 1.061405429)))
    )
    result = poly * math.exp(-x * x)
    return result if x >= 0 else 2.0 - result


def _approximate_p_value(t: float) -> float:
    x = abs(t)
    return _erfc(x / math.sqrt(2.0))


def t_test(
    with_behavior: list[float],
    without_behavior: list[float],
) -> tuple[float, float] | None:
    if len(with_behavior) < 3 or len(without_behavior) < 3:
        return None

    n1 = float(len(with_behavior))
    n2 = float(len(without_behavior))
    mean1 = _mean(with_behavior)
    mean2 = _mean(without_behavior)
    var1 = _std_dev(with_behavior) ** 2
    var2 = _std_dev(without_behavior) ** 2

    pooled_se = math.sqrt(var1 / n1 + var2 / n2)
    if pooled_se <= 0:
        return None

    t = (mean1 - mean2) / pooled_se
    p_value = _approximate_p_value(abs(t))

    return (t, p_value)


def cohens_d(
    with_behavior: list[float],
    without_behavior: list[float],
) -> float | None:
    if len(with_behavior) < 3 or len(without_behavior) < 3:
        return None

    mean1 = _mean(with_behavior)
    mean2 = _mean(without_behavior)
    sd1 = _std_dev(with_behavior)
    sd2 = _std_dev(without_behavior)
    n1 = float(len(with_behavior))
    n2 = float(len(without_behavior))

    pooled_sd = math.sqrt(((n1 - 1) * sd1 * sd1 + (n2 - 1) * sd2 * sd2) / (n1 + n2 - 2))
    if pooled_sd <= 0:
        return None

    return (mean1 - mean2) / pooled_sd


def analyze_correlation(
    behavior_name: str,
    metric_name: str,
    with_behavior: list[float],
    without_behavior: list[float],
    higher_is_better: bool = True,
) -> CorrelationResult | None:
    test_result = t_test(with_behavior, without_behavior)
    if test_result is None:
        return None
    effect_size = cohens_d(with_behavior, without_behavior)
    if effect_size is None:
        return None

    mean_diff = _mean(with_behavior) - _mean(without_behavior)
    if test_result[1] >= 0.05:
        direction = CorrelationDirection.NEUTRAL
    elif (higher_is_better and mean_diff > 0) or (not higher_is_better and mean_diff < 0):
        direction = CorrelationDirection.POSITIVE
    else:
        direction = CorrelationDirection.NEGATIVE

    return CorrelationResult(
        behavior_name=behavior_name,
        metric_name=metric_name,
        effect_size=effect_size,
        p_value=test_result[1],
        is_significant=test_result[1] < 0.05,
        direction=direction,
        sample_size_with=len(with_behavior),
        sample_size_without=len(without_behavior),
        mean_with=_mean(with_behavior),
        mean_without=_mean(without_behavior),
    )


def interpret_effect_size(d: float) -> str:
    abs_d = abs(d)
    if abs_d < 0.2:
        return "Negligible"
    elif abs_d < 0.5:
        return "Small"
    elif abs_d < 0.8:
        return "Medium"
    else:
        return "Large"
