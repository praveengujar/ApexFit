"""Daily metrics sync and query routes."""

from __future__ import annotations

from datetime import date, timedelta

from fastapi import APIRouter, Depends, Query
from sqlalchemy.ext.asyncio import AsyncSession

from app.auth.dependencies import get_current_user
from app.auth.models import AuthUser
from app.db.repositories import metrics_repo, user_repo
from app.db.session import get_session
from app.schemas.common import PaginatedResponse
from app.schemas.metrics import DailyMetricResponse, MetricsSyncRequest, RawMetricsSyncRequest

router = APIRouter()


@router.post("/sync", response_model=list[DailyMetricResponse])
async def sync_metrics(
    body: MetricsSyncRequest,
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
) -> list[DailyMetricResponse]:
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        user = await user_repo.create(
            session, firebase_uid=current_user.uid, email=current_user.email
        )

    results = []
    for item in body.metrics:
        metric = await metrics_repo.upsert(
            session, user.id, **item.model_dump()
        )
        results.append(DailyMetricResponse.model_validate(metric))
    return results


@router.get("/daily", response_model=PaginatedResponse[DailyMetricResponse])
async def get_daily_metrics(
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
    from_date: date | None = Query(None),
    to_date: date | None = Query(None),
    page: int = Query(1, ge=1),
    page_size: int = Query(20, ge=1, le=100),
) -> PaginatedResponse[DailyMetricResponse]:
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        return PaginatedResponse.create([], 0, page, page_size)

    offset = (page - 1) * page_size
    metrics, total = await metrics_repo.list_by_date_range(
        session, user.id, from_date, to_date, offset=offset, limit=page_size
    )
    items = [DailyMetricResponse.model_validate(m) for m in metrics]
    return PaginatedResponse.create(items, total, page, page_size)


@router.get("/weekly")
async def get_weekly_metrics(
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
    end_date: date | None = Query(None),
) -> dict:
    """Return weekly aggregated metrics with baselines."""
    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        return {"week": [], "baselines": {}}

    target = end_date or date.today()
    week_start = target - timedelta(days=6)

    # Fetch week data
    week_metrics, _ = await metrics_repo.list_by_date_range(
        session, user.id, week_start, target, offset=0, limit=7
    )

    # Fetch 28-day history for baselines
    baseline_start = target - timedelta(days=28)
    history, _ = await metrics_repo.list_by_date_range(
        session, user.id, baseline_start, target, offset=0, limit=28
    )

    # Compute baselines
    def avg(values: list) -> float | None:
        filtered = [v for v in values if v is not None]
        return sum(filtered) / len(filtered) if filtered else None

    baselines = {
        "hrv_rmssd": avg([m.hrv_rmssd for m in history]),
        "resting_heart_rate": avg([m.resting_heart_rate for m in history]),
        "vo2_max": avg([m.vo2_max for m in history]),
        "active_calories": avg([m.active_calories for m in history]),
        "steps": avg([m.steps for m in history]),
    }

    # Format weekly data
    week = [
        {
            "date": str(m.date),
            "strain_score": m.strain_score,
            "recovery_score": m.recovery_score,
            "recovery_zone": m.recovery_zone,
            "steps": m.steps,
            "active_calories": m.active_calories,
            "hrv_rmssd": m.hrv_rmssd,
            "resting_heart_rate": m.resting_heart_rate,
        }
        for m in sorted(week_metrics, key=lambda m: m.date)
    ]

    return {"week": week, "baselines": baselines}


@router.post("/sync-raw", response_model=list[DailyMetricResponse])
async def sync_raw_metrics(
    body: RawMetricsSyncRequest,
    current_user: AuthUser = Depends(get_current_user),
    session: AsyncSession = Depends(get_session),
) -> list[DailyMetricResponse]:
    """Accept raw vitals and compute scores server-side using engines."""
    from app.services.scoring_service import compute_recovery, compute_strain

    user = await user_repo.get_by_firebase_uid(session, current_user.uid)
    if not user:
        user = await user_repo.create(
            session, firebase_uid=current_user.uid, email=current_user.email
        )

    results = []
    for item in body.metrics:
        # Fetch 28-day history for baselines
        from_date = item.date - timedelta(days=28)
        history, _ = await metrics_repo.list_by_date_range(
            session, user.id, from_date, item.date, limit=28
        )

        # Build historical lists for recovery baselines
        hist_hrv = [m.hrv_rmssd for m in history if m.hrv_rmssd is not None]
        hist_rhr = [m.resting_heart_rate for m in history if m.resting_heart_rate is not None]
        hist_sleep = [m.sleep_performance for m in history if m.sleep_performance is not None]
        hist_resp = [m.respiratory_rate for m in history if m.respiratory_rate is not None]
        hist_spo2 = [m.spo2 for m in history if m.spo2 is not None]

        # Compute recovery
        recovery_result = compute_recovery(
            hrv=item.hrv_rmssd,
            resting_heart_rate=item.resting_heart_rate,
            sleep_performance=item.sleep_efficiency,
            respiratory_rate=item.respiratory_rate,
            spo2=item.spo2,
            skin_temperature_deviation=item.skin_temperature_deviation,
            historical_hrv=hist_hrv or None,
            historical_rhr=hist_rhr or None,
            historical_sleep=hist_sleep or None,
            historical_resp=hist_resp or None,
            historical_spo2=hist_spo2 or None,
        )

        # Compute strain from HR samples
        strain_result = None
        if item.hr_samples and user.max_heart_rate:
            raw_samples = [(int(s[0]), s[1]) for s in item.hr_samples]
            strain_result = compute_strain(user.max_heart_rate, raw_samples)

        # Upsert the metric with computed scores
        metric_data = {
            "date": item.date,
            "hrv_rmssd": item.hrv_rmssd,
            "resting_heart_rate": item.resting_heart_rate,
            "respiratory_rate": item.respiratory_rate,
            "spo2": item.spo2,
            "steps": item.steps,
            "active_calories": item.active_calories,
            "vo2_max": item.vo2_max,
            "sleep_duration_hours": item.sleep_duration_hours,
            "recovery_score": recovery_result.score if recovery_result else None,
            "recovery_zone": recovery_result.zone.value if recovery_result else None,
            "strain_score": strain_result.strain if strain_result else None,
            "sleep_performance": item.sleep_efficiency,
        }

        metric = await metrics_repo.upsert(session, user.id, **metric_data)
        results.append(DailyMetricResponse.model_validate(metric))

    return results
