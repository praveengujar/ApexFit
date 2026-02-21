import SwiftUI
import SwiftData

struct WearableSelectionView: View {
    let profile: UserProfile
    var onContinue: () -> Void

    @State private var selectedDevice: String? = nil

    private let devices: [(id: String, name: String, icon: String, color: Color)] = [
        ("APPLE_WATCH", "Apple Watch", "applewatch", AppColors.primaryBlue),
        ("GARMIN", "Garmin", "applewatch", AppColors.recoveryGreen),
        ("AMAZFIT", "Amazfit", "applewatch", AppColors.recoveryYellow),
        ("OURA_RING", "Oura Ring", "circle.circle", AppColors.lavender),
        ("OTHER", "Other", "desktopcomputer", AppColors.textSecondary),
    ]

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                // Header
                VStack(spacing: AppTheme.spacingMD) {
                    Image(systemName: "applewatch")
                        .font(.system(size: 64))
                        .foregroundStyle(AppColors.recoveryGreen)

                    Text("Your Wearable")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("Select the device you use to track your health data. This helps Zyva optimize your experience.")
                        .font(AppTypography.bodyLarge)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingLG)
                }

                Spacer()
                    .frame(height: AppTheme.spacingXXL)

                // Device options
                VStack(spacing: AppTheme.spacingSM) {
                    ForEach(devices, id: \.id) { device in
                        DeviceOptionCard(
                            name: device.name,
                            icon: device.icon,
                            color: device.color,
                            isSelected: selectedDevice == device.id,
                            onTap: { selectedDevice = device.id }
                        )
                    }
                }
                .padding(.horizontal, AppTheme.spacingLG)

                Spacer()

                // Buttons
                VStack(spacing: AppTheme.spacingMD) {
                    Button(action: {
                        profile.wearableDevice = selectedDevice
                        profile.updatedAt = Date()
                        onContinue()
                    }) {
                        Text("Continue")
                            .font(AppTypography.heading3)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget + 8)
                            .background(selectedDevice != nil ? AppColors.primaryBlue : AppColors.primaryBlue.opacity(0.5))
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                    }

                    Button(action: onContinue) {
                        Text("Skip for now")
                            .font(AppTypography.bodyMedium)
                            .foregroundStyle(AppColors.textSecondary)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget)
                    }
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .padding(.bottom, AppTheme.spacingXL)
            }
        }
        .onAppear {
            selectedDevice = profile.wearableDevice
        }
    }
}

// MARK: - Device Option Card

private struct DeviceOptionCard: View {
    let name: String
    let icon: String
    let color: Color
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: AppTheme.spacingMD) {
                ZStack {
                    RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                        .fill(color.opacity(0.15))
                        .frame(width: 44, height: 44)

                    Image(systemName: icon)
                        .font(.system(size: 20))
                        .foregroundStyle(color)
                }

                Text(name)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                Spacer()

                if isSelected {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 22))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
            .padding(AppTheme.cardPadding)
            .background(isSelected ? AppColors.primaryBlue.opacity(0.1) : AppColors.backgroundCard)
            .overlay(
                RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium)
                    .stroke(isSelected ? AppColors.primaryBlue : Color.clear, lineWidth: 1.5)
            )
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
    }
}
