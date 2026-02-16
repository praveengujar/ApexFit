import SwiftUI
import Charts

// MARK: - Data Models

/// A single stress measurement at a point in time.
struct StressDataPoint: Identifiable {
    let id = UUID()
    let timestamp: Date
    let score: Double
}

/// An activity period (sleep or workout) to overlay on the stress chart.
struct ActivityMarker: Identifiable {
    let id = UUID()
    let type: ActivityType
    let start: Date
    let end: Date

    enum ActivityType: String {
        case sleep
        case workout

        var icon: String {
            switch self {
            case .sleep: return "moon.fill"
            case .workout: return "figure.run"
            }
        }

        var color: Color {
            switch self {
            case .sleep: return AppColors.sleepDeep
            case .workout: return AppColors.primaryBlue
            }
        }
    }
}

// MARK: - Stress Level Helper

/// Maps a stress score to a display level and color.
private struct StressLevel {
    let label: String
    let color: Color

    init(score: Double) {
        switch score {
        case ..<1.0:
            self.label = "LOW"
            self.color = AppColors.teal
        case 1.0..<2.0:
            self.label = "MEDIUM"
            self.color = AppColors.recoveryYellow
        case 2.0..<2.5:
            self.label = "HIGH"
            self.color = Color.orange
        default:
            self.label = "VERY HIGH"
            self.color = AppColors.recoveryRed
        }
    }
}

// MARK: - StressChartCard

/// A card displaying the Stress Monitor time-series chart with gradient coloring
/// based on stress level. Supports activity overlay markers for sleep and workout periods.
struct StressChartCard: View {
    let dataPoints: [StressDataPoint]
    var activities: [ActivityMarker] = []
    var lastUpdated: Date? = nil
    var currentScore: Double? = nil
    var currentLevel: String? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            // MARK: - Header
            header

            // MARK: - Subheader
            subheader

            // MARK: - Chart
            chartContent
                .frame(height: 180)

            // MARK: - Activity Legend
            if !activities.isEmpty {
                activityLegend
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .accessibilityElement(children: .contain)
        .accessibilityLabel("Stress monitor chart")
    }

    // MARK: - Header

    private var header: some View {
        HStack {
            Text("STRESS MONITOR")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Subheader

    private var subheader: some View {
        HStack {
            if let lastUpdated {
                Text("Last updated \(lastUpdated.formatted(date: .omitted, time: .shortened))")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Spacer()

            if let currentScore {
                let level = resolvedStressLevel
                HStack(spacing: AppTheme.spacingXS) {
                    Text(level.label)
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(level.color)
                    Text(String(format: "%.1f", currentScore))
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(level.color)
                }
            }
        }
    }

    /// Resolves the stress level from the explicit `currentLevel` prop or from the score.
    private var resolvedStressLevel: StressLevel {
        if let currentScore {
            return StressLevel(score: currentScore)
        }
        return StressLevel(score: 0)
    }

    // MARK: - Chart

    private var chartContent: some View {
        Chart {
            // Activity marker backgrounds
            ForEach(activities) { activity in
                RectangleMark(
                    xStart: .value("Start", activity.start),
                    xEnd: .value("End", activity.end),
                    yStart: .value("Bottom", 0),
                    yEnd: .value("Top", 3.0)
                )
                .foregroundStyle(activity.type.color.opacity(0.08))

                // Top bar indicator for activity
                RectangleMark(
                    xStart: .value("Start", activity.start),
                    xEnd: .value("End", activity.end),
                    yStart: .value("BarBottom", 2.85),
                    yEnd: .value("BarTop", 3.0)
                )
                .foregroundStyle(activity.type.color.opacity(0.6))
            }

            // Area fill under the stress line
            ForEach(dataPoints) { point in
                AreaMark(
                    x: .value("Time", point.timestamp),
                    y: .value("Stress", point.score)
                )
                .foregroundStyle(areaGradient)
                .interpolationMethod(.catmullRom)
            }

            // Stress line
            ForEach(dataPoints) { point in
                LineMark(
                    x: .value("Time", point.timestamp),
                    y: .value("Stress", point.score)
                )
                .foregroundStyle(lineGradient)
                .lineStyle(StrokeStyle(lineWidth: 2))
                .interpolationMethod(.catmullRom)
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic(desiredCount: 5)) { value in
                AxisValueLabel(format: .dateTime.hour().minute())
                    .font(.system(size: 10))
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading, values: [0, 1.0, 2.0, 3.0]) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [4, 4]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.4))
                AxisValueLabel {
                    if let doubleValue = value.as(Double.self) {
                        Text(String(format: "%.1f", doubleValue))
                            .font(.system(size: 10))
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }
        }
        .chartYScale(domain: 0...3.0)
        .chartPlotStyle { plotArea in
            plotArea
                .background(Color.clear)
        }
        .chartOverlay { proxy in
            // Activity icons overlay
            GeometryReader { geo in
                ForEach(activities) { activity in
                    let midTime = Date(
                        timeIntervalSince1970: (activity.start.timeIntervalSince1970 + activity.end.timeIntervalSince1970) / 2
                    )
                    if let xPos = proxy.position(forX: midTime),
                       let plotFrame = proxy.plotFrame {
                        let frame = geo[plotFrame]
                        Image(systemName: activity.type.icon)
                            .font(.system(size: 10, weight: .medium))
                            .foregroundStyle(activity.type.color)
                            .position(x: frame.origin.x + xPos, y: frame.origin.y + 8)
                    }
                }
            }
        }
    }

    // MARK: - Gradients

    /// Line gradient that shifts from teal (low) through green and yellow to orange (high).
    private var lineGradient: LinearGradient {
        LinearGradient(
            colors: [AppColors.teal, AppColors.recoveryGreen, AppColors.recoveryYellow, .orange],
            startPoint: .bottom,
            endPoint: .top
        )
    }

    /// Subtle area fill gradient beneath the stress line.
    private var areaGradient: LinearGradient {
        LinearGradient(
            colors: [
                AppColors.teal.opacity(0.0),
                AppColors.teal.opacity(0.1),
                AppColors.recoveryGreen.opacity(0.15),
                AppColors.recoveryYellow.opacity(0.2),
                Color.orange.opacity(0.25)
            ],
            startPoint: .bottom,
            endPoint: .top
        )
    }

    // MARK: - Activity Legend

    private var activityLegend: some View {
        HStack(spacing: AppTheme.spacingMD) {
            ForEach(Array(Set(activities.map(\.type.rawValue))).sorted(), id: \.self) { typeRaw in
                if let activity = activities.first(where: { $0.type.rawValue == typeRaw }) {
                    HStack(spacing: AppTheme.spacingXS) {
                        Image(systemName: activity.type.icon)
                            .font(.system(size: 11))
                            .foregroundStyle(activity.type.color)
                        Text(activity.type.rawValue.capitalized)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }
        }
    }
}

// MARK: - Preview

#Preview("Stress Monitor") {
    let calendar = Calendar.current
    let today = calendar.startOfDay(for: Date())

    // Generate sample stress data points throughout a day
    let samplePoints: [StressDataPoint] = stride(from: 0, through: 780, by: 15).map { minute in
        let timestamp = calendar.date(byAdding: .minute, value: minute, to: today)!
        let hour = Double(minute) / 60.0

        // Simulate stress pattern: low during sleep, rising during day
        let baseStress: Double
        switch hour {
        case 0..<6:
            baseStress = 0.3 + Double.random(in: -0.15...0.15)
        case 6..<8:
            baseStress = 0.8 + Double.random(in: -0.2...0.2)
        case 8..<10:
            baseStress = 1.5 + Double.random(in: -0.3...0.3)
        case 10..<12:
            baseStress = 1.8 + Double.random(in: -0.3...0.3)
        default:
            baseStress = 1.4 + Double.random(in: -0.3...0.3)
        }
        return StressDataPoint(timestamp: timestamp, score: max(0, min(3.0, baseStress)))
    }

    let sampleActivities: [ActivityMarker] = [
        ActivityMarker(
            type: .sleep,
            start: calendar.date(byAdding: .hour, value: 0, to: today)!,
            end: calendar.date(byAdding: .hour, value: 7, to: today)!
        ),
        ActivityMarker(
            type: .workout,
            start: calendar.date(byAdding: .hour, value: 8, to: today)!,
            end: calendar.date(byAdding: .minute, value: 555, to: today)!
        )
    ]

    StressChartCard(
        dataPoints: samplePoints,
        activities: sampleActivities,
        lastUpdated: calendar.date(byAdding: .hour, value: 13, to: today),
        currentScore: 1.5,
        currentLevel: "MEDIUM"
    )
    .padding(AppTheme.spacingMD)
    .background(AppColors.backgroundPrimary)
}
