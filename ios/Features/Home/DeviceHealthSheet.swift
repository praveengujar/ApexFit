import SwiftUI
import SwiftData

struct DeviceHealthSheet: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(HealthKitManager.self) private var healthKitManager
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var selectedDevice: String? = nil
    @State private var isRequestingAccess = false
    @State private var accessGranted = false

    private var profile: UserProfile? { profiles.first }

    private let devices: [(id: String, name: String, icon: String, color: Color)] = [
        ("APPLE_WATCH", "Apple Watch", "applewatch", AppColors.primaryBlue),
        ("GARMIN", "Garmin", "applewatch", AppColors.recoveryGreen),
        ("AMAZFIT", "Amazfit", "applewatch", AppColors.recoveryYellow),
        ("OURA_RING", "Oura Ring", "circle.circle", AppColors.lavender),
        ("OTHER", "Other", "desktopcomputer", AppColors.textSecondary),
    ]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    // Section 1: Wearable Device
                    wearableSection

                    // Section 2: Health Access
                    healthAccessSection
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Device & Health")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
        }
        .onAppear {
            selectedDevice = profile?.wearableDevice
            accessGranted = healthKitManager.isAuthorized
        }
    }

    // MARK: - Wearable Section

    private var wearableSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "applewatch")
                    .font(.caption)
                    .foregroundStyle(AppColors.textTertiary)
                Text("Your Wearable")
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)
            }
            .padding(.top, AppTheme.spacingSM)

            ForEach(devices, id: \.id) { device in
                Button {
                    selectedDevice = device.id
                    profile?.wearableDevice = device.id
                    profile?.updatedAt = Date()
                } label: {
                    HStack(spacing: AppTheme.spacingMD) {
                        ZStack {
                            RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                                .fill(device.color.opacity(0.15))
                                .frame(width: 44, height: 44)
                            Image(systemName: device.icon)
                                .font(.system(size: 20))
                                .foregroundStyle(device.color)
                        }

                        Text(device.name)
                            .font(AppTypography.labelLarge)
                            .foregroundStyle(AppColors.textPrimary)

                        Spacer()

                        if selectedDevice == device.id {
                            Image(systemName: "checkmark.circle.fill")
                                .font(.system(size: 22))
                                .foregroundStyle(AppColors.primaryBlue)
                        }
                    }
                    .padding(AppTheme.cardPadding)
                    .background(selectedDevice == device.id ? AppColors.primaryBlue.opacity(0.1) : AppColors.backgroundCard)
                    .overlay(
                        RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium)
                            .stroke(selectedDevice == device.id ? AppColors.primaryBlue : Color.clear, lineWidth: 1.5)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
            }
        }
    }

    // MARK: - Health Access Section

    private var healthAccessSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "heart.text.square.fill")
                    .font(.caption)
                    .foregroundStyle(AppColors.textTertiary)
                Text("Health Data Access")
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)
            }
            .padding(.top, AppTheme.spacingSM)

            // Status card
            VStack(spacing: AppTheme.spacingSM) {
                HStack(spacing: AppTheme.spacingSM) {
                    Image(systemName: accessGranted ? "checkmark.circle.fill" : "exclamationmark.triangle.fill")
                        .foregroundStyle(accessGranted ? AppColors.recoveryGreen : AppColors.recoveryYellow)

                    Text(accessGranted ? "Apple Health access granted" : "Apple Health access not granted")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textPrimary)

                    Spacer()
                }

                if !healthKitManager.isHealthKitAvailable {
                    Text("HealthKit is not available on this device.")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                } else if !accessGranted {
                    Text("Grant access to Apple Health to enable recovery scores, strain tracking, and sleep analysis.")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
            }
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))

            // Grant access button
            if !accessGranted && healthKitManager.isHealthKitAvailable {
                Button {
                    requestAccess()
                } label: {
                    HStack(spacing: AppTheme.spacingSM) {
                        if isRequestingAccess {
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
                .disabled(isRequestingAccess)
            }
        }
    }

    // MARK: - Actions

    private func requestAccess() {
        isRequestingAccess = true
        Task {
            do {
                try await healthKitManager.requestAuthorization()
                await MainActor.run {
                    withAnimation(AppTheme.animationDefault) {
                        accessGranted = true
                        isRequestingAccess = false
                    }
                }
            } catch {
                await MainActor.run {
                    isRequestingAccess = false
                }
            }
        }
    }
}
