import SwiftUI
import Charts

/// Data point representing a single day in the weekly strain/recovery chart.
struct WeeklyDataPoint: Identifiable {
    let id = UUID()
    let date: Date
    let strain: Double
    let recoveryScore: Double?
    let recoveryZone: RecoveryZone?

    /// Short day label like "Mon 9"
    var dayLabel: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "EEE d"
        return formatter.string(from: date)
    }
}

/// A card displaying the weekly Strain & Recovery chart with dual Y-axes.
/// Strain is shown as a blue line with point markers. Recovery is shown as
/// colored circle markers (green/yellow/red) with percentage labels.
struct WeeklyStrainRecoveryChart: View {
    let weekData: [WeeklyDataPoint]
    var todayIndex: Int? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            // MARK: - Header
            header

            // MARK: - Chart
            chartContent
                .frame(height: 220)
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .accessibilityElement(children: .contain)
        .accessibilityLabel("Weekly strain and recovery chart")
    }

    // MARK: - Header

    private var header: some View {
        HStack {
            Text("STRAIN & RECOVERY")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "info.circle")
                .font(.system(size: 16, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Chart

    private var chartContent: some View {
        Chart {
            // Today highlight column
            if let todayIndex, todayIndex >= 0, todayIndex < weekData.count {
                let todayPoint = weekData[todayIndex]
                RectangleMark(
                    x: .value("Day", todayPoint.dayLabel),
                    yStart: .value("Start", 0),
                    yEnd: .value("End", 21)
                )
                .foregroundStyle(Color.white.opacity(0.05))
            }

            // Strain line
            ForEach(weekData) { point in
                LineMark(
                    x: .value("Day", point.dayLabel),
                    y: .value("Strain", point.strain)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .lineStyle(StrokeStyle(lineWidth: 2))
                .interpolationMethod(.catmullRom)

                PointMark(
                    x: .value("Day", point.dayLabel),
                    y: .value("Strain", point.strain)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .symbolSize(36)
                .annotation(position: .top, spacing: 4) {
                    Text(String(format: "%.1f", point.strain))
                        .font(.system(size: 9, weight: .semibold))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }

            // Recovery points
            ForEach(weekData) { point in
                if let recovery = point.recoveryScore, let zone = point.recoveryZone {
                    let recoveryScaled = recovery / 100.0 * 21.0
                    PointMark(
                        x: .value("Day", point.dayLabel),
                        y: .value("Recovery", recoveryScaled)
                    )
                    .foregroundStyle(AppColors.recoveryColor(for: zone))
                    .symbolSize(64)
                    .symbol(.circle)
                    .annotation(position: .bottom, spacing: 4) {
                        Text("\(Int(recovery))%")
                            .font(.system(size: 9, weight: .semibold))
                            .foregroundStyle(AppColors.recoveryColor(for: zone))
                    }
                }
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic) { value in
                AxisValueLabel()
                    .font(.system(size: 10))
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading, values: [0, 7, 14, 21]) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [4, 4]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.4))
                AxisValueLabel {
                    if let intValue = value.as(Int.self) {
                        Text("\(intValue)")
                            .font(.system(size: 10))
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }

            // Right axis labels for recovery percentage
            AxisMarks(position: .trailing, values: [0, 7, 14, 21]) { value in
                AxisValueLabel {
                    if let intValue = value.as(Int.self) {
                        let percentage = Int(Double(intValue) / 21.0 * 100.0)
                        Text("\(percentage)%")
                            .font(.system(size: 10))
                            .foregroundStyle(recoveryAxisColor(for: percentage))
                    }
                }
            }
        }
        .chartYScale(domain: 0...21)
        .chartPlotStyle { plotArea in
            plotArea
                .background(Color.clear)
        }
    }

    // MARK: - Helpers

    /// Returns the recovery zone color for a given percentage to color
    /// the right-axis labels (0-33% red, 34-66% yellow, 67-100% green).
    private func recoveryAxisColor(for percentage: Int) -> Color {
        switch percentage {
        case 67...100:
            return AppColors.recoveryGreen
        case 34..<67:
            return AppColors.recoveryYellow
        default:
            return AppColors.recoveryRed
        }
    }
}

// MARK: - Preview

#Preview("Weekly Strain & Recovery") {
    let calendar = Calendar.current
    let today = Date()
    let sampleData: [WeeklyDataPoint] = (0..<7).map { dayOffset in
        let date = calendar.date(byAdding: .day, value: dayOffset - 6, to: today)!
        let strains: [Double] = [8.2, 12.5, 6.1, 15.3, 10.8, 14.2, 9.7]
        let recoveries: [Double] = [72, 55, 82, 38, 65, 78, 45]
        let zones: [RecoveryZone] = [.green, .yellow, .green, .red, .yellow, .green, .yellow]
        return WeeklyDataPoint(
            date: date,
            strain: strains[dayOffset],
            recoveryScore: recoveries[dayOffset],
            recoveryZone: zones[dayOffset]
        )
    }

    WeeklyStrainRecoveryChart(weekData: sampleData, todayIndex: 6)
        .padding(AppTheme.spacingMD)
        .background(AppColors.backgroundPrimary)
}
