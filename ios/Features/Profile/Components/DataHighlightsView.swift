import SwiftUI

struct DataHighlightsView: View {
    @Binding var period: ProfileTimePeriod
    let bestSleepPct: Double
    let peakRecoveryPct: Double
    let maxStrain: Double

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Data Highlights")
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)

            VStack(spacing: AppTheme.spacingMD) {
                ProfileTimePeriodPicker(selected: $period)

                HStack(spacing: 0) {
                    CircularGaugeView(
                        value: bestSleepPct,
                        maxValue: 100,
                        label: "SLEEP",
                        unit: "%",
                        color: AppColors.sleepDeep,
                        size: .small
                    )
                    .frame(maxWidth: .infinity)

                    CircularGaugeView(
                        value: peakRecoveryPct,
                        maxValue: 99,
                        label: "RECOVERY",
                        unit: "%",
                        color: AppColors.recoveryGreen,
                        size: .small
                    )
                    .frame(maxWidth: .infinity)

                    CircularGaugeView(
                        value: maxStrain,
                        maxValue: 21,
                        label: "STRAIN",
                        unit: "",
                        color: AppColors.primaryBlue,
                        size: .small
                    )
                    .frame(maxWidth: .infinity)
                }

                HStack(spacing: 0) {
                    Text("Best Sleep")
                        .frame(maxWidth: .infinity)
                    Text("Peak Recovery")
                        .frame(maxWidth: .infinity)
                    Text("Max Strain")
                        .frame(maxWidth: .infinity)
                }
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
            }
            .cardStyle()
        }
    }
}
