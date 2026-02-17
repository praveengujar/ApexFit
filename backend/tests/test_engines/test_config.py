"""Test that ScoringConfig.json loads correctly."""

from app.engines.config import get_scoring_config


def test_config_loads_successfully():
    config = get_scoring_config()
    assert config.version >= 1


def test_config_recovery_weights_sum_to_one():
    config = get_scoring_config()
    w = config.recovery.weights
    total = w.hrv + w.restingHeartRate + w.sleep + w.respiratoryRate + w.spo2 + w.skinTemperature
    assert abs(total - 1.0) < 0.01


def test_config_sleep_composite_weights_sum_to_one():
    config = get_scoring_config()
    w = config.sleep.compositeWeights
    total = w.sufficiency + w.efficiency + w.consistency + w.disturbances
    assert abs(total - 1.0) < 0.01


def test_config_strain_zones():
    config = get_scoring_config()
    z = config.strain.zones
    assert z.light.min < z.moderate.min < z.high.min < z.overreaching.min


def test_config_hr_zone_boundaries():
    config = get_scoring_config()
    b = config.heartRateZones.boundaries
    assert len(b) == 6
    for i in range(1, len(b)):
        assert b[i] > b[i - 1]


def test_config_sleep_planner():
    config = get_scoring_config()
    m = config.sleepPlanner.goalMultipliers
    assert m.peak >= m.perform >= m.getBy
