import SwiftUI
import SwiftData

struct HealthTabView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingMD) {
                    journalCard
                    sleepPlannerCard
                    healthMetricsCard
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Health")
        }
    }

    private var journalCard: some View {
        NavigationLink(destination: Text("Journal")) {
            HStack {
                VStack(alignment: .leading, spacing: AppTheme.spacingXS) {
                    Label("Journal", systemImage: "book.fill")
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.teal)
                    Text("Track behaviors that impact your recovery")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundStyle(AppColors.textTertiary)
            }
            .cardStyle()
        }
    }

    private var sleepPlannerCard: some View {
        NavigationLink(destination: Text("Sleep Planner")) {
            HStack {
                VStack(alignment: .leading, spacing: AppTheme.spacingXS) {
                    Label("Sleep Planner", systemImage: "moon.fill")
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.sleepDeep)
                    Text("Get your recommended bedtime")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundStyle(AppColors.textTertiary)
            }
            .cardStyle()
        }
    }

    private var healthMetricsCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            Text("Health Metrics")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if let latest = metrics.first {
                LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingSM) {
                    MetricTile(title: "Resting HR", value: latest.restingHeartRate?.bpmFormatted ?? "--", icon: "heart.fill", color: AppColors.recoveryRed)
                    MetricTile(title: "HRV", value: latest.hrvRMSSD?.msFormatted ?? "--", icon: "waveform.path.ecg", color: AppColors.recoveryGreen)
                    MetricTile(title: "SpO2", value: latest.spo2 != nil ? "\(Int(latest.spo2!))%" : "--", icon: "lungs.fill", color: AppColors.primaryBlue)
                    MetricTile(title: "Steps", value: latest.steps.formattedWithComma, icon: "figure.walk", color: AppColors.teal)
                }
            } else {
                Text("No health data available yet")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, AppTheme.spacingLG)
            }
        }
        .cardStyle()
    }
}
