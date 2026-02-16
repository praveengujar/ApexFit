"""Main API router aggregating all sub-routers."""

from __future__ import annotations

from fastapi import APIRouter

from app.api import (
    coach,
    config_routes,
    dashboard,
    healthspan,
    journal,
    metrics,
    notifications,
    recovery,
    sleep,
    strain,
    stress,
    teams,
    users,
    workouts,
)

api_router = APIRouter()

api_router.include_router(users.router, prefix="/users", tags=["users"])
api_router.include_router(metrics.router, prefix="/metrics", tags=["metrics"])
api_router.include_router(recovery.router, prefix="/recovery", tags=["recovery"])
api_router.include_router(strain.router, prefix="/strain", tags=["strain"])
api_router.include_router(sleep.router, prefix="/sleep", tags=["sleep"])
api_router.include_router(workouts.router, prefix="/workouts", tags=["workouts"])
api_router.include_router(journal.router, prefix="/journal", tags=["journal"])
api_router.include_router(coach.router, prefix="/coach", tags=["coach"])
api_router.include_router(teams.router, prefix="/teams", tags=["teams"])
api_router.include_router(healthspan.router, prefix="/healthspan", tags=["healthspan"])
api_router.include_router(
    notifications.router, prefix="/notifications", tags=["notifications"]
)
api_router.include_router(dashboard.router, prefix="/dashboard", tags=["dashboard"])
api_router.include_router(stress.router, prefix="/stress", tags=["stress"])
api_router.include_router(config_routes.router, prefix="/config", tags=["config"])
