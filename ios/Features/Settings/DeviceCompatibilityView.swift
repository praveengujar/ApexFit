import SwiftUI

struct DeviceCompatibilityView: View {
    @State private var capabilities: DeviceCapabilities?
    @State private var isLoading = true

    private let capabilityService = DeviceCapabilityService()

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                if isLoading {
                    LoadingStateView(message: "Assessing device capabilities...")
                } else if let caps = capabilities {
                    capabilityBadge(caps.capabilityLevel)
                    featureList(caps)
                    if !caps.limitationMessages.isEmpty {
                        limitationsCard(caps.limitationMessages)
                    }
                }
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Device Compatibility")
        .navigationBarTitleDisplayMode(.inline)
        .task { await loadCapabilities() }
    }

    // MARK: - Capability Badge

    private func capabilityBadge(_ level: CapabilityLevel) -> some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: capabilityIcon(level))
                .font(.system(size: 48))
                .foregroundStyle(capabilityColor(level))

            Text(level.rawValue.capitalized)
                .font(AppTypography.heading2)
                .foregroundStyle(AppColors.textPrimary)

            Text(level.description)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }

    private func capabilityIcon(_ level: CapabilityLevel) -> String {
        switch level {
        case .excellent: return "star.circle.fill"
        case .good: return "checkmark.circle.fill"
        case .basic: return "minus.circle.fill"
        case .limited: return "exclamationmark.circle.fill"
        }
    }

    private func capabilityColor(_ level: CapabilityLevel) -> Color {
        switch level {
        case .excellent: return AppColors.recoveryGreen
        case .good: return AppColors.primaryBlue
        case .basic: return AppColors.recoveryYellow
        case .limited: return AppColors.recoveryRed
        }
    }

    // MARK: - Feature List

    private func featureList(_ caps: DeviceCapabilities) -> some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Feature Availability")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            featureRow("Heart Rate Monitoring", available: caps.hasHeartRate, icon: "heart.fill")
            featureRow("Sleep Stage Analysis", available: caps.hasSleepStages, icon: "moon.stars.fill")
            featureRow("HRV Beat-to-Beat (RMSSD)", available: caps.hasHRVBeatToBeat, icon: "waveform.path.ecg")
            featureRow("Blood Oxygen (SpO2)", available: caps.hasSpO2, icon: "lungs.fill")
            featureRow("VO2 Max", available: caps.hasVO2Max, icon: "bolt.heart.fill")
            featureRow("Respiratory Rate", available: caps.hasRespiratoryRate, icon: "wind")
        }
        .cardStyle()
    }

    private func featureRow(_ name: String, available: Bool, icon: String) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: icon)
                .font(.system(size: 14))
                .foregroundStyle(available ? AppColors.recoveryGreen : AppColors.textTertiary)
                .frame(width: 24)

            Text(name)
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: available ? "checkmark.circle.fill" : "xmark.circle.fill")
                .font(.system(size: 16))
                .foregroundStyle(available ? AppColors.recoveryGreen : AppColors.recoveryRed.opacity(0.6))
        }
    }

    // MARK: - Limitations

    private func limitationsCard(_ messages: [String]) -> some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Label("Limitations", systemImage: "exclamationmark.triangle.fill")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.recoveryYellow)

            ForEach(messages, id: \.self) { message in
                HStack(alignment: .top, spacing: AppTheme.spacingSM) {
                    Image(systemName: "info.circle")
                        .font(.caption)
                        .foregroundStyle(AppColors.textTertiary)
                        .padding(.top, 2)
                    Text(message)
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
            }
        }
        .cardStyle()
    }

    // MARK: - Load

    private func loadCapabilities() async {
        capabilities = await capabilityService.assessCapabilities()
        isLoading = false
    }
}
