import SwiftUI

struct NotableStatsView: View {
    let lowestRHR: Double?
    let highestRHR: Double?
    let lowestHRV: Double?
    let highestHRV: Double?
    let maxHeartRate: Double?
    let longestSleepHours: Double?
    let lowestRecovery: Double?

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            SectionDividerLabel(title: "NOTABLE STATS")

            VStack(spacing: AppTheme.spacingSM) {
                StatRow(
                    icon: "heart.fill",
                    iconColor: AppColors.recoveryRed,
                    label: "Lowest RHR",
                    value: lowestRHR.map { "\(Int($0))" } ?? "--",
                    unit: "BPM"
                )

                StatRow(
                    icon: "heart.fill",
                    iconColor: AppColors.recoveryRed,
                    label: "Highest RHR",
                    value: highestRHR.map { "\(Int($0))" } ?? "--",
                    unit: "BPM"
                )

                StatRow(
                    icon: "waveform.path.ecg",
                    iconColor: AppColors.primaryBlue,
                    label: "Lowest HRV",
                    value: lowestHRV.map { "\(Int($0))" } ?? "--",
                    unit: "ms"
                )

                StatRow(
                    icon: "waveform.path.ecg",
                    iconColor: AppColors.primaryBlue,
                    label: "Highest HRV",
                    value: highestHRV.map { "\(Int($0))" } ?? "--",
                    unit: "ms"
                )

                StatRow(
                    icon: "bolt.heart.fill",
                    iconColor: AppColors.zone5,
                    label: "Max Heart Rate",
                    value: maxHeartRate.map { "\(Int($0))" } ?? "--",
                    unit: "BPM"
                )

                StatRow(
                    icon: "moon.zzz.fill",
                    iconColor: AppColors.sleepDeep,
                    label: "Longest Sleep",
                    value: longestSleepHours.map { $0.formattedOneDecimal } ?? "--",
                    unit: "hrs"
                )

                StatRow(
                    icon: "arrow.down.circle.fill",
                    iconColor: AppColors.recoveryRed,
                    label: "Lowest Recovery",
                    value: lowestRecovery.map { "\(Int($0))" } ?? "--",
                    unit: "%"
                )
            }
        }
    }
}

// MARK: - Stat Row

private struct StatRow: View {
    let icon: String
    let iconColor: Color
    let label: String
    let value: String
    let unit: String

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 15, weight: .medium))
                .foregroundStyle(iconColor)
                .frame(width: 30, height: 30)
                .background(AppColors.backgroundTertiary)
                .clipShape(Circle())

            Text(label)
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            HStack(spacing: AppTheme.spacingXS) {
                Text(value)
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.textPrimary)

                Text(unit)
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }
}
