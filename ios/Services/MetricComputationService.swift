import Foundation
import SwiftData

/// Master coordinator that enforces correct computation order:
/// 1. Baselines (28-day rolling)
/// 2. Sleep (needed for Recovery)
/// 3. Recovery (depends on sleep performance + baselines)
/// 4. Strain + Workouts (independent, but update daily metric)
/// 5. Additional data (steps, calories, VO2Max)
/// 6. Stress (intraday timeline + daily average)
actor MetricComputationService {
    private let modelContext: ModelContext
    private let queryService: HealthKitQueryService

    init(modelContext: ModelContext) {
        self.modelContext = modelContext
        self.queryService = HealthKitQueryService()
    }

    /// Run full computation pipeline for a date.
    func computeAllMetrics(for date: Date) async throws {
        print("[Zyva]   Pipeline start for \(date.formatted(date: .abbreviated, time: .omitted))")

        let profile = try fetchUserProfile()
        print("[Zyva]   ✓ UserProfile found: maxHR=\(profile.estimatedMaxHR), sleepBaseline=\(profile.sleepBaselineHours)h")

        let dailyMetric = try getOrCreateDailyMetric(for: date)
        let maxHR = profile.estimatedMaxHR

        // Step 1: Update baselines
        print("[Zyva]   Step 1: Baselines...")
        let baselineService = BaselineService(modelContext: modelContext)
        try await baselineService.recomputeBaselines()
        print("[Zyva]   ✓ Baselines done")

        // Step 2: Process sleep (with composite scoring + consistency)
        print("[Zyva]   Step 2: Sleep...")
        let sleepService = SleepService(modelContext: modelContext, queryService: queryService)
        try await sleepService.processSleep(
            for: date,
            dailyMetric: dailyMetric,
            baselineSleepHours: profile.sleepBaselineHours
        )
        print("[Zyva]   ✓ Sleep done — duration=\(dailyMetric.sleepDurationHours ?? -1)h, score=\(dailyMetric.sleepScore ?? -1)")

        // Step 3: Compute recovery (now includes skin temperature)
        print("[Zyva]   Step 3: Recovery...")
        let recoveryService = RecoveryService(modelContext: modelContext, queryService: queryService)
        try await recoveryService.computeRecovery(for: date, dailyMetric: dailyMetric)
        print("[Zyva]   ✓ Recovery done — score=\(dailyMetric.recoveryScore ?? -1), zone=\(dailyMetric.recoveryZone?.rawValue ?? "nil")")

        // Step 4: Compute strain and process workouts
        print("[Zyva]   Step 4: Strain...")
        let strainService = StrainService(queryService: queryService)
        try await strainService.updateStrain(for: dailyMetric, maxHeartRate: maxHR)
        print("[Zyva]   ✓ Strain done — score=\(dailyMetric.strainScore)")

        print("[Zyva]   Step 4b: Workouts...")
        let workoutService = WorkoutService(queryService: queryService)
        try await workoutService.processWorkouts(
            for: date,
            dailyMetric: dailyMetric,
            maxHeartRate: maxHR,
            bodyWeightKG: profile.weightKG
        )
        print("[Zyva]   ✓ Workouts done — count=\(dailyMetric.workouts.count)")

        // Step 5: Additional data
        print("[Zyva]   Step 5: Additional data...")
        async let steps = queryService.fetchSteps(for: date)
        async let calories = queryService.fetchActiveCalories(for: date)
        async let vo2Max = queryService.fetchVO2Max(for: date)

        dailyMetric.steps = try await steps
        dailyMetric.activeCalories = try await calories
        dailyMetric.vo2Max = try await vo2Max
        print("[Zyva]   ✓ Steps=\(dailyMetric.steps), Cal=\(Int(dailyMetric.activeCalories)), VO2=\(dailyMetric.vo2Max ?? -1)")

        // Step 5b: Body composition for Longevity
        if let bodyFatPct = try? await queryService.fetchBodyFatPercentage(for: date) {
            dailyMetric.leanBodyMassPct = 100.0 - bodyFatPct
        } else if let lbm = try? await queryService.fetchLeanBodyMass(for: date),
                  let weight = profile.weightKG, weight > 0 {
            dailyMetric.leanBodyMassPct = (lbm / weight) * 100.0
        }

        // Step 6: Compute stress timeline and daily average
        print("[Zyva]   Step 6: Stress...")
        try await computeStress(for: date, dailyMetric: dailyMetric)
        print("[Zyva]   ✓ Stress done — avg=\(dailyMetric.stressAverage ?? -1)")

        // Mark as computed
        dailyMetric.isComputed = true
        dailyMetric.computedAt = Date()

        try modelContext.save()
        print("[Zyva]   ✓ Pipeline COMPLETE for \(date.formatted(date: .abbreviated, time: .omitted))")
    }

    /// Quick update: only refresh strain (for real-time updates during the day).
    func quickStrainUpdate(for date: Date) async throws {
        let profile = try fetchUserProfile()
        let dailyMetric = try getOrCreateDailyMetric(for: date)

        let strainService = StrainService(queryService: queryService)
        try await strainService.updateStrain(for: dailyMetric, maxHeartRate: profile.estimatedMaxHR)

        let steps = try await queryService.fetchSteps(for: date)
        let calories = try await queryService.fetchActiveCalories(for: date)
        dailyMetric.steps = steps
        dailyMetric.activeCalories = calories
        dailyMetric.computedAt = Date()

        try modelContext.save()
    }

    // MARK: - Stress Computation

    /// Compute stress timeline for the day and store daily average.
    private func computeStress(for date: Date, dailyMetric: DailyMetric) async throws {
        // Build stress baselines from recent daily metrics
        let cutoffDate = date.daysAgo(14)
        let descriptor = FetchDescriptor<DailyMetric>(
            predicate: #Predicate { $0.date >= cutoffDate && $0.date < date },
            sortBy: [SortDescriptor(\.date, order: .forward)]
        )
        let recentMetrics = try modelContext.fetch(descriptor)

        // StressService is @MainActor — hop to main actor for init + sync methods
        let stressService = await StressService(queryService: queryService)
        await stressService.computeBaselines(using: recentMetrics)

        // Async methods handle their own actor hopping
        try await stressService.computeStressTimeline(for: date)
        dailyMetric.stressAverage = await stressService.computeDailyStressAverage()
    }

    // MARK: - Helpers

    private func fetchUserProfile() throws -> UserProfile {
        let descriptor = FetchDescriptor<UserProfile>()
        guard let profile = try modelContext.fetch(descriptor).first else {
            throw ComputationError.noUserProfile
        }
        return profile
    }

    private func getOrCreateDailyMetric(for date: Date) throws -> DailyMetric {
        let startOfDay = date.startOfDay
        let descriptor = FetchDescriptor<DailyMetric>(
            predicate: #Predicate { $0.date == startOfDay }
        )

        if let existing = try modelContext.fetch(descriptor).first {
            return existing
        }

        let metric = DailyMetric(date: date)
        let profile = try fetchUserProfile()
        metric.userProfile = profile
        modelContext.insert(metric)
        return metric
    }
}

enum ComputationError: Error, LocalizedError {
    case noUserProfile
    case insufficientData(String)

    var errorDescription: String? {
        switch self {
        case .noUserProfile:
            return "No user profile found. Please complete onboarding."
        case .insufficientData(let detail):
            return "Insufficient data: \(detail)"
        }
    }
}
