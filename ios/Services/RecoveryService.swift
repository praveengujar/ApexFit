import Foundation
import SwiftData

actor RecoveryService {
    private let modelContext: ModelContext
    private let queryService: HealthKitQueryService
    private let baselineService: BaselineService

    init(modelContext: ModelContext, queryService: HealthKitQueryService = HealthKitQueryService()) {
        self.modelContext = modelContext
        self.queryService = queryService
        self.baselineService = BaselineService(modelContext: modelContext)
    }

    /// Compute recovery score for a given date.
    func computeRecovery(for date: Date, dailyMetric: DailyMetric) async throws {
        // Fetch overnight data
        let sleepStart = date.yesterday.startOfDay.addingTimeInterval(20 * 3600) // 8 PM
        let sleepEnd = date.startOfDay.addingTimeInterval(10 * 3600) // 10 AM

        // Fetch vital signs during sleep — each wrapped individually so one failure
        // doesn't kill the entire recovery computation.
        let fetchedRR = (try? await queryService.fetchRRIntervals(from: sleepStart, to: sleepEnd)) ?? []
        let fetchedHRV = (try? await queryService.fetchHRVSamples(from: sleepStart, to: sleepEnd)) ?? []
        let fetchedRHR = try? await queryService.fetchRestingHeartRate(for: date)
        let fetchedRespRate = try? await queryService.fetchRespiratoryRate(for: date)
        let fetchedSpO2 = try? await queryService.fetchSpO2(for: date)
        let fetchedSkinTemp = try? await queryService.fetchSkinTemperature(for: date)

        // Compute best HRV value
        let sdnn = fetchedHRV.last?.sdnn
        let hrvResult = HRVCalculator.bestHRV(rrIntervals: fetchedRR.isEmpty ? nil : fetchedRR, sdnnValue: sdnn)
        let effectiveHRV = HRVCalculator.effectiveHRV(from: hrvResult)

        // Store raw values
        dailyMetric.hrvRMSSD = hrvResult.rmssd
        dailyMetric.hrvSDNN = sdnn
        dailyMetric.restingHeartRate = fetchedRHR
        dailyMetric.respiratoryRate = fetchedRespRate
        dailyMetric.spo2 = fetchedSpO2
        dailyMetric.skinTemperature = fetchedSkinTemp

        // Get baselines (includes skin temperature)
        let baselines = RecoveryBaselines(
            hrv: try await baselineService.getBaseline(for: .hrv),
            restingHeartRate: try await baselineService.getBaseline(for: .restingHeartRate),
            sleepPerformance: try await baselineService.getBaseline(for: .sleepPerformance),
            respiratoryRate: try await baselineService.getBaseline(for: .respiratoryRate),
            spo2: try await baselineService.getBaseline(for: .spo2),
            skinTemperature: try await baselineService.getBaseline(for: .skinTemperature)
        )

        // Build recovery input
        let input = RecoveryInput(
            hrv: effectiveHRV,
            restingHeartRate: fetchedRHR,
            sleepPerformance: dailyMetric.sleepPerformance,
            respiratoryRate: fetchedRespRate,
            spo2: fetchedSpO2,
            skinTemperatureDeviation: fetchedSkinTemp
        )

        // Compute
        let result = RecoveryEngine.computeRecovery(input: input, baselines: baselines)

        // Update metric
        dailyMetric.recoveryScore = result.score
        dailyMetric.recoveryZone = result.zone
    }
}
