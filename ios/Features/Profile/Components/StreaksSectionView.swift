import SwiftUI

struct StreaksSectionView: View {
    let sleepStreak: Int
    let greenRecoveryStreak: Int
    let strainStreak: Int

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            SectionDividerLabel(title: "STREAKS")

            HStack(spacing: AppTheme.spacingSM) {
                StreakItem(
                    emoji: "\u{1F319}",
                    count: sleepStreak,
                    label: "70%+ Sleep",
                    accentColor: AppColors.sleepDeep
                )

                StreakItem(
                    emoji: "\u{2728}",
                    count: greenRecoveryStreak,
                    label: "Green Recovery",
                    accentColor: AppColors.recoveryGreen
                )

                StreakItem(
                    emoji: "\u{1F4AA}",
                    count: strainStreak,
                    label: "10+ Strain",
                    accentColor: AppColors.primaryBlue
                )
            }
        }
    }
}

// MARK: - Streak Item

private struct StreakItem: View {
    let emoji: String
    let count: Int
    let label: String
    let accentColor: Color

    var body: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Text(emoji)
                .font(.system(size: 24))

            Text("\(count) Days")
                .font(.system(size: 15, weight: .bold))
                .foregroundStyle(AppColors.textPrimary)

            Text(label)
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }
}

// MARK: - Section Divider Label

struct SectionDividerLabel: View {
    let title: String

    var body: some View {
        Text(title)
            .font(.system(size: 11, weight: .bold))
            .foregroundStyle(AppColors.textTertiary)
            .tracking(1.5)
    }
}
