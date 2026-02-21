import BackgroundTasks
import Foundation
import SwiftData

final class BackgroundTaskManager {
    static let shared = BackgroundTaskManager()

    private var modelContainer: ModelContainer?

    private init() {}

    func configure(modelContainer: ModelContainer) {
        self.modelContainer = modelContainer
        Task {
            await BackgroundSyncCoordinator.shared.configure(modelContainer: modelContainer)
        }
    }

    func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: HealthKitConstants.backgroundRefreshTaskID,
            using: nil
        ) { task in
            guard let refreshTask = task as? BGAppRefreshTask else { return }
            self.handleAppRefresh(task: refreshTask)
        }

        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: HealthKitConstants.backgroundProcessingTaskID,
            using: nil
        ) { task in
            guard let processingTask = task as? BGProcessingTask else { return }
            self.handleProcessing(task: processingTask)
        }
    }

    func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: HealthKitConstants.backgroundRefreshTaskID)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 minutes
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Failed to schedule app refresh: \(error)")
        }
    }

    func scheduleProcessingTask() {
        let request = BGProcessingTaskRequest(identifier: HealthKitConstants.backgroundProcessingTaskID)
        request.requiresNetworkConnectivity = false
        request.requiresExternalPower = true
        request.earliestBeginDate = Date(timeIntervalSinceNow: 60 * 60) // 1 hour
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Failed to schedule processing task: \(error)")
        }
    }

    private func handleAppRefresh(task: BGAppRefreshTask) {
        scheduleAppRefresh()

        let syncTask = Task {
            do {
                try await BackgroundSyncCoordinator.shared.performQuickSync()
            } catch {
                print("Background refresh failed: \(error)")
            }
        }

        task.expirationHandler = {
            syncTask.cancel()
        }

        Task {
            _ = await syncTask.result
            task.setTaskCompleted(success: true)
        }
    }

    private func handleProcessing(task: BGProcessingTask) {
        scheduleProcessingTask()

        let processingTask = Task {
            do {
                try await BackgroundSyncCoordinator.shared.performFullSync()
            } catch {
                print("Background processing failed: \(error)")
            }
        }

        task.expirationHandler = {
            processingTask.cancel()
        }

        Task {
            _ = await processingTask.result
            task.setTaskCompleted(success: true)
        }
    }
}
