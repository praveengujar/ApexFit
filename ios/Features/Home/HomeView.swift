import SwiftUI
import SwiftData

struct HomeView: View {
    @Environment(DataLoadingCoordinator.self) private var dataLoadingCoordinator
    @Environment(\.modelContext) private var modelContext

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    @Query(sort: \JournalEntry.date, order: .reverse)
    private var journalEntries: [JournalEntry]

    @State private var selectedDate = Date()
    @State private var showSleepDashboard = false
    @State private var showRecoveryDashboard = false
    @State private var showStrainDashboard = false
    @State private var showAddActivity = false
    @State private var showStartActivity = false
    @State private var showProfile = false
    @State private var showDeviceHealth = false
    @State private var gaugeRowInitialOffset: CGFloat? = nil
    @State private var gaugeRowCurrentOffset: CGFloat = 0

    private var selectedMetric: DailyMetric? {
        let startOfDay = Calendar.current.startOfDay(for: selectedDate)
        return metrics.first { Calendar.current.isDate($0.date, inSameDayAs: startOfDay) }
    }

    private var weekMetrics: [DailyMetric] {
        let calendar = Calendar.current
        let endOfWeek = calendar.startOfDay(for: selectedDate)
        guard let startOfWeek = calendar.date(byAdding: .day, value: -6, to: endOfWeek) else { return [] }
        return metrics.filter { $0.date >= startOfWeek && $0.date <= calendar.date(byAdding: .day, value: 1, to: endOfWeek)! }
            .sorted { $0.date < $1.date }
    }

    private var weekJournalEntries: [JournalEntry] {
        let calendar = Calendar.current
        guard let weekStart = calendar.date(from: calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: selectedDate)) else { return [] }
        guard let weekEnd = calendar.date(byAdding: .day, value: 7, to: weekStart) else { return [] }
        return journalEntries.filter { $0.date >= weekStart && $0.date < weekEnd }
    }

    // Distance (in pts) over which the gauge row collapses
    private let collapseDistance: CGFloat = 120

    private var collapseProgress: CGFloat {
        guard let initial = gaugeRowInitialOffset else { return 0 }
        let scrolled = initial - gaugeRowCurrentOffset
        guard scrolled > 0 else { return 0 }
        return CGFloat(min(1, scrolled / collapseDistance))
    }

    private var isPinned: Bool { collapseProgress >= 1.0 }

    var body: some View {
        NavigationStack {
            ZStack(alignment: .top) {
                ScrollView {
                    VStack(spacing: AppTheme.spacingMD) {
                        // 1. Header Bar
                        DateNavigationHeader(selectedDate: $selectedDate, streak: journalStreak, onProfileTap: { showProfile = true }, onWatchTap: { showDeviceHealth = true })

                        // 2. Three Equal Gauges
                        DashboardGaugeRow(
                            metric: selectedMetric,
                            collapseProgress: collapseProgress,
                            onSleepTap: { showSleepDashboard = true },
                            onRecoveryTap: { showRecoveryDashboard = true },
                            onStrainTap: { showStrainDashboard = true }
                        )
                        .background(
                            GeometryReader { geo in
                                Color.clear.preference(
                                    key: GaugeOffsetKey.self,
                                    value: geo.frame(in: .named("scroll")).minY
                                )
                            }
                        )
                        .opacity(isPinned ? 0 : 1)

                        // 3. Health Monitor + Stress Monitor
                        monitorCards

                        // 4. My Day
                        myDaySection

                        // 5. Tonight's Sleep
                        TonightsSleepCard(
                            recommendedBedtime: recommendedBedtime,
                            alarmTime: nil,
                            alarmEnabled: false,
                            onEditAlarm: {},
                            onTap: {}
                        )

                        // 6. My Journal
                        JournalWeekCard(
                            journalEntries: weekJournalEntries,
                            currentDate: selectedDate,
                            onTap: {},
                            onInsightsTap: {}
                        )

                        // 7. My Plan
                        MyPlanCard(
                            planName: planName,
                            daysLeft: planDaysLeft,
                            progressPercent: planProgress,
                            onTap: {}
                        )

                        // 8. My Dashboard (Vitals)
                        vitalMetrics

                        // 9. Stress Monitor Chart
                        StressChartCard(
                            dataPoints: stressDataPoints,
                            activities: stressActivities,
                            lastUpdated: selectedMetric?.computedAt,
                            currentScore: selectedMetric?.stressAverage,
                            currentLevel: stressLevel
                        )

                        // 10. Strain & Recovery Weekly Chart
                        WeeklyStrainRecoveryChart(
                            weekData: weeklyChartData,
                            todayIndex: todayWeekIndex
                        )

                        // 11. Bottom Metric Rows
                        bottomMetrics

                        Spacer(minLength: AppTheme.spacingXL)
                    }
                    .padding(.horizontal, AppTheme.spacingMD)
                    .padding(.top, AppTheme.spacingSM)
                }
                .coordinateSpace(name: "scroll")
                .refreshable {
                    await dataLoadingCoordinator.forceRefresh(modelContext: modelContext)
                }
                .onPreferenceChange(GaugeOffsetKey.self) { value in
                    if gaugeRowInitialOffset == nil {
                        gaugeRowInitialOffset = value
                    }
                    gaugeRowCurrentOffset = value
                }

                // Pinned compact gauge row
                if isPinned {
                    DashboardGaugeRow(
                        metric: selectedMetric,
                        collapseProgress: 1.0,
                        onSleepTap: { showSleepDashboard = true },
                        onRecoveryTap: { showRecoveryDashboard = true },
                        onStrainTap: { showStrainDashboard = true }
                    )
                    .transition(.opacity)
                }
            }
            .background(AppColors.backgroundPrimary)
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $showSleepDashboard) {
                SleepDashboardView()
            }
            .navigationDestination(isPresented: $showRecoveryDashboard) {
                RecoveryDashboardView()
            }
            .navigationDestination(isPresented: $showStrainDashboard) {
                StrainDashboardView()
            }
            .navigationDestination(isPresented: $showProfile) {
                ProfileView()
            }
            .sheet(isPresented: $showAddActivity) {
                AddActivityView()
            }
            .fullScreenCover(isPresented: $showStartActivity) {
                StartActivityView()
            }
            .sheet(isPresented: $showDeviceHealth) {
                DeviceHealthSheet()
            }
        }
    }

    // MARK: - Monitor Cards (Screenshot 1)

    private var monitorCards: some View {
        HStack(spacing: AppTheme.spacingSM) {
            HealthMonitorCard(
                metricsInRange: healthMetricsInRange,
                totalMetrics: healthMetricsTotal,
                onTap: {}
            )
            StressMonitorCard(
                stressScore: selectedMetric?.stressAverage,
                lastUpdated: selectedMetric?.computedAt,
                onTap: {}
            )
        }
    }

    // MARK: - My Day Section (Screenshot 1)

    private var myDaySection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("My Day")
                    .font(AppTypography.heading2)
                    .foregroundStyle(AppColors.textPrimary)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "plus")
                        .font(.title3)
                        .foregroundStyle(AppColors.textPrimary)
                        .frame(width: 36, height: 36)
                        .background(AppColors.backgroundTertiary)
                        .clipShape(Circle())
                }
            }

            DailyOutlookCard(onTap: {})

            todaysActivities

            // Add/Start Activity Buttons
            HStack(spacing: AppTheme.spacingSM) {
                Button(action: { showAddActivity = true }) {
                    HStack {
                        Image(systemName: "plus")
                        Text("ADD ACTIVITY")
                    }
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.backgroundTertiary)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
                }

                Button(action: { showStartActivity = true }) {
                    HStack {
                        Image(systemName: "timer")
                        Text("START ACTIVITY")
                    }
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.backgroundTertiary)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
                }
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    private var todaysActivities: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("TODAY'S ACTIVITIES")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)
                Spacer()
                Image(systemName: "arrow.up.right.and.arrow.down.left")
                    .font(.caption)
                    .foregroundStyle(AppColors.textSecondary)
            }

            // Sleep activity
            if let sleepSession = selectedMetric?.sleepSessions.first(where: { $0.isMainSleep }) {
                DashboardActivityCard(session: sleepSession)
            }

            // Workout activities
            if let workouts = selectedMetric?.workouts {
                ForEach(workouts, id: \.id) { workout in
                    DashboardActivityCard(workout: workout)
                }
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundTertiary.opacity(0.5))
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
    }

    // MARK: - Vital Metrics (Screenshot 3)

    private var vitalMetrics: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("My Dashboard")
                    .font(AppTypography.heading2)
                    .foregroundStyle(AppColors.textPrimary)
                Spacer()
                Button(action: {}) {
                    HStack(spacing: AppTheme.spacingXS) {
                        Text("CUSTOMIZE")
                            .font(AppTypography.labelSmall)
                        Image(systemName: "pencil")
                            .font(.caption)
                    }
                    .foregroundStyle(AppColors.textSecondary)
                }
            }

            DashboardMetricRow(
                icon: "waveform.path.ecg",
                label: "HEART RATE VARIABILITY",
                value: selectedMetric?.hrvRMSSD.map { String(Int($0)) } ?? "--",
                baseline: hrvBaseline.map { String(Int($0)) },
                trendUp: metricTrend(current: selectedMetric?.hrvRMSSD, baseline: hrvBaseline)
            )

            DashboardMetricRow(
                icon: "heart.fill",
                label: "RESTING HEART RATE",
                value: selectedMetric?.restingHeartRate.map { String(Int($0)) } ?? "--",
                baseline: rhrBaseline.map { String(Int($0)) },
                trendUp: metricTrend(current: selectedMetric?.restingHeartRate, baseline: rhrBaseline, invertedBetter: true)
            )

            DashboardMetricRow(
                icon: "lungs.fill",
                label: "VO\u{2082} MAX",
                value: selectedMetric?.vo2Max.map { String(Int($0)) } ?? "--",
                baseline: vo2Baseline.map { String(Int($0)) },
                trendUp: metricTrend(current: selectedMetric?.vo2Max, baseline: vo2Baseline)
            )
        }
    }

    // MARK: - Bottom Metrics (Screenshot 4)

    private var bottomMetrics: some View {
        VStack(spacing: AppTheme.spacingSM) {
            DashboardMetricRow(
                icon: "heart.text.square",
                label: "HR ZONES 1-3 (WEEKLY)",
                value: formattedZoneTime(weeklyZone13Minutes),
                baseline: formattedZoneTime(baselineZone13Minutes),
                trendUp: metricTrend(current: weeklyZone13Minutes, baseline: baselineZone13Minutes)
            )

            DashboardMetricRow(
                icon: "heart.text.square.fill",
                label: "HR ZONES 4-5 (WEEKLY)",
                value: formattedZoneTime(weeklyZone45Minutes),
                baseline: formattedZoneTime(baselineZone45Minutes),
                trendUp: metricTrend(current: weeklyZone45Minutes, baseline: baselineZone45Minutes)
            )

            DashboardMetricRow(
                icon: "flame.fill",
                label: "CALORIES",
                value: selectedMetric.map { "\(Int($0.activeCalories).formattedWithComma)" } ?? "--",
                baseline: caloriesBaseline.map { "\(Int($0).formattedWithComma)" },
                trendUp: metricTrend(current: selectedMetric?.activeCalories, baseline: caloriesBaseline)
            )

            DashboardMetricRow(
                icon: "figure.walk",
                label: "STEPS",
                value: selectedMetric.map { "\($0.steps.formattedWithComma)" } ?? "--",
                baseline: stepsBaseline.map { "\(Int($0).formattedWithComma)" },
                trendUp: metricTrend(current: selectedMetric.map { Double($0.steps) }, baseline: stepsBaseline)
            )
        }
    }

    // MARK: - Computed Properties

    private var journalStreak: Int {
        var streak = 0
        let calendar = Calendar.current
        var checkDate = calendar.startOfDay(for: Date())
        for entry in journalEntries.sorted(by: { $0.date > $1.date }) {
            if calendar.isDate(entry.date, inSameDayAs: checkDate) && entry.isComplete {
                streak += 1
                checkDate = calendar.date(byAdding: .day, value: -1, to: checkDate) ?? checkDate
            } else {
                break
            }
        }
        return streak
    }

    private var recommendedBedtime: Date? {
        let calendar = Calendar.current
        var components = calendar.dateComponents([.year, .month, .day], from: selectedDate)
        components.hour = 22
        components.minute = 0
        return calendar.date(from: components)
    }

    private var planName: String { "STRAIN COACH PLAN" }
    private var planDaysLeft: Int { 5 }
    private var planProgress: Double { 72 }

    // MARK: - Health Monitor

    private var healthMetricsInRange: Int {
        var count = 0
        if let hrv = selectedMetric?.hrvRMSSD, let base = hrvBaseline { if abs(hrv - base) / base < 0.2 { count += 1 } }
        if let rhr = selectedMetric?.restingHeartRate, let base = rhrBaseline { if abs(rhr - base) / base < 0.15 { count += 1 } }
        if let spo2 = selectedMetric?.spo2, spo2 >= 95 { count += 1 }
        if let resp = selectedMetric?.respiratoryRate, resp >= 12 && resp <= 20 { count += 1 }
        if let vo2 = selectedMetric?.vo2Max, let base = vo2Baseline { if abs(vo2 - base) / base < 0.1 { count += 1 } }
        return count
    }

    private var healthMetricsTotal: Int { 5 }

    // MARK: - Baselines (28-day rolling averages)

    private var recentMetrics: [DailyMetric] {
        let calendar = Calendar.current
        guard let start = calendar.date(byAdding: .day, value: -28, to: selectedDate) else { return [] }
        return metrics.filter { $0.date >= start && $0.date < calendar.startOfDay(for: selectedDate) && $0.isComputed }
    }

    private var hrvBaseline: Double? {
        let values = recentMetrics.compactMap(\.hrvRMSSD)
        return values.isEmpty ? nil : values.reduce(0, +) / Double(values.count)
    }

    private var rhrBaseline: Double? {
        let values = recentMetrics.compactMap(\.restingHeartRate)
        return values.isEmpty ? nil : values.reduce(0, +) / Double(values.count)
    }

    private var vo2Baseline: Double? {
        let values = recentMetrics.compactMap(\.vo2Max)
        return values.isEmpty ? nil : values.reduce(0, +) / Double(values.count)
    }

    private var caloriesBaseline: Double? {
        let values = recentMetrics.map(\.activeCalories)
        return values.isEmpty ? nil : values.reduce(0, +) / Double(values.count)
    }

    private var stepsBaseline: Double? {
        let values = recentMetrics.map { Double($0.steps) }
        return values.isEmpty ? nil : values.reduce(0, +) / Double(values.count)
    }

    // MARK: - Weekly Zone Aggregation

    private var weeklyZone13Minutes: Double? {
        let workouts = weekMetrics.flatMap(\.workouts)
        guard !workouts.isEmpty else { return nil }
        return workouts.reduce(0) { $0 + $1.zone1Minutes + $1.zone2Minutes + $1.zone3Minutes }
    }

    private var weeklyZone45Minutes: Double? {
        let workouts = weekMetrics.flatMap(\.workouts)
        guard !workouts.isEmpty else { return nil }
        return workouts.reduce(0) { $0 + $1.zone4Minutes + $1.zone5Minutes }
    }

    private var baselineZone13Minutes: Double? { weeklyZone13Minutes.map { $0 * 0.6 } }
    private var baselineZone45Minutes: Double? { weeklyZone45Minutes.map { $0 * 0.4 } }

    // MARK: - Stress

    private var stressLevel: String? {
        guard let score = selectedMetric?.stressAverage else { return nil }
        switch score {
        case ..<1: return "LOW"
        case 1..<2: return "MEDIUM"
        case 2..<2.5: return "HIGH"
        default: return "VERY HIGH"
        }
    }

    private var stressDataPoints: [StressDataPoint] { [] }
    private var stressActivities: [ActivityMarker] { [] }

    // MARK: - Weekly Chart Data

    private var weeklyChartData: [WeeklyDataPoint] {
        weekMetrics.map { metric in
            WeeklyDataPoint(
                date: metric.date,
                strain: metric.strainScore,
                recoveryScore: metric.recoveryScore,
                recoveryZone: metric.recoveryZone
            )
        }
    }

    private var todayWeekIndex: Int? {
        weeklyChartData.firstIndex { Calendar.current.isDateInToday($0.date) }
    }

    // MARK: - Helpers

    private func metricTrend(current: Double?, baseline: Double?, invertedBetter: Bool = false) -> Bool? {
        guard let current, let baseline, baseline > 0 else { return nil }
        let isHigher = current > baseline
        return invertedBetter ? !isHigher : isHigher
    }

    private func formattedZoneTime(_ minutes: Double?) -> String {
        guard let minutes else { return "--" }
        let hours = Int(minutes) / 60
        let mins = Int(minutes) % 60
        return "\(hours):\(String(format: "%02d", mins))"
    }
}

// MARK: - Preference Key

private struct GaugeOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
