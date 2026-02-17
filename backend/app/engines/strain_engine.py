"""Strain engine — mirrors shared/engine/StrainEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass

from app.engines.config import HeartRateZoneConfig, StrainConfig
from app.engines.hr_zone_calculator import HeartRateZoneCalculator


@dataclass
class HeartRateSample:
    timestamp_millis: int
    bpm: float
    duration_seconds: float


@dataclass
class StrainResult:
    strain: float
    weighted_hr_area: float
    zone1_minutes: float
    zone2_minutes: float
    zone3_minutes: float
    zone4_minutes: float
    zone5_minutes: float


class StrainEngine:
    def __init__(
        self,
        max_heart_rate: int,
        strain_config: StrainConfig,
        hr_zone_config: HeartRateZoneConfig,
    ) -> None:
        self._k = strain_config.scalingFactor
        self._c = strain_config.logOffsetConstant
        self._strain_config = strain_config
        self._zone_calculator = HeartRateZoneCalculator(max_heart_rate, hr_zone_config)

    def compute_strain(self, samples: list[HeartRateSample]) -> StrainResult:
        weighted_hr_area = 0.0
        zone_minutes = [0.0, 0.0, 0.0, 0.0, 0.0]

        for sample in samples:
            duration_minutes = sample.duration_seconds / 60.0
            zone_multiplier = self._zone_calculator.multiplier(sample.bpm)
            zone_num = self._zone_calculator.zone_number(sample.bpm)

            weighted_hr_area += duration_minutes * zone_multiplier

            if 1 <= zone_num <= 5:
                zone_minutes[zone_num - 1] += duration_minutes

        raw_strain = self._k * math.log10(weighted_hr_area + self._c)
        clamped_strain = max(
            self._strain_config.minValue,
            min(self._strain_config.maxValue, raw_strain),
        )

        return StrainResult(
            strain=clamped_strain,
            weighted_hr_area=weighted_hr_area,
            zone1_minutes=zone_minutes[0],
            zone2_minutes=zone_minutes[1],
            zone3_minutes=zone_minutes[2],
            zone4_minutes=zone_minutes[3],
            zone5_minutes=zone_minutes[4],
        )

    def compute_workout_strain(self, raw_samples: list[tuple[int, float]]) -> StrainResult:
        hr_samples = estimate_durations(raw_samples)
        return self.compute_strain(hr_samples)


def estimate_durations(
    raw_samples: list[tuple[int, float]],
    max_duration_seconds: float = 60.0,
) -> list[HeartRateSample]:
    if len(raw_samples) <= 1:
        return [HeartRateSample(ts, bpm, 5.0) for ts, bpm in raw_samples]

    result: list[HeartRateSample] = []
    for i in range(len(raw_samples)):
        if i < len(raw_samples) - 1:
            duration = min(
                (raw_samples[i + 1][0] - raw_samples[i][0]) / 1000.0,
                max_duration_seconds,
            )
        else:
            duration = result[-1].duration_seconds if result else 5.0

        result.append(
            HeartRateSample(
                timestamp_millis=raw_samples[i][0],
                bpm=raw_samples[i][1],
                duration_seconds=duration,
            )
        )
    return result
