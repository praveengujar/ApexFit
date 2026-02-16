import SwiftUI
import Charts

struct HRTimelineView: View {
    let samples: [(date: Date, bpm: Double)]

    private var maxBPM: Double {
        samples.map(\.bpm).max() ?? 200
    }

    private var minBPM: Double {
        samples.map(\.bpm).min() ?? 60
    }

    private var averageBPM: Double {
        guard !samples.isEmpty else { return 0 }
        return samples.map(\.bpm).reduce(0, +) / Double(samples.count)
    }

    /// Zone boundaries as BPM percentages of estimated max HR.
    /// Uses the max sample BPM as a proxy for max HR if it is > 150.
    private var estimatedMaxHR: Double {
        let observedMax = maxBPM
        return observedMax > 150 ? observedMax * 1.05 : 190
    }

    private var zoneBoundaries: [Double] {
        let maxHR = estimatedMaxHR
        return [
            maxHR * 0.50, // Zone 1 lower
            maxHR * 0.60, // Zone 2 lower
            maxHR * 0.70, // Zone 3 lower
            maxHR * 0.80, // Zone 4 lower
            maxHR * 0.90, // Zone 5 lower
        ]
    }

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Heart Rate Timeline")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if samples.isEmpty {
                emptyState
            } else {
                chartContent
            }
        }
        .cardStyle()
    }

    // MARK: - Chart Content

    private var chartContent: some View {
        Chart {
            // Zone background bands
            zoneBackgroundMarks

            // HR line
            ForEach(Array(samples.enumerated()), id: \.offset) { _, sample in
                LineMark(
                    x: .value("Time", sample.date),
                    y: .value("BPM", sample.bpm)
                )
                .foregroundStyle(
                    .linearGradient(
                        colors: [AppColors.zone2, AppColors.zone4, AppColors.zone5],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                )
                .lineStyle(StrokeStyle(lineWidth: 1.5))
                .interpolationMethod(.catmullRom)

                AreaMark(
                    x: .value("Time", sample.date),
                    y: .value("BPM", sample.bpm)
                )
                .foregroundStyle(
                    .linearGradient(
                        colors: [AppColors.primaryBlue.opacity(0.0), AppColors.primaryBlue.opacity(0.15)],
                        startPoint: .bottom,
                        endPoint: .top
                    )
                )
                .interpolationMethod(.catmullRom)
            }

            // Average line
            RuleMark(y: .value("Avg", averageBPM))
                .foregroundStyle(AppColors.textTertiary)
                .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 3]))
                .annotation(position: .top, alignment: .leading) {
                    Text("Avg \(Int(averageBPM))")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                }
        }
        .chartYScale(domain: Swift.max(minBPM - 10, 40)...maxBPM + 10)
        .chartYAxis {
            AxisMarks(position: .leading) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                AxisValueLabel {
                    if let bpm = value.as(Double.self) {
                        Text("\(Int(bpm))")
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
                        Text(date.timeString)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
        }
        .frame(height: 220)
    }

    // MARK: - Zone Background Marks

    @ChartContentBuilder
    private var zoneBackgroundMarks: some ChartContent {
        let maxHR = estimatedMaxHR
        let z1Lower = maxHR * 0.50
        let z2Lower = maxHR * 0.60
        let z3Lower = maxHR * 0.70
        let z4Lower = maxHR * 0.80
        let z5Lower = maxHR * 0.90

        RectangleMark(yStart: .value("", z1Lower), yEnd: .value("", z2Lower))
            .foregroundStyle(AppColors.zone1.opacity(0.06))
        RectangleMark(yStart: .value("", z2Lower), yEnd: .value("", z3Lower))
            .foregroundStyle(AppColors.zone2.opacity(0.06))
        RectangleMark(yStart: .value("", z3Lower), yEnd: .value("", z4Lower))
            .foregroundStyle(AppColors.zone3.opacity(0.06))
        RectangleMark(yStart: .value("", z4Lower), yEnd: .value("", z5Lower))
            .foregroundStyle(AppColors.zone4.opacity(0.06))
        RectangleMark(yStart: .value("", z5Lower), yEnd: .value("", maxHR))
            .foregroundStyle(AppColors.zone5.opacity(0.06))
    }

    // MARK: - Empty State

    private var emptyState: some View {
        HStack {
            Spacer()
            VStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "heart.text.clipboard")
                    .font(.largeTitle)
                    .foregroundStyle(AppColors.textTertiary)
                Text("No heart rate data available")
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textSecondary)
            }
            .padding(.vertical, AppTheme.spacingXL)
            Spacer()
        }
    }
}

#Preview {
    let now = Date()
    let samples: [(date: Date, bpm: Double)] = stride(from: 0, to: 60, by: 1).map { minute in
        let date = now.addingTimeInterval(TimeInterval(minute * 60))
        let bpm = 120.0 + sin(Double(minute) / 10.0) * 30 + Double.random(in: -5...5)
        return (date: date, bpm: bpm)
    }

    HRTimelineView(samples: samples)
        .padding()
        .background(AppColors.backgroundPrimary)
}
