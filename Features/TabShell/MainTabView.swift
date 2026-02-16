import SwiftUI

struct MainTabView: View {
    @State private var selectedTab: AppTab = .home

    var body: some View {
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
    }
}

enum AppTab: String {
    case home, longevity, community, myPlan, coach
}
