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

    func loadDataIfNeeded(modelContainer: ModelContainer, isAuthorized: Bool) async {
        guard isAuthorized else { return }
        guard !hasLoadedThisSession else {
            // Already loaded this session — do a quick refresh for today only
            await quickRefresh(modelContainer: modelContainer)
            return
        }

        state = .loading(progress: "Loading health data...")
        hasLoadedThisSession = true

        do {
            let context = ModelContext(modelContainer)
            let service = MetricComputationService(modelContext: context)
            let today = Date()

            // Backfill recent days first (oldest to newest) so baselines build up
            for dayOffset in stride(from: 7, through: 1, by: -1) {
                let date = today.daysAgo(dayOffset)
                if try await isAlreadyComputed(date: date, context: context) {
                    continue
                }
                state = .loading(progress: "Processing \(date.shortDateString)...")
                try await service.computeAllMetrics(for: date)
            }

            // Compute today (always recompute for freshness)
            state = .loading(progress: "Processing today...")
            try await service.computeAllMetrics(for: today)

            state = .complete
        } catch {
            print("DataLoadingCoordinator error: \(error)")
            state = .error(error.localizedDescription)
        }
    }

    private func quickRefresh(modelContainer: ModelContainer) async {
        do {
            let context = ModelContext(modelContainer)
            let service = MetricComputationService(modelContext: context)
            try await service.quickStrainUpdate(for: Date())
        } catch {
            print("Quick refresh error: \(error)")
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
