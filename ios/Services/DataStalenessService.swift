import Foundation

struct DataStalenessReport {
    var heartRateStale: Bool
    var sleepDataMissing: Bool
    var workoutDataStale: Bool
    var hrvStale: Bool
    var messages: [String]
}

struct DataStalenessService {
    func checkStaleness(for metric: DailyMetric) -> DataStalenessReport {
        var messages: [String] = []
        let now = Date()

        let heartRateStale = isStale(
            lastUpdate: metric.computedAt,
            thresholdHours: HealthKitConstants.heartRateStalenessHours,
            from: now
        )
        if heartRateStale {
            messages.append("Heart rate data may be stale (last update > \(HealthKitConstants.heartRateStalenessHours)h ago)")
        }

        let sleepDataMissing = metric.sleepDurationHours == nil
        if sleepDataMissing {
            messages.append("No sleep data recorded for last night")
        }

        let hrvStale = metric.hrvRMSSD == nil && metric.hrvSDNN == nil
        if hrvStale {
            messages.append("No HRV data available")
        }

        let workoutDataStale = false // Workouts are event-based, not periodic

        return DataStalenessReport(
            heartRateStale: heartRateStale,
            sleepDataMissing: sleepDataMissing,
            workoutDataStale: workoutDataStale,
            hrvStale: hrvStale,
            messages: messages
        )
    }

    private func isStale(lastUpdate: Date?, thresholdHours: Int, from now: Date) -> Bool {
        guard let lastUpdate else { return true }
        let hoursSince = now.timeIntervalSince(lastUpdate) / 3600
        return hoursSince > Double(thresholdHours)
    }
}
