import SwiftUI

struct DayStreakView: View {
    let dayStreak: Int

    var body: some View {
        HStack(spacing: AppTheme.spacingSM) {
            Text("\u{1F525}")
                .font(.system(size: 24))

            VStack(alignment: .leading, spacing: 2) {
                Text("DAY STREAK")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(AppColors.textSecondary)
                    .tracking(1)

                Text("\(dayStreak)")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(AppColors.textTertiary)
        }
        .cardStyle()
    }
}
