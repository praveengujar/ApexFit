import Foundation

struct HeartRateSample {
    let timestamp: Date
    let bpm: Double
    let durationSeconds: Double
}

struct StrainResult {
    let strain: Double
    let weightedHRArea: Double
    let zone1Minutes: Double
    let zone2Minutes: Double
    let zone3Minutes: Double
    let zone4Minutes: Double
    let zone5Minutes: Double
}

struct StrainEngine {
    private let k: Double = ConfigurationManager.shared.config.strain.scalingFactor
    private let c: Double = ConfigurationManager.shared.config.strain.logOffsetConstant
    private let zoneCalculator: HeartRateZoneCalculator

    init(maxHeartRate: Int) {
        self.zoneCalculator = HeartRateZoneCalculator(maxHeartRate: maxHeartRate)
    }

    /// Compute strain from an array of HR samples with timestamps.
    /// Samples should be sorted by timestamp.
    func computeStrain(from samples: [HeartRateSample]) -> StrainResult {
        var weightedHRArea: Double = 0
        var zoneMinutes = [0.0, 0.0, 0.0, 0.0, 0.0]

        for sample in samples {
            let durationMinutes = sample.durationSeconds / 60.0
            let zoneMultiplier = zoneCalculator.multiplier(for: sample.bpm)
            let zoneNum = zoneCalculator.zoneNumber(for: sample.bpm)

            weightedHRArea += durationMinutes * zoneMultiplier

            if zoneNum >= 1 && zoneNum <= 5 {
                zoneMinutes[zoneNum - 1] += durationMinutes
            }
        }

        let rawStrain = k * log10(weightedHRArea + c)
        let clampedStrain = rawStrain.clamped(to: HealthKitConstants.strainMinValue...HealthKitConstants.strainMaxValue)

        return StrainResult(
            strain: clampedStrain,
            weightedHRArea: weightedHRArea,
            zone1Minutes: zoneMinutes[0],
            zone2Minutes: zoneMinutes[1],
            zone3Minutes: zoneMinutes[2],
            zone4Minutes: zoneMinutes[3],
            zone5Minutes: zoneMinutes[4]
        )
    }

    /// Convert raw HealthKit HR samples (timestamp, bpm) into HeartRateSamples with estimated durations.
    static func estimateDurations(from rawSamples: [(date: Date, bpm: Double)]) -> [HeartRateSample] {
        guard rawSamples.count > 1 else {
            return rawSamples.map { HeartRateSample(timestamp: $0.date, bpm: $0.bpm, durationSeconds: 5.0) }
        }

        var result: [HeartRateSample] = []
        for i in 0..<rawSamples.count {
            let duration: Double
            if i < rawSamples.count - 1 {
                duration = Swift.min(rawSamples[i + 1].date.timeIntervalSince(rawSamples[i].date), ConfigurationManager.shared.config.heartRateZones.sampleMaxDurationSeconds)
            } else {
                duration = result.last?.durationSeconds ?? 5.0
            }

            result.append(HeartRateSample(
                timestamp: rawSamples[i].date,
                bpm: rawSamples[i].bpm,
                durationSeconds: duration
            ))
        }
        return result
    }

    /// Compute strain for a specific time window (e.g., a single workout)
    func computeWorkoutStrain(from samples: [(date: Date, bpm: Double)]) -> StrainResult {
        let hrSamples = StrainEngine.estimateDurations(from: samples)
        return computeStrain(from: hrSamples)
    }
}
