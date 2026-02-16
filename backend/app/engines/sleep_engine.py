"""Sleep engine — mirrors shared/engine/SleepEngine.kt."""

from __future__ import annotations

import math
from dataclasses import dataclass, field
from datetime import UTC, datetime

from app.engines.config import SleepConfig


@dataclass
class SleepSessionData:
    start_date_millis: int
    end_date_millis: int
    total_sleep_minutes: float
    time_in_bed_minutes: float
    light_minutes: float
    deep_minutes: float
    rem_minutes: float
    awake_minutes: float
    awakenings: int
    sleep_onset_latency_minutes: float | None
    sleep_efficiency: float
    stages: list[SleepStageData] = field(default_factory=list)


@dataclass
class SleepStageData:
    type: str  # "light", "deep", "rem", "awake", "inBed"
    start_date_millis: int
    end_date_millis: int
    duration_minutes: float


@dataclass
class SleepConsistencyInput:
    recent_bedtime_minutes: list[float] = field(default_factory=list)
    recent_wake_time_minutes: list[float] = field(default_factory=list)


@dataclass
class SleepAnalysisResult:
    main_sleep: SleepSessionData | None
    naps: list[SleepSessionData]
    total_sleep_hours: float
    sleep_need_hours: float
    sleep_performance: float
    sleep_debt_hours: float
    sleep_score: float
    sleep_efficiency: float
    sleep_consistency: float
    restorative_sleep_pct: float
    disturbances_per_hour: float
    deep_sleep_pct: float
    rem_sleep_pct: float


def _clamp(value: float, lo: float, hi: float) -> float:
    return max(lo, min(hi, value))


def _std_dev(values: list[float]) -> float:
    if len(values) <= 1:
        return 0.0
    mean = sum(values) / len(values)
    variance = sum((v - mean) ** 2 for v in values) / len(values)
    return math.sqrt(variance)


def _minutes_since_midnight(epoch_millis: int) -> float:
    dt = datetime.fromtimestamp(epoch_millis / 1000.0, tz=UTC)
    return dt.hour * 60.0 + dt.minute + dt.second / 60.0


class SleepEngine:
    def __init__(self, config: SleepConfig) -> None:
        self._config = config

    def classify_sessions(
        self, sessions: list[SleepSessionData]
    ) -> tuple[SleepSessionData | None, list[SleepSessionData]]:
        if not sessions:
            return None, []

        sorted_sessions = sorted(sessions, key=lambda s: s.total_sleep_minutes, reverse=True)
        main = sorted_sessions[0]
        max_nap_minutes = self._config.sessionDetection.maximumNapDurationHours * 60
        min_duration_minutes = self._config.sessionDetection.minimumDurationMinutes
        naps = [
            s
            for s in sorted_sessions[1:]
            if min_duration_minutes <= s.total_sleep_minutes <= max_nap_minutes
        ]

        return main, naps

    def compute_sleep_need(
        self,
        baseline_hours: float,
        today_strain: float,
        sleep_debt_hours: float,
        nap_hours_today: float,
    ) -> float:
        strain_supplement = 0.0
        for supp in self._config.strainSupplements:
            if today_strain < supp.strainBelow:
                strain_supplement = supp.addHours
                break

        debt_repayment = sleep_debt_hours * self._config.debtRepaymentRate
        nap_credit = min(nap_hours_today, self._config.sessionDetection.napCreditCapHours)

        return baseline_hours + strain_supplement + debt_repayment - nap_credit

    def compute_sleep_performance(
        self, actual_sleep_hours: float, sleep_need_hours: float,
    ) -> float:
        if sleep_need_hours <= 0:
            return 0.0
        return _clamp((actual_sleep_hours / sleep_need_hours) * 100, 0.0, 100.0)

    def compute_sleep_debt(
        self, past_week_sleep_hours: list[float], past_week_sleep_needs: list[float]
    ) -> float:
        debt = 0.0
        count = min(len(past_week_sleep_hours), len(past_week_sleep_needs))
        for i in range(count):
            deficit = past_week_sleep_needs[i] - past_week_sleep_hours[i]
            debt += max(0.0, deficit)
        return debt

    def compute_sleep_consistency(
        self,
        current_bedtime_minutes: float,
        current_wake_time_minutes: float,
        recent_bedtime_minutes: list[float],
        recent_wake_time_minutes: list[float],
    ) -> float:
        if not recent_bedtime_minutes:
            return 100.0

        all_bedtimes = recent_bedtime_minutes + [current_bedtime_minutes]
        all_wake_times = recent_wake_time_minutes + [current_wake_time_minutes]

        bedtime_std = _std_dev(all_bedtimes)
        wake_time_std = _std_dev(all_wake_times)
        avg_std = (bedtime_std + wake_time_std) / 2.0

        score = 100.0 * math.exp(-avg_std / self._config.consistencyDecayTau)
        return _clamp(score, 0.0, 100.0)

    def compute_restorative_sleep_pct(self, session: SleepSessionData) -> float:
        if session.total_sleep_minutes <= 0:
            return 0.0
        return ((session.deep_minutes + session.rem_minutes) / session.total_sleep_minutes) * 100

    def compute_disturbances_per_hour(self, session: SleepSessionData) -> float:
        hours = session.total_sleep_minutes / 60.0
        if hours <= 0:
            return 0.0
        return session.awakenings / hours

    def compute_composite_sleep_score(
        self,
        sufficiency: float,
        efficiency: float,
        consistency: float,
        disturbances_per_hour: float,
    ) -> float:
        disturbance_score = max(
            0.0,
            min(100.0, 100 - disturbances_per_hour * self._config.disturbanceScaling),
        )

        w = self._config.compositeWeights
        score = (
            w.sufficiency * sufficiency
            + w.efficiency * efficiency
            + w.consistency * consistency
            + w.disturbances * disturbance_score
        )

        return _clamp(score, 0.0, 100.0)

    def analyze(
        self,
        sessions: list[SleepSessionData],
        baseline_sleep_hours: float,
        today_strain: float,
        past_week_sleep_hours: list[float],
        past_week_sleep_needs: list[float],
        consistency_input: SleepConsistencyInput | None = None,
    ) -> SleepAnalysisResult:
        if consistency_input is None:
            consistency_input = SleepConsistencyInput()

        main, naps = self.classify_sessions(sessions)

        total_main_sleep = main.total_sleep_minutes if main else 0.0
        nap_hours = sum(n.total_sleep_minutes for n in naps) / 60.0
        total_sleep_hours = (total_main_sleep / 60.0) + nap_hours

        sleep_debt = self.compute_sleep_debt(past_week_sleep_hours, past_week_sleep_needs)
        sleep_need = self.compute_sleep_need(
            baseline_sleep_hours, today_strain, sleep_debt, nap_hours
        )
        performance = self.compute_sleep_performance(total_sleep_hours, sleep_need)

        efficiency = main.sleep_efficiency if main else 0.0
        restorative_pct = self.compute_restorative_sleep_pct(main) if main else 0.0
        disturbances = self.compute_disturbances_per_hour(main) if main else 0.0
        deep_pct = (
            (main.deep_minutes / main.total_sleep_minutes * 100)
            if main and main.total_sleep_minutes > 0
            else 0.0
        )
        rem_pct = (
            (main.rem_minutes / main.total_sleep_minutes * 100)
            if main and main.total_sleep_minutes > 0
            else 0.0
        )

        if main is not None:
            bedtime_min = _minutes_since_midnight(main.start_date_millis)
            wake_min = _minutes_since_midnight(main.end_date_millis)
            consistency = self.compute_sleep_consistency(
                bedtime_min,
                wake_min,
                consistency_input.recent_bedtime_minutes,
                consistency_input.recent_wake_time_minutes,
            )
        else:
            consistency = 100.0

        sleep_score = self.compute_composite_sleep_score(
            performance, efficiency, consistency, disturbances
        )

        return SleepAnalysisResult(
            main_sleep=main,
            naps=naps,
            total_sleep_hours=total_sleep_hours,
            sleep_need_hours=sleep_need,
            sleep_performance=performance,
            sleep_debt_hours=sleep_debt,
            sleep_score=sleep_score,
            sleep_efficiency=efficiency,
            sleep_consistency=consistency,
            restorative_sleep_pct=restorative_pct,
            disturbances_per_hour=disturbances,
            deep_sleep_pct=deep_pct,
            rem_sleep_pct=rem_pct,
        )
