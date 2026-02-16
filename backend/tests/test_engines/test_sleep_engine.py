"""Sleep engine tests — mirrors SleepEngineTest.kt."""

from tests.test_engines.conftest import SLEEP_CONFIG

from app.engines.sleep_engine import SleepEngine, SleepSessionData


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


engine = SleepEngine(SLEEP_CONFIG)


def _session(
    total_minutes: float,
    deep_minutes: float = 0.0,
    rem_minutes: float = 0.0,
    awake_minutes: float = 0.0,
    awakenings: int = 0,
    efficiency: float = 0.95,
) -> SleepSessionData:
    return SleepSessionData(
        start_date_millis=0,
        end_date_millis=int(total_minutes * 60 * 1000),
        total_sleep_minutes=total_minutes,
        time_in_bed_minutes=total_minutes / efficiency,
        light_minutes=total_minutes - deep_minutes - rem_minutes - awake_minutes,
        deep_minutes=deep_minutes,
        rem_minutes=rem_minutes,
        awake_minutes=awake_minutes,
        awakenings=awakenings,
        sleep_onset_latency_minutes=10.0,
        sleep_efficiency=efficiency,
    )


def test_classify_sessions_empty_returns_none_and_empty():
    main, naps = engine.classify_sessions([])
    assert main is None
    assert naps == []


def test_classify_sessions_single_session_main_sleep_no_naps():
    s = _session(480.0)
    main, naps = engine.classify_sessions([s])
    assert main is not None
    _approx(480.0, main.total_sleep_minutes)
    assert naps == []


def test_classify_sessions_main_plus_nap_classifies_correctly():
    main_sleep = _session(480.0)
    nap = _session(45.0)
    main, naps = engine.classify_sessions([main_sleep, nap])
    assert main is not None
    _approx(480.0, main.total_sleep_minutes)
    assert len(naps) == 1
    _approx(45.0, naps[0].total_sleep_minutes)


def test_classify_sessions_short_session_filtered_out():
    main_sleep = _session(480.0)
    too_short = _session(20.0)  # below minimumDurationMinutes=30
    _, naps = engine.classify_sessions([main_sleep, too_short])
    assert naps == []


def test_compute_sleep_need_low_strain_no_supplement():
    # baseline=7.5, strain=5 (below 8 -> add 0.0), debt=0, naps=0
    need = engine.compute_sleep_need(7.5, 5.0, 0.0, 0.0)
    _approx(7.5, need)


def test_compute_sleep_need_high_strain_adds_supplement():
    # baseline=7.5, strain=15 (below 18 -> add 0.5), debt=2.0 (repay 20%=0.4), naps=0
    need = engine.compute_sleep_need(7.5, 15.0, 2.0, 0.0)
    _approx(8.4, need)


def test_compute_sleep_performance_perfect_sleep_returns_100():
    _approx(100.0, engine.compute_sleep_performance(8.0, 8.0))


def test_compute_sleep_performance_half_sleep_returns_50():
    _approx(50.0, engine.compute_sleep_performance(4.0, 8.0))


def test_compute_sleep_debt_all_deficits_accumulates_debt():
    actuals = [7.0, 6.0, 7.0]
    needs = [8.0, 8.0, 8.0]
    # Deficits: 1, 2, 1 -> debt = 4.0
    _approx(4.0, engine.compute_sleep_debt(actuals, needs))


def test_compute_sleep_debt_surplus_ignored():
    actuals = [9.0, 10.0]
    needs = [8.0, 8.0]
    _approx(0.0, engine.compute_sleep_debt(actuals, needs))


def test_compute_composite_sleep_score_perfect_inputs_returns_100():
    # disturbScore = max(0, 100 - 0*20) = 100
    # score = 0.5*100 + 0.25*100 + 0.15*100 + 0.10*100 = 100
    score = engine.compute_composite_sleep_score(100.0, 100.0, 100.0, 0.0)
    _approx(100.0, score)


def test_compute_composite_sleep_score_poor_inputs_low_score():
    # disturbScore = max(0, 100 - 3.0*20) = 40
    # score = 0.5*50 + 0.25*70 + 0.15*40 + 0.10*40 = 25+17.5+6+4 = 52.5
    score = engine.compute_composite_sleep_score(50.0, 70.0, 40.0, 3.0)
    _approx(52.5, score)


def test_compute_restorative_sleep_pct_correct_ratio():
    s = _session(480.0, deep_minutes=90.0, rem_minutes=110.0)
    # (90 + 110) / 480 * 100 = 41.67
    _approx(41.67, engine.compute_restorative_sleep_pct(s))


def test_compute_disturbances_per_hour_correct_rate():
    s = _session(480.0, awakenings=4)
    # 4 awakenings / 8 hours = 0.5
    _approx(0.5, engine.compute_disturbances_per_hour(s))


def test_compute_sleep_consistency_no_prior_history_returns_100():
    result = engine.compute_sleep_consistency(
        current_bedtime_minutes=-120.0,  # 10 PM
        current_wake_time_minutes=420.0,  # 7 AM
        recent_bedtime_minutes=[],
        recent_wake_time_minutes=[],
    )
    _approx(100.0, result)
