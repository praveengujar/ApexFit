import SwiftUI

struct DailyOutlookCard: View {
    var onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: AppTheme.spacingMD) {
                Image(systemName: "sun.max.fill")
                    .font(.system(size: 20))
                    .foregroundStyle(AppColors.textSecondary)

                Text("Your Daily Outlook")
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(AppColors.textSecondary)
            }
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Your Daily Outlook")
        .accessibilityHint("Tap to view daily outlook details")
        .accessibilityAddTraits(.isButton)
    }
}
