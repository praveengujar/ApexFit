import SwiftUI
import SwiftData

struct RecoveryDetailView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    private var todayMetric: DailyMetric? {
        metrics.first { $0.date.isToday }
    }

    private var recoveryScore: Double {
        todayMetric?.recoveryScore ?? 0
    }

    private var recoveryZone: RecoveryZone {
        todayMetric?.recoveryZone ?? RecoveryZone.from(score: recoveryScore)
    }

    private var zoneColor: Color {
        AppColors.recoveryColor(for: recoveryZone)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    gaugeSection
                    contributorsSection
                    insightSection
                    trendLink
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Recovery")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Gauge Section

    private var gaugeSection: some View {
        VStack(spacing: AppTheme.spacingMD) {
            CircularGaugeView(
                value: recoveryScore,
                maxValue: 99,
                label: "Recovery",
                unit: "%",
                color: zoneColor,
                size: .large
            )

            HStack(spacing: AppTheme.spacingSM) {
                Circle()
                    .fill(zoneColor)
                    .frame(width: 10, height: 10)

                Text(recoveryZone.label)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(zoneColor)
            }

            Text(Date().formatted(.dateTime.weekday(.wide).month(.abbreviated).day()))
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingMD)
    }

    // MARK: - Contributors Section

    private var contributorsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Contributors")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            RecoveryContributorsView(metric: todayMetric)
        }
    }

    // MARK: - Insight Section

    private var insightSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Recovery Insight")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            RecoveryInsightView(metric: todayMetric)
        }
    }

    // MARK: - Trend Link

    private var trendLink: some View {
        NavigationLink(destination: RecoveryTrendView()) {
            HStack {
                Image(systemName: "chart.line.uptrend.xyaxis")
                    .font(.title3)
                    .foregroundStyle(AppColors.primaryBlue)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Recovery Trend")
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.textPrimary)
                    Text("View your recovery over time")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    RecoveryDetailView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
