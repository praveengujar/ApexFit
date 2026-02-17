"""Statistical engine tests — mirrors StatisticalEngineTest.kt."""

from app.engines.statistical_engine import (
    CorrelationDirection,
    analyze_correlation,
    cohens_d,
    interpret_effect_size,
    t_test,
)


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


def test_t_test_small_samples_returns_none():
    assert t_test([1.0, 2.0], [3.0, 4.0]) is None


def test_t_test_identical_distributions_returns_none():
    # stdDev = 0, pooledSE = 0 -> returns None
    assert t_test([5.0, 5.0, 5.0], [5.0, 5.0, 5.0]) is None


def test_t_test_clearly_different_groups_significant():
    with_b = [10.0, 11.0, 12.0, 10.0, 11.0]
    without_b = [5.0, 6.0, 5.0, 6.0, 5.0]
    result = t_test(with_b, without_b)
    assert result is not None
    assert result[0] > 0  # positive t (with > without)
    assert result[1] < 0.05  # significant


def test_cohens_d_small_samples_returns_none():
    assert cohens_d([1.0, 2.0], [3.0, 4.0]) is None


def test_cohens_d_clearly_different_large_effect():
    with_b = [10.0, 11.0, 12.0, 10.0, 11.0]
    without_b = [5.0, 6.0, 5.0, 6.0, 5.0]
    d = cohens_d(with_b, without_b)
    assert d is not None
    assert d > 0.8  # large effect


def test_cohens_d_identical_means_returns_zero():
    d = cohens_d([5.0, 6.0, 7.0], [5.0, 6.0, 7.0])
    assert d is not None
    _approx(0.0, d)


def test_interpret_effect_size_correct_labels():
    assert interpret_effect_size(0.1) == "Negligible"
    assert interpret_effect_size(0.3) == "Small"
    assert interpret_effect_size(0.6) == "Medium"
    assert interpret_effect_size(1.0) == "Large"


def test_analyze_correlation_significant_returns_positive():
    with_b = [10.0, 11.0, 12.0, 10.0, 11.0]
    without_b = [5.0, 6.0, 5.0, 6.0, 5.0]
    result = analyze_correlation(
        behavior_name="Meditation",
        metric_name="Recovery",
        with_behavior=with_b,
        without_behavior=without_b,
        higher_is_better=True,
    )
    assert result is not None
    assert result.is_significant
    assert result.direction == CorrelationDirection.POSITIVE
    assert result.sample_size_with == 5
    assert result.sample_size_without == 5
