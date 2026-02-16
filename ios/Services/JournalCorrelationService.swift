import Foundation
import SwiftData

actor JournalCorrelationService {
    private let modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    /// Compute correlations between journal behaviors and a target metric.
    func computeCorrelations(
        targetMetric: TargetMetric,
        minimumEntries: Int = 14
    ) async throws -> [CorrelationResult] {
        let entries = try fetchAllJournalEntries()
        let metrics = try fetchAllDailyMetrics()

        guard entries.count >= minimumEntries else { return [] }

        // Build date-indexed lookup
        var metricsByDate: [Date: DailyMetric] = [:]
        for metric in metrics {
            metricsByDate[metric.date.startOfDay] = metric
        }

        // Collect all unique behavior IDs
        var behaviorValues: [String: (withBehavior: [Double], withoutBehavior: [Double])] = [:]

        for entry in entries {
            guard let metric = metricsByDate[entry.date.startOfDay] else { continue }
            guard let metricValue = targetMetric.extractValue(from: metric) else { continue }

            for response in entry.responses {
                let isActive: Bool
                switch response.responseType {
                case .toggle:
                    isActive = response.toggleValue ?? false
                case .numeric:
                    isActive = (response.numericValue ?? 0) > 0
                case .scale:
                    isActive = response.scaleValue != nil && response.scaleValue != "None"
                }

                let key = response.behaviorID
                if behaviorValues[key] == nil {
                    behaviorValues[key] = ([], [])
                }
                if isActive {
                    behaviorValues[key]?.withBehavior.append(metricValue)
                } else {
                    behaviorValues[key]?.withoutBehavior.append(metricValue)
                }
            }
        }

        // Run statistical tests
        var results: [CorrelationResult] = []
        for (behaviorID, values) in behaviorValues {
            guard let result = StatisticalEngine.analyzeCorrelation(
                behaviorName: behaviorID,
                metricName: targetMetric.displayName,
                withBehavior: values.withBehavior,
                withoutBehavior: values.withoutBehavior,
                higherIsBetter: targetMetric.higherIsBetter
            ) else { continue }

            results.append(result)
        }

        // Sort by significance then effect size
        return results.sorted { a, b in
            if a.isSignificant != b.isSignificant { return a.isSignificant }
            return abs(a.effectSize) > abs(b.effectSize)
        }
    }

    private func fetchAllJournalEntries() throws -> [JournalEntry] {
        let descriptor = FetchDescriptor<JournalEntry>(
            sortBy: [SortDescriptor(\.date, order: .forward)]
        )
        return try modelContext.fetch(descriptor)
    }

    private func fetchAllDailyMetrics() throws -> [DailyMetric] {
        let descriptor = FetchDescriptor<DailyMetric>(
            sortBy: [SortDescriptor(\.date, order: .forward)]
        )
        return try modelContext.fetch(descriptor)
    }
}

enum TargetMetric: String, CaseIterable {
    case recovery
    case strain
    case sleepPerformance
    case hrv
    case restingHeartRate

    var displayName: String {
        switch self {
        case .recovery: return "Recovery"
        case .strain: return "Strain"
        case .sleepPerformance: return "Sleep Performance"
        case .hrv: return "HRV"
        case .restingHeartRate: return "Resting Heart Rate"
        }
    }

    var higherIsBetter: Bool {
        switch self {
        case .recovery, .sleepPerformance, .hrv: return true
        case .strain, .restingHeartRate: return false
        }
    }

    func extractValue(from metric: DailyMetric) -> Double? {
        switch self {
        case .recovery: return metric.recoveryScore
        case .strain: return metric.strainScore
        case .sleepPerformance: return metric.sleepPerformance
        case .hrv: return metric.hrvRMSSD ?? metric.hrvSDNN
        case .restingHeartRate: return metric.restingHeartRate
        }
    }
}
