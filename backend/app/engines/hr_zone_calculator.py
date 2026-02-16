"""Heart rate zone calculator — mirrors shared/engine/HeartRateZoneCalculator.kt."""

from __future__ import annotations

from dataclasses import dataclass

from app.engines.config import HeartRateZoneConfig


@dataclass
class HeartRateZone:
    zone: int
    name: str
    lower_bound: float
    upper_bound: float
    multiplier: float


class HeartRateZoneCalculator:
    def __init__(self, max_heart_rate: int, hr_config: HeartRateZoneConfig) -> None:
        self.max_heart_rate = max_heart_rate
        self._hr_config = hr_config
        self._zones: list[HeartRateZone] | None = None

    @property
    def zones(self) -> list[HeartRateZone]:
        if self._zones is None:
            max_hr = float(self.max_heart_rate)
            b = self._hr_config.boundaries
            m = self._hr_config.multipliers
            self._zones = [
                HeartRateZone(1, "Warm-Up", max_hr * b[0], max_hr * b[1], m[0]),
                HeartRateZone(2, "Fat Burn", max_hr * b[1], max_hr * b[2], m[1]),
                HeartRateZone(3, "Aerobic", max_hr * b[2], max_hr * b[3], m[2]),
                HeartRateZone(4, "Threshold", max_hr * b[3], max_hr * b[4], m[3]),
                HeartRateZone(5, "Anaerobic", max_hr * b[4], max_hr * b[5], m[4]),
            ]
        return self._zones

    def zone(self, heart_rate: float) -> HeartRateZone | None:
        percentage = heart_rate / float(self.max_heart_rate)
        b = self._hr_config.boundaries

        if percentage < b[0]:
            return None
        elif percentage < b[1]:
            return self.zones[0]
        elif percentage < b[2]:
            return self.zones[1]
        elif percentage < b[3]:
            return self.zones[2]
        elif percentage < b[4]:
            return self.zones[3]
        else:
            return self.zones[4]

    def zone_number(self, heart_rate: float) -> int:
        z = self.zone(heart_rate)
        return z.zone if z else 0

    def multiplier(self, heart_rate: float) -> float:
        z = self.zone(heart_rate)
        return z.multiplier if z else 0.0

    def zone_boundaries(self) -> list[tuple[int, int, int]]:
        return [(z.zone, int(z.lower_bound), int(z.upper_bound)) for z in self.zones]
