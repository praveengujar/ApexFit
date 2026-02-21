"""Healthspan service — VitalOS Age computation using the full LongevityEngine."""

from __future__ import annotations

import uuid
from datetime import date, timedelta

from sqlalchemy.ext.asyncio import AsyncSession

from app.db.repositories import metrics_repo
from app.engines.longevity_engine import (
    LongevityResult,
    MetricID,
    MetricInput,
    compute,
)
from app.models.healthspan import HealthspanScore
from app.models.user import User


def _avg(values: list[float | None]) -> float | None:
    filtered = [v for v in values if v is not None]
    return sum(filtered) / len(filtered) if filtered else None


async def compute_healthspan(
    session: AsyncSession, user_id: uuid.UUID
) -> HealthspanScore | None:
    """Compute a daily healthspan / VitalOS Age score using the LongevityEngine.

    Replaces the previous simplified linear model with the full
    dose-response curve engine (9 metrics, Gompertz mortality model).
    """
    user = await session.get(User, user_id)
    if not user or not user.date_of_birth:
        return None

    today = date.today()
    chrono_age = (today - user.date_of_birth).days / 365.25

    # Fetch 180-day and 28-day windows for the longevity engine
    start_180 = today - timedelta(days=180)
    start_28 = today - timedelta(days=28)

    metrics_180, _ = await metrics_repo.list_by_date_range(
        session, user_id, start_180, today, limit=180
    )
    if not metrics_180:
        return None

    metrics_28 = [m for m in metrics_180 if m.date >= start_28]

    # Build averages for longevity metrics
    def avg_field_180(field: str) -> float | None:
        return _avg([getattr(m, field) for m in metrics_180])

    def avg_field_28(field: str) -> float | None:
        return _avg([getattr(m, field) for m in metrics_28])

    inputs: list[MetricInput] = [
        MetricInput(
            id=MetricID.HOURS_OF_SLEEP,
            six_month_avg=avg_field_180("sleep_duration_hours"),
            thirty_day_avg=avg_field_28("sleep_duration_hours"),
        ),
        MetricInput(
            id=MetricID.RESTING_HEART_RATE,
            six_month_avg=avg_field_180("resting_heart_rate"),
            thirty_day_avg=avg_field_28("resting_heart_rate"),
        ),
        MetricInput(
            id=MetricID.VO2_MAX,
            six_month_avg=avg_field_180("vo2_max"),
            thirty_day_avg=avg_field_28("vo2_max"),
        ),
        MetricInput(
            id=MetricID.DAILY_STEPS,
            six_month_avg=_avg([float(m.steps) for m in metrics_180 if m.steps is not None]),
            thirty_day_avg=_avg([float(m.steps) for m in metrics_28 if m.steps is not None]),
        ),
    ]

    # Filter out inputs with no data
    inputs = [i for i in inputs if i.six_month_avg is not None or i.thirty_day_avg is not None]

    if not inputs:
        return None

    result: LongevityResult = compute(chrono_age, inputs)

    # Build recovery/sleep/activity sub-scores for the ORM model
    recovery_vals = [m.recovery_score for m in metrics_28 if m.recovery_score is not None]
    sleep_vals = [m.sleep_performance for m in metrics_28 if m.sleep_performance is not None]
    strain_vals = [m.strain_score for m in metrics_28 if m.strain_score is not None]

    avg_recovery = sum(recovery_vals) / len(recovery_vals) if recovery_vals else None
    avg_sleep = sum(sleep_vals) / len(sleep_vals) if sleep_vals else None
    avg_strain = sum(strain_vals) / len(strain_vals) if strain_vals else None
    activity_score = min(100.0, (avg_strain / 21.0) * 100.0) if avg_strain else None

    score = HealthspanScore(
        user_id=user_id,
        date=today,
        vitalos_age=round(result.zyva_age, 1),
        biological_age=round(result.zyva_age, 1),
        cardiovascular_score=round(avg_recovery, 1) if avg_recovery else None,
        recovery_score=round(avg_recovery, 1) if avg_recovery else None,
        sleep_score=round(avg_sleep, 1) if avg_sleep else None,
        activity_score=round(activity_score, 1) if activity_score else None,
    )
    session.add(score)
    await session.flush()
    return score
