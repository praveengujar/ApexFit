import SwiftUI

struct ProfileHeaderView: View {
    let displayName: String
    let initials: String
    let age: Int?
    let memberSince: String

    var body: some View {
        VStack(spacing: AppTheme.spacingSM) {
            // Avatar circle
            ZStack {
                Circle()
                    .fill(AppColors.backgroundTertiary)
                    .frame(width: 80, height: 80)

                Text(initials)
                    .font(.system(size: 28, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
            }

            // Name
            Text(displayName)
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)

            // Subtitle
            HStack(spacing: AppTheme.spacingXS) {
                if let age {
                    Text("Age \(age)")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
            }

            // Edit button
            Button(action: {}) {
                HStack(spacing: AppTheme.spacingXS) {
                    Image(systemName: "pencil")
                        .font(.system(size: 12))
                    Text("EDIT")
                        .font(AppTypography.labelSmall)
                }
                .foregroundStyle(AppColors.primaryBlue)
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.vertical, AppTheme.spacingSM)
                .background(AppColors.backgroundTertiary)
                .clipShape(Capsule())
            }

            // Member since
            HStack(spacing: AppTheme.spacingXS) {
                Text("\u{1F451}")
                    .font(.system(size: 12))
                Text("Member since \(memberSince)")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textSecondary)
            }
            .padding(.top, AppTheme.spacingXS)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingMD)
    }
}
