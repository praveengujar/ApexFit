import SwiftUI
import SwiftData
import Charts

struct RecoveryTrendView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    @State private var selectedPeriod: TrendPeriod = .month

    private var filteredMetrics: [DailyMetric] {
        let cutoff = Date().daysAgo(selectedPeriod.days)
        return allMetrics
            .filter { $0.date >= cutoff && $0.recoveryScore != nil }
            .sorted { $0.date < $1.date }
    }

    private var averageRecovery: Double {
        let scores = filteredMetrics.compactMap { $0.recoveryScore }
        guard !scores.isEmpty else { return 0 }
        return scores.reduce(0, +) / Double(scores.count)
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
        .navigationTitle("Recovery Trend")
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
            Text("Recovery Score")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Chart {
                // Green zone band (67-99)
                RectangleMark(
                    xStart: nil,
                    xEnd: nil,
                    yStart: .value("Green Lower", 67),
                    yEnd: .value("Green Upper", 99)
                )
                .foregroundStyle(AppColors.recoveryGreen.opacity(0.08))

                // Yellow zone band (34-67)
                RectangleMark(
                    xStart: nil,
                    xEnd: nil,
                    yStart: .value("Yellow Lower", 34),
                    yEnd: .value("Yellow Upper", 67)
                )
                .foregroundStyle(AppColors.recoveryYellow.opacity(0.08))

                // Red zone band (0-34)
                RectangleMark(
                    xStart: nil,
                    xEnd: nil,
                    yStart: .value("Red Lower", 0),
                    yEnd: .value("Red Upper", 34)
                )
                .foregroundStyle(AppColors.recoveryRed.opacity(0.08))

                // Average line
                if averageRecovery > 0 {
                    RuleMark(y: .value("Average", averageRecovery))
                        .foregroundStyle(AppColors.textTertiary)
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [5, 3]))
                        .annotation(position: .top, alignment: .trailing) {
                            Text("Avg \(averageRecovery.formattedNoDecimal)%")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }
                }

                // Recovery line
                ForEach(filteredMetrics, id: \.id) { metric in
                    if let score = metric.recoveryScore {
                        LineMark(
                            x: .value("Date", metric.date),
                            y: .value("Recovery", score)
                        )
                        .foregroundStyle(AppColors.recoveryGreen)
                        .lineStyle(StrokeStyle(lineWidth: 2))

                        PointMark(
                            x: .value("Date", metric.date),
                            y: .value("Recovery", score)
                        )
                        .foregroundStyle(AppColors.recoveryColor(for: score))
                        .symbolSize(20)
                    }
                }
            }
            .chartYScale(domain: 0...100)
            .chartYAxis {
                AxisMarks(position: .leading, values: [0, 34, 67, 100]) { value in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                        .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                    AxisValueLabel {
                        if let intVal = value.as(Int.self) {
                            Text("\(intVal)")
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
                    title: "Average",
                    value: averageRecovery > 0 ? averageRecovery.formattedNoDecimal + "%" : "--",
                    icon: "chart.bar.fill",
                    color: AppColors.recoveryColor(for: averageRecovery)
                )
                MetricTile(
                    title: "Data Points",
                    value: "\(filteredMetrics.count)",
                    icon: "number",
                    color: AppColors.primaryBlue
                )
                MetricTile(
                    title: "Best",
                    value: bestScore,
                    icon: "arrow.up",
                    color: AppColors.recoveryGreen
                )
                MetricTile(
                    title: "Lowest",
                    value: worstScore,
                    icon: "arrow.down",
                    color: AppColors.recoveryRed
                )
            }
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

    private var bestScore: String {
        let scores = filteredMetrics.compactMap { $0.recoveryScore }
        guard let best = scores.max() else { return "--" }
        return best.formattedNoDecimal + "%"
    }

    private var worstScore: String {
        let scores = filteredMetrics.compactMap { $0.recoveryScore }
        guard let worst = scores.min() else { return "--" }
        return worst.formattedNoDecimal + "%"
    }
}

#Preview {
    NavigationStack {
        RecoveryTrendView()
    }
    .modelContainer(for: DailyMetric.self, inMemory: true)
}
