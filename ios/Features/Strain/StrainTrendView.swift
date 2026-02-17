import SwiftUI
import SwiftData
import Charts

struct StrainTrendView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    @State private var selectedPeriod: TrendPeriod = .month

    private var filteredMetrics: [DailyMetric] {
        let cutoff = Date().daysAgo(selectedPeriod.days)
        return allMetrics
            .filter { $0.date >= cutoff }
            .sorted { $0.date < $1.date }
    }

    private var averageStrain: Double {
        let scores = filteredMetrics.map { $0.strainScore }
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
        .navigationTitle("Strain Trend")
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
            Text("Daily Strain")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Chart {
                // Average line
                if averageStrain > 0 {
                    RuleMark(y: .value("Average", averageStrain))
                        .foregroundStyle(AppColors.textTertiary)
                        .lineStyle(StrokeStyle(lineWidth: 1, dash: [5, 3]))
                        .annotation(position: .top, alignment: .trailing) {
                            Text("Avg \(averageStrain.formattedOneDecimal)")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }
                }

                // Strain bars
                ForEach(filteredMetrics, id: \.id) { metric in
                    BarMark(
                        x: .value("Date", metric.date, unit: .day),
                        y: .value("Strain", metric.strainScore)
                    )
                    .foregroundStyle(strainBarColor(for: metric.strainZone))
                    .cornerRadius(3)
                }
            }
            .chartYScale(domain: 0...21)
            .chartYAxis {
                AxisMarks(position: .leading, values: [0, 8, 14, 18, 21]) { value in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                        .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                    AxisValueLabel {
                        if let dbl = value.as(Double.self) {
                            Text(dbl.formattedOneDecimal)
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
                    value: averageStrain > 0 ? averageStrain.formattedOneDecimal : "--",
                    icon: "chart.bar.fill",
                    color: AppColors.primaryBlue
                )
                MetricTile(
                    title: "Workouts",
                    value: "\(filteredMetrics.reduce(0) { $0 + $1.workoutCount })",
                    icon: "figure.run",
                    color: AppColors.teal
                )
                MetricTile(
                    title: "Peak Strain",
                    value: peakStrain,
                    icon: "arrow.up",
                    color: AppColors.zone5
                )
                MetricTile(
                    title: "Rest Days",
                    value: "\(filteredMetrics.filter { $0.strainScore < 4 }.count)",
                    icon: "bed.double.fill",
                    color: AppColors.sleepDeep
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

    private var peakStrain: String {
        let scores = filteredMetrics.map { $0.strainScore }
        guard let peak = scores.max(), peak > 0 else { return "--" }
        return peak.formattedOneDecimal
    }

    private func strainBarColor(for zone: StrainZone) -> Color {
        switch zone {
        case .light: return AppColors.zone1
        case .moderate: return AppColors.zone2
        case .high: return AppColors.zone4
        case .overreaching: return AppColors.zone5
        }
    }
}

#Preview {
    NavigationStack {
        StrainTrendView()
    }
    .modelContainer(for: DailyMetric.self, inMemory: true)
}
