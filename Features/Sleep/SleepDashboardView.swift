import SwiftUI
import SwiftData
import Charts

struct SleepDashboardView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    // MARK: - Derived Data

    private var todayMetric: DailyMetric? {
        allMetrics.first { $0.date.isToday } ?? allMetrics.first { $0.date.isYesterday }
    }

    private var latestSleep: SleepSession? {
        todayMetric?.sleepSessions
            .filter { $0.isMainSleep }
            .sorted { $0.startDate > $1.startDate }
            .first
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

    // MARK: - Computed Sleep Metrics

    private var sleepPerformance: Double {
        todayMetric?.sleepScore ?? todayMetric?.sleepPerformance ?? 0
    }

    private var hoursVsNeeded: Double {
        todayMetric?.sleepPerformance ?? 0
    }

    private var sleepConsistency: Double {
        todayMetric?.sleepConsistency ?? 0
    }

    private var sleepEfficiency: Double {
        todayMetric?.sleepEfficiencyPct ?? latestSleep?.sleepEfficiency ?? 0
    }

    private var sleepStressPercent: Double {
        guard let stress = todayMetric?.stressAverage else { return 0 }
        return min(100, (stress / 3.0) * 100)
    }

    private var sleepDurationHours: Double {
        todayMetric?.sleepDurationHours ?? latestSleep.map { $0.totalSleepMinutes / 60.0 } ?? 0
    }

    private var sleepNeedHours: Double {
        todayMetric?.sleepNeedHours ?? HealthKitConstants.defaultSleepBaselineHours
    }

    private var sleepDebtHours: Double {
        todayMetric?.sleepDebtHours ?? 0
    }

    // Average stage percentages from past 30 days for "typical range"
    private var avgAwakePct: Double { averageStagePercent(\.awakeMinutes) }
    private var avgLightPct: Double { averageStagePercent(\.lightSleepMinutes) }
    private var avgDeepPct: Double { averageStagePercent(\.deepSleepMinutes) }
    private var avgRemPct: Double { averageStagePercent(\.remSleepMinutes) }

    private func averageStagePercent(_ keyPath: KeyPath<SleepSession, Double>) -> Double {
        let sessions = past30DaysMetrics.compactMap { metric in
            metric.sleepSessions.first { $0.isMainSleep }
        }
        guard !sessions.isEmpty else { return 0 }
        let percentages = sessions.compactMap { session -> Double? in
            guard session.totalSleepMinutes > 0 else { return nil }
            return (session[keyPath: keyPath] / session.totalSleepMinutes) * 100
        }
        guard !percentages.isEmpty else { return 0 }
        return percentages.reduce(0, +) / Double(percentages.count)
    }

    // MARK: - Body

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    // Module 1: Sleep Performance Overview
                    sleepPerformanceSection

                    // Module 2: Last Night's Sleep
                    lastNightsSleepSection

                    // Module 3a: Hours vs. Needed
                    hoursVsNeededSection

                    // Module 3b: Sleep Consistency
                    sleepConsistencySection

                    // Module 4a: Sleep Efficiency
                    sleepEfficiencySection

                    // Module 4b: Sleep Stress
                    sleepStressSection

                    // Modules 5-9: Weekly Trends
                    weeklyTrendsSection
                }
                .padding(.bottom, AppTheme.spacingXL)
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

    // MARK: - Module 1: Sleep Performance Overview

    private var sleepPerformanceSection: some View {
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
                    .trim(from: 0, to: (sleepPerformance / 100.0).clamped(to: 0...1) * 0.75)
                    .stroke(
                        LinearGradient(
                            colors: [AppColors.sleepLight.opacity(0.6), AppColors.sleepLight],
                            startPoint: .leading,
                            endPoint: .trailing
                        ),
                        style: StrokeStyle(lineWidth: 14, lineCap: .round)
                    )
                    .rotationEffect(.degrees(135))
                    .animation(.easeInOut(duration: 0.8), value: sleepPerformance)

                VStack(spacing: 2) {
                    HStack(alignment: .firstTextBaseline, spacing: 0) {
                        Text(sleepPerformance > 0 ? "\(Int(sleepPerformance))" : "--")
                            .font(.system(size: 64, weight: .bold, design: .rounded))
                            .foregroundStyle(AppColors.textPrimary)
                        Text("%")
                            .font(.system(size: 28, weight: .bold, design: .rounded))
                            .foregroundStyle(AppColors.textPrimary)
                    }
                    Text("SLEEP\nPERFORMANCE")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .tracking(1)
                }
            }
            .frame(width: 240, height: 240)
            .padding(.top, AppTheme.spacingLG)

            // Page dots
            HStack(spacing: 6) {
                Circle().fill(performanceStatusColor(for: sleepPerformance)).frame(width: 8, height: 8)
                Circle().fill(AppColors.textTertiary.opacity(0.4)).frame(width: 8, height: 8)
                Circle().fill(AppColors.textTertiary.opacity(0.4)).frame(width: 8, height: 8)
            }

            // Sub-metrics card
            VStack(spacing: 0) {
                subMetricRow(
                    icon: "clock.fill",
                    title: "HOURS VS. NEEDED",
                    value: hoursVsNeeded,
                    isFirst: true
                )
                Divider().overlay(AppColors.backgroundTertiary)
                subMetricRow(
                    icon: "moon.stars.fill",
                    title: "SLEEP CONSISTENCY",
                    value: sleepConsistency
                )
                Divider().overlay(AppColors.backgroundTertiary)
                subMetricRow(
                    icon: "waveform.path.ecg",
                    title: "SLEEP EFFICIENCY",
                    value: sleepEfficiency
                )
                Divider().overlay(AppColors.backgroundTertiary)
                subMetricRow(
                    icon: "brain.head.profile",
                    title: "HIGH SLEEP STRESS",
                    value: sleepStressPercent,
                    invertedScale: true
                )

                // Legend
                HStack(spacing: AppTheme.spacingLG) {
                    legendDot(color: performanceStatusColor(for: 30), label: "Poor")
                    legendDot(color: AppColors.textTertiary, label: "Sufficient")
                    legendDot(color: AppColors.recoveryGreen, label: "Optimal")
                }
                .padding(.vertical, AppTheme.spacingSM)
                .padding(.horizontal, AppTheme.cardPadding)
            }
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
            .padding(.horizontal, AppTheme.spacingMD)

            // Insight card
            insightCard
        }
    }

    private func subMetricRow(icon: String, title: String, value: Double, isFirst: Bool = false, invertedScale: Bool = false) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundStyle(AppColors.textSecondary)
                .frame(width: 24)

            Text(title)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(AppColors.textPrimary)
                .tracking(0.5)

            Spacer()

            // Status bar
            statusBar(value: value, invertedScale: invertedScale)

            Text(value > 0 ? "\(Int(value))%" : "--%")
                .font(.system(size: 17, weight: .semibold, design: .rounded))
                .foregroundStyle(AppColors.textPrimary)
                .frame(width: 50, alignment: .trailing)
        }
        .padding(.horizontal, AppTheme.cardPadding)
        .padding(.vertical, 14)
    }

    private func statusBar(value: Double, invertedScale: Bool = false) -> some View {
        let status: MetricStatus = invertedScale
            ? (value < 15 ? .optimal : (value < 30 ? .sufficient : .poor))
            : (value >= 85 ? .optimal : (value >= 60 ? .sufficient : .poor))

        return HStack(spacing: 2) {
            RoundedRectangle(cornerRadius: 2)
                .fill(status == .poor ? performanceStatusColor(for: 30) : AppColors.textTertiary.opacity(0.2))
                .frame(width: 24, height: 4)
            RoundedRectangle(cornerRadius: 2)
                .fill(status == .sufficient ? AppColors.textTertiary : AppColors.textTertiary.opacity(0.2))
                .frame(width: 24, height: 4)
            RoundedRectangle(cornerRadius: 2)
                .fill(status == .optimal ? AppColors.recoveryGreen : AppColors.textTertiary.opacity(0.2))
                .frame(width: 24, height: 4)
        }
    }

    private var insightCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text(sleepInsightText)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textPrimary)
                .fixedSize(horizontal: false, vertical: true)

            NavigationLink(destination: SleepPlannerView()) {
                HStack(spacing: AppTheme.spacingXS) {
                    Text("EXPLORE YOUR SLEEP INSIGHTS")
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

    private var sleepInsightText: String {
        if sleepPerformance >= 85 {
            return "Great sleep! Your sleep metrics are looking solid. Keep up your current sleep habits for optimal recovery."
        } else if sleepPerformance >= 60 {
            return "Some aspects of your sleep need improvement. Maintaining Sleep Efficiency while working on Sleep Stress could help you improve your overall sleep."
        } else {
            return "Your sleep performance is below optimal. Focus on getting more hours of sleep and maintaining a consistent sleep schedule."
        }
    }

    // MARK: - Module 2: Last Night's Sleep

    private var lastNightsSleepSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            // Header
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("Last Night's Sleep")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)
                    Text("Today vs. prior 30 days")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
                Spacer()
                Button(action: {}) {
                    HStack(spacing: 4) {
                        Text("EDIT")
                            .font(.system(size: 13, weight: .bold))
                            .tracking(0.5)
                        Image(systemName: "pencil")
                            .font(.system(size: 12))
                    }
                    .foregroundStyle(AppColors.textSecondary)
                }
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingXL)

            // Hours of Sleep card with HR chart
            hoursOfSleepCard

            // Sleep Stages breakdown
            sleepStagesCard

            // Restorative Sleep
            restorativeSleepRow
        }
    }

    private var hoursOfSleepCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("HOURS OF SLEEP")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "info.circle")
                        .font(.system(size: 14))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text(formatDurationColonStyle(hours: sleepDurationHours))
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                if sleepDurationHours < sleepNeedHours {
                    Image(systemName: "arrowtriangle.down.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryYellow)
                } else {
                    Image(systemName: "arrowtriangle.up.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryGreen)
                }
            }

            Text(formatDurationColonStyle(hours: sleepNeedHours))
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textTertiary)

            // Heart rate chart during sleep (using sleep stages as proxy)
            if let sleep = latestSleep, !sleep.stages.isEmpty {
                HypnogramView(stages: sleep.stages.sorted { $0.startDate < $1.startDate })
                    .padding(.vertical, AppTheme.spacingXS)
            } else {
                // Placeholder chart area
                RoundedRectangle(cornerRadius: 4)
                    .fill(AppColors.backgroundTertiary)
                    .frame(height: 120)
                    .overlay(
                        Text("Sleep data chart")
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    )
            }
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
    }

    private var sleepStagesCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("TYPICAL RANGE")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
                Spacer()
                Text("DURATION")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
                Text(formatDurationColonStyle(hours: (latestSleep?.timeInBedMinutes ?? 0) / 60.0))
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                    .frame(width: 50, alignment: .trailing)
            }

            if let sleep = latestSleep {
                let totalWithAwake = sleep.totalSleepMinutes + sleep.awakeMinutes
                let awakePct = totalWithAwake > 0 ? (sleep.awakeMinutes / totalWithAwake) * 100 : 0

                sleepStageRow(
                    label: "AWAKE",
                    percentage: awakePct,
                    duration: sleep.awakeMinutes,
                    color: AppColors.sleepAwake.opacity(0.7),
                    avgPercentage: avgAwakePct
                )

                sleepStageRow(
                    label: "LIGHT",
                    percentage: sleep.lightSleepPercentage,
                    duration: sleep.lightSleepMinutes,
                    color: AppColors.sleepLight,
                    avgPercentage: avgLightPct
                )

                sleepStageRow(
                    label: "SWS (DEEP)",
                    percentage: sleep.deepSleepPercentage,
                    duration: sleep.deepSleepMinutes,
                    color: Color(hex: "#E876B0"),
                    avgPercentage: avgDeepPct
                )

                sleepStageRow(
                    label: "REM",
                    percentage: sleep.remSleepPercentage,
                    duration: sleep.remSleepMinutes,
                    color: AppColors.sleepDeep,
                    avgPercentage: avgRemPct
                )
            } else {
                Text("No sleep stage data available")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textTertiary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingLG)
            }
        }
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.vertical, AppTheme.spacingSM)
    }

    private func sleepStageRow(label: String, percentage: Double, duration: Double, color: Color, avgPercentage: Double) -> some View {
        VStack(spacing: 4) {
            HStack {
                Circle()
                    .stroke(AppColors.textPrimary, lineWidth: 1.5)
                    .frame(width: 20, height: 20)

                Text(label)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)

                Text("\(Int(percentage))%")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(color)

                Spacer()

                Text(formatDurationColonStyle(minutes: duration))
                    .font(.system(size: 15, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                    .frame(width: 50, alignment: .trailing)
            }

            // Progress bar with typical range
            GeometryReader { geometry in
                let width = geometry.size.width
                let currentWidth = (percentage / 100.0) * width
                let avgWidth = (avgPercentage / 100.0) * width

                ZStack(alignment: .leading) {
                    // Typical range (hatched background)
                    RoundedRectangle(cornerRadius: 3)
                        .fill(AppColors.textTertiary.opacity(0.15))
                        .frame(width: width)

                    // Typical range indicator line
                    if avgWidth > 0 {
                        Rectangle()
                            .fill(AppColors.textTertiary.opacity(0.5))
                            .frame(width: 1.5, height: 12)
                            .offset(x: min(avgWidth, width - 2))
                    }

                    // Current value bar
                    RoundedRectangle(cornerRadius: 3)
                        .fill(color)
                        .frame(width: max(currentWidth, 2))
                }
            }
            .frame(height: 10)
        }
        .padding(.vertical, 4)
    }

    private var restorativeSleepRow: some View {
        let restorativeMinutes = (latestSleep?.deepSleepMinutes ?? 0) + (latestSleep?.remSleepMinutes ?? 0)
        let restorativeHours = restorativeMinutes / 60.0
        let targetHours = sleepNeedHours * 0.45 // ~45% target

        return HStack {
            HStack(spacing: 6) {
                RoundedRectangle(cornerRadius: 2)
                    .fill(LinearGradient(colors: [Color(hex: "#E876B0"), AppColors.sleepDeep], startPoint: .leading, endPoint: .trailing))
                    .frame(width: 16, height: 16)
                Text("RESTORATIVE SLEEP")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 0) {
                HStack(spacing: 4) {
                    Text(formatDurationColonStyle(hours: restorativeHours))
                        .font(.system(size: 17, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    if restorativeHours < targetHours {
                        Image(systemName: "arrowtriangle.down.fill")
                            .font(.system(size: 8))
                            .foregroundStyle(AppColors.recoveryYellow)
                    }
                }
                Text(formatDurationColonStyle(hours: targetHours))
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.vertical, AppTheme.spacingSM)
        .background(AppColors.backgroundCard)
    }

    // MARK: - Module 3a: Hours vs. Needed

    private var hoursVsNeededSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("HOURS VS. NEEDED")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "info.circle")
                        .font(.system(size: 14))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            // Percentage display
            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text("\(Int(hoursVsNeeded))%")
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                if hoursVsNeeded < 85 {
                    Image(systemName: "arrowtriangle.down.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryYellow)
                }
            }
            Text("100%")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textTertiary)

            // Hours of sleep bar
            HStack {
                Image(systemName: "moon.fill")
                    .font(.system(size: 10))
                    .foregroundStyle(AppColors.textTertiary)
                Text("HOURS OF SLEEP")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
                Spacer()
                Text(formatDurationColonStyle(hours: sleepDurationHours))
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
            }

            // Progress bar showing actual vs needed
            GeometryReader { geometry in
                let fraction = sleepNeedHours > 0 ? min(sleepDurationHours / sleepNeedHours, 1.0) : 0
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(AppColors.textTertiary.opacity(0.2))
                    RoundedRectangle(cornerRadius: 4)
                        .fill(
                            LinearGradient(
                                colors: [AppColors.primaryBlue, AppColors.sleepLight],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .frame(width: geometry.size.width * fraction)
                }
            }
            .frame(height: 12)

            // Sleep needed
            HStack {
                Text("SLEEP NEEDED")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
                Spacer()
                Text(formatDurationColonStyle(hours: sleepNeedHours))
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
            }

            Divider().overlay(AppColors.backgroundTertiary)

            // Breakdown
            let baselineNeed = HealthKitConstants.defaultSleepBaselineHours
            let strainSupplement = computeStrainSupplement()
            let debtRepayment = sleepDebtHours * 0.2

            sleepNeedBreakdownRow(icon: "square.fill", color: AppColors.textTertiary.opacity(0.5), label: "Healthy Minimum", value: baselineNeed)
            sleepNeedBreakdownRow(icon: "square.fill", color: AppColors.primaryBlue, label: "Recent Strain", value: strainSupplement, prefix: "+")
            sleepNeedBreakdownRow(icon: "square.fill", color: AppColors.textTertiary.opacity(0.3), label: "Sleep Debt", value: debtRepayment, prefix: "+")
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.top, AppTheme.spacingLG)
    }

    private func sleepNeedBreakdownRow(icon: String, color: Color, label: String, value: Double, prefix: String = "") -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.system(size: 8))
                .foregroundStyle(color)
            Text(label)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text("\(prefix)\(formatDurationColonStyle(hours: value))")
                .font(.system(size: 13, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.textPrimary)
        }
    }

    // MARK: - Module 3b: Sleep Consistency

    private var sleepConsistencySection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("SLEEP CONSISTENCY")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "info.circle")
                        .font(.system(size: 14))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text("\(Int(sleepConsistency))%")
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                if sleepConsistency > 0 {
                    Image(systemName: "arrowtriangle.up.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryGreen)
                }
            }

            // Optimal bed/waketime labels
            HStack {
                Spacer()
                Text("- - -  OPTIMAL BED/WAKETIME")
                    .font(.system(size: 10, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
            }

            // Bedtime/waketime chart
            consistencyChart
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.top, AppTheme.spacingLG)
    }

    private var consistencyChart: some View {
        let weekSleepData = weekMetrics.compactMap { metric -> (date: Date, bedtime: Date, wakeTime: Date)? in
            guard let sleep = metric.sleepSessions.first(where: { $0.isMainSleep }) else { return nil }
            return (date: metric.date, bedtime: sleep.startDate, wakeTime: sleep.endDate)
        }

        return VStack(spacing: 0) {
            if weekSleepData.isEmpty {
                Text("Not enough data for consistency chart")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textTertiary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingLG)
            } else {
                Chart {
                    ForEach(weekSleepData, id: \.date) { data in
                        let bedHour = hourOfDay(data.bedtime)
                        let wakeHour = hourOfDay(data.wakeTime)

                        // Bedtime bar (top, evening hours)
                        BarMark(
                            x: .value("Day", data.date, unit: .day),
                            yStart: .value("Start", 24),
                            yEnd: .value("Bed", bedHour)
                        )
                        .foregroundStyle(AppColors.sleepLight.opacity(0.6))
                        .cornerRadius(3)

                        // Wake time bar (bottom, morning hours)
                        BarMark(
                            x: .value("Day", data.date, unit: .day),
                            yStart: .value("Wake", wakeHour),
                            yEnd: .value("End", 0)
                        )
                        .foregroundStyle(AppColors.sleepLight.opacity(0.6))
                        .cornerRadius(3)

                        // Show bedtime/waketime labels for last entry
                        if data.date == weekSleepData.last?.date {
                            PointMark(
                                x: .value("Day", data.date, unit: .day),
                                y: .value("Bed", bedHour)
                            )
                            .foregroundStyle(.clear)
                            .annotation(position: .trailing) {
                                Text(data.bedtime.hourMinuteString)
                                    .font(.system(size: 10, weight: .semibold))
                                    .foregroundStyle(AppColors.textPrimary)
                            }

                            PointMark(
                                x: .value("Day", data.date, unit: .day),
                                y: .value("Wake", wakeHour)
                            )
                            .foregroundStyle(.clear)
                            .annotation(position: .trailing) {
                                Text(data.wakeTime.hourMinuteString)
                                    .font(.system(size: 10, weight: .semibold))
                                    .foregroundStyle(AppColors.textPrimary)
                            }
                        }
                    }
                }
                .chartYScale(domain: 0...24)
                .chartYAxis {
                    AxisMarks(values: [5, 9, 13, 17, 21]) { value in
                        AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                            .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                        AxisValueLabel {
                            if let hour = value.as(Double.self) {
                                Text(formatHourLabel(hour))
                                    .font(AppTypography.caption)
                                    .foregroundStyle(AppColors.textTertiary)
                            }
                        }
                    }
                }
                .chartXAxis {
                    AxisMarks(values: .stride(by: .day)) { value in
                        AxisValueLabel {
                            if let date = value.as(Date.self) {
                                VStack(spacing: 0) {
                                    Text(date.dayOfWeek)
                                        .font(.system(size: 10))
                                        .foregroundStyle(date.isToday ? AppColors.textPrimary : AppColors.textTertiary)
                                }
                            }
                        }
                    }
                }
                .frame(height: 200)
            }
        }
    }

    // MARK: - Module 4a: Sleep Efficiency

    private var sleepEfficiencySection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("SLEEP EFFICIENCY")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "info.circle")
                        .font(.system(size: 14))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text("\(Int(sleepEfficiency))%")
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                if sleepEfficiency < 90 {
                    Image(systemName: "arrowtriangle.down.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryYellow)
                }
            }
            Text("90%")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textTertiary)

            // Asleep timeline bar
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text("ASLEEP")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                        .tracking(0.5)
                    Spacer()
                    Text(formatDurationColonStyle(hours: sleepDurationHours))
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                }

                // Asleep blocks visualization
                if let sleep = latestSleep {
                    SleepBlocksBar(session: sleep, showAsleep: true)
                        .frame(height: 16)
                }
            }

            // Awake timeline bar
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text("AWAKE")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                        .tracking(0.5)
                    Spacer()
                    Text(formatDurationColonStyle(minutes: latestSleep?.awakeMinutes ?? 0))
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                }

                if let sleep = latestSleep {
                    SleepBlocksBar(session: sleep, showAsleep: false)
                        .frame(height: 16)
                }
            }

            Divider().overlay(AppColors.backgroundTertiary)

            // Wake Events
            HStack {
                Image(systemName: "square.fill")
                    .font(.system(size: 8))
                    .foregroundStyle(AppColors.textTertiary)
                Text("WAKE EVENTS")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Text("\(latestSleep?.awakenings ?? 0)")
                    .font(.system(size: 17, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
            }
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.top, AppTheme.spacingLG)
    }

    // MARK: - Module 4b: Sleep Stress

    private var sleepStressSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("SLEEP STRESS")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Spacer()
                Button(action: {}) {
                    Image(systemName: "info.circle")
                        .font(.system(size: 14))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            HStack(alignment: .firstTextBaseline, spacing: 4) {
                Text("\(Int(sleepStressPercent))%")
                    .font(.system(size: 28, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                if sleepStressPercent > 15 {
                    Image(systemName: "arrowtriangle.up.fill")
                        .font(.system(size: 10))
                        .foregroundStyle(AppColors.recoveryYellow)
                }
            }

            let baselineStress = averageStressBaseline()
            Text("\(Int(baselineStress))%")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textTertiary)

            // Stress line chart placeholder
            if let sleep = latestSleep {
                sleepStressChart(sleep: sleep)
            }

            // High / Medium / Low breakdown
            let highPct = min(sleepStressPercent, 100)
            let lowPct = max(100 - sleepStressPercent * 3, 0)
            let medPct = 100 - highPct - lowPct

            stressBreakdownRow(label: "HIGH", percentage: highPct, duration: (latestSleep?.totalSleepMinutes ?? 0) * highPct / 100, color: AppColors.zone4)
            stressBreakdownRow(label: "MEDIUM", percentage: medPct, duration: (latestSleep?.totalSleepMinutes ?? 0) * medPct / 100, color: AppColors.teal)
            stressBreakdownRow(label: "LOW", percentage: lowPct, duration: (latestSleep?.totalSleepMinutes ?? 0) * lowPct / 100, color: AppColors.primaryBlue)
        }
        .cardStyle()
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.top, AppTheme.spacingLG)
    }

    private func sleepStressChart(sleep: SleepSession) -> some View {
        // Generate simulated stress curve from sleep stages
        let points = generateStressPoints(sleep: sleep)

        return Chart {
            // Stress threshold line
            RuleMark(y: .value("Threshold", 3.0))
                .foregroundStyle(AppColors.textTertiary.opacity(0.3))
                .lineStyle(StrokeStyle(lineWidth: 0.5))

            ForEach(Array(points.enumerated()), id: \.offset) { _, point in
                LineMark(
                    x: .value("Time", point.time),
                    y: .value("Stress", point.value)
                )
                .foregroundStyle(AppColors.teal)
                .lineStyle(StrokeStyle(lineWidth: 1.5))

                AreaMark(
                    x: .value("Time", point.time),
                    yStart: .value("Base", 0),
                    yEnd: .value("Stress", point.value)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [AppColors.teal.opacity(0.15), AppColors.teal.opacity(0.02)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
            }
        }
        .chartYScale(domain: 0...3.5)
        .chartYAxis {
            AxisMarks(values: [0, 1, 2, 3]) { value in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                AxisValueLabel {
                    if let v = value.as(Double.self) {
                        Text(v.formattedOneDecimal)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
        }
        .chartXAxis {
            AxisMarks(values: .automatic(desiredCount: 4)) { value in
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

    private func stressBreakdownRow(label: String, percentage: Double, duration: Double, color: Color) -> some View {
        VStack(spacing: 4) {
            HStack {
                Text(label)
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)
                Text("\(Int(percentage))%")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(color)
                Spacer()
                Text(formatDurationColonStyle(minutes: duration))
                    .font(.system(size: 15, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
            }

            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 3)
                        .fill(AppColors.textTertiary.opacity(0.15))
                    RoundedRectangle(cornerRadius: 3)
                        .fill(color)
                        .frame(width: max(geometry.size.width * (percentage / 100.0), 2))
                }
            }
            .frame(height: 10)
        }
    }

    // MARK: - Modules 5-9: Weekly Trends

    private var weeklyTrendsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingLG) {
            Text("Weekly Trends")
                .font(AppTypography.heading1)
                .foregroundStyle(AppColors.textPrimary)
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingXL)

            // Module 5: Sleep Performance
            weeklyChartCard(title: "SLEEP PERFORMANCE") {
                sleepPerformanceWeeklyChart
            }

            // Module 5: Hours vs Needed (Hours)
            weeklyChartCard(title: "HOURS VS. NEEDED (HOURS)") {
                hoursVsNeededWeeklyChart
            }

            // Module 6: Hours vs Needed (%)
            weeklyChartCard(title: "HOURS VS. NEEDED (%)") {
                hoursVsNeededPercentWeeklyChart
            }

            // Module 6: Restorative Sleep (Hours)
            weeklyChartCard(title: "RESTORATIVE SLEEP (HOURS)") {
                restorativeSleepWeeklyChart
            }

            // Module 7: Sleep Consistency
            weeklyChartCard(title: "SLEEP CONSISTENCY") {
                sleepConsistencyWeeklyChart
            }

            // Module 7: Time in Bed
            weeklyChartCard(title: "TIME IN BED") {
                timeInBedWeeklyChart
            }

            // Module 8: Sleep Efficiency
            weeklyChartCard(title: "SLEEP EFFICIENCY") {
                sleepEfficiencyWeeklyChart
            }

            // Module 8: Sleep Debt
            weeklyChartCard(title: "SLEEP DEBT") {
                sleepDebtWeeklyChart
            }

            // Module 9: Sleep Stress
            weeklyChartCard(title: "SLEEP STRESS") {
                sleepStressWeeklyChart
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

    private var sleepPerformanceWeeklyChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.sleepPerformance ?? 0, isToday: $0.date.isToday) }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Performance", item.value)
                )
                .foregroundStyle(AppColors.sleepLight.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...100)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var hoursVsNeededWeeklyChart: some View {
        let actualData = weekMetrics.compactMap { m -> (id: String, date: Date, hours: Double)? in
            guard let h = m.sleepDurationHours else { return nil }
            return (id: m.id.uuidString + "a", date: m.date, hours: h)
        }
        let needData = weekMetrics.compactMap { m -> (id: String, date: Date, hours: Double)? in
            guard let n = m.sleepNeedHours else { return nil }
            return (id: m.id.uuidString + "n", date: m.date, hours: n)
        }
        return Chart {
            ForEach(actualData, id: \.id) { item in
                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Hours", item.hours),
                    series: .value("Type", "Actual")
                )
                .foregroundStyle(AppColors.textSecondary)
                .lineStyle(StrokeStyle(lineWidth: 2))
            }
            ForEach(needData, id: \.id) { item in
                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Need", item.hours),
                    series: .value("Type", "Needed")
                )
                .foregroundStyle(AppColors.teal)
                .lineStyle(StrokeStyle(lineWidth: 2))
            }
        }
        .chartYScale(domain: 0...12)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var hoursVsNeededPercentWeeklyChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.sleepPerformance ?? 0, isToday: $0.date.isToday) }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Percent", item.value)
                )
                .foregroundStyle(AppColors.sleepLight.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...100)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var restorativeSleepWeeklyChart: some View {
        let data = weekMetrics.map { metric -> (id: UUID, date: Date, deep: Double, rem: Double) in
            let sleep = metric.sleepSessions.first { $0.isMainSleep }
            let deepH = (sleep?.deepSleepMinutes ?? 0) / 60.0
            let remH = (sleep?.remSleepMinutes ?? 0) / 60.0
            return (id: metric.id, date: metric.date, deep: deepH, rem: remH)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Deep", item.deep)
                )
                .foregroundStyle(Color(hex: "#E876B0"))
                .cornerRadius(3)

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("REM", item.rem)
                )
                .foregroundStyle(AppColors.sleepDeep)
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...6)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var sleepConsistencyWeeklyChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, value: $0.sleepConsistency ?? 0, isToday: $0.date.isToday) }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Consistency", item.value)
                )
                .foregroundStyle(AppColors.sleepLight.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...100)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var timeInBedWeeklyChart: some View {
        let data = weekMetrics.compactMap { metric -> (id: UUID, date: Date, bedHour: Double, wakeHour: Double, isToday: Bool)? in
            guard let sleep = metric.sleepSessions.first(where: { $0.isMainSleep }) else { return nil }
            let bed = hourOfDay(sleep.startDate)
            let wake = hourOfDay(sleep.endDate)
            return (id: metric.id, date: metric.date, bedHour: bed, wakeHour: wake, isToday: metric.date.isToday)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    yStart: .value("Max", 24),
                    yEnd: .value("Bed", item.bedHour)
                )
                .foregroundStyle(AppColors.primaryBlue.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    yStart: .value("Wake", item.wakeHour),
                    yEnd: .value("Min", 0)
                )
                .foregroundStyle(AppColors.primaryBlue.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...24)
        .chartYAxis {
            AxisMarks(values: [0, 6, 12, 18, 24]) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                    .foregroundStyle(AppColors.textTertiary.opacity(0.2))
            }
        }
        .sleepChartXAxis(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    private var sleepEfficiencyWeeklyChart: some View {
        let data = weekMetrics.map { metric -> (id: UUID, date: Date, efficiency: Double) in
            let eff = metric.sleepEfficiencyPct ?? metric.sleepSessions.first(where: { $0.isMainSleep })?.sleepEfficiency ?? 0
            return (id: metric.id, date: metric.date, efficiency: eff)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                LineMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Efficiency", item.efficiency)
                )
                .foregroundStyle(AppColors.sleepLight)
                .lineStyle(StrokeStyle(lineWidth: 2))
            }
        }
        .chartYScale(domain: 70...100)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var sleepDebtWeeklyChart: some View {
        let data = weekMetrics.map { (id: $0.id, date: $0.date, debt: $0.sleepDebtHours ?? 0, isToday: $0.date.isToday) }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Debt", item.debt)
                )
                .foregroundStyle(AppColors.sleepLight.opacity(item.isToday ? 0.4 : 0.7))
                .cornerRadius(3)
            }
        }
        .chartYScale(domain: 0...4)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 180)
    }

    private var sleepStressWeeklyChart: some View {
        let data = weekMetrics.map { metric -> (id: UUID, date: Date, low: Double, med: Double, high: Double) in
            let stress = metric.stressAverage ?? 0
            let highPct = min(stress / 3.0, 1.0)
            let lowPct = max(1.0 - stress, 0)
            let medPct = max(1.0 - highPct - lowPct, 0)
            let total = metric.sleepDurationHours ?? 0
            return (id: metric.id, date: metric.date, low: total * lowPct, med: total * medPct, high: total * highPct)
        }
        return Chart {
            ForEach(data, id: \.id) { item in
                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Low", item.low)
                )
                .foregroundStyle(AppColors.primaryBlue)

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("Med", item.med)
                )
                .foregroundStyle(AppColors.teal)

                BarMark(
                    x: .value("Day", item.date, unit: .day),
                    y: .value("High", item.high)
                )
                .foregroundStyle(AppColors.zone4)
            }
        }
        .chartYScale(domain: 0...10)
        .sleepChartAxes(weekMetrics: weekMetrics)
        .frame(height: 200)
    }

    // MARK: - Helpers

    private func performanceStatusColor(for value: Double) -> Color {
        switch value {
        case 85...100: return AppColors.recoveryGreen
        case 60..<85: return AppColors.recoveryYellow
        default: return AppColors.zone4
        }
    }

    private func legendDot(color: Color, label: String) -> some View {
        HStack(spacing: 4) {
            RoundedRectangle(cornerRadius: 2)
                .fill(color)
                .frame(width: 12, height: 4)
            Text(label)
                .font(.system(size: 11))
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    private func formatDurationColonStyle(hours: Double) -> String {
        let totalMinutes = Int(hours * 60)
        let h = totalMinutes / 60
        let m = totalMinutes % 60
        return "\(h):\(String(format: "%02d", m))"
    }

    private func formatDurationColonStyle(minutes: Double) -> String {
        formatDurationColonStyle(hours: minutes / 60.0)
    }

    private func hourOfDay(_ date: Date) -> Double {
        let components = Calendar.current.dateComponents([.hour, .minute], from: date)
        return Double(components.hour ?? 0) + Double(components.minute ?? 0) / 60.0
    }

    private func formatHourLabel(_ hour: Double) -> String {
        let h = Int(hour)
        switch h {
        case 0: return "12AM"
        case 1...11: return "\(h)AM"
        case 12: return "12PM"
        case 13...23: return "\(h - 12)PM"
        default: return ""
        }
    }

    private func computeStrainSupplement() -> Double {
        let strain = todayMetric?.strainScore ?? 0
        switch strain {
        case ..<8: return 0
        case 8..<14: return 0.25
        case 14..<18: return 0.5
        default: return 0.75
        }
    }

    private func averageStressBaseline() -> Double {
        let stresses = past30DaysMetrics.compactMap(\.stressAverage)
        guard !stresses.isEmpty else { return 0 }
        let avg = stresses.reduce(0, +) / Double(stresses.count)
        return min(100, (avg / 3.0) * 100)
    }

    private func generateStressPoints(sleep: SleepSession) -> [StressPoint] {
        var points: [StressPoint] = []
        let startTime = sleep.startDate
        let endTime = sleep.endDate
        let duration = endTime.timeIntervalSince(startTime)
        let stepCount = 60

        for i in 0...stepCount {
            let fraction = Double(i) / Double(stepCount)
            let time = startTime.addingTimeInterval(duration * fraction)

            // Simulate stress curve: higher at start, lower in middle, slight rise at end
            let base = 1.5
            let startEffect = max(0, 1.0 - fraction * 3) * 0.8
            let endEffect = max(0, fraction - 0.7) * 2.0 * 0.6
            let midDip = sin(fraction * .pi) * 0.5
            let noise = Double.random(in: -0.3...0.3)
            let value = max(0, min(3.0, base + startEffect + endEffect - midDip + noise))

            points.append(StressPoint(time: time, value: value))
        }

        return points
    }
}

// MARK: - Supporting Types

private enum MetricStatus {
    case poor, sufficient, optimal
}

private struct StressPoint {
    let time: Date
    let value: Double
}

// MARK: - Sleep Blocks Bar (Efficiency visualization)

struct SleepBlocksBar: View {
    let session: SleepSession
    let showAsleep: Bool

    var body: some View {
        GeometryReader { geometry in
            let totalDuration = session.timeInBedMinutes
            if totalDuration > 0 {
                let sortedStages = session.stages.sorted { $0.startDate < $1.startDate }
                HStack(spacing: 1) {
                    ForEach(Array(sortedStages.enumerated()), id: \.offset) { _, stage in
                        let fraction = stage.durationMinutes / totalDuration
                        let isAsleep = stage.stageType != .awake && stage.stageType != .inBed
                        let shouldShow = showAsleep ? isAsleep : !isAsleep

                        if shouldShow && fraction > 0.005 {
                            RoundedRectangle(cornerRadius: 2)
                                .fill(stageBlockColor(stage.stageType))
                                .frame(width: max(geometry.size.width * fraction, 1))
                        } else if fraction > 0.005 {
                            RoundedRectangle(cornerRadius: 2)
                                .fill(AppColors.textTertiary.opacity(0.1))
                                .frame(width: max(geometry.size.width * fraction, 1))
                        }
                    }
                }
                .clipShape(RoundedRectangle(cornerRadius: 3))
            }
        }
    }

    private func stageBlockColor(_ type: SleepStageType) -> Color {
        switch type {
        case .deep: return AppColors.sleepDeep
        case .rem: return AppColors.sleepREM
        case .light: return AppColors.sleepLight
        case .awake: return AppColors.textTertiary.opacity(0.5)
        case .inBed: return AppColors.textTertiary.opacity(0.1)
        }
    }
}

// MARK: - Chart Axis Modifiers

extension View {
    func sleepChartAxes(weekMetrics: [DailyMetric]) -> some View {
        self
            .chartYAxis {
                AxisMarks(position: .leading) { _ in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [3, 3]))
                        .foregroundStyle(AppColors.textTertiary.opacity(0.2))
                }
            }
            .sleepChartXAxis(weekMetrics: weekMetrics)
    }

    func sleepChartXAxis(weekMetrics: [DailyMetric]) -> some View {
        self
            .chartXAxis {
                AxisMarks(values: .stride(by: .day)) { value in
                    AxisValueLabel {
                        if let date = value.as(Date.self) {
                            VStack(spacing: 0) {
                                Text(date.dayOfWeek)
                                    .font(.system(size: 10))
                                Text("\(Calendar.current.component(.day, from: date))")
                                    .font(.system(size: 10))
                            }
                            .foregroundStyle(date.isToday ? AppColors.textPrimary : AppColors.textTertiary)
                        }
                    }
                }
            }
    }
}

// MARK: - Preview

#Preview {
    SleepDashboardView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
