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

        // Fetch vital signs during sleep
        async let rrIntervals = queryService.fetchRRIntervals(from: sleepStart, to: sleepEnd)
        async let hrvSamples = queryService.fetchHRVSamples(from: sleepStart, to: sleepEnd)
        async let rhr = queryService.fetchRestingHeartRate(for: date)
        async let respRate = queryService.fetchRespiratoryRate(for: date)
        async let spo2 = queryService.fetchSpO2(for: date)
        async let skinTemp = queryService.fetchSkinTemperature(for: date)

        let fetchedRR = try await rrIntervals
        let fetchedHRV = try await hrvSamples
        let fetchedRHR = try await rhr
        let fetchedRespRate = try await respRate
        let fetchedSpO2 = try await spo2
        let fetchedSkinTemp = try await skinTemp

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
