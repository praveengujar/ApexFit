import Foundation
import SwiftData

/// Handles syncing local computed metrics to the cloud backend.
actor SyncService {
    private let apiClient = APIClient.shared
    private let modelContext: ModelContext

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
    }

    /// Sync all unsynced daily metrics to the cloud.
    func syncPendingMetrics() async throws {
        let descriptor = FetchDescriptor<DailyMetric>(
            predicate: #Predicate { $0.isComputed && !$0.syncedToCloud },
            sortBy: [SortDescriptor(\.date, order: .forward)]
        )
        let unsyncedMetrics = try modelContext.fetch(descriptor)

        for metric in unsyncedMetrics {
            try await syncMetric(metric)
        }
    }

    /// Sync a single daily metric.
    func syncMetric(_ metric: DailyMetric) async throws {
        let workouts = metric.workouts.map { workout in
            APIModels.WorkoutSyncData(
                workoutType: workout.workoutType,
                workoutName: workout.workoutName,
                startDate: workout.startDate,
                endDate: workout.endDate,
                strainScore: workout.strainScore,
                averageHeartRate: workout.averageHeartRate,
                maxHeartRate: workout.maxHeartRate,
                activeCalories: workout.activeCalories,
                zone1Minutes: workout.zone1Minutes,
                zone2Minutes: workout.zone2Minutes,
                zone3Minutes: workout.zone3Minutes,
                zone4Minutes: workout.zone4Minutes,
                zone5Minutes: workout.zone5Minutes
            )
        }

        let request = APIModels.MetricsSyncRequest(
            date: metric.date,
            recoveryScore: metric.recoveryScore,
            recoveryZone: metric.recoveryZone?.rawValue,
            strainScore: metric.strainScore,
            sleepPerformance: metric.sleepPerformance,
            hrvRmssd: metric.hrvRMSSD,
            hrvSdnn: metric.hrvSDNN,
            restingHeartRate: metric.restingHeartRate,
            respiratoryRate: metric.respiratoryRate,
            spo2: metric.spo2,
            steps: metric.steps,
            activeCalories: metric.activeCalories,
            vo2Max: metric.vo2Max,
            sleepDurationHours: metric.sleepDurationHours,
            sleepNeedHours: metric.sleepNeedHours,
            workouts: workouts
        )

        try await apiClient.requestVoid(.syncMetrics(body: request))
        metric.syncedToCloud = true
        try modelContext.save()
    }

    /// Fetch metric history from the server.
    func fetchMetricHistory(from: Date, to: Date) async throws -> [APIModels.DailyMetricResponse] {
        let response: APIModels.MetricsListResponse = try await apiClient.request(
            .getDailyMetrics(from: from, to: to),
            responseType: APIModels.MetricsListResponse.self
        )
        return response.metrics
    }
}
