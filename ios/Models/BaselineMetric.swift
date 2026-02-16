import Foundation
import SwiftData

@Model
final class BaselineMetric {
    var id: UUID
    var metricType: BaselineMetricType
    var mean: Double
    var standardDeviation: Double
    var sampleCount: Int
    var windowStartDate: Date
    var windowEndDate: Date
    var updatedAt: Date

    init(metricType: BaselineMetricType, mean: Double, standardDeviation: Double, sampleCount: Int, windowStartDate: Date, windowEndDate: Date) {
        self.id = UUID()
        self.metricType = metricType
        self.mean = mean
        self.standardDeviation = standardDeviation
        self.sampleCount = sampleCount
        self.windowStartDate = windowStartDate
        self.windowEndDate = windowEndDate
        self.updatedAt = Date()
    }

    var isValid: Bool {
        sampleCount >= 3 && standardDeviation > 0
    }

    func zScore(for value: Double) -> Double {
        guard standardDeviation > 0 else { return 0 }
        return (value - mean) / standardDeviation
    }
}

enum BaselineMetricType: String, Codable, CaseIterable {
    case hrv
    case restingHeartRate
    case respiratoryRate
    case spo2
    case skinTemperature
    case sleepDuration
    case sleepPerformance
    case strain
    case steps
    case deepSleepPercentage
    case remSleepPercentage
}
