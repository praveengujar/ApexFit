"""ScoringConfig Pydantic models — mirrors shared/model/config/ScoringConfig.kt."""

from __future__ import annotations

from functools import lru_cache
from pathlib import Path

from pydantic import BaseModel, ConfigDict


class ScoreRange(BaseModel):
    model_config = ConfigDict(extra="ignore")
    min: int
    max: int


class ValueRange(BaseModel):
    model_config = ConfigDict(extra="ignore")
    min: float
    max: float


class RecoveryWeights(BaseModel):
    model_config = ConfigDict(extra="ignore")
    hrv: float
    restingHeartRate: float
    sleep: float
    respiratoryRate: float
    spo2: float
    skinTemperature: float


class RecoveryZoneConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    green: ScoreRange
    yellow: ScoreRange
    red: ScoreRange


class RecoveryStrainTargets(BaseModel):
    model_config = ConfigDict(extra="ignore")
    green: ValueRange
    yellow: ValueRange
    red: ValueRange


class RecoveryInsightThresholds(BaseModel):
    model_config = ConfigDict(extra="ignore")
    hrvPercentChange: float
    rhrDeltaBPM: float
    sleepPerformanceHigh: float
    sleepPerformanceLow: float
    skinTempDeviationCelsius: float


class RecoveryConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    weights: RecoveryWeights
    sigmoidSteepness: float
    scoreRange: ScoreRange
    zones: RecoveryZoneConfig
    strainTargets: RecoveryStrainTargets
    insightThresholds: RecoveryInsightThresholds


class SleepCompositeWeights(BaseModel):
    model_config = ConfigDict(extra="ignore")
    sufficiency: float
    efficiency: float
    consistency: float
    disturbances: float


class StrainSupplement(BaseModel):
    model_config = ConfigDict(extra="ignore")
    strainBelow: float
    addHours: float


class SleepDefaults(BaseModel):
    model_config = ConfigDict(extra="ignore")
    baselineHours: float
    onsetLatencyMinutes: float


class SleepSessionDetection(BaseModel):
    model_config = ConfigDict(extra="ignore")
    gapToleranceMinutes: float
    minimumDurationMinutes: float
    maximumNapDurationHours: float
    napCreditCapHours: float


class SleepConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    compositeWeights: SleepCompositeWeights
    consistencyWindowNights: int
    consistencyDecayTau: float
    disturbanceScaling: float
    strainSupplements: list[StrainSupplement]
    debtRepaymentRate: float
    defaults: SleepDefaults
    sessionDetection: SleepSessionDetection


class StrainZoneConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    light: ValueRange
    moderate: ValueRange
    high: ValueRange
    overreaching: ValueRange


class StrainConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    scalingFactor: float
    logOffsetConstant: float
    maxValue: float
    minValue: float
    zones: StrainZoneConfig


class HeartRateZoneConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    boundaries: list[float]
    multipliers: list[float]
    sampleMaxDurationSeconds: float


class BaselineConfigModel(BaseModel):
    model_config = ConfigDict(extra="ignore")
    windowDays: int
    minimumSamples: int
    fallbackDays: int
    exponentialAlpha: float


class SleepGoalMultipliers(BaseModel):
    model_config = ConfigDict(extra="ignore")
    peak: float
    perform: float
    getBy: float


class SleepPlannerConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    goalMultipliers: SleepGoalMultipliers


class ScoringConfig(BaseModel):
    model_config = ConfigDict(extra="ignore")
    version: int
    recovery: RecoveryConfig
    sleep: SleepConfig
    strain: StrainConfig
    heartRateZones: HeartRateZoneConfig
    baselines: BaselineConfigModel
    sleepPlanner: SleepPlannerConfig


@lru_cache(maxsize=1)
def get_scoring_config() -> ScoringConfig:
    """Load and cache the ScoringConfig from the bundled JSON file."""
    path = Path(__file__).parent / "scoring_config.json"
    return ScoringConfig.model_validate_json(path.read_text())
