import Foundation

struct HeartRateZone {
    let zone: Int
    let name: String
    let lowerBound: Double
    let upperBound: Double
    let multiplier: Double
}

struct HeartRateZoneCalculator {
    let maxHeartRate: Int

    private var hrConfig: HeartRateZoneConfig { ConfigurationManager.shared.config.heartRateZones }

    var zones: [HeartRateZone] {
        let maxHR = Double(maxHeartRate)
        let b = hrConfig.boundaries
        let m = hrConfig.multipliers
        return [
            HeartRateZone(zone: 1, name: "Warm-Up", lowerBound: maxHR * b[0], upperBound: maxHR * b[1], multiplier: m[0]),
            HeartRateZone(zone: 2, name: "Fat Burn", lowerBound: maxHR * b[1], upperBound: maxHR * b[2], multiplier: m[1]),
            HeartRateZone(zone: 3, name: "Aerobic", lowerBound: maxHR * b[2], upperBound: maxHR * b[3], multiplier: m[2]),
            HeartRateZone(zone: 4, name: "Threshold", lowerBound: maxHR * b[3], upperBound: maxHR * b[4], multiplier: m[3]),
            HeartRateZone(zone: 5, name: "Anaerobic", lowerBound: maxHR * b[4], upperBound: maxHR * b[5], multiplier: m[4]),
        ]
    }

    func zone(for heartRate: Double) -> HeartRateZone? {
        let maxHR = Double(maxHeartRate)
        let percentage = heartRate / maxHR
        let b = hrConfig.boundaries

        if percentage < b[0] {
            return nil // Below Zone 1
        } else if percentage < b[1] {
            return zones[0]
        } else if percentage < b[2] {
            return zones[1]
        } else if percentage < b[3] {
            return zones[2]
        } else if percentage < b[4] {
            return zones[3]
        } else {
            return zones[4]
        }
    }

    func zoneNumber(for heartRate: Double) -> Int {
        zone(for: heartRate)?.zone ?? 0
    }

    func multiplier(for heartRate: Double) -> Double {
        zone(for: heartRate)?.multiplier ?? 0
    }

    func zoneBoundaries() -> [(zone: Int, lower: Int, upper: Int)] {
        zones.map { (zone: $0.zone, lower: Int($0.lowerBound), upper: Int($0.upperBound)) }
    }
}
