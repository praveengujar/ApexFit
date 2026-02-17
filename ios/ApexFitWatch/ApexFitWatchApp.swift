import SwiftUI
import SwiftData

@main
struct ApexFitWatchApp: App {
    @State private var connectivityManager = WatchConnectivityManager.shared
    @State private var workoutManager = WatchWorkoutManager()

    var body: some Scene {
        WindowGroup {
            GlanceDashboardView()
                .environment(connectivityManager)
                .environment(workoutManager)
                .onAppear {
                    connectivityManager.activate()
                }
        }
        .modelContainer(for: [
            DailyMetric.self,
            WorkoutRecord.self,
            SleepSession.self,
            UserProfile.self,
            JournalEntry.self
        ])
    }
}
