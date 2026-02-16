import SwiftUI

struct MyPlanTabPlaceholder: View {
    var body: some View {
        NavigationStack {
            VStack(spacing: AppTheme.spacingLG) {
                Image(systemName: "calendar")
                    .font(.system(size: 64))
                    .foregroundStyle(AppColors.primaryBlue)

                Text("My Plan")
                    .font(AppTypography.heading1)
                    .foregroundStyle(AppColors.textPrimary)

                Text("Personalized training and recovery plans. Coming soon.")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, AppTheme.spacingXL)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(AppColors.backgroundPrimary)
            .navigationTitle("My Plan")
        }
    }
}
