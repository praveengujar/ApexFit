import SwiftUI
import SwiftData

@main
struct ApexFitApp: App {
    let modelContainer: ModelContainer
    let healthKitManager = HealthKitManager.shared
    let dataLoadingCoordinator = DataLoadingCoordinator()

    init() {
        do {
            modelContainer = try SwiftDataContainer.create()
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }

        BackgroundTaskManager.shared.configure(modelContainer: modelContainer)
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(healthKitManager)
                .environment(dataLoadingCoordinator)
                .preferredColorScheme(.dark)
        }
        .modelContainer(modelContainer)
    }
}

struct RootView: View {
    @Environment(HealthKitManager.self) private var healthKitManager
    @Query private var profiles: [UserProfile]

    private var hasCompletedOnboarding: Bool {
        profiles.first?.hasCompletedOnboarding ?? false
    }

    var body: some View {
        if hasCompletedOnboarding {
            MainTabView()
        } else {
            OnboardingFlowView()
        }
    }
}
