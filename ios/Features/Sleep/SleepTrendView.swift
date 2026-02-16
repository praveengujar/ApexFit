import SwiftUI
import SwiftData
import Charts

struct SleepTrendView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    @State private var selectedPeriod: TrendPeriod = .month

    private var filteredMetrics: [DailyMetric] {
        let cutoff = Date().daysAgo(selectedPeriod.days)
        return allMetrics
            .filter { $0.date >= cutoff && $0.sleepDurationHours != nil }
            .sorted { $0.date < $1.date }
    }

    private var averageSleepHours: Double {
        let durations = filteredMetrics.compactMap { $0.sleepDurationHours }
        guard !durations.isEmpty else { return 0 }
        return durations.reduce(0, +) / Double(durations.count)
    }

    private var averageSleepNeed: Double {
        let needs = filteredMetrics.compactMap { $0.sleepNeedHours }
        guard !needs.isEmpty else { return HealthKitConstants.defaultSleepBaselineHours }
        return needs.reduce(0, +) / Double(needs.count)
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingLG) {
                periodPicker
                chartSection
                summarySection
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Sleep Trend")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - Period Picker

    private var periodPicker: some View {
        Picker("Period", selection: $selectedPeriod) {
            ForEach(TrendPeriod.allCases) { period in
                Text(period.label).tag(period)
            }
        }
        .pickerStyle(.segmented)
    }

    // MARK: - Chart

    private var chartSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Sleep Duration")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Chart {
                // Sleep need overlay line
                if averageSleepNeed > 0 {
                    RuleMark(y: .value("Sleep Need", averageSleepNeed))
                        .foregroundStyle(AppColors.textTertiary)
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [5, 3]))
                        .annotation(position: .top, alignment: .trailing) {
                            Text("Need \(averageSleepNeed.formattedOneDecimal)h")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }
                }

                // Sleep duration bars
                ForEach(filteredMetrics, id: \.id) { metric in
                    if let duration = metric.sleepDurationHours {
                        BarMark(
                            x: .value("Date", metric.date, unit: .day),
                            y: .value("Hours", duration)
                        )
                        .foregroundStyle(sleepBarColor(performance: metric.sleepPerformance))
                        .cornerRadius(3)
                    }
                }
            }
            .chartYScale(domain: 0...12)
            .chartYAxis {
                AxisMarks(position: .leading, values: [0, 3, 6, 9, 12]) { value in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                        .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                    AxisValueLabel {
                        if let hrs = value.as(Double.self) {
                            Text("\(Int(hrs))h")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }
                    }
                }
            }
            .chartXAxis {
                AxisMarks(values: .stride(by: .day, count: xAxisStride)) { value in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                        .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                    AxisValueLabel {
                        if let date = value.as(Date.self) {
                            Text(date.dayOfWeek)
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }
                    }
                }
            }
            .frame(height: 260)
            .padding(.vertical, AppTheme.spacingSM)
        }
        .cardStyle()
    }

    // MARK: - Summary

    private var summarySection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Summary")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingSM) {
                MetricTile(
                    title: "Avg Duration",
                    value: averageSleepHours > 0 ? averageSleepHours.formattedHoursMinutes : "--",
                    icon: "moon.fill",
                    color: AppColors.sleepDeep
                )
                MetricTile(
                    title: "Avg Performance",
                    value: averagePerformance,
                    icon: "chart.bar.fill",
                    color: performanceColor
                )
                MetricTile(
                    title: "Best Night",
                    value: bestNight,
                    icon: "arrow.up",
                    color: AppColors.recoveryGreen
                )
                MetricTile(
                    title: "Worst Night",
                    value: worstNight,
                    icon: "arrow.down",
                    color: AppColors.recoveryRed
                )
            }

            // Color legend
            HStack(spacing: AppTheme.spacingMD) {
                legendItem(color: AppColors.recoveryGreen, label: ">= 85%")
                legendItem(color: AppColors.recoveryYellow, label: "70-85%")
                legendItem(color: AppColors.recoveryRed, label: "< 70%")
            }
            .frame(maxWidth: .infinity)
            .padding(.top, AppTheme.spacingXS)
        }
    }

    // MARK: - Helpers

    private var xAxisStride: Int {
        switch selectedPeriod {
        case .week: return 1
        case .month: return 5
        case .quarter: return 14
        }
    }

    private func sleepBarColor(performance: Double?) -> Color {
        guard let perf = performance else { return AppColors.sleepLight }
        switch perf {
        case 85...100: return AppColors.recoveryGreen
        case 70..<85: return AppColors.recoveryYellow
        default: return AppColors.recoveryRed
        }
    }

    private var averagePerformance: String {
        let perfs = filteredMetrics.compactMap { $0.sleepPerformance }
        guard !perfs.isEmpty else { return "--" }
        let avg = perfs.reduce(0, +) / Double(perfs.count)
        return avg.formattedPercentage
    }

    private var performanceColor: Color {
        let perfs = filteredMetrics.compactMap { $0.sleepPerformance }
        guard !perfs.isEmpty else { return AppColors.textTertiary }
        let avg = perfs.reduce(0, +) / Double(perfs.count)
        return sleepBarColor(performance: avg)
    }

    private var bestNight: String {
        let durations = filteredMetrics.compactMap { $0.sleepDurationHours }
        guard let best = durations.max() else { return "--" }
        return best.formattedHoursMinutes
    }

    private var worstNight: String {
        let durations = filteredMetrics.compactMap { $0.sleepDurationHours }
        guard let worst = durations.min() else { return "--" }
        return worst.formattedHoursMinutes
    }

    private func legendItem(color: Color, label: String) -> some View {
        HStack(spacing: AppTheme.spacingXS) {
            Circle()
                .fill(color)
                .frame(width: 8, height: 8)
            Text(label)
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textTertiary)
        }
    }
}

#Preview {
    NavigationStack {
        SleepTrendView()
    }
    .modelContainer(for: DailyMetric.self, inMemory: true)
}
