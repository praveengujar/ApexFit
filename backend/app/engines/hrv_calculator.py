"""HRV calculator — mirrors shared/engine/HRVCalculator.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass
from enum import StrEnum


class HRVMethod(StrEnum):
    RMSSD_FROM_RR_INTERVALS = "RMSSD_FROM_RR_INTERVALS"
    RMSSD_FROM_HEALTH_CONNECT = "RMSSD_FROM_HEALTH_CONNECT"
    SDNN_FROM_HEALTH_CONNECT = "SDNN_FROM_HEALTH_CONNECT"


@dataclass
class HRVResult:
    rmssd: float | None
    sdnn: float | None
    method: HRVMethod


def compute_rmssd(rr_intervals_seconds: list[float]) -> float | None:
    if len(rr_intervals_seconds) <= 1:
        return None

    intervals: list[float] = []
    for i in range(1, len(rr_intervals_seconds)):
        interval = (rr_intervals_seconds[i] - rr_intervals_seconds[i - 1]) * 1000
        if 200.0 <= interval <= 2000.0:
            intervals.append(interval)

    if len(intervals) <= 1:
        return None

    squared_diffs: list[float] = []
    for i in range(1, len(intervals)):
        diff = intervals[i] - intervals[i - 1]
        squared_diffs.append(diff * diff)

    if not squared_diffs:
        return None

    mean_squared_diff = sum(squared_diffs) / len(squared_diffs)
    return math.sqrt(mean_squared_diff)


def best_hrv(
    rmssd_value: float | None = None,
    sdnn_value: float | None = None,
) -> HRVResult:
    if rmssd_value is not None:
        return HRVResult(
            rmssd=rmssd_value,
            sdnn=sdnn_value,
            method=HRVMethod.RMSSD_FROM_HEALTH_CONNECT,
        )

    if sdnn_value is not None:
        return HRVResult(
            rmssd=None,
            sdnn=sdnn_value,
            method=HRVMethod.SDNN_FROM_HEALTH_CONNECT,
        )

    return HRVResult(None, None, HRVMethod.SDNN_FROM_HEALTH_CONNECT)


def effective_hrv(result: HRVResult) -> float | None:
    return result.rmssd if result.rmssd is not None else result.sdnn
