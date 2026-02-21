import SwiftUI
import SwiftData

// MARK: - Time Period

enum ProfileTimePeriod: String, CaseIterable {
    case oneMonth = "1M"
    case threeMonths = "3M"
    case allTime = "ALL TIME"

    var days: Int? {
        switch self {
        case .oneMonth: return 30
        case .threeMonths: return 90
        case .allTime: return nil
        }
    }
}

// MARK: - Activity Stats

struct ProfileActivityTypeStats: Identifiable {
    let id = UUID()
    let workoutType: String
    let displayName: String
    let count: Int
    let avgStrain: Double
    var proportion: CGFloat
}

// MARK: - Computed State

struct ProfileComputedState {
    // Header
    var displayName: String = ""
    var initials: String = ""
    var age: Int?
    var memberSince: String = ""

    // Achievements
    var level: Int = 0
    var greenRecoveryCount: Int = 0
    var zyvaAge: Double = 0
    var yearsYoungerOlder: Double = 0

    // Streak
    var dayStreak: Int = 0

    // Data Highlights
    var bestSleepPct: Double = 0
    var peakRecoveryPct: Double = 0
    var maxStrain: Double = 0

    // Streaks
    var sleepStreak: Int = 0
    var greenRecoveryStreak: Int = 0
    var strainStreak: Int = 0

    // Notable Stats
    var lowestRHR: Double?
    var highestRHR: Double?
    var lowestHRV: Double?
    var highestHRV: Double?
    var maxHeartRate: Double?
    var longestSleepHours: Double?
    var lowestRecovery: Double?

    // Activity Summary
    var totalActivities: Int = 0
    var activityBreakdown: [ProfileActivityTypeStats] = []
}

// MARK: - Profile View

struct ProfileView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var allMetrics: [DailyMetric]

    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @Environment(\.dismiss) private var dismiss

    @State private var highlightsPeriod: ProfileTimePeriod = .allTime
    @State private var activityPeriod: ProfileTimePeriod = .allTime
    @State private var state = ProfileComputedState()
    @State private var isLoading = true

    private var userProfile: UserProfile? { profiles.first }

    var body: some View {
        VStack(spacing: 0) {
            // Top bar
            HStack {
                Button { dismiss() } label: {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundStyle(AppColors.textPrimary)
                        .frame(width: 32, height: 32)
                }

                Spacer()

                Text("PROFILE")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(2)

                Spacer()

                // Symmetry placeholder
                Color.clear.frame(width: 32, height: 32)
            }
            .padding(.horizontal, AppTheme.spacingSM)
            .padding(.vertical, AppTheme.spacingSM)

            if isLoading {
                Spacer()
                ProgressView()
                    .tint(AppColors.primaryBlue)
                Spacer()
            } else {
                ScrollView {
                    VStack(spacing: AppTheme.spacingLG) {
                        // 1. Header
                        ProfileHeaderView(
                            displayName: state.displayName,
                            initials: state.initials,
                            age: state.age,
                            memberSince: state.memberSince
                        )

                        // 2. Achievement Cards
                        AchievementCardsView(
                            level: state.level,
                            greenRecoveryCount: state.greenRecoveryCount,
                            zyvaAge: state.zyvaAge,
                            yearsYoungerOlder: state.yearsYoungerOlder
                        )

                        // 3. Day Streak
                        DayStreakView(dayStreak: state.dayStreak)

                        // 4. Data Highlights
                        DataHighlightsView(
                            period: $highlightsPeriod,
                            bestSleepPct: state.bestSleepPct,
                            peakRecoveryPct: state.peakRecoveryPct,
                            maxStrain: state.maxStrain
                        )

                        // 5. Streaks
                        StreaksSectionView(
                            sleepStreak: state.sleepStreak,
                            greenRecoveryStreak: state.greenRecoveryStreak,
                            strainStreak: state.strainStreak
                        )

                        // 6. Notable Stats
                        NotableStatsView(
                            lowestRHR: state.lowestRHR,
                            highestRHR: state.highestRHR,
                            lowestHRV: state.lowestHRV,
                            highestHRV: state.highestHRV,
                            maxHeartRate: state.maxHeartRate,
                            longestSleepHours: state.longestSleepHours,
                            lowestRecovery: state.lowestRecovery
                        )

                        // 7. Activity Summary
                        ActivitySummaryView(
                            period: $activityPeriod,
                            totalActivities: state.totalActivities,
                            breakdown: state.activityBreakdown
                        )

                        Spacer(minLength: AppTheme.spacingXL)
                    }
                    .padding(.horizontal, AppTheme.spacingMD)
                }
            }
        }
        .background(AppColors.backgroundPrimary)
        .navigationBarHidden(true)
        .task { computeAll() }
        .onChange(of: highlightsPeriod) { _, newValue in
            computeHighlights(period: newValue)
        }
        .onChange(of: activityPeriod) { _, newValue in
            computeActivitySummary(period: newValue)
        }
    }

    // MARK: - Computation

    private func computeAll() {
        guard let profile = userProfile else {
            isLoading = false
            return
        }

        // Header
        state.displayName = profile.displayName.isEmpty ? "User" : profile.displayName
        let parts = state.displayName.split(separator: " ")
        state.initials = parts.prefix(2).compactMap { $0.first.map(String.init) }.joined()
        state.age = profile.age

        let formatter = DateFormatter()
        formatter.dateFormat = "MMM yyyy"
        state.memberSince = formatter.string(from: profile.createdAt)

        // Level: green recoveries / 6
        let greenCount = allMetrics.filter { $0.recoveryZone == .green }.count
        state.greenRecoveryCount = greenCount
        state.level = max(1, greenCount / 6)

        // Zyva Age
        computeZyvaAge(profile: profile)

        // Day Streak
        state.dayStreak = computeDayStreak()

        // Conditional Streaks
        state.sleepStreak = computeConditionalStreak { ($0.sleepPerformance ?? 0) >= 70 }
        state.greenRecoveryStreak = computeConditionalStreak { $0.recoveryZone == .green }
        state.strainStreak = computeConditionalStreak { $0.strainScore >= 10 }

        // Data Highlights
        computeHighlights(period: highlightsPeriod)

        // Notable Stats
        computeNotableStats()

        // Activity Summary
        computeActivitySummary(period: activityPeriod)

        isLoading = false
    }

    private func computeZyvaAge(profile: UserProfile) {
        let age = Double(profile.age ?? 30)
        let now = Date()
        let metrics180 = allMetrics.filter { $0.date >= now.daysAgo(180) && $0.date <= now }
        let metrics30 = allMetrics.filter { $0.date >= now.daysAgo(30) && $0.date <= now }
        let metricsWeek = allMetrics.filter { $0.date >= now.daysAgo(7) && $0.date <= now }

        let inputs = buildLongevityInputs(
            metrics180: metrics180,
            metrics30: metrics30,
            metricsWeek: metricsWeek,
            maxHR: Double(profile.estimatedMaxHR)
        )

        let result = LongevityEngine.compute(
            chronologicalAge: age,
            inputs: inputs,
            weekStart: now.daysAgo(7),
            weekEnd: now
        )

        state.zyvaAge = result.zyvaAge
        state.yearsYoungerOlder = result.yearsYoungerOlder
    }

    private func buildLongevityInputs(
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

        // Weekly zone times
        let weeklyZ13_180 = weeklyZoneAvg(metrics: metrics180, zone13: true) / 60.0
        let weeklyZ13_30 = weeklyZoneAvg(metrics: metrics30, zone13: true) / 60.0
        inputs.append(.init(id: .hrZones1to3Weekly,
                            sixMonthAvg: weeklyZ13_180 > 0 ? weeklyZ13_180 : nil,
                            thirtyDayAvg: weeklyZ13_30 > 0 ? weeklyZ13_30 : nil))

        let weeklyZ45_180 = weeklyZoneAvg(metrics: metrics180, zone13: false) / 60.0
        let weeklyZ45_30 = weeklyZoneAvg(metrics: metrics30, zone13: false) / 60.0
        inputs.append(.init(id: .hrZones4to5Weekly,
                            sixMonthAvg: weeklyZ45_180 > 0 ? weeklyZ45_180 : nil,
                            thirtyDayAvg: weeklyZ45_30 > 0 ? weeklyZ45_30 : nil))

        // Strength activity weekly
        let str180 = weeklyStrengthAvg(metrics: metrics180) / 60.0
        let str30 = weeklyStrengthAvg(metrics: metrics30) / 60.0
        inputs.append(.init(id: .strengthActivityWeekly,
                            sixMonthAvg: str180 > 0 ? str180 : nil,
                            thirtyDayAvg: str30 > 0 ? str30 : nil))

        // Steps
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
        return (totalMinutes / Double(totalDays)) * 7.0
    }

    private func weeklyStrengthAvg(metrics: [DailyMetric]) -> Double {
        guard !metrics.isEmpty else { return 0 }
        let totalDays = max(1, metrics.count)
        let totalMinutes = metrics.reduce(0.0) { acc, m in
            acc + m.workouts.filter(\.isStrengthWorkout).reduce(0.0) { $0 + $1.durationMinutes }
        }
        return (totalMinutes / Double(totalDays)) * 7.0
    }

    private func computeDayStreak() -> Int {
        let calendar = Calendar.current
        let sorted = allMetrics.sorted { $0.date > $1.date }
        var streak = 0
        var checkDate = calendar.startOfDay(for: Date())

        for metric in sorted {
            let metricDate = calendar.startOfDay(for: metric.date)
            if calendar.isDate(metricDate, inSameDayAs: checkDate) {
                streak += 1
                checkDate = calendar.date(byAdding: .day, value: -1, to: checkDate) ?? checkDate
            } else if metricDate < checkDate {
                break
            }
        }
        return streak
    }

    private func computeConditionalStreak(_ condition: (DailyMetric) -> Bool) -> Int {
        let calendar = Calendar.current
        let sorted = allMetrics.sorted { $0.date > $1.date }
        var streak = 0
        var checkDate = calendar.startOfDay(for: Date())

        for metric in sorted {
            let metricDate = calendar.startOfDay(for: metric.date)
            if calendar.isDate(metricDate, inSameDayAs: checkDate) {
                if condition(metric) {
                    streak += 1
                    checkDate = calendar.date(byAdding: .day, value: -1, to: checkDate) ?? checkDate
                } else {
                    break
                }
            } else if metricDate < checkDate {
                break
            }
        }
        return streak
    }

    private func computeHighlights(period: ProfileTimePeriod) {
        let filtered = filteredMetrics(for: period)
        state.bestSleepPct = filtered.compactMap(\.sleepPerformance).max() ?? 0
        state.peakRecoveryPct = filtered.compactMap(\.recoveryScore).max() ?? 0
        state.maxStrain = filtered.map(\.strainScore).max() ?? 0
    }

    private func computeNotableStats() {
        let rhrs = allMetrics.compactMap(\.restingHeartRate)
        state.lowestRHR = rhrs.min()
        state.highestRHR = rhrs.max()

        let hrvs = allMetrics.compactMap(\.hrvRMSSD)
        state.lowestHRV = hrvs.min()
        state.highestHRV = hrvs.max()

        let allWorkouts = allMetrics.flatMap(\.workouts)
        state.maxHeartRate = allWorkouts.compactMap(\.maxHeartRate).max()

        let sleepHours = allMetrics.compactMap(\.sleepDurationHours)
        state.longestSleepHours = sleepHours.max()

        let recoveries = allMetrics.compactMap(\.recoveryScore)
        state.lowestRecovery = recoveries.min()
    }

    private func computeActivitySummary(period: ProfileTimePeriod) {
        let filtered = filteredMetrics(for: period)
        let allWorkouts = filtered.flatMap(\.workouts)
        state.totalActivities = allWorkouts.count

        let grouped = Dictionary(grouping: allWorkouts, by: \.workoutType)
        let maxCount = grouped.values.map(\.count).max() ?? 1

        state.activityBreakdown = grouped.map { type, workouts in
            let avgStrain = workouts.isEmpty ? 0 : workouts.map(\.strainScore).reduce(0, +) / Double(workouts.count)
            return ProfileActivityTypeStats(
                workoutType: type,
                displayName: type.replacingOccurrences(of: "_", with: " ").capitalized,
                count: workouts.count,
                avgStrain: avgStrain,
                proportion: CGFloat(workouts.count) / CGFloat(maxCount)
            )
        }
        .sorted { $0.count > $1.count }
    }

    private func filteredMetrics(for period: ProfileTimePeriod) -> [DailyMetric] {
        guard let days = period.days else { return allMetrics }
        let cutoff = Date().daysAgo(days)
        return allMetrics.filter { $0.date >= cutoff }
    }
}
