import SwiftUI

struct ActivitySummaryView: View {
    @Binding var period: ProfileTimePeriod
    let totalActivities: Int
    let breakdown: [ProfileActivityTypeStats]

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Activity Summary")
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)

            VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
                ProfileTimePeriodPicker(selected: $period)

                // Total count
                VStack(alignment: .leading, spacing: 2) {
                    Text("\(totalActivities)x")
                        .font(.system(size: 32, weight: .bold))
                        .foregroundStyle(AppColors.textPrimary)

                    Text("TOTAL ACTIVITIES")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(AppColors.textSecondary)
                        .tracking(1)
                }

                // Header
                HStack {
                    Text("ACTIVITY | AVG. STRAIN")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(AppColors.textTertiary)
                        .tracking(1)

                    Spacer()

                    Text("TOTAL")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(AppColors.textTertiary)
                        .tracking(1)
                }

                // Activity rows
                ForEach(Array(breakdown.prefix(5).enumerated()), id: \.element.id) { index, activity in
                    ActivityRow(activity: activity)

                    if index < min(breakdown.count, 5) - 1 {
                        Divider()
                            .background(AppColors.textTertiary.opacity(0.15))
                    }
                }
            }
            .cardStyle()
        }
    }
}

// MARK: - Activity Row

private struct ActivityRow: View {
    let activity: ProfileActivityTypeStats

    var body: some View {
        VStack(spacing: AppTheme.spacingSM) {
            HStack {
                Text(activity.displayName)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)

                Text(String(format: "%.1f", activity.avgStrain))
                    .font(.system(size: 13))
                    .foregroundStyle(AppColors.textSecondary)

                Spacer()

                Text("\(activity.count)x")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(AppColors.primaryBlue)
            }

            // Proportional bar
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 3)
                        .fill(AppColors.backgroundTertiary)
                        .frame(height: 6)

                    RoundedRectangle(cornerRadius: 3)
                        .fill(AppColors.primaryBlue)
                        .frame(width: geo.size.width * activity.proportion, height: 6)
                }
            }
            .frame(height: 6)
        }
    }
}
