import Foundation
import SwiftData

actor BackgroundSyncCoordinator {
    static let shared = BackgroundSyncCoordinator()

    private var isRunning = false
    private var modelContainer: ModelContainer?

    func configure(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
    }

    func performQuickSync() async throws {
        guard !isRunning else { return }
        guard let modelContainer else { return }
        isRunning = true
        defer { isRunning = false }

        let context = ModelContext(modelContainer)
        let service = MetricComputationService(modelContext: context)
        try await service.quickStrainUpdate(for: Date())
    }

    func performFullSync() async throws {
        guard !isRunning else { return }
        guard let modelContainer else { return }
        isRunning = true
        defer { isRunning = false }

        let context = ModelContext(modelContainer)
        let service = MetricComputationService(modelContext: context)
        try await service.computeAllMetrics(for: Date())
    }
}
