"""Config API routes — serves ScoringConfig for client parity checks."""

from __future__ import annotations

from fastapi import APIRouter

from app.engines.config import get_scoring_config

router = APIRouter()


@router.get("/scoring")
async def get_scoring_config_endpoint() -> dict:
    """Return the current ScoringConfig JSON."""
    return get_scoring_config().model_dump()
