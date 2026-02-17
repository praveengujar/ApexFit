import SwiftUI
import SwiftData

struct MaxHRSettingsView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var manualHR: Int = 190
    @State private var showResetConfirmation = false

    private var userProfile: UserProfile? {
        profiles.first
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                currentSourceCard
                manualEntryCard
                resetButton
                explanationCard
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Max Heart Rate")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            manualHR = userProfile?.estimatedMaxHR ?? 190
        }
        .alert("Reset Max HR", isPresented: $showResetConfirmation) {
            Button("Reset", role: .destructive) { resetToAgeEstimate() }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("This will reset your max heart rate to the age-based estimate.")
        }
    }

    // MARK: - Current Source Card

    private var currentSourceCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Current Max HR")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)

            HStack(alignment: .lastTextBaseline, spacing: 4) {
                Text("\(userProfile?.estimatedMaxHR ?? 190)")
                    .font(AppTypography.metricLarge)
                    .foregroundStyle(AppColors.recoveryRed)
                Text("BPM")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
            }

            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: sourceIcon)
                    .font(.caption)
                    .foregroundStyle(sourceColor)
                Text("Source: \(sourceLabel)")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .cardStyle()
    }

    private var sourceIcon: String {
        switch userProfile?.maxHeartRateSource {
        case .userInput: return "pencil.circle.fill"
        case .observed: return "waveform.path.ecg"
        case .ageEstimate, .none: return "calendar.circle.fill"
        }
    }

    private var sourceLabel: String {
        switch userProfile?.maxHeartRateSource {
        case .userInput: return "Manual Entry"
        case .observed: return "Observed During Workout"
        case .ageEstimate, .none: return "Age Estimate (220 - age)"
        }
    }

    private var sourceColor: Color {
        switch userProfile?.maxHeartRateSource {
        case .userInput: return AppColors.primaryBlue
        case .observed: return AppColors.recoveryGreen
        case .ageEstimate, .none: return AppColors.recoveryYellow
        }
    }

    // MARK: - Manual Entry Card

    private var manualEntryCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Set Manually")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            HStack {
                Text("\(manualHR) BPM")
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.textPrimary)
                    .contentTransition(.numericText())
                Spacer()
            }

            Picker("Max Heart Rate", selection: $manualHR) {
                ForEach(140...220, id: \.self) { value in
                    Text("\(value)").tag(value)
                }
            }
            .pickerStyle(.wheel)
            .frame(height: 120)

            Button {
                saveManualHR()
            } label: {
                Text("Apply")
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.primaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
            }
        }
        .cardStyle()
    }

    // MARK: - Reset Button

    private var resetButton: some View {
        Group {
            if userProfile?.maxHeartRateSource != .ageEstimate {
                Button {
                    showResetConfirmation = true
                } label: {
                    HStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "arrow.counterclockwise")
                        Text("Reset to Age Estimate")
                    }
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.recoveryYellow)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.recoveryYellow.opacity(0.1))
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
                }
            }
        }
    }

    // MARK: - Explanation Card

    private var explanationCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Label("How Max HR Affects Your Zones", systemImage: "info.circle")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.primaryBlue)

            Text("Your max heart rate is used to calculate personalized heart rate zones, which determine strain scoring and workout intensity classification.")
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)

            VStack(alignment: .leading, spacing: 4) {
                zoneExplanationRow("Zone 1", "50-60%", "Light", AppColors.zone1)
                zoneExplanationRow("Zone 2", "60-70%", "Moderate", AppColors.zone2)
                zoneExplanationRow("Zone 3", "70-80%", "Vigorous", AppColors.zone3)
                zoneExplanationRow("Zone 4", "80-90%", "Hard", AppColors.zone4)
                zoneExplanationRow("Zone 5", "90-100%", "Maximum", AppColors.zone5)
            }
        }
        .cardStyle()
    }

    private func zoneExplanationRow(_ zone: String, _ range: String, _ label: String, _ color: Color) -> some View {
        HStack {
            Circle()
                .fill(color)
                .frame(width: 8, height: 8)
            Text(zone)
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textPrimary)
                .frame(width: 50, alignment: .leading)
            Text(range)
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textSecondary)
                .frame(width: 60, alignment: .leading)
            Text(label)
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    // MARK: - Actions

    private func saveManualHR() {
        guard let profile = userProfile else { return }
        profile.maxHeartRate = manualHR
        profile.maxHeartRateSource = .userInput
        profile.updatedAt = Date()
        try? modelContext.save()
    }

    private func resetToAgeEstimate() {
        guard let profile = userProfile else { return }
        profile.maxHeartRate = nil
        profile.maxHeartRateSource = .ageEstimate
        profile.updatedAt = Date()
        try? modelContext.save()
        manualHR = profile.estimatedMaxHR
    }
}
