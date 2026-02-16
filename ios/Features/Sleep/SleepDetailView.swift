import SwiftUI
import SwiftData

struct SleepDetailView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    private var todayMetric: DailyMetric? {
        metrics.first { $0.date.isToday } ?? metrics.first { $0.date.isYesterday }
    }

    private var latestSleep: SleepSession? {
        todayMetric?.sleepSessions
            .filter { $0.isMainSleep }
            .sorted { $0.startDate > $1.startDate }
            .first
    }

    private var sleepPerformance: Double {
        todayMetric?.sleepPerformance ?? latestSleep?.sleepPerformance ?? 0
    }

    private var sleepDurationHours: Double {
        todayMetric?.sleepDurationHours ?? latestSleep.map { $0.totalSleepMinutes / 60.0 } ?? 0
    }

    private var sleepNeedHours: Double {
        todayMetric?.sleepNeedHours ?? HealthKitConstants.defaultSleepBaselineHours
    }

    private var sleepEfficiency: Double {
        latestSleep?.sleepEfficiency ?? 0
    }

    private var performanceColor: Color {
        switch sleepPerformance {
        case 85...100: return AppColors.recoveryGreen
        case 70..<85: return AppColors.recoveryYellow
        default: return AppColors.recoveryRed
        }
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    gaugeSection
                    durationSection
                    sleepStagesSection
                    sleepMetricsSection
                    linksSection
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Sleep")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Gauge

    private var gaugeSection: some View {
        VStack(spacing: AppTheme.spacingMD) {
            CircularGaugeView(
                value: sleepPerformance,
                maxValue: 100,
                label: "Sleep",
                unit: "%",
                color: performanceColor,
                size: .large
            )

            Text("Sleep Performance")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)

            if let sleep = latestSleep {
                Text("\(sleep.startDate.hourMinuteString) - \(sleep.endDate.hourMinuteString)")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingMD)
    }

    // MARK: - Duration vs Need

    private var durationSection: some View {
        HStack(spacing: AppTheme.spacingLG) {
            VStack(spacing: AppTheme.spacingXS) {
                Text(sleepDurationHours.formattedHoursMinutes)
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.sleepDeep)
                Text("Actual Sleep")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Rectangle()
                .fill(AppColors.backgroundTertiary)
                .frame(width: 1, height: 40)

            VStack(spacing: AppTheme.spacingXS) {
                Text(sleepNeedHours.formattedHoursMinutes)
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.textPrimary)
                Text("Sleep Need")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Rectangle()
                .fill(AppColors.backgroundTertiary)
                .frame(width: 1, height: 40)

            VStack(spacing: AppTheme.spacingXS) {
                Text(sleepEfficiency > 0 ? sleepEfficiency.formattedPercentage : "--")
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(sleepEfficiency >= 85 ? AppColors.recoveryGreen : AppColors.recoveryYellow)
                Text("Efficiency")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }

    // MARK: - Sleep Stages

    private var sleepStagesSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Sleep Stages")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if let sleep = latestSleep {
                SleepStageChartView(session: sleep)

                if !sleep.stages.isEmpty {
                    HypnogramView(stages: sleep.stages.sorted { $0.startDate < $1.startDate })
                }
            } else {
                noDataCard(message: "No sleep stage data available")
            }
        }
    }

    // MARK: - Sleep Metrics Grid

    private var sleepMetricsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Sleep Metrics")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingSM) {
                MetricTile(
                    title: "Deep Sleep",
                    value: latestSleep.map { "\(Int($0.deepSleepMinutes))m (\($0.deepSleepPercentage.formattedNoDecimal)%)" } ?? "--",
                    icon: "moon.zzz.fill",
                    color: AppColors.sleepDeep
                )
                MetricTile(
                    title: "REM Sleep",
                    value: latestSleep.map { "\(Int($0.remSleepMinutes))m (\($0.remSleepPercentage.formattedNoDecimal)%)" } ?? "--",
                    icon: "brain.head.profile",
                    color: AppColors.sleepREM
                )
                MetricTile(
                    title: "Light Sleep",
                    value: latestSleep.map { "\(Int($0.lightSleepMinutes))m (\($0.lightSleepPercentage.formattedNoDecimal)%)" } ?? "--",
                    icon: "moon.fill",
                    color: AppColors.sleepLight
                )
                MetricTile(
                    title: "Awakenings",
                    value: latestSleep.map { "\($0.awakenings)" } ?? "--",
                    icon: "eye.fill",
                    color: AppColors.sleepAwake
                )
                MetricTile(
                    title: "Time in Bed",
                    value: latestSleep.map { sleep in (sleep.timeInBedMinutes / 60.0).formattedHoursMinutes } ?? "--",
                    icon: "bed.double.fill",
                    color: AppColors.lavender
                )
                MetricTile(
                    title: "Sleep Debt",
                    value: todayMetric?.sleepDebtHours.map { $0.formattedHoursMinutes } ?? "--",
                    icon: "exclamationmark.triangle.fill",
                    color: AppColors.recoveryYellow
                )
            }
        }
    }

    // MARK: - Links

    private var linksSection: some View {
        VStack(spacing: AppTheme.spacingSM) {
            NavigationLink(destination: SleepPlannerView()) {
                linkRow(icon: "clock.badge.checkmark", title: "Sleep Planner", subtitle: "Plan your bedtime for optimal recovery")
            }
            .buttonStyle(.plain)

            NavigationLink(destination: SleepTrendView()) {
                linkRow(icon: "chart.line.uptrend.xyaxis", title: "Sleep Trend", subtitle: "View your sleep over time")
            }
            .buttonStyle(.plain)
        }
    }

    // MARK: - Helpers

    private func linkRow(icon: String, title: String, subtitle: String) -> some View {
        HStack {
            Image(systemName: icon)
                .font(.title3)
                .foregroundStyle(AppColors.sleepDeep)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)
                Text(subtitle)
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

    private func noDataCard(message: String) -> some View {
        HStack {
            Spacer()
            VStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "moon.zzz")
                    .font(.largeTitle)
                    .foregroundStyle(AppColors.textTertiary)
                Text(message)
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
            }
            .padding(.vertical, AppTheme.spacingLG)
            Spacer()
        }
        .cardStyle()
    }
}

#Preview {
    SleepDetailView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
