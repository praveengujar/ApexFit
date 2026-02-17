"""Sleep planner engine — mirrors shared/engine/SleepPlannerEngine.kt."""

from __future__ import annotations

from dataclasses import dataclass
from enum import StrEnum

from app.engines.config import SleepPlannerConfig


class SleepGoalType(StrEnum):
    PEAK = "Peak"
    PERFORM = "Perform"
    GET_BY = "Get By"

    @property
    def description(self) -> str:
        return {
            SleepGoalType.PEAK: "Full sleep need for maximum recovery",
            SleepGoalType.PERFORM: "Solid sleep for a good recovery day",
            SleepGoalType.GET_BY: "Minimum viable sleep — reduced recovery",
        }[self]

    def multiplier(self, config: SleepPlannerConfig) -> float:
        return {
            SleepGoalType.PEAK: config.goalMultipliers.peak,
            SleepGoalType.PERFORM: config.goalMultipliers.perform,
            SleepGoalType.GET_BY: config.goalMultipliers.getBy,
        }[self]


@dataclass
class SleepPlannerResult:
    sleep_need_hours: float
    required_sleep_duration: float
    recommended_bedtime_millis: int
    expected_wake_time_millis: int
    goal: SleepGoalType
    baseline_need: float
    strain_supplement: float
    debt_repayment: float
    nap_credit: float


class SleepPlannerEngine:
    def __init__(self, config: SleepPlannerConfig) -> None:
        self._config = config

    def plan(
        self,
        sleep_need_hours: float,
        goal: SleepGoalType,
        desired_wake_time_millis: int,
        estimated_onset_latency_minutes: float = 15.0,
        baseline_need: float = 7.5,
        strain_supplement: float = 0.0,
        debt_repayment: float = 0.0,
        nap_credit: float = 0.0,
    ) -> SleepPlannerResult:
        required_sleep = sleep_need_hours * goal.multiplier(self._config)
        total_time_in_bed_hours = required_sleep + (estimated_onset_latency_minutes / 60.0)
        bedtime_millis = desired_wake_time_millis - int(total_time_in_bed_hours * 3600 * 1000)

        return SleepPlannerResult(
            sleep_need_hours=sleep_need_hours,
            required_sleep_duration=required_sleep,
            recommended_bedtime_millis=bedtime_millis,
            expected_wake_time_millis=desired_wake_time_millis,
            goal=goal,
            baseline_need=baseline_need,
            strain_supplement=strain_supplement,
            debt_repayment=debt_repayment,
            nap_credit=nap_credit,
        )

    def estimate_wake_time(self, recent_wake_time_minutes_from_midnight: list[int]) -> int:
        if not recent_wake_time_minutes_from_midnight:
            return 7 * 60  # Default to 7:00 AM
        total = sum(recent_wake_time_minutes_from_midnight)
        return total // len(recent_wake_time_minutes_from_midnight)

    def estimate_onset_latency(self, historical_latencies: list[float]) -> float:
        if not historical_latencies:
            return 15.0
        return sum(historical_latencies) / len(historical_latencies)
