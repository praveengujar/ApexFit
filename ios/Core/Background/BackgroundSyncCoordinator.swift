import Foundation
import SwiftData

actor BackgroundSyncCoordinator {
    static let shared = BackgroundSyncCoordinator()

    private var isRunning = false

    func performQuickSync() async throws {
        guard !isRunning else { return }
        isRunning = true
        defer { isRunning = false }

        let queryService = HealthKitQueryService()
        let today = Date()

        // Fetch latest vital signs
        async let hrSamples = queryService.fetchHeartRateSamples(
            from: today.hoursAgo(4),
            to: today
        )
        async let steps = queryService.fetchSteps(for: today)
        async let calories = queryService.fetchActiveCalories(for: today)

        // Await all
        let _ = try await (hrSamples, steps, calories)

        // Note: Full metric computation is done by MetricComputationService
        // This just primes the cache for when the app opens
    }

    func performFullSync() async throws {
        guard !isRunning else { return }
        isRunning = true
        defer { isRunning = false }

        // Full sync: recompute all metrics for today
        // This runs during overnight charging
        let queryService = HealthKitQueryService()
        let today = Date()
        let yesterday = today.yesterday

        // Fetch sleep data (most important for recovery)
        let _ = try await queryService.fetchSleepSamples(
            from: yesterday.startOfDay,
            to: today.endOfDay
        )

        // Fetch workouts
        let _ = try await queryService.fetchWorkouts(
            from: today.startOfDay,
            to: today.endOfDay
        )

        // Note: Full computation pipeline triggered by MetricComputationService
    }
}
