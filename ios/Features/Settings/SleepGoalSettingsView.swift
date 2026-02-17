import SwiftUI
import SwiftData

struct SleepGoalSettingsView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var sleepHours: Double = 7.5

    private var userProfile: UserProfile? {
        profiles.first
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                currentGoalCard
                sliderCard
                breakdownCard
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Sleep Goal")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            sleepHours = userProfile?.sleepBaselineHours ?? 7.5
        }
    }

    // MARK: - Current Goal

    private var currentGoalCard: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Text("Sleep Baseline")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)

            HStack(alignment: .lastTextBaseline, spacing: 4) {
                Text(sleepHours.formattedOneDecimal)
                    .font(AppTypography.metricLarge)
                    .foregroundStyle(AppColors.sleepDeep)
                    .contentTransition(.numericText())
                Text("hours")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Text(sleepHours.formattedHoursMinutes)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textTertiary)
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }

    // MARK: - Slider

    private var sliderCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Adjust Sleep Need")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            HStack {
                Text("6h")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)

                Slider(
                    value: $sleepHours,
                    in: 6.0...10.0,
                    step: 0.25
                ) {
                    Text("Sleep Hours")
                } onEditingChanged: { isEditing in
                    if !isEditing {
                        saveSleepGoal()
                    }
                }
                .tint(AppColors.sleepDeep)

                Text("10h")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }

            // Tick marks
            HStack {
                ForEach(Array(stride(from: 6.0, through: 10.0, by: 0.5)), id: \.self) { value in
                    if value == 6.0 || value == 10.0 {
                        Spacer(minLength: 0)
                    } else {
                        Spacer()
                        Circle()
                            .fill(sleepHours >= value ? AppColors.sleepDeep : AppColors.textTertiary.opacity(0.3))
                            .frame(width: 4, height: 4)
                    }
                }
            }
            .padding(.horizontal, 4)

            Text("The default recommendation is 7.5 hours. Adjust based on your personal sleep needs.")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .cardStyle()
    }

    // MARK: - Breakdown

    private var breakdownCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Label("Sleep Need Calculation", systemImage: "info.circle")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.primaryBlue)

            VStack(spacing: AppTheme.spacingSM) {
                breakdownRow(
                    label: "Base Sleep Need",
                    value: sleepHours.formattedHoursMinutes,
                    icon: "bed.double.fill",
                    color: AppColors.sleepDeep
                )
                breakdownRow(
                    label: "Time to Fall Asleep",
                    value: "~15m",
                    icon: "clock",
                    color: AppColors.sleepLight
                )
                breakdownRow(
                    label: "Target In-Bed Time",
                    value: (sleepHours + 0.25).formattedHoursMinutes,
                    icon: "moon.fill",
                    color: AppColors.lavender
                )

                Divider()
                    .overlay(AppColors.textTertiary.opacity(0.3))

                HStack {
                    Text("Sleep Performance Target")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)
                    Spacer()
                    Text("85-100%")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.recoveryGreen)
                }
            }
        }
        .cardStyle()
    }

    private func breakdownRow(label: String, value: String, icon: String, color: Color) -> some View {
        HStack {
            Image(systemName: icon)
                .font(.caption)
                .foregroundStyle(color)
                .frame(width: 20)
            Text(label)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text(value)
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
        }
    }

    // MARK: - Save

    private func saveSleepGoal() {
        guard let profile = userProfile else { return }
        profile.sleepBaselineHours = sleepHours
        profile.updatedAt = Date()
        try? modelContext.save()
    }
}
