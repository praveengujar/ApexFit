import SwiftUI
import Charts

struct TrendChartView: View {
    let metricName: String
    let dataPoints: [(Date, Double)]
    let color: Color
    var targetLine: Double?

    @State private var period: TrendPeriod = .month

    private var filteredData: [(Date, Double)] {
        let cutoff = period.startDate
        return dataPoints.filter { $0.0 >= cutoff }
    }

    private var average: Double {
        guard !filteredData.isEmpty else { return 0 }
        return filteredData.map(\.1).reduce(0, +) / Double(filteredData.count)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            headerRow
            PeriodPicker(period: $period)
            chart
            statsRow
        }
        .cardStyle()
    }

    // MARK: - Header

    private var headerRow: some View {
        HStack {
            Text(metricName)
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)
            Spacer()
            if !filteredData.isEmpty {
                Text("Avg: \(average.formattedOneDecimal)")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
    }

    // MARK: - Chart

    private var chart: some View {
        Group {
            if filteredData.isEmpty {
                noDataPlaceholder
            } else {
                chartContent
                    .frame(height: 200)
            }
        }
    }

    private var chartContent: some View {
        Chart {
            // Main data line
            ForEach(Array(filteredData.enumerated()), id: \.offset) { _, point in
                LineMark(
                    x: .value("Date", point.0),
                    y: .value(metricName, point.1)
                )
                .foregroundStyle(color)
                .interpolationMethod(.catmullRom)

                AreaMark(
                    x: .value("Date", point.0),
                    y: .value(metricName, point.1)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [color.opacity(0.3), color.opacity(0.0)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .interpolationMethod(.catmullRom)
            }

            // Average line (dashed)
            RuleMark(y: .value("Average", average))
                .foregroundStyle(AppColors.textTertiary)
                .lineStyle(StrokeStyle(lineWidth: 1, dash: [5, 3]))
                .annotation(position: .top, alignment: .trailing) {
                    Text("avg")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                }

            // Target line
            if let target = targetLine {
                RuleMark(y: .value("Target", target))
                    .foregroundStyle(AppColors.recoveryGreen.opacity(0.6))
                    .lineStyle(StrokeStyle(lineWidth: 1, dash: [8, 4]))
                    .annotation(position: .top, alignment: .leading) {
                        Text("target")
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.recoveryGreen)
                    }
            }
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: xAxisStride, count: xAxisStrideCount)) {
                AxisGridLine()
                    .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                AxisValueLabel(format: xAxisFormat)
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading) {
                AxisGridLine()
                    .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                AxisValueLabel()
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
    }

    private var xAxisStride: Calendar.Component {
        switch period {
        case .week: return .day
        case .month: return .weekOfYear
        case .quarter: return .month
        }
    }

    private var xAxisStrideCount: Int {
        switch period {
        case .week: return 1
        case .month: return 1
        case .quarter: return 1
        }
    }

    private var xAxisFormat: Date.FormatStyle {
        switch period {
        case .week:
            return .dateTime.weekday(.abbreviated)
        case .month:
            return .dateTime.month(.abbreviated).day()
        case .quarter:
            return .dateTime.month(.abbreviated)
        }
    }

    private var noDataPlaceholder: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "chart.xyaxis.line")
                .font(.title)
                .foregroundStyle(AppColors.textTertiary)
            Text("No data for this period")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 200)
    }

    // MARK: - Stats Row

    private var statsRow: some View {
        Group {
            if !filteredData.isEmpty {
                let values = filteredData.map(\.1)
                let minVal = values.min() ?? 0
                let maxVal = values.max() ?? 0
                HStack {
                    statItem(label: "Min", value: minVal.formattedOneDecimal)
                    Spacer()
                    statItem(label: "Avg", value: average.formattedOneDecimal)
                    Spacer()
                    statItem(label: "Max", value: maxVal.formattedOneDecimal)
                }
            }
        }
    }

    private func statItem(label: String, value: String) -> some View {
        VStack(spacing: 2) {
            Text(label)
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textTertiary)
            Text(value)
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
        }
    }
}
