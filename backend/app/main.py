"""Zyva FastAPI application entry-point."""

from __future__ import annotations

import logging
from collections.abc import AsyncGenerator
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.router import api_router
from app.config import get_settings
from app.core.exceptions import register_exception_handlers
from app.core.middleware import RequestLoggingMiddleware
from app.core.redis_client import close_redis, init_redis
from app.db.session import dispose_engine, init_engine

logger = logging.getLogger("zyva")


@asynccontextmanager
async def lifespan(_app: FastAPI) -> AsyncGenerator[None, None]:
    """Startup / shutdown lifecycle hook."""
    settings = get_settings()
    logger.info("Starting Zyva API (env=%s)", settings.environment)

    # Initialise shared resources
    init_engine(settings.database_url)
    await init_redis(settings.redis_url)

    yield

    # Teardown
    await close_redis()
    await dispose_engine()
    logger.info("Zyva API shut down cleanly")


def create_app() -> FastAPI:
    """Factory that builds the FastAPI application."""
    settings = get_settings()

    application = FastAPI(
        title="Zyva API",
        version="0.1.0",
        docs_url="/docs" if not settings.is_production else None,
        redoc_url="/redoc" if not settings.is_production else None,
        lifespan=lifespan,
    )

    # CORS
    application.add_middleware(
        CORSMiddleware,
        allow_origins=settings.parsed_cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # Custom middleware
    application.add_middleware(RequestLoggingMiddleware)

    # Exception handlers
    register_exception_handlers(application)

    # Routers
    application.include_router(api_router, prefix="/api/v1")

    # Health check
    @application.get("/health", tags=["health"])
    async def health_check() -> dict[str, str]:
        return {"status": "ok"}

    return application


app = create_app()
