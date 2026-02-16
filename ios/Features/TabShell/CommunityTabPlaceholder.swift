import SwiftUI

struct CommunityTabPlaceholder: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: AppTheme.spacingLG) {
                Image(systemName: "person.3.fill")
                    .font(.system(size: 64))
                    .foregroundStyle(AppColors.recoveryGreen)

                Text("Community")
                    .font(AppTypography.heading1)
                    .foregroundStyle(AppColors.textPrimary)

                Text("Teams, leaderboards, and social challenges. Coming soon.")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, AppTheme.spacingXL)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Community")
        }
    }
}
