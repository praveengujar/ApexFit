import SwiftUI
import SwiftData
import Charts

struct LongevityDashboardView: View {
    @Environment(DataLoadingCoordinator.self) private var dataLoadingCoordinator
    @Environment(\.modelContext) private var modelContext

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var selectedWeekEnd: Date = Date()
    @State private var longevityResult: LongevityEngine.Result?
    @State private var weeklyTrend: [LongevityEngine.Result] = []
    @State private var expandedMetricID: LongevityEngine.MetricID?
    @State private var isLoading = true

    // Scroll tracking for sticky header
    @State private var heroOffset: CGFloat = 0
    @State private var heroInitialOffset: CGFloat?
    private let collapseThreshold: CGFloat = 250

    private var isPinned: Bool {
        guard let initial = heroInitialOffset else { return false }
        return (initial - heroOffset) > collapseThreshold
    }

    private var userProfile: UserProfile? { profiles.first }
    private var chronologicalAge: Double { Double(userProfile?.age ?? 30) }

    var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                ScrollView {
                    VStack(spacing: 0) {
                        // Header
                        weekNavigationHeader
                            .padding(.top, AppTheme.spacingSM)

                        // Hero blob section
                        heroSection
                            .background(
                                GeometryReader { geo in
                                    Color.clear.preference(
                                        key: LongevityScrollOffsetKey.self,
                                        value: geo.frame(in: .named("longevityScroll")).minY
                                    )
                                }
                            )
                            .padding(.vertical, AppTheme.spacingLG)

                        // Pace of Aging gauge
                        if let result = longevityResult {
                            PaceOfAgingGauge(pace: result.paceOfAging)
                                .padding(.horizontal, AppTheme.spacingMD)
                                .padding(.bottom, AppTheme.spacingLG)
                        }

                        // Weekly insight card
                        insightCard
                            .padding(.horizontal, AppTheme.spacingMD)
                            .padding(.bottom, AppTheme.spacingLG)

                        // Average legend
                        averageLegend
                            .padding(.horizontal, AppTheme.spacingMD)
                            .padding(.bottom, AppTheme.spacingSM)

                        // Sleep section
                        metricSection(category: .sleep)
                            .padding(.horizontal, AppTheme.spacingMD)

                        // Strain section
                        metricSection(category: .strain)
                            .padding(.horizontal, AppTheme.spacingMD)

                        // Fitness section
                        metricSection(category: .fitness)
                            .padding(.horizontal, AppTheme.spacingMD)

                        // Trend View
                        trendViewSection
                            .padding(.horizontal, AppTheme.spacingMD)
                            .padding(.bottom, AppTheme.spacingXXL)
                    }
                }
                .coordinateSpace(name: "longevityScroll")
                .onPreferenceChange(LongevityScrollOffsetKey.self) { value in
                    if heroInitialOffset == nil { heroInitialOffset = value }
                    heroOffset = value
                }

                // Sticky compact header
                if isPinned, let result = longevityResult {
                    LongevityStickyHeader(
                        yearsYoungerOlder: result.yearsYoungerOlder,
                        zyvaAge: result.zyvaAge,
                        paceOfAging: result.paceOfAging
                    )
                    .transition(.opacity)
                    .animation(AppTheme.animationDefault, value: isPinned)
                }
            }
            .refreshable {
                await dataLoadingCoordinator.forceRefresh(modelContext: modelContext)
            }
            .background(AppColors.backgroundPrimary)
            .navigationBarHidden(true)
            .task { loadData() }
            .onChange(of: selectedWeekEnd) { _, _ in loadData() }
        }
    }

    // MARK: - Week Navigation Header

    private var weekNavigationHeader: some View {
        VStack(spacing: AppTheme.spacingXS) {
            Text("LONGEVITY")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(AppColors.textPrimary)
                .tracking(1)

            Text("NEXT UPDATE IN \(daysUntilNextMonday) DAYS")
                .font(.system(size: 11))
                .foregroundStyle(AppColors.textTertiary)
                .tracking(0.5)

            HStack(spacing: AppTheme.spacingMD) {
                Button { navigateWeek(by: -1) } label: {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                }

                Text(weekRangeString)
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)

                Button { navigateWeek(by: 1) } label: {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(isCurrentWeek ? AppColors.textTertiary.opacity(0.3) : AppColors.textSecondary)
                }
                .disabled(isCurrentWeek)
            }
            .padding(.top, AppTheme.spacingXS)
        }
    }

    // MARK: - Hero Section

    private var heroSection: some View {
        TimelineView(.animation(minimumInterval: 1.0 / 30.0)) { timeline in
            OrganicBlobView(
                zyvaAge: longevityResult?.zyvaAge ?? chronologicalAge,
                yearsYoungerOlder: longevityResult?.yearsYoungerOlder ?? 0,
                animationPhase: timeline.date.timeIntervalSinceReferenceDate * 0.3,
                size: 260
            )
        }
        .frame(height: 280)
    }

    // MARK: - Insight Card

    private var insightCard: some View {
        Group {
            if let result = longevityResult {
                VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                    Text(result.overallInsightTitle)
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.textPrimary)

                    Text(result.overallInsightBody)
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                        .fixedSize(horizontal: false, vertical: true)

                    HStack {
                        Text("EXPLORE YOUR WEEKLY INSIGHTS")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(AppColors.primaryBlue)
                        Image(systemName: "arrow.right")
                            .font(.system(size: 10))
                            .foregroundStyle(AppColors.primaryBlue)
                    }
                    .padding(.top, AppTheme.spacingXS)
                }
                .cardStyle()
            }
        }
    }

    // MARK: - Average Legend

    private var averageLegend: some View {
        HStack(spacing: AppTheme.spacingMD) {
            Spacer()
            HStack(spacing: 4) {
                Image(systemName: "arrowtriangle.down.fill")
                    .font(.system(size: 8))
                    .foregroundStyle(AppColors.textSecondary)
                Text("6 Month avg.")
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textSecondary)
            }

            Rectangle()
                .fill(AppColors.textTertiary)
                .frame(width: 1, height: 14)

            HStack(spacing: 4) {
                Image(systemName: "arrowtriangle.up.fill")
                    .font(.system(size: 8))
                    .foregroundStyle(AppColors.textTertiary)
                Text("30 Day avg.")
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
    }

    // MARK: - Metric Sections

    private func metricSection(category: LongevityEngine.Category) -> some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text(category.displayName)
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)
                .padding(.top, AppTheme.spacingLG)

            if let results = longevityResult?.metricResults.filter({ $0.id.category == category }) {
                ForEach(results, id: \.id) { result in
                    LongevityMetricCard(
                        result: result,
                        isExpanded: expandedMetricID == result.id,
                        onToggle: {
                            withAnimation(AppTheme.animationDefault) {
                                expandedMetricID = expandedMetricID == result.id ? nil : result.id
                            }
                        }
                    )
                }
            }
        }
    }

    // MARK: - Trend View

    private var trendViewSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingLG) {
            Text("Trend View")
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)
                .padding(.top, AppTheme.spacingLG)

            // Zyva Age Trend
            trendCard(title: "ZYVA AGE TREND") {
                zyvaAgeTrendChart
            }

            // Pace of Aging Trend
            trendCard(title: "PACE OF AGING TREND") {
                paceOfAgingTrendChart
            }
        }
    }

    private func trendCard<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
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
    }

    private var zyvaAgeTrendChart: some View {
        Chart {
            ForEach(weeklyTrend.indices, id: \.self) { i in
                let result = weeklyTrend[i]
                // Zyva Age line
                LineMark(
                    x: .value("Week", result.weekEnd),
                    y: .value("Age", result.zyvaAge)
                )
                .foregroundStyle(AppColors.longevityGreen)
                .lineStyle(StrokeStyle(lineWidth: 2))
                .symbol {
                    Circle().fill(AppColors.longevityGreen).frame(width: 6)
                }

                // Chronological Age line (flat)
                LineMark(
                    x: .value("Week", result.weekEnd),
                    y: .value("Chrono", result.chronologicalAge)
                )
                .foregroundStyle(AppColors.textTertiary)
                .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 3]))
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                AxisValueLabel()
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: .weekOfYear)) { value in
                AxisValueLabel(format: .dateTime.month(.abbreviated).day())
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .chartLegend(position: .top, alignment: .leading) {
            HStack(spacing: AppTheme.spacingMD) {
                HStack(spacing: 4) {
                    Circle().fill(AppColors.longevityGreen).frame(width: 8)
                    Text("YOUR ZYVA AGE")
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                }
                HStack(spacing: 4) {
                    RoundedRectangle(cornerRadius: 1).fill(AppColors.textTertiary).frame(width: 12, height: 2)
                    Text("CHRONOLOGICAL AGE")
                        .font(.system(size: 10, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }
        }
        .frame(height: 180)
    }

    private var paceOfAgingTrendChart: some View {
        Chart {
            ForEach(weeklyTrend.indices, id: \.self) { i in
                let result = weeklyTrend[i]
                LineMark(
                    x: .value("Week", result.weekEnd),
                    y: .value("Pace", result.paceOfAging)
                )
                .foregroundStyle(AppColors.primaryBlue)
                .lineStyle(StrokeStyle(lineWidth: 2))
                .symbol {
                    Circle().fill(AppColors.primaryBlue).frame(width: 6)
                }
            }

            // Reference line at 1.0x
            RuleMark(y: .value("Baseline", 1.0))
                .foregroundStyle(AppColors.textTertiary.opacity(0.5))
                .lineStyle(StrokeStyle(lineWidth: 1, dash: [4, 3]))
        }
        .chartYScale(domain: -1...3)
        .chartYAxis {
            AxisMarks(values: [-1.0, 0.0, 1.0, 2.0, 3.0]) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                AxisValueLabel {
                    if let v = value.as(Double.self) {
                        Text(String(format: "%.1fx", v))
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: .weekOfYear)) { value in
                AxisValueLabel(format: .dateTime.month(.abbreviated).day())
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .frame(height: 180)
    }

    // MARK: - Data Loading

    private func loadData() {
        guard let profile = userProfile else {
            isLoading = false
            return
        }

        let age = Double(profile.age ?? 30)

        // Build inputs from DailyMetric history
        let sixMonthCutoff = selectedWeekEnd.daysAgo(180)
        let thirtyDayCutoff = selectedWeekEnd.daysAgo(30)
        let weekCutoff = selectedWeekEnd.daysAgo(7)

        let metrics180 = allMetrics.filter { $0.date >= sixMonthCutoff && $0.date <= selectedWeekEnd }
        let metrics30 = allMetrics.filter { $0.date >= thirtyDayCutoff && $0.date <= selectedWeekEnd }
        let metricsWeek = allMetrics.filter { $0.date >= weekCutoff && $0.date <= selectedWeekEnd }

        let inputs = buildInputs(metrics180: metrics180, metrics30: metrics30, metricsWeek: metricsWeek, maxHR: Double(profile.estimatedMaxHR))

        longevityResult = LongevityEngine.compute(
            chronologicalAge: age,
            inputs: inputs,
            weekStart: selectedWeekEnd.daysAgo(7),
            weekEnd: selectedWeekEnd
        )

        // Build 8-week trend
        weeklyTrend = (0..<8).reversed().compactMap { weeksAgo in
            let weekEnd = selectedWeekEnd.daysAgo(weeksAgo * 7)
            let m180 = allMetrics.filter { $0.date >= weekEnd.daysAgo(180) && $0.date <= weekEnd }
            let m30 = allMetrics.filter { $0.date >= weekEnd.daysAgo(30) && $0.date <= weekEnd }
            let mWeek = allMetrics.filter { $0.date >= weekEnd.daysAgo(7) && $0.date <= weekEnd }
            guard !m180.isEmpty else { return nil }
            let weekInputs = buildInputs(metrics180: m180, metrics30: m30, metricsWeek: mWeek, maxHR: Double(profile.estimatedMaxHR))
            return LongevityEngine.compute(
                chronologicalAge: age,
                inputs: weekInputs,
                weekStart: weekEnd.daysAgo(7),
                weekEnd: weekEnd
            )
        }

        isLoading = false
    }

    private func buildInputs(
        metrics180: [DailyMetric],
        metrics30: [DailyMetric],
        metricsWeek: [DailyMetric],
        maxHR: Double
    ) -> [LongevityEngine.MetricInput] {
        var inputs: [LongevityEngine.MetricInput] = []

        // Sleep Consistency
        let sc180 = metrics180.compactMap(\.sleepConsistency)
        let sc30 = metrics30.compactMap(\.sleepConsistency)
        inputs.append(.init(id: .sleepConsistency,
                            sixMonthAvg: sc180.isEmpty ? nil : sc180.reduce(0, +) / Double(sc180.count),
                            thirtyDayAvg: sc30.isEmpty ? nil : sc30.reduce(0, +) / Double(sc30.count)))

        // Hours of Sleep
        let sh180 = metrics180.compactMap(\.sleepDurationHours)
        let sh30 = metrics30.compactMap(\.sleepDurationHours)
        inputs.append(.init(id: .hoursOfSleep,
                            sixMonthAvg: sh180.isEmpty ? nil : sh180.reduce(0, +) / Double(sh180.count),
                            thirtyDayAvg: sh30.isEmpty ? nil : sh30.reduce(0, +) / Double(sh30.count)))

        // Weekly zone times (aggregate from workouts)
        let weeklyZ13_180 = weeklyZoneAvg(metrics: metrics180, zone13: true) / 60.0  // convert to hours
        let weeklyZ13_30 = weeklyZoneAvg(metrics: metrics30, zone13: true) / 60.0
        inputs.append(.init(id: .hrZones1to3Weekly,
                            sixMonthAvg: weeklyZ13_180 > 0 ? weeklyZ13_180 : nil,
                            thirtyDayAvg: weeklyZ13_30 > 0 ? weeklyZ13_30 : nil))

        let weeklyZ45_180 = weeklyZoneAvg(metrics: metrics180, zone13: false) / 60.0
        let weeklyZ45_30 = weeklyZoneAvg(metrics: metrics30, zone13: false) / 60.0
        inputs.append(.init(id: .hrZones4to5Weekly,
                            sixMonthAvg: weeklyZ45_180 > 0 ? weeklyZ45_180 : nil,
                            thirtyDayAvg: weeklyZ45_30 > 0 ? weeklyZ45_30 : nil))

        // Strength activity weekly (hours)
        let str180 = weeklyStrengthAvg(metrics: metrics180) / 60.0
        let str30 = weeklyStrengthAvg(metrics: metrics30) / 60.0
        inputs.append(.init(id: .strengthActivityWeekly,
                            sixMonthAvg: str180 > 0 ? str180 : nil,
                            thirtyDayAvg: str30 > 0 ? str30 : nil))

        // Steps (daily average)
        let steps180 = metrics180.map { Double($0.steps) }
        let steps30 = metrics30.map { Double($0.steps) }
        inputs.append(.init(id: .dailySteps,
                            sixMonthAvg: steps180.isEmpty ? nil : steps180.reduce(0, +) / Double(steps180.count),
                            thirtyDayAvg: steps30.isEmpty ? nil : steps30.reduce(0, +) / Double(steps30.count)))

        // VO2 Max
        let vo2_180 = metrics180.compactMap(\.vo2Max)
        let vo2_30 = metrics30.compactMap(\.vo2Max)
        inputs.append(.init(id: .vo2Max,
                            sixMonthAvg: vo2_180.isEmpty ? nil : vo2_180.reduce(0, +) / Double(vo2_180.count),
                            thirtyDayAvg: vo2_30.isEmpty ? nil : vo2_30.reduce(0, +) / Double(vo2_30.count)))

        // RHR
        let rhr180 = metrics180.compactMap(\.restingHeartRate)
        let rhr30 = metrics30.compactMap(\.restingHeartRate)
        inputs.append(.init(id: .restingHeartRate,
                            sixMonthAvg: rhr180.isEmpty ? nil : rhr180.reduce(0, +) / Double(rhr180.count),
                            thirtyDayAvg: rhr30.isEmpty ? nil : rhr30.reduce(0, +) / Double(rhr30.count)))

        // Lean Body Mass
        let lbm180 = metrics180.compactMap(\.leanBodyMassPct)
        let lbm30 = metrics30.compactMap(\.leanBodyMassPct)
        inputs.append(.init(id: .leanBodyMass,
                            sixMonthAvg: lbm180.isEmpty ? nil : lbm180.reduce(0, +) / Double(lbm180.count),
                            thirtyDayAvg: lbm30.isEmpty ? nil : lbm30.reduce(0, +) / Double(lbm30.count)))

        return inputs
    }

    /// Compute average weekly zone 1-3 or 4-5 minutes from a set of metrics.
    private func weeklyZoneAvg(metrics: [DailyMetric], zone13: Bool) -> Double {
        guard !metrics.isEmpty else { return 0 }
        let totalDays = max(1, metrics.count)
        let totalMinutes = metrics.reduce(0.0) { acc, m in
            acc + m.workouts.reduce(0.0) { a, w in
                if zone13 {
                    return a + w.zone1Minutes + w.zone2Minutes + w.zone3Minutes
                } else {
                    return a + w.zone4Minutes + w.zone5Minutes
                }
            }
        }
        // Convert to weekly: (total minutes / total days) * 7
        return (totalMinutes / Double(totalDays)) * 7.0
    }

    /// Compute average weekly strength activity minutes.
    private func weeklyStrengthAvg(metrics: [DailyMetric]) -> Double {
        guard !metrics.isEmpty else { return 0 }
        let totalDays = max(1, metrics.count)
        let totalMinutes = metrics.reduce(0.0) { acc, m in
            acc + m.workouts.filter(\.isStrengthWorkout).reduce(0.0) { $0 + $1.durationMinutes }
        }
        return (totalMinutes / Double(totalDays)) * 7.0
    }

    // MARK: - Helpers

    private var weekRangeString: String {
        let start = selectedWeekEnd.daysAgo(6)
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d"
        return "\(formatter.string(from: start)) - \(formatter.string(from: selectedWeekEnd))"
    }

    private var isCurrentWeek: Bool {
        Calendar.current.isDate(selectedWeekEnd, inSameDayAs: Date()) ||
        selectedWeekEnd > Date()
    }

    private var daysUntilNextMonday: Int {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())
        let weekday = calendar.component(.weekday, from: today)
        // Sunday=1, Monday=2, ..., Saturday=7
        let daysToMonday = weekday == 1 ? 1 : (9 - weekday)
        return daysToMonday
    }

    private func navigateWeek(by offset: Int) {
        selectedWeekEnd = Calendar.current.date(byAdding: .day, value: offset * 7, to: selectedWeekEnd) ?? selectedWeekEnd
    }
}

// MARK: - Preference Key

private struct LongevityScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
