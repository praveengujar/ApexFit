import Foundation
import SwiftData

actor StrainService {
    private let queryService: HealthKitQueryService

    init(queryService: HealthKitQueryService = HealthKitQueryService()) {
        self.queryService = queryService
    }

    /// Compute day strain from all HR samples for the given date.
    func computeDayStrain(for date: Date, maxHeartRate: Int) async throws -> StrainResult {
        let samples = try await queryService.fetchHeartRateSamples(
            from: date.startOfDay,
            to: date.endOfDay
        )

        let engine = StrainEngine(maxHeartRate: maxHeartRate)
        return engine.computeWorkoutStrain(from: samples)
    }

    /// Update daily metric with strain data.
    func updateStrain(for dailyMetric: DailyMetric, maxHeartRate: Int) async throws {
        let result = try await computeDayStrain(for: dailyMetric.date, maxHeartRate: maxHeartRate)

        dailyMetric.strainScore = result.strain
    }
}
