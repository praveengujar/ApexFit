import SwiftUI

struct CoachTabPlaceholder: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: AppTheme.spacingLG) {
                Image(systemName: "brain.head.profile")
                    .font(.system(size: 64))
                    .foregroundStyle(AppColors.teal)

                Text("AI Coach")
                    .font(AppTypography.heading1)
                    .foregroundStyle(AppColors.textPrimary)

                Text("Your personal health coach powered by AI. Coming soon.")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, AppTheme.spacingXL)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Coach")
        }
    }
}
