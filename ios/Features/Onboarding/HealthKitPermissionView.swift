import SwiftUI

struct HealthKitPermissionView: View {
    @Environment(HealthKitManager.self) private var healthKitManager
    var onContinue: () -> Void

    @State private var isRequesting = false
    @State private var authorizationState: AuthorizationState = .idle

    private enum AuthorizationState: Equatable {
        case idle
        case requesting
        case granted
        case failed(String)
        case unavailable
    }

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                // Header
                VStack(spacing: AppTheme.spacingMD) {
                    Image(systemName: "heart.circle.fill")
                        .font(.system(size: 64))
                        .foregroundStyle(AppColors.recoveryGreen)

                    Text("Health Access")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("ApexFit uses Apple Health to provide recovery scores, strain tracking, and sleep analysis.")
                        .font(AppTypography.bodyLarge)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingLG)
                }

                Spacer()
                    .frame(height: AppTheme.spacingXXL)

                // Data types list
                VStack(spacing: AppTheme.spacingMD) {
                    HealthDataRow(icon: "heart.fill", color: AppColors.recoveryRed, title: "Heart Rate", description: "Continuous heart rate & resting HR")
                    HealthDataRow(icon: "moon.zzz.fill", color: AppColors.lavender, title: "Sleep", description: "Sleep stages and duration")
                    HealthDataRow(icon: "figure.run", color: AppColors.recoveryYellow, title: "Workouts", description: "Exercise type, duration, and intensity")
                    HealthDataRow(icon: "waveform.path.ecg", color: AppColors.teal, title: "HRV", description: "Heart rate variability for recovery")
                    HealthDataRow(icon: "figure.walk", color: AppColors.primaryBlue, title: "Steps", description: "Daily step count and activity")
                }
                .padding(.horizontal, AppTheme.spacingLG)

                Spacer()

                // Status messages
                switch authorizationState {
                case .granted:
                    HStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundStyle(AppColors.recoveryGreen)
                        Text("Health access granted")
                            .font(AppTypography.labelLarge)
                            .foregroundStyle(AppColors.recoveryGreen)
                    }
                    .padding(.bottom, AppTheme.spacingMD)

                case .failed(let message):
                    VStack(spacing: AppTheme.spacingXS) {
                        HStack(spacing: AppTheme.spacingSM) {
                            Image(systemName: "exclamationmark.triangle.fill")
                                .foregroundStyle(AppColors.recoveryYellow)
                            Text("Authorization issue")
                                .font(AppTypography.labelLarge)
                                .foregroundStyle(AppColors.recoveryYellow)
                        }
                        Text(message)
                            .font(AppTypography.bodySmall)
                            .foregroundStyle(AppColors.textSecondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding(.horizontal, AppTheme.spacingLG)
                    .padding(.bottom, AppTheme.spacingMD)

                case .unavailable:
                    HStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundStyle(AppColors.textSecondary)
                        Text("HealthKit is not available on this device")
                            .font(AppTypography.bodySmall)
                            .foregroundStyle(AppColors.textSecondary)
                    }
                    .padding(.bottom, AppTheme.spacingMD)

                default:
                    EmptyView()
                }

                // Buttons
                VStack(spacing: AppTheme.spacingMD) {
                    if authorizationState == .granted {
                        Button(action: onContinue) {
                            Text("Continue")
                                .font(AppTypography.heading3)
                                .foregroundStyle(.white)
                                .frame(maxWidth: .infinity)
                                .frame(height: AppTheme.minimumTapTarget + 8)
                                .background(AppColors.primaryBlue)
                                .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                        }
                    } else {
                        Button(action: requestAccess) {
                            HStack(spacing: AppTheme.spacingSM) {
                                if authorizationState == .requesting {
                                    ProgressView()
                                        .tint(.white)
                                } else {
                                    Image(systemName: "heart.fill")
                                }
                                Text("Grant Access")
                            }
                            .font(AppTypography.heading3)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget + 8)
                            .background(AppColors.primaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                        }
                        .disabled(authorizationState == .requesting)
                    }

                    // Skip option for users who want to continue without HealthKit
                    if authorizationState != .granted {
                        Button(action: onContinue) {
                            Text("Skip for now")
                                .font(AppTypography.bodyMedium)
                                .foregroundStyle(AppColors.textSecondary)
                                .frame(maxWidth: .infinity)
                                .frame(height: AppTheme.minimumTapTarget)
                        }
                    }
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .padding(.bottom, AppTheme.spacingXL)
            }
        }
        .onAppear {
            if !healthKitManager.isHealthKitAvailable {
                authorizationState = .unavailable
            } else if healthKitManager.isAuthorized {
                authorizationState = .granted
            }
        }
    }

    // MARK: - Actions

    private func requestAccess() {
        authorizationState = .requesting
        Task {
            do {
                try await healthKitManager.requestAuthorization()
                await MainActor.run {
                    withAnimation(AppTheme.animationDefault) {
                        authorizationState = .granted
                    }
                }
            } catch {
                await MainActor.run {
                    withAnimation(AppTheme.animationDefault) {
                        authorizationState = .failed(error.localizedDescription)
                    }
                }
            }
        }
    }
}

// MARK: - Health Data Row

private struct HealthDataRow: View {
    let icon: String
    let color: Color
    let title: String
    let description: String

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            ZStack {
                RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                    .fill(color.opacity(0.15))
                    .frame(width: 44, height: 44)

                Image(systemName: icon)
                    .font(.system(size: 20))
                    .foregroundStyle(color)
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                Text(description)
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Spacer()
        }
    }
}

#Preview {
    HealthKitPermissionView {
        print("Continue")
    }
    .environment(HealthKitManager.shared)
}
