import SwiftUI
import SwiftData
import Charts

struct RecoveryDashboardView: View {
    @Environment(DataLoadingCoordinator.self) private var dataLoadingCoordinator
    @Environment(\.modelContext) private var modelContext

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    // MARK: - Derived Data

    private var todayMetric: DailyMetric? {
        allMetrics.first { $0.date.isToday } ?? allMetrics.first { $0.date.isYesterday }
    }

    private var yesterdayMetric: DailyMetric? {
        allMetrics.first { $0.date.isYesterday }
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

    private var recoveryScore: Double {
        todayMetric?.recoveryScore ?? 0
    }

    private var recoveryZone: RecoveryZone {
        todayMetric?.recoveryZone ?? RecoveryZone.from(score: recoveryScore)
    }

    private var zoneColor: Color {
        AppColors.recoveryColor(for: recoveryZone)
    }

    private var todayHRV: Double { todayMetric?.hrvRMSSD ?? 0 }
    private var todayRHR: Double { todayMetric?.restingHeartRate ?? 0 }
    private var todayRespRate: Double { todayMetric?.respiratoryRate ?? 0 }
    private var todaySleepPerf: Double { todayMetric?.sleepPerformance ?? 0 }

    // MARK: - 30-Day Baselines

    private var baselineHRV: Double {
        let values = past30DaysMetrics.compactMap(\.hrvRMSSD)
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineRHR: Double {
        let values = past30DaysMetrics.compactMap(\.restingHeartRate)
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineRespRate: Double {
        let values = past30DaysMetrics.compactMap(\.respiratoryRate)
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    private var baselineSleepPerf: Double {
        let values = past30DaysMetrics.compactMap(\.sleepPerformance)
        guard !values.isEmpty else { return 0 }
        return values.reduce(0, +) / Double(values.count)
    }

    // MARK: - Body

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    // Module 1: Recovery Overview
                    recoveryOverviewSection

                    // Module 2: Recovery Insights
                    recoveryInsightsSection

                    // Modules 3-7: Weekly Trends
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

    // MARK: - Module 1: Recovery Overview

    private var recoveryOverviewSection: some View {
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
                    .trim(from: 0, to: (recoveryScore / 99.0).clamped(to: 0...1) * 0.75)
                    .stroke(
                        LinearGradient(
                            colors: [zoneColor.opacity(0.6), zoneColor],
                            startPoint: .leading,
                            endPoint: .trailing
                        ),
                        style: StrokeStyle(lineWidth: 14, lineCap: .round)
                    )
                    .rotationEffect(.degrees(135))
                    .animation(.easeInOut(duration: 0.8), value: recoveryScore)

                VStack(spacing: 2) {
                    HStack(alignment: .firstTextBaseline, spacing: 0) {
                        Text(recoveryScore > 0 ? "\(Int(recoveryScore))" : "--")
                            .font(.system(size: 64, weight: .bold, design: .rounded))
                            .foregroundStyle(AppColors.textPrimary)
                        Text("%")
                            .font(.system(size: 28, weight: .bold, design: .rounded))
                            .foregroundStyle(AppColors.textPrimary)
                    }
                    Text("RECOVERY")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AppColors.textSecondary)
                        .tracking(1)
                }
            }
            .frame(width: 240, height: 240)
            .padding(.top, AppTheme.spacingLG)

            // Sub-metrics card
            subMetricsCard

            // Insight card
            insightCard
        }
    }

    // MARK: - Sub-Metrics Card

    private var subMetricsCard: some View {
        VStack(spacing: 0) {
            recoveryMetricRow(
                icon: "waveform.path.ecg",
                title: "HEART RATE VARIABILITY",
                currentValue: todayHRV,
                baseline: baselineHRV,
                format: .integer,
                higherIsBetter: true,
                isFirst: true
            )
            Divider().overlay(AppColors.backgroundTertiary)
            recoveryMetricRow(
                icon: "heart.fill",
                title: "RESTING HEART RATE",
                currentValue: todayRHR,
                baseline: baselineRHR,
                format: .integer,
                higherIsBetter: false
            )
            Divider().overlay(AppColors.backgroundTertiary)
            recoveryMetricRow(
                icon: "lungs.fill",
                title: "RESPIRATORY RATE",
                currentValue: todayRespRate,
                baseline: baselineRespRate,
                format: .oneDecimal,
                higherIsBetter: false
            )
            Divider().overlay(AppColors.backgroundTertiary)
            recoveryMetricRow(
                icon: "moon.fill",
                title: "SLEEP PERFORMANCE",
                currentValue: todaySleepPerf,
                baseline: baselineSleepPerf,
                format: .percent,
                higherIsBetter: true
            )

            // Footer: "Today vs. last 30 days"
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

    private enum MetricFormat {
        case integer, oneDecimal, percent
    }

    private func recoveryMetricRow(
        icon: String,
        title: String,
        currentValue: Double,
        baseline: Double,
        format: MetricFormat,
        higherIsBetter: Bool,
        isFirst: Bool = false
    ) -> some View {
        let isWorse: Bool = {
            if higherIsBetter { return currentValue < baseline }
            return currentValue > baseline
        }()
        let arrowUp = higherIsBetter ? (currentValue >= baseline) : (currentValue > baseline)
        let arrowColor = isWorse ? AppColors.recoveryYellow : AppColors.recoveryGreen

        let formattedCurrent: String = {
            switch format {
            case .integer: return currentValue > 0 ? "\(Int(currentValue))" : "--"
            case .oneDecimal: return currentValue > 0 ? currentValue.formattedOneDecimal : "--"
            case .percent: return currentValue > 0 ? "\(Int(currentValue))%" : "--%"
            }
        }()

        let formattedBaseline: String = {
            switch format {
            case .integer: return baseline > 0 ? "\(Int(baseline))" : "--"
            case .oneDecimal: return baseline > 0 ? baseline.formattedOneDecimal : "--"
            case .percent: return baseline > 0 ? "\(Int(baseline))%" : "--%"
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
                    if currentValue > 0 && baseline > 0 {
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

    // MARK: - Insight Card

    private var insightCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text(insightText)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textPrimary)
                .fixedSize(horizontal: false, vertical: true)

            Button(action: {}) {
                HStack(spacing: AppTheme.spacingXS) {
                    Text("EXPLORE YOUR RECOVERY INSIGHTS")
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

    private var insightText: String {
        guard baselineHRV > 0, todayHRV > 0 else {
            return "Wear your device to sleep tonight for a full recovery analysis."
        }

        let hrvPctChange = ((todayHRV - baselineHRV) / baselineHRV) * 100
        let direction = hrvPctChange >= 0 ? "higher" : "lower"
        let pctStr = "\(Int(abs(hrvPctChange)))%"

        var text = "Your HRV is \(pctStr) \(direction) than usual"

        if hrvPctChange < -10 {
            text += " which can be due to stress, dehydration, or other lifestyle factors. Take it easy to let your body fully recover."
        } else if hrvPctChange > 10 {
            text += " indicating good recovery. Your body is primed for performance today."
        } else {
            text += ". Your metrics are within normal range. Maintain your current habits for consistent recovery."
        }

        return text
    }

    // MARK: - Module 2: Recovery Insights

    private var recoveryInsightsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingLG) {
            // Recovery Insights card
            Button(action: {}) {
                VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                    HStack {
                        Image(systemName: "lightbulb.fill")
                            .font(.system(size: 16))
                            .foregroundStyle(AppColors.textSecondary)
                        Text("RECOVERY INSIGHTS")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundStyle(AppColors.textPrimary)
                            .tracking(0.5)
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 12))
                            .foregroundStyle(AppColors.textTertiary)
                    }

                    Text("Some of your behaviors from yesterday may have affected your Recovery score today.")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                        .fixedSize(horizontal: false, vertical: true)

                    // Behavior tags
                    behaviorTagsView
                }
                .cardStyle()
            }
            .buttonStyle(.plain)
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingLG)
        }
    }

    private var behaviorTags: [(label: String, positive: Bool)] {
        var tags: [(String, Bool)] = []

        // Heuristic-based behavior insights from yesterday's data
        if let yesterday = yesterdayMetric {
            if yesterday.strainScore > 13 {
                tags.append(("13+ Strain", false))
            }
            if let sleepPerf = yesterday.sleepPerformance, sleepPerf < 80 {
                tags.append(("Low Sleep", false))
            }
            if yesterday.strainScore > 0, yesterday.strainScore <= 8 {
                tags.append(("Light Activity", true))
            }
        }

        if todayHRV > baselineHRV && baselineHRV > 0 {
            tags.append(("Good HRV", true))
        }
        if todayRHR < baselineRHR && baselineRHR > 0 {
            tags.append(("Low RHR", true))
        }
        if todayRHR > baselineRHR * 1.1 && baselineRHR > 0 {
            tags.append(("Elevated RHR", false))
        }

        // Always show at least some tags for demo purposes
        if tags.isEmpty {
            tags = [("Caffeine", true), ("13+ Strain", false), ("Early Workout", false)]
        }

        return tags
    }

    private var behaviorTagsView: some View {
        let tags = behaviorTags
        let visibleTags = Array(tags.prefix(3))
        let remaining = tags.count - visibleTags.count

        return VStack(alignment: .leading, spacing: AppTheme.spacingXS) {
            HStack(spacing: AppTheme.spacingSM) {
                ForEach(Array(visibleTags.enumerated()), id: \.offset) { _, tag in
                    behaviorTag(label: tag.label, positive: tag.positive)
                }
            }

            if remaining > 0 {
                Text("+\(remaining) more")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(AppColors.backgroundTertiary)
                    .clipShape(Capsule())
            }
        }
    }

    private func behaviorTag(label: String, positive: Bool) -> some View {
        HStack(spacing: 4) {
            Image(systemName: positive ? "arrowtriangle.up.fill" : "arrowtriangle.down.fill")
                .font(.system(size: 8))
            Text(label)
                .font(.system(size: 13, weight: .semibold))
        }
        .foregroundStyle(positive ? AppColors.recoveryGreen : AppColors.recoveryYellow)
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(
            (positive ? AppColors.recoveryGreen : AppColors.recoveryYellow).opacity(0.15)
        )
        .clipShape(Capsule())
    }

    // MARK: - Weekly Trends

    private var weeklyTrendsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingLG) {
            Text("Weekly Trends")
                .font(AppTypography.heading1)
                .foregroundStyle(AppColors.textPrimary)
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingXL)

            // Module 3: Recovery
            weeklyChartCard(title: "RECOVERY") {
                weeklyRecoveryChart
            }

            // Module 4: HRV
            weeklyChartCard(title: "HEART RATE VARIABILITY") {
                weeklyHRVChart
            }

            // Module 5: RHR
            weeklyChartCard(title: "RESTING HEART RATE") {
                weeklyRHRChart
            }

            // Module 6: Respiratory Rate
            weeklyChartCard(title: "RESPIRATORY RATE") {
                weeklyRespiratoryRateChart
            }

            // Module 7: Sleep Performance
            weeklyChartCard(title: "SLEEP PERFORMANCE") {
                weeklySleepPerformanceChart
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

    // MARK: - Weekly Charts

    private var weeklyRecoveryChart: some View {
        let data = weekMetrics.map { metric -> (id: UUID, date: Date, score: Double, isToday: Bool) in
            let score = metric.recoveryScore ?? 0
            return (id: metric.id, date: metric.date, score: score, isToday: metric.date.isToday)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", 0),
                        yEnd: .value("End", 100)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Recovery", item.score)
                )
                .foregroundStyle(recoveryBarColor(score: item.score))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...100)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var weeklyHRVChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.hrvRMSSD ?? 0, isToday: $0.date.isToday) }
        let minVal = (data.map(\.value).min() ?? 20) - 5
        let maxVal = (data.map(\.value).max() ?? 50) + 5
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", minVal),
                        yEnd: .value("End", maxVal)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("HRV", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .lineStyle(StrokeStyle(lineWidth: 2))

                PointMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("HRV", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .symbolSize(40)
            }
        }
        .chartYScale(domain: minVal...maxVal)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var weeklyRHRChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.restingHeartRate ?? 0, isToday: $0.date.isToday) }
        let minVal = (data.map(\.value).min() ?? 50) - 5
        let maxVal = (data.map(\.value).max() ?? 80) + 5
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", minVal),
                        yEnd: .value("End", maxVal)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("RHR", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .lineStyle(StrokeStyle(lineWidth: 2))

                PointMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("RHR", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .symbolSize(40)
            }
        }
        .chartYScale(domain: minVal...maxVal)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var weeklyRespiratoryRateChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.respiratoryRate ?? 0, isToday: $0.date.isToday) }
        let minVal = (data.map(\.value).min() ?? 14) - 1
        let maxVal = (data.map(\.value).max() ?? 18) + 1
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", minVal),
                        yEnd: .value("End", maxVal)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("RR", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .lineStyle(StrokeStyle(lineWidth: 2))

                PointMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("RR", item.value)
                )
                .foregroundStyle(AppColors.sleepLight)
                .symbolSize(40)
            }
        }
        .chartYScale(domain: minVal...maxVal)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var weeklySleepPerformanceChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.sleepPerformance ?? 0, isToday: $0.date.isToday) }
        return Chart {
            ForEach(data, id: \.id) { item in
                if item.isToday {
                    RectangleMark(
                        x: .value("Day", item.date, unit: .day),
                        yStart: .value("Start", 0),
                        yEnd: .value("End", 100)
                    )
                    .foregroundStyle(AppColors.textTertiary.opacity(0.1))
                }

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Sleep", item.value)
                )
                .foregroundStyle(AppColors.sleepLight.opacity(0.6))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...100)
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Helpers

    private func recoveryBarColor(score: Double) -> Color {
        switch score {
        case 67...100: return AppColors.recoveryGreen
        case 34..<67: return AppColors.recoveryYellow
        default: return AppColors.recoveryRed
        }
    }
}

// MARK: - Preview

#Preview {
    RecoveryDashboardView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
