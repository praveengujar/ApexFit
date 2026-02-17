import SwiftUI
import SwiftData

struct SleepPlannerView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    @State private var selectedGoal: SleepGoal = .perform
    @State private var wakeTime: Date = defaultWakeTime

    private var todayMetric: DailyMetric? {
        metrics.first { $0.date.isToday }
    }

    private var baselineNeed: Double {
        HealthKitConstants.defaultSleepBaselineHours
    }

    private var strainSupplement: Double {
        let strain = todayMetric?.strainScore ?? 0
        switch strain {
        case ..<8: return 0
        case 8..<14: return 0.25
        case 14..<18: return 0.5
        default: return 0.75
        }
    }

    private var debtRepayment: Double {
        (todayMetric?.sleepDebtHours ?? 0) * 0.2
    }

    private var napCredit: Double {
        let naps = todayMetric?.sleepSessions.filter { $0.isNap } ?? []
        let napHours = naps.reduce(0.0) { $0 + $1.totalSleepHours }
        return Swift.min(napHours, HealthKitConstants.napCreditCapHours)
    }

    private var totalSleepNeed: Double {
        baselineNeed + strainSupplement + debtRepayment - napCredit
    }

    private var plannerResult: SleepPlannerResult {
        SleepPlannerEngine.plan(
            sleepNeedHours: totalSleepNeed,
            goal: selectedGoal,
            desiredWakeTime: wakeTime,
            baselineNeed: baselineNeed,
            strainSupplement: strainSupplement,
            debtRepayment: debtRepayment,
            napCredit: napCredit
        )
    }

    private static var defaultWakeTime: Date {
        var components = Calendar.current.dateComponents([.year, .month, .day], from: Date().tomorrow)
        components.hour = 7
        components.minute = 0
        return Calendar.current.date(from: components) ?? Date().tomorrow
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingLG) {
                goalPicker
                sleepNeedBreakdown
                bedtimeCard
                timelineVisualization
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Sleep Planner")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - Goal Picker

    private var goalPicker: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Sleep Goal")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Picker("Goal", selection: $selectedGoal) {
                ForEach(SleepGoal.allCases, id: \.self) { goal in
                    Text(goal.label).tag(goal)
                }
            }
            .pickerStyle(.segmented)

            Text(selectedGoal.description)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Sleep Need Breakdown

    private var sleepNeedBreakdown: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Sleep Need Breakdown")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            VStack(spacing: AppTheme.spacingXS) {
                breakdownRow(label: "Baseline Need", value: baselineNeed, icon: "moon.fill", color: AppColors.sleepDeep, sign: "")
                breakdownRow(label: "Strain Supplement", value: strainSupplement, icon: "flame.fill", color: AppColors.zone4, sign: "+")
                breakdownRow(label: "Debt Repayment", value: debtRepayment, icon: "exclamationmark.triangle.fill", color: AppColors.recoveryYellow, sign: "+")
                breakdownRow(label: "Nap Credit", value: napCredit, icon: "powersleep", color: AppColors.teal, sign: "-")

                Divider()
                    .overlay(AppColors.backgroundTertiary)

                HStack {
                    Text("Total Sleep Need")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textPrimary)
                    Spacer()
                    Text(totalSleepNeed.formattedHoursMinutes)
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.sleepDeep)
                }
                .padding(.top, AppTheme.spacingXS)

                HStack {
                    Text("Required (\(selectedGoal.label))")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textTertiary)
                    Spacer()
                    Text(plannerResult.requiredSleepDuration.formattedHoursMinutes)
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textSecondary)
                }
            }
            .cardStyle()
        }
    }

    // MARK: - Bedtime Card

    private var bedtimeCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Recommended Schedule")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            VStack(spacing: AppTheme.spacingMD) {
                // Wake time picker
                HStack {
                    Image(systemName: "sun.max.fill")
                        .foregroundStyle(AppColors.recoveryYellow)
                    Text("Wake Time")
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textPrimary)
                    Spacer()
                    DatePicker("", selection: $wakeTime, displayedComponents: .hourAndMinute)
                        .labelsHidden()
                        .tint(AppColors.primaryBlue)
                }

                Divider()
                    .overlay(AppColors.backgroundTertiary)

                // Recommended bedtime
                HStack {
                    Image(systemName: "moon.stars.fill")
                        .foregroundStyle(AppColors.sleepDeep)
                    Text("Bedtime")
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textPrimary)
                    Spacer()
                    Text(plannerResult.recommendedBedtime.hourMinuteString)
                        .font(AppTypography.metricSmall)
                        .foregroundStyle(AppColors.sleepDeep)
                }

                Divider()
                    .overlay(AppColors.backgroundTertiary)

                // Wake time display
                HStack {
                    Image(systemName: "alarm.fill")
                        .foregroundStyle(AppColors.recoveryYellow)
                    Text("Wake Up")
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textPrimary)
                    Spacer()
                    Text(plannerResult.expectedWakeTime.hourMinuteString)
                        .font(AppTypography.metricSmall)
                        .foregroundStyle(AppColors.recoveryYellow)
                }
            }
            .cardStyle()
        }
    }

    // MARK: - Timeline Visualization

    private var timelineVisualization: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Tonight's Timeline")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            GeometryReader { geometry in
                let totalWidth = geometry.size.width
                let bedtime = plannerResult.recommendedBedtime
                let wakeUp = plannerResult.expectedWakeTime
                let totalDuration = wakeUp.timeIntervalSince(bedtime)
                let sleepDuration = plannerResult.requiredSleepDuration * 3600
                let onsetWidth = totalDuration > 0 ? (totalDuration - sleepDuration) / totalDuration * totalWidth : 0

                VStack(spacing: AppTheme.spacingXS) {
                    // Timeline bar
                    HStack(spacing: 0) {
                        // Onset latency segment
                        if onsetWidth > 2 {
                            RoundedRectangle(cornerRadius: 4)
                                .fill(AppColors.textTertiary.opacity(0.3))
                                .frame(width: max(onsetWidth, 2))
                        }

                        // Sleep segment
                        RoundedRectangle(cornerRadius: 4)
                            .fill(
                                LinearGradient(
                                    colors: [AppColors.sleepDeep, AppColors.sleepLight],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                    }
                    .frame(height: 20)
                    .clipShape(RoundedRectangle(cornerRadius: 4))

                    // Time labels
                    HStack {
                        Text(bedtime.hourMinuteString)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)

                        Spacer()

                        Text(plannerResult.requiredSleepDuration.formattedHoursMinutes + " sleep")
                            .font(AppTypography.captionBold)
                            .foregroundStyle(AppColors.sleepDeep)

                        Spacer()

                        Text(wakeUp.hourMinuteString)
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
            .frame(height: 44)
            .cardStyle()
        }
    }

    // MARK: - Helpers

    private func breakdownRow(label: String, value: Double, icon: String, color: Color, sign: String) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.caption)
                .foregroundStyle(color)
                .frame(width: 20)

            Text(label)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)

            Spacer()

            Text("\(sign)\(value.formattedHoursMinutes)")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
        }
    }
}

#Preview {
    NavigationStack {
        SleepPlannerView()
    }
    .modelContainer(for: DailyMetric.self, inMemory: true)
}
