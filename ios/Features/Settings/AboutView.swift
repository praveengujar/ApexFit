import SwiftUI

struct AboutView: View {
    private var appVersion: String {
        Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0"
    }

    private var buildNumber: String {
        Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "1"
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                appInfoCard
                linksSection
                creditsSection
                swiftUIBadge
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("About")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - App Info

    private var appInfoCard: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "mountain.2.fill")
                .font(.system(size: 48))
                .foregroundStyle(
                    LinearGradient(
                        colors: [AppColors.primaryBlue, AppColors.teal],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )

            Text("Zyva")
                .font(AppTypography.heading1)
                .foregroundStyle(AppColors.textPrimary)

            Text("Your personal recovery and performance tracker")
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)

            HStack(spacing: AppTheme.spacingMD) {
                VStack(spacing: 2) {
                    Text("Version")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                    Text(appVersion)
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)
                }
                VStack(spacing: 2) {
                    Text("Build")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                    Text(buildNumber)
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)
                }
            }
            .padding(.top, AppTheme.spacingSM)
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }

    // MARK: - Links

    private var linksSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Legal")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            linkRow(
                title: "Privacy Policy",
                icon: "hand.raised.fill",
                color: AppColors.primaryBlue,
                urlString: "https://zyva.app/privacy"
            )

            linkRow(
                title: "Terms of Service",
                icon: "doc.text.fill",
                color: AppColors.lavender,
                urlString: "https://zyva.app/terms"
            )
        }
    }

    private func linkRow(title: String, icon: String, color: Color, urlString: String) -> some View {
        Button {
            if let url = URL(string: urlString) {
                UIApplication.shared.open(url)
            }
        } label: {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: icon)
                    .font(.system(size: 14))
                    .foregroundStyle(color)
                    .frame(width: 32, height: 32)
                    .background(color.opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

                Text(title)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                Spacer()

                Image(systemName: "arrow.up.right.square")
                    .font(.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
        .buttonStyle(.plain)
    }

    // MARK: - Credits

    private var creditsSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Credits")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                creditRow("Health data powered by", value: "Apple HealthKit")
                creditRow("Charts powered by", value: "Swift Charts")
                creditRow("Persistence powered by", value: "SwiftData")
            }
            .cardStyle()
        }
    }

    private func creditRow(_ label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text(value)
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textPrimary)
        }
    }

    // MARK: - SwiftUI Badge

    private var swiftUIBadge: some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "swift")
                .font(.system(size: 16))
                .foregroundStyle(AppColors.recoveryRed)
            Text("Made with SwiftUI")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingSM)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }
}
