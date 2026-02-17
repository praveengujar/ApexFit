import SwiftUI
import SwiftData

struct SettingsView: View {
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    private var userProfile: UserProfile? {
        profiles.first
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    profileSection
                    preferencesSection
                    dataSection
                    aboutSection
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Profile Section

    private var profileSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            sectionHeader("Profile", icon: "person.fill")

            NavigationLink(destination: MaxHRSettingsView()) {
                settingsRow(
                    title: "Max Heart Rate",
                    detail: "\(userProfile?.estimatedMaxHR ?? 190) BPM",
                    icon: "heart.fill",
                    iconColor: AppColors.recoveryRed
                )
            }

            NavigationLink(destination: SleepGoalSettingsView()) {
                settingsRow(
                    title: "Sleep Goal",
                    detail: (userProfile?.sleepBaselineHours ?? 7.5).formattedOneDecimal + "h",
                    icon: "moon.fill",
                    iconColor: AppColors.sleepDeep
                )
            }
        }
    }

    // MARK: - Preferences Section

    private var preferencesSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            sectionHeader("Preferences", icon: "slider.horizontal.3")

            NavigationLink(destination: UnitSettingsView()) {
                settingsRow(
                    title: "Units",
                    detail: userProfile?.preferredUnits.rawValue.capitalized ?? "Metric",
                    icon: "ruler",
                    iconColor: AppColors.primaryBlue
                )
            }

            NavigationLink(destination: NotificationSettingsView()) {
                settingsRow(
                    title: "Notifications",
                    detail: nil,
                    icon: "bell.fill",
                    iconColor: AppColors.recoveryYellow
                )
            }
        }
    }

    // MARK: - Data Section

    private var dataSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            sectionHeader("Data", icon: "externaldrive.fill")

            NavigationLink(destination: HealthKitStatusView()) {
                settingsRow(
                    title: "HealthKit Status",
                    detail: nil,
                    icon: "heart.text.square.fill",
                    iconColor: AppColors.recoveryGreen
                )
            }

            NavigationLink(destination: DeviceCompatibilityView()) {
                settingsRow(
                    title: "Device Compatibility",
                    detail: nil,
                    icon: "applewatch",
                    iconColor: AppColors.teal
                )
            }
        }
    }

    // MARK: - About Section

    private var aboutSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            sectionHeader("About", icon: "info.circle.fill")

            NavigationLink(destination: AboutView()) {
                settingsRow(
                    title: "About ApexFit",
                    detail: nil,
                    icon: "questionmark.circle",
                    iconColor: AppColors.lavender
                )
            }
        }
    }

    // MARK: - Reusable Components

    private func sectionHeader(_ title: String, icon: String) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.caption)
                .foregroundStyle(AppColors.textTertiary)
            Text(title)
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)
        }
        .padding(.top, AppTheme.spacingSM)
    }

    private func settingsRow(title: String, detail: String?, icon: String, iconColor: Color) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundStyle(iconColor)
                .frame(width: 32, height: 32)
                .background(iconColor.opacity(0.15))
                .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

            Text(title)
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            if let detail {
                Text(detail)
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundStyle(AppColors.textTertiary)
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }
}
