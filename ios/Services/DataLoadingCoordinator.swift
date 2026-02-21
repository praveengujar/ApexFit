import Foundation
import Observation
import SwiftData

@Observable
@MainActor
final class DataLoadingCoordinator {
    enum LoadState: Equatable {
        case idle
        case loading(progress: String)
        case complete
        case error(String)
    }

    var state: LoadState = .idle

    private var hasLoadedThisSession = false

    func loadDataIfNeeded(modelContext: ModelContext, isAuthorized: Bool) async {
        print("[Zyva] loadDataIfNeeded called — isAuthorized: \(isAuthorized), hasLoadedThisSession: \(hasLoadedThisSession)")
        guard isAuthorized else {
            print("[Zyva] Skipping data load — not authorized")
            return
        }
        guard !hasLoadedThisSession else {
            print("[Zyva] Already loaded this session — quick refresh only")
            await quickRefresh(modelContext: modelContext)
            return
        }

        state = .loading(progress: "Loading health data...")
        hasLoadedThisSession = true

        do {
            let service = MetricComputationService(modelContext: modelContext)
            let today = Date()

            // Backfill recent days first (oldest to newest) so baselines build up
            for dayOffset in stride(from: 7, through: 1, by: -1) {
                let date = today.daysAgo(dayOffset)
                if try isAlreadyComputed(date: date, context: modelContext) {
                    print("[Zyva] Day \(date.shortDateString) already computed, skipping")
                    continue
                }
                state = .loading(progress: "Processing \(date.shortDateString)...")
                print("[Zyva] Computing metrics for \(date.shortDateString)...")
                try await service.computeAllMetrics(for: date)
                print("[Zyva] ✓ Done computing \(date.shortDateString)")
            }

            // Compute today (always recompute for freshness)
            state = .loading(progress: "Processing today...")
            print("[Zyva] Computing metrics for today...")
            try await service.computeAllMetrics(for: today)
            print("[Zyva] ✓ Done computing today")

            try modelContext.save()
            print("[Zyva] ✓ All data saved to SwiftData")

            state = .complete
        } catch {
            print("[Zyva] ✘ DataLoadingCoordinator error: \(error)")
            state = .error(error.localizedDescription)
        }
    }

    private func quickRefresh(modelContext: ModelContext) async {
        do {
            let service = MetricComputationService(modelContext: modelContext)
            try await service.quickStrainUpdate(for: Date())
            try modelContext.save()
        } catch {
            print("[Zyva] Quick refresh error: \(error)")
        }
    }

    private func isAlreadyComputed(date: Date, context: ModelContext) throws -> Bool {
        let startOfDay = date.startOfDay
        let descriptor = FetchDescriptor<DailyMetric>(
            predicate: #Predicate { $0.date == startOfDay && $0.isComputed == true }
        )
        let results = try context.fetch(descriptor)
        return !results.isEmpty
    }
}
