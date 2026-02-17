"""Muscular load engine — mirrors shared/engine/MuscularLoadEngine.kt."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass
class MuscularLoadResult:
    load: float
    volume_score: float
    intensity_score: float
    workout_type: str


_EFFECTIVE_MASS_FACTORS: dict[str, float] = {
    "traditionalStrengthTraining": 1.0,
    "functionalStrengthTraining": 0.9,
    "crossTraining": 0.85,
    "highIntensityIntervalTraining": 0.8,
    "coreTraining": 0.6,
    "yoga": 0.4,
    "pilates": 0.5,
    "flexibility": 0.3,
    "wrestling": 0.9,
    "boxing": 0.8,
    "kickboxing": 0.85,
    "martialArts": 0.85,
    "climbing": 0.85,
    "rowing": 0.75,
}

_STRENGTH_TYPES = {
    "traditionalStrengthTraining",
    "functionalStrengthTraining",
    "coreTraining",
}

_HIGH_INTENSITY_TYPES = {
    "crossTraining",
    "highIntensityIntervalTraining",
    "wrestling",
    "boxing",
    "kickboxing",
    "martialArts",
    "climbing",
}


def compute_load(
    workout_type: str,
    duration_minutes: float,
    average_heart_rate: float,
    max_heart_rate_during_workout: float,
    user_max_heart_rate: float,
    body_weight_kg: float | None = None,
    rpe: int | None = None,
) -> MuscularLoadResult:
    effective_mass_factor = _EFFECTIVE_MASS_FACTORS.get(workout_type, 0.5)
    volume_score = duration_minutes * effective_mass_factor

    avg_hr_ratio = average_heart_rate / user_max_heart_rate
    peak_hr_ratio = max_heart_rate_during_workout / user_max_heart_rate
    intensity_score = max(0.0, min(1.0, avg_hr_ratio * peak_hr_ratio))

    calibration_factor = 2.0
    load = volume_score * intensity_score * calibration_factor

    if rpe is not None:
        rpe_adjustment = 1.0 + (rpe - 5.0) * 0.1
        load *= rpe_adjustment

    load = max(0.0, min(100.0, load))

    return MuscularLoadResult(
        load=load,
        volume_score=volume_score,
        intensity_score=intensity_score,
        workout_type=workout_type,
    )


def is_strength_workout(workout_type: str) -> bool:
    return workout_type in _STRENGTH_TYPES or workout_type in _HIGH_INTENSITY_TYPES
