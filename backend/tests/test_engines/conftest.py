"""Test config mirroring shared/src/commonTest/kotlin/.../TestConfig.kt."""

from __future__ import annotations

import sys
from pathlib import Path

import pytest

# Ensure 'app' package is importable from tests
sys.path.insert(0, str(Path(__file__).resolve().parents[2]))

from app.engines.config import (
    HeartRateZoneConfig,
    RecoveryConfig,
    RecoveryInsightThresholds,
    RecoveryStrainTargets,
    RecoveryWeights,
    RecoveryZoneConfig,
    ScoreRange,
    SleepCompositeWeights,
    SleepConfig,
    SleepDefaults,
    SleepGoalMultipliers,
    SleepPlannerConfig,
    SleepSessionDetection,
    StrainConfig,
    StrainSupplement,
    StrainZoneConfig,
    ValueRange,
)

DEFAULT_MAX_HR = 200

HR_ZONE_CONFIG = HeartRateZoneConfig(
    boundaries=[0.50, 0.60, 0.70, 0.80, 0.90, 1.00],
    multipliers=[1.0, 2.0, 3.0, 4.0, 5.0],
    sampleMaxDurationSeconds=600.0,
)

STRAIN_CONFIG = StrainConfig(
    scalingFactor=6.0,
    logOffsetConstant=1.0,
    maxValue=21.0,
    minValue=0.0,
    zones=StrainZoneConfig(
        light=ValueRange(min=0.0, max=8.0),
        moderate=ValueRange(min=8.0, max=14.0),
        high=ValueRange(min=14.0, max=18.0),
        overreaching=ValueRange(min=18.0, max=21.0),
    ),
)

RECOVERY_CONFIG = RecoveryConfig(
    weights=RecoveryWeights(
        hrv=0.40,
        restingHeartRate=0.25,
        sleep=0.20,
        respiratoryRate=0.05,
        spo2=0.05,
        skinTemperature=0.05,
    ),
    sigmoidSteepness=1.5,
    scoreRange=ScoreRange(min=1, max=99),
    zones=RecoveryZoneConfig(
        green=ScoreRange(min=67, max=99),
        yellow=ScoreRange(min=34, max=66),
        red=ScoreRange(min=1, max=33),
    ),
    strainTargets=RecoveryStrainTargets(
        green=ValueRange(min=14.0, max=18.0),
        yellow=ValueRange(min=8.0, max=13.9),
        red=ValueRange(min=2.0, max=7.9),
    ),
    insightThresholds=RecoveryInsightThresholds(
        hrvPercentChange=10.0,
        rhrDeltaBPM=3.0,
        sleepPerformanceHigh=95.0,
        sleepPerformanceLow=70.0,
        skinTempDeviationCelsius=0.5,
    ),
)

SLEEP_CONFIG = SleepConfig(
    compositeWeights=SleepCompositeWeights(
        sufficiency=0.50,
        efficiency=0.25,
        consistency=0.15,
        disturbances=0.10,
    ),
    consistencyWindowNights=4,
    consistencyDecayTau=60.0,
    disturbanceScaling=20.0,
    strainSupplements=[
        StrainSupplement(strainBelow=8.0, addHours=0.0),
        StrainSupplement(strainBelow=14.0, addHours=0.25),
        StrainSupplement(strainBelow=18.0, addHours=0.5),
        StrainSupplement(strainBelow=999.0, addHours=0.75),
    ],
    debtRepaymentRate=0.20,
    defaults=SleepDefaults(
        baselineHours=7.5,
        onsetLatencyMinutes=15.0,
    ),
    sessionDetection=SleepSessionDetection(
        gapToleranceMinutes=30.0,
        minimumDurationMinutes=30.0,
        maximumNapDurationHours=3.0,
        napCreditCapHours=1.5,
    ),
)

SLEEP_PLANNER_CONFIG = SleepPlannerConfig(
    goalMultipliers=SleepGoalMultipliers(
        peak=1.0,
        perform=0.85,
        getBy=0.70,
    ),
)
