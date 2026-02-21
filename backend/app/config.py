"""Application configuration loaded from environment variables."""

from __future__ import annotations

import json
from functools import lru_cache
from typing import Any

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application settings populated from environment / .env file."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
    )

    # Database
    database_url: str = "postgresql+asyncpg://zyva:zyva_dev@localhost:5432/zyva"

    # Redis
    redis_url: str = "redis://localhost:6379/0"

    # Firebase
    firebase_project_id: str = ""
    firebase_credentials_path: str = ""

    # Google Cloud
    gcp_project_id: str = ""

    # Security
    secret_key: str = "change-me"
    cors_origins: str = '["http://localhost:3000"]'

    # Environment
    environment: str = "dev"

    @property
    def is_production(self) -> bool:
        return self.environment == "prod"

    @property
    def parsed_cors_origins(self) -> list[str]:
        try:
            origins: Any = json.loads(self.cors_origins)
            if isinstance(origins, list):
                return [str(o) for o in origins]
        except (json.JSONDecodeError, TypeError):
            pass
        return [self.cors_origins]


@lru_cache(maxsize=1)
def get_settings() -> Settings:
    """Return a cached Settings instance."""
    return Settings()
