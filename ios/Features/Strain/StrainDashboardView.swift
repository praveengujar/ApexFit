import SwiftUI
import SwiftData
import Charts

struct StrainDashboardView: View {
    @Environment(DataLoadingCoordinator.self) private var dataLoadingCoordinator
    @Environment(\.modelContext) private var modelContext

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    // MARK: - WHOOP-Style Zone Colors

    private static let whoopZone1 = Color(hex: "#8A8A8E")
    private static let whoopZone2 = Color(hex: "#4A90D9")
    private static let whoopZone3 = Color(hex: "#16EC06")
    private static let whoopZone4 = Color(hex: "#FF8C00")
    private static let whoopZone5 = Color(hex: "#FF0026")

    // MARK: - Derived Data

    private var todayMetric: DailyMetric? {
        allMetrics.first { $0.date.isToday } ?? allMetrics.first { $0.date.isYesterday }
    }

    private var weekMetrics: [DailyMetric] {
        let cutoff = Date().daysAgo(7)
        return allMetrics
            .filter { $0.date >= cutoff }
            .sorted { $0.date < $1.date }
    }

    private var past30DaysMetrics: [DailyMetric] {
        let cutoff = Date().daysAgo(30)
        return allMetrics
            .filter { $0.date >= cutoff }
            .sorted { $0.date < $1.date }
    }

    // MARK: - Today's Metrics

    private var strainScore: Double { todayMetric?.strainScore ?? 0 }

    private var recoveryZone: RecoveryZone {
        todayMetric?.recoveryZone ?? RecoveryZone.from(score: todayMetric?.recoveryScore ?? 0)
    }

    private var todayZone13: Double {
        guard let m = todayMetric else { return 0 }
        return m.workouts.reduce(0.0) { $0 + $1.zone1Minutes + $1.zone2Minutes + $1.zone3Minutes }
    }

    private var todayZone45: Double {
        guard let m = todayMetric else { return 0 }
        return m.workouts.reduce(0.0) { $0 + $1.zone4Minutes + $1.zone5Minutes }
    }

    private var todayStrength: Double {
        guard let m = todayMetric else { return 0 }
        return m.workouts.filter { $0.isStrengthWorkout }.reduce(0.0) { $0 + $1.durationMinutes }
    }

    private var todaySteps: Int { todayMetric?.steps ?? 0 }

    // MARK: - 30-Day Baselines

    private var baselineZone13: Double {
        let values = past30DaysMetrics.map { m in
            m.workouts.reduce(0.0) { $0 + $1.zone1Minutes + $1.zone2Minutes + $1.zone3Minutes }
        }
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineZone45: Double {
        let values = past30DaysMetrics.map { m in
            m.workouts.reduce(0.0) { $0 + $1.zone4Minutes + $1.zone5Minutes }
        }
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineStrength: Double {
        let values = past30DaysMetrics.map { m in
            m.workouts.filter { $0.isStrengthWorkout }.reduce(0.0) { $0 + $1.durationMinutes }
        }
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineSteps: Double {
        let values = past30DaysMetrics.map { Double($0.steps) }
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    // MARK: - Helpers

    private func formatMinutes(_ minutes: Double) -> String {
        let total = Int(minutes)
        let h = total / 60
        let m = total % 60
        return "\(h):\(String(format: "%02d", m))"
    }

    // MARK: - Body

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    strainOverviewSection
                    weeklyTrendsSection
                }
                .padding(.bottom, AppTheme.spacingXL)
            }
            .refreshable {
                await dataLoadingCoordinator.forceRefresh(modelContext: modelContext)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("TODAY")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: {}) {
                        Image(systemName: "info.circle")
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }
        }
    }

    // MARK: - Module 1: Strain Overview

    private var strainOverviewSection: some View {
        VStack(spacing: AppTheme.spacingLG) {
            // Large circular gauge
            ZStack {
                Circle()
                    .trim(from: 0, to: 0.75)
                    .stroke(
                        AppColors.textTertiary.opacity(0.3),
                        style: StrokeStyle(lineWidth: 14, lineCap: .round)
                    )
                    .rotationEffect(.degrees(135))

                Circle()
                    .trim(from: 0, to: (strainScore / 21.0).clamped(to: 0...1) * 0.75)
                    .stroke(
                        LinearGradient(
                            colors: [AppColors.primaryBlue.opacity(0.6), AppColors.primaryBlue],
                            startPoint: .leading,
                            endPoint: .trailing
                        ),
                        style: StrokeStyle(lineWidth: 14, lineCap: .round)
                    )
                    .rotationEffect(.degrees(135))
                    .animation(.easeInOut(duration: 0.8), value: strainScore)

                VStack(spacing: 2) {
                    Text(strainScore > 0 ? strainScore.formattedOneDecimal : "--")
                        .font(.system(size: 64, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    Text("STRAIN")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AppColors.textSecondary)
                        .tracking(1)
                }
            }
            .frame(width: 240, height: 240)
            .padding(.top, AppTheme.spacingLG)

            subMetricsCard
            strainInsightCard
        }
    }

    // MARK: - Sub-Metrics Card

    private var subMetricsCard: some View {
        VStack(spacing: 0) {
            strainMetricRow(
                icon: "heart.text.square",
                title: "HEART RATE ZONES 1-3",
                currentValue: todayZone13,
                baseline: baselineZone13,
                format: .timeMinutes,
                isFirst: true
            )
            Divider().overlay(AppColors.backgroundTertiary)
            strainMetricRow(
                icon: "heart.text.square.fill",
                title: "HEART RATE ZONES 4-5",
                currentValue: todayZone45,
                baseline: baselineZone45,
                format: .timeMinutes
            )
            Divider().overlay(AppColors.backgroundTertiary)
            strainMetricRow(
                icon: "dumbbell.fill",
                title: "STRENGTH ACTIVITY TIME",
                currentValue: todayStrength,
                baseline: baselineStrength,
                format: .timeMinutes
            )
            Divider().overlay(AppColors.backgroundTertiary)
            strainMetricRow(
                icon: "figure.walk",
                title: "STEPS",
                currentValue: Double(todaySteps),
                baseline: baselineSteps,
                format: .steps
            )

            // Footer
            HStack(spacing: 4) {
                Image(systemName: "arrowtriangle.up.fill")
                    .font(.system(size: 8))
                    .foregroundStyle(AppColors.recoveryGreen)
                Image(systemName: "arrowtriangle.down.fill")
                    .font(.system(size: 8))
                    .foregroundStyle(AppColors.recoveryRed)
                Text("Today")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundStyle(AppColors.textPrimary)
                Text("vs. last 30 days")
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
            }
            .padding(.vertical, AppTheme.spacingSM)
            .padding(.horizontal, AppTheme.cardPadding)
            .frame(maxWidth: .infinity)
            .background(AppColors.backgroundTertiary.opacity(0.5))
        }
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .padding(.horizontal, AppTheme.spacingMD)
    }

    private enum StrainMetricFormat {
        case timeMinutes
        case steps
    }

    private func strainMetricRow(
        icon: String,
        title: String,
        currentValue: Double,
        baseline: Double,
        format: StrainMetricFormat,
        isFirst: Bool = false
    ) -> some View {
        // All strain sub-metrics: higher is better
        let isWorse = currentValue < baseline && baseline > 0
        let arrowUp = currentValue >= baseline
        let arrowColor = isWorse ? AppColors.recoveryYellow : AppColors.recoveryGreen

        let formattedCurrent: String = {
            switch format {
            case .timeMinutes: return formatMinutes(currentValue)
            case .steps: return Int(currentValue).formattedWithComma
            }
        }()

        let formattedBaseline: String = {
            switch format {
            case .timeMinutes: return formatMinutes(baseline)
            case .steps: return Int(baseline).formattedWithComma
            }
        }()

        return HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundStyle(AppColors.textSecondary)
                .frame(width: 24)

            Text(title)
                .font(.system(size: 12, weight: .bold))
                .foregroundStyle(AppColors.textPrimary)
                .tracking(0.5)
                .lineLimit(1)

            Spacer()

            VStack(alignment: .trailing, spacing: 0) {
                HStack(spacing: 4) {
                    Text(formattedCurrent)
                        .font(.system(size: 20, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    if baseline > 0 {
                        Image(systemName: arrowUp ? "arrowtriangle.up.fill" : "arrowtriangle.down.fill")
                            .font(.system(size: 8))
                            .foregroundStyle(arrowColor)
                    }
                }
                Text(formattedBaseline)
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .padding(.horizontal, AppTheme.cardPadding)
        .padding(.vertical, 14)
    }

    // MARK: - Strain Insight Card

    private var strainInsightCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text(strainInsightText)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textPrimary)
                .fixedSize(horizontal: false, vertical: true)

            Button(action: {}) {
                HStack(spacing: AppTheme.spacingXS) {
                    Text("EXPLORE YOUR STRAIN INSIGHTS")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AppColors.teal)
                        .tracking(0.5)
                    Image(systemName: "arrow.right")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(AppColors.teal)
                }
            }
        }
        .padding(AppTheme.cardPadding)
        .background(
            RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium)
                .stroke(AppColors.teal.opacity(0.4), lineWidth: 1)
                .background(AppColors.backgroundCard.clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium)))
        )
        .padding(.horizontal, AppTheme.spacingMD)
    }

    private var strainInsightText: String {
        let target = RecoveryEngine.strainTarget(for: recoveryZone)

        if strainScore < target.lowerBound {
            return "Your optimal activity level today is low. Consider taking a rest day for recovery, or try a light workout that will help you achieve a Day Strain under \(target.upperBound.formattedOneDecimal)."
        } else if strainScore <= target.upperBound {
            return "You're on track! Your current strain of \(strainScore.formattedOneDecimal) is within your optimal range of \(target.lowerBound.formattedOneDecimal)–\(target.upperBound.formattedOneDecimal) for today."
        } else {
            return "Your strain is higher than recommended for your recovery level. Consider winding down to allow your body adequate recovery time."
        }
    }

    // MARK: - Weekly Trends

    private var weeklyTrendsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingLG) {
            Text("Weekly Trends")
                .font(AppTypography.heading1)
                .foregroundStyle(AppColors.textPrimary)
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingXL)

            weeklyChartCard(title: "STRAIN") {
                weeklyStrainChart
            }

            weeklyChartCard(title: "HR ZONES 1-3") {
                weeklyZone13Chart
            }

            weeklyChartCard(title: "HR ZONES 4-5") {
                weeklyZone45Chart
            }

            weeklyChartCard(title: "STEPS") {
                weeklyStepsChart
            }

            weeklyChartCard(title: "CALORIES") {
                weeklyCaloriesChart
            }
        }
    }

    private func weeklyChartCard<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text(title)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
            }

            content()
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
    }

    // MARK: - Weekly Strain Chart

    private var weeklyStrainChart: some View {
        let data = weekMetrics.map { m in
            (id: m.id, date: m.date, strain: m.strainScore, label: m.strainScore.formattedOneDecimal, isToday: m.date.isToday)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", 0),
                        yEnd: .value("End", 21)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Strain", item.strain)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .cornerRadius(3)
                .annotation(position: .top) {
                    Text(item.label)
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
        }
        .chartYScale(domain: 0...21)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Weekly HR Zones 1-3 Stacked Chart

    private var weeklyZone13Chart: some View {
        let barData: [(id: String, date: Date, zone: String, minutes: Double)] = weekMetrics.flatMap { m in
            let z1 = m.workouts.reduce(0.0) { $0 + $1.zone1Minutes }
            let z2 = m.workouts.reduce(0.0) { $0 + $1.zone2Minutes }
            let z3 = m.workouts.reduce(0.0) { $0 + $1.zone3Minutes }
            return [
                (id: "\(m.id)_1", date: m.date, zone: "Zone 1", minutes: z1),
                (id: "\(m.id)_2", date: m.date, zone: "Zone 2", minutes: z2),
                (id: "\(m.id)_3", date: m.date, zone: "Zone 3", minutes: z3)
            ]
        }
        let totals: [(id: UUID, date: Date, total: Double, label: String, isToday: Bool)] = weekMetrics.map { m in
            let t = m.workouts.reduce(0.0) { $0 + $1.zone1Minutes + $1.zone2Minutes + $1.zone3Minutes }
            return (id: m.id, date: m.date, total: t, label: formatMinutes(t), isToday: m.date.isToday)
        }
        let maxY = max((totals.map(\.total).max() ?? 30) * 1.3, 10)

        return VStack(alignment: .leading, spacing: 4) {
            // Legend
            HStack(spacing: 12) {
                Spacer()
                zoneLegendItem(color: Self.whoopZone1, label: "ZONE 1")
                zoneLegendItem(color: Self.whoopZone2, label: "ZONE 2")
                zoneLegendItem(color: Self.whoopZone3, label: "ZONE 3")
            }

            zone13StackedChart(barData: barData, totals: totals, maxY: maxY)
        }
    }

    private func zone13StackedChart(
        barData: [(id: String, date: Date, zone: String, minutes: Double)],
        totals: [(id: UUID, date: Date, total: Double, label: String, isToday: Bool)],
        maxY: Double
    ) -> some View {
        Chart {
            ForEach(totals, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("S", 0),
                        yEnd: .value("E", maxY)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }
            }
            ForEach(barData, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Min", item.minutes)
                )
                .foregroundStyle(by: .value("Zone", item.zone))
                .cornerRadius(2)
            }
        }
        .chartForegroundStyleScale([
            "Zone 1": Self.whoopZone1,
            "Zone 2": Self.whoopZone2,
            "Zone 3": Self.whoopZone3
        ])
        .chartLegend(.hidden)
        .chartYScale(domain: 0...maxY)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Weekly HR Zones 4-5 Stacked Chart

    private var weeklyZone45Chart: some View {
        let barData: [(id: String, date: Date, zone: String, minutes: Double)] = weekMetrics.flatMap { m in
            let z4 = m.workouts.reduce(0.0) { $0 + $1.zone4Minutes }
            let z5 = m.workouts.reduce(0.0) { $0 + $1.zone5Minutes }
            return [
                (id: "\(m.id)_4", date: m.date, zone: "Zone 4", minutes: z4),
                (id: "\(m.id)_5", date: m.date, zone: "Zone 5", minutes: z5)
            ]
        }
        let totals: [(id: UUID, date: Date, total: Double, label: String, isToday: Bool)] = weekMetrics.map { m in
            let t = m.workouts.reduce(0.0) { $0 + $1.zone4Minutes + $1.zone5Minutes }
            return (id: m.id, date: m.date, total: t, label: formatMinutes(t), isToday: m.date.isToday)
        }
        let maxY = max((totals.map(\.total).max() ?? 20) * 1.3, 5)

        return VStack(alignment: .leading, spacing: 4) {
            // Legend
            HStack(spacing: 12) {
                Spacer()
                zoneLegendItem(color: Self.whoopZone4, label: "ZONE 4")
                zoneLegendItem(color: Self.whoopZone5, label: "ZONE 5")
            }

            zone45StackedChart(barData: barData, totals: totals, maxY: maxY)
        }
    }

    private func zone45StackedChart(
        barData: [(id: String, date: Date, zone: String, minutes: Double)],
        totals: [(id: UUID, date: Date, total: Double, label: String, isToday: Bool)],
        maxY: Double
    ) -> some View {
        Chart {
            ForEach(totals, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("S", 0),
                        yEnd: .value("E", maxY)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }
            }
            ForEach(barData, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Min", item.minutes)
                )
                .foregroundStyle(by: .value("Zone", item.zone))
                .cornerRadius(2)
            }
        }
        .chartForegroundStyleScale([
            "Zone 4": Self.whoopZone4,
            "Zone 5": Self.whoopZone5
        ])
        .chartLegend(.hidden)
        .chartYScale(domain: 0...maxY)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Weekly Steps Chart

    private var weeklyStepsChart: some View {
        let data = weekMetrics.map { m in
            (id: m.id, date: m.date, steps: Double(m.steps), label: m.steps.formattedWithComma, isToday: m.date.isToday)
        }
        let maxY = max((data.map(\.steps).max() ?? 10000) * 1.2, 1000)
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", 0),
                        yEnd: .value("End", maxY)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Steps", item.steps)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .cornerRadius(3)
                .annotation(position: .top) {
                    Text(item.label)
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
        }
        .chartYScale(domain: 0...maxY)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Weekly Calories Chart

    private var weeklyCaloriesChart: some View {
        let data = weekMetrics.map { m in
            (id: m.id, date: m.date, cal: m.activeCalories, label: Int(m.activeCalories).formattedWithComma, isToday: m.date.isToday)
        }
        let maxY = max((data.map(\.cal).max() ?? 2000) * 1.2, 500)
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", 0),
                        yEnd: .value("End", maxY)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Calories", item.cal)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .cornerRadius(3)
                .annotation(position: .top) {
                    Text(item.label)
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
        }
        .chartYScale(domain: 0...maxY)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Legend Helper

    private func zoneLegendItem(color: Color, label: String) -> some View {
        HStack(spacing: 4) {
            RoundedRectangle(cornerRadius: 2)
                .fill(color)
                .frame(width: 10, height: 10)
            Text(label)
                .font(.system(size: 10))
                .foregroundStyle(AppColors.textSecondary)
        }
    }
}

// MARK: - Preview

#Preview {
    StrainDashboardView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
