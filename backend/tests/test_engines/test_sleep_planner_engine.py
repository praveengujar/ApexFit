"""Sleep planner engine tests — mirrors SleepPlannerEngineTest.kt."""

from tests.test_engines.conftest import SLEEP_PLANNER_CONFIG

from app.engines.sleep_planner_engine import SleepGoalType, SleepPlannerEngine


def _approx(expected, actual, tolerance=0.01):
    assert abs(expected - actual) < tolerance, f"expected {expected} but got {actual}"


engine = SleepPlannerEngine(SLEEP_PLANNER_CONFIG)

# Use a reference wake time (7 AM as millis offset)
WAKE_TIME_7AM = 7 * 3600 * 1000


def test_plan_peak_goal_full_sleep_duration():
    # need=8.0, PEAK (mult=1.0) -> requiredSleep = 8.0
    result = engine.plan(
        sleep_need_hours=8.0,
        goal=SleepGoalType.PEAK,
        desired_wake_time_millis=WAKE_TIME_7AM,
    )
    _approx(8.0, result.required_sleep_duration)


def test_plan_perform_goal_reduced_duration():
    # need=8.0, PERFORM (mult=0.85) -> requiredSleep = 6.8
    result = engine.plan(
        sleep_need_hours=8.0,
        goal=SleepGoalType.PERFORM,
        desired_wake_time_millis=WAKE_TIME_7AM,
    )
    _approx(6.8, result.required_sleep_duration)


def test_plan_get_by_goal_minimum_duration():
    # need=8.0, GET_BY (mult=0.70) -> requiredSleep = 5.6
    result = engine.plan(
        sleep_need_hours=8.0,
        goal=SleepGoalType.GET_BY,
        desired_wake_time_millis=WAKE_TIME_7AM,
    )
    _approx(5.6, result.required_sleep_duration)


def test_plan_bedtime_is_correct_offset():
    # requiredSleep=8.0, latency=15min -> totalInBed = 8.25h
    # bedtime = wake - 8.25 * 3600000 = wake - 29700000
    result = engine.plan(
        sleep_need_hours=8.0,
        goal=SleepGoalType.PEAK,
        desired_wake_time_millis=WAKE_TIME_7AM,
        estimated_onset_latency_minutes=15.0,
    )
    expected_bedtime = WAKE_TIME_7AM - int(8.25 * 3600 * 1000)
    assert result.recommended_bedtime_millis == expected_bedtime


def test_estimate_wake_time_empty_returns_7am():
    assert engine.estimate_wake_time([]) == 420


def test_estimate_wake_time_with_history_returns_average():
    # [360(6AM), 420(7AM), 480(8AM)] -> avg = 420 (7AM)
    assert engine.estimate_wake_time([360, 420, 480]) == 420


def test_estimate_onset_latency_empty_returns_15():
    _approx(15.0, engine.estimate_onset_latency([]))


def test_estimate_onset_latency_with_history_returns_average():
    _approx(15.0, engine.estimate_onset_latency([10.0, 20.0, 15.0]))
