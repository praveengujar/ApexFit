import SwiftUI
import SwiftData

struct MainTabView: View {
    @Environment(HealthKitManager.self) private var healthKitManager
    @Environment(DataLoadingCoordinator.self) private var dataLoadingCoordinator
    @Environment(\.modelContext) private var modelContext
    @State private var selectedTab: AppTab = .home

    var body: some View {
        ZStack {
            TabView(selection: $selectedTab) {
                HomeView()
                    .tabItem {
                        Label("Home", systemImage: "house.fill")
                    }
                    .tag(AppTab.home)

                LongevityDashboardView()
                    .tabItem {
                        Label("Longevity", systemImage: "heart.fill")
                    }
                    .tag(AppTab.longevity)

                CommunityTabPlaceholder()
                    .tabItem {
                        Label("Community", systemImage: "person.2.fill")
                    }
                    .tag(AppTab.community)

                MyPlanTabPlaceholder()
                    .tabItem {
                        Label("My Plan", systemImage: "calendar")
                    }
                    .tag(AppTab.myPlan)

                CoachTabPlaceholder()
                    .tabItem {
                        Label("Coach", systemImage: "brain.head.profile")
                    }
                    .tag(AppTab.coach)
            }
            .tint(AppColors.primaryBlue)

            // Loading overlay
            if case .loading(let progress) = dataLoadingCoordinator.state {
                VStack(spacing: AppTheme.spacingMD) {
                    ProgressView()
                        .controlSize(.large)
                        .tint(AppColors.primaryBlue)
                    Text(progress)
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(.ultraThinMaterial)
                .ignoresSafeArea()
            }
        }
        .task {
            await dataLoadingCoordinator.loadDataIfNeeded(
                modelContainer: modelContext.container,
                isAuthorized: healthKitManager.isAuthorized
            )
        }
    }
}

enum AppTab: String {
    case home, longevity, community, myPlan, coach
}
