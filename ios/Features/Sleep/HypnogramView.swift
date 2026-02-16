import SwiftUI
import Charts

struct HypnogramView: View {
    let stages: [SleepStage]

    /// Map stage types to a numeric Y-axis value for the hypnogram.
    /// Lower values = deeper sleep, higher = lighter/awake.
    private func yValue(for stageType: SleepStageType) -> Double {
        switch stageType {
        case .awake: return 4
        case .rem: return 3
        case .light: return 2
        case .deep: return 1
        case .inBed: return 4
        }
    }

    private func stageColor(for stageType: SleepStageType) -> Color {
        AppColors.sleepStageColor(stageType)
    }

    private var yLabels: [(String, Double)] {
        [
            ("Deep", 1),
            ("Light", 2),
            ("REM", 3),
            ("Awake", 4),
        ]
    }

    /// Expand stages into data points for a step chart.
    private var dataPoints: [HypnogramPoint] {
        var points: [HypnogramPoint] = []

        for stage in stages {
            guard stage.stageType != .inBed else { continue }
            let y = yValue(for: stage.stageType)

            // Start of stage
            points.append(HypnogramPoint(
                date: stage.startDate,
                value: y,
                stageType: stage.stageType
            ))
            // End of stage (just before next)
            points.append(HypnogramPoint(
                date: stage.endDate,
                value: y,
                stageType: stage.stageType
            ))
        }

        return points.sorted { $0.date < $1.date }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Hypnogram")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if stages.isEmpty {
                emptyState
            } else {
                chartContent
                timeLegend
            }
        }
        .cardStyle()
    }

    // MARK: - Chart

    private var chartContent: some View {
        Chart {
            ForEach(Array(dataPoints.enumerated()), id: \.offset) { _, point in
                LineMark(
                    x: .value("Time", point.date),
                    y: .value("Stage", point.value)
                )
                .foregroundStyle(stageColor(for: point.stageType))
                .lineStyle(StrokeStyle(lineWidth: 2))
                .interpolationMethod(.stepCenter)

                AreaMark(
                    x: .value("Time", point.date),
                    yStart: .value("Base", 0.5),
                    yEnd: .value("Stage", point.value)
                )
                .foregroundStyle(
                    .linearGradient(
                        colors: [AppColors.sleepDeep.opacity(0.2), AppColors.sleepDeep.opacity(0.0)],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                )
                .interpolationMethod(.stepCenter)
            }
        }
        .chartYScale(domain: 0.5...4.5)
        .chartYAxis {
            AxisMarks(position: .leading, values: [1, 2, 3, 4]) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                AxisValueLabel {
                    if let dbl = value.as(Double.self) {
                        let label = yLabels.first { $0.1 == dbl }?.0 ?? ""
                        Text(label)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic(desiredCount: 5)) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                AxisValueLabel {
                    if let date = value.as(Date.self) {
                        Text(date.hourMinuteString)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
        }
        .frame(height: 180)
    }

    // MARK: - Time Legend

    private var timeLegend: some View {
        HStack {
            if let first = stages.first, let last = stages.last {
                Text("Bedtime: \(first.startDate.hourMinuteString)")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)

                Spacer()

                Text("Wake: \(last.endDate.hourMinuteString)")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
    }

    // MARK: - Empty State

    private var emptyState: some View {
        HStack {
            Spacer()
            Text("No stage data available for hypnogram")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textTertiary)
                .padding(.vertical, AppTheme.spacingLG)
            Spacer()
        }
    }
}

// MARK: - Data Point

private struct HypnogramPoint {
    let date: Date
    let value: Double
    let stageType: SleepStageType
}

#Preview {
    HypnogramView(stages: [])
        .padding()
        .background(AppColors.backgroundPrimary)
}
