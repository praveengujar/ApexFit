"""Baseline engine tests — mirrors BaselineEngineTest.kt."""

import pytest

from app.engines.baseline_engine import BaselineResult, compute_baseline, update_baseline, z_score


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


def test_compute_baseline_empty_returns_none():
    assert compute_baseline([]) is None


def test_compute_baseline_two_values_returns_none():
    assert compute_baseline([60.0, 70.0]) is None


def test_compute_baseline_three_values_returns_valid():
    result = compute_baseline([60.0, 70.0, 80.0])
    assert result is not None
    _approx(70.0, result.mean)
    assert result.sample_count == 3
    assert result.is_valid


def test_compute_baseline_identical_values_floors_std_dev():
    result = compute_baseline([50.0, 50.0, 50.0])
    assert result is not None
    _approx(50.0, result.mean)
    _approx(0.001, result.standard_deviation, tolerance=0.0001)
    assert result.is_valid


def test_z_score_mean_value_returns_zero():
    baseline = BaselineResult(mean=70.0, standard_deviation=10.0, sample_count=10, window_days=28)
    _approx(0.0, z_score(70.0, baseline), tolerance=0.001)


def test_z_score_one_std_dev_above_returns_one():
    baseline = BaselineResult(mean=70.0, standard_deviation=10.0, sample_count=10, window_days=28)
    _approx(1.0, z_score(80.0, baseline), tolerance=0.001)


def test_update_baseline_shifts_toward_new_value():
    current = BaselineResult(mean=60.0, standard_deviation=5.0, sample_count=10, window_days=28)
    updated = update_baseline(current, 70.0, alpha=0.1)
    # newMean = 60*0.9 + 70*0.1 = 61.0
    _approx(61.0, updated.mean)
    assert updated.sample_count == 11
    # newVariance = 25*0.9 + (70-61)^2*0.1 = 22.5 + 8.1 = 30.6
    # newStdDev = sqrt(30.6) = 5.532
    _approx(5.532, updated.standard_deviation)
