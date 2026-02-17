import SwiftUI
import HealthKit

struct HealthKitStatusView: View {
    @State private var statuses: [(dataType: HealthKitDataType, status: HKAuthorizationStatus)] = []
    @State private var isLoading = true

    private let healthManager = HealthKitManager.shared

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                summaryCard
                statusList
                openSettingsButton
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("HealthKit Status")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { loadStatuses() }
    }

    // MARK: - Summary Card

    private var summaryCard: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: healthManager.isHealthKitAvailable ? "heart.text.square.fill" : "xmark.circle.fill")
                .font(.system(size: 40))
                .foregroundStyle(healthManager.isHealthKitAvailable ? AppColors.recoveryGreen : AppColors.recoveryRed)

            Text(healthManager.isHealthKitAvailable ? "HealthKit Available" : "HealthKit Unavailable")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if !statuses.isEmpty {
                let authorizedCount = statuses.filter { $0.status == .sharingAuthorized }.count
                Text("\(authorizedCount) of \(statuses.count) data types authorized")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .frame(maxWidth: .infinity)
        .cardStyle()
    }

    // MARK: - Status List

    private var statusList: some View {
        Group {
            if isLoading {
                LoadingStateView(message: "Checking authorizations...")
            } else {
                LazyVStack(spacing: AppTheme.spacingSM) {
                    ForEach(statuses, id: \.dataType) { item in
                        statusRow(dataType: item.dataType, status: item.status)
                    }
                }
            }
        }
    }

    private func statusRow(dataType: HealthKitDataType, status: HKAuthorizationStatus) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            statusIcon(for: status)

            Text(dataType.displayName)
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Text(statusLabel(for: status))
                .font(AppTypography.labelSmall)
                .foregroundStyle(statusColor(for: status))
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    private func statusIcon(for status: HKAuthorizationStatus) -> some View {
        Image(systemName: statusIconName(for: status))
            .font(.system(size: 14, weight: .bold))
            .foregroundStyle(statusColor(for: status))
            .frame(width: 28, height: 28)
            .background(statusColor(for: status).opacity(0.15))
            .clipShape(Circle())
    }

    private func statusIconName(for status: HKAuthorizationStatus) -> String {
        switch status {
        case .sharingAuthorized: return "checkmark"
        case .notDetermined: return "questionmark"
        case .sharingDenied: return "xmark"
        @unknown default: return "questionmark"
        }
    }

    private func statusLabel(for status: HKAuthorizationStatus) -> String {
        switch status {
        case .sharingAuthorized: return "Authorized"
        case .notDetermined: return "Not Determined"
        case .sharingDenied: return "Denied"
        @unknown default: return "Unknown"
        }
    }

    private func statusColor(for status: HKAuthorizationStatus) -> Color {
        switch status {
        case .sharingAuthorized: return AppColors.recoveryGreen
        case .notDetermined: return AppColors.recoveryYellow
        case .sharingDenied: return AppColors.recoveryRed
        @unknown default: return AppColors.textTertiary
        }
    }

    // MARK: - Open Settings Button

    private var openSettingsButton: some View {
        Button {
            if let url = URL(string: UIApplication.openSettingsURLString) {
                UIApplication.shared.open(url)
            }
        } label: {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "gear")
                Text("Open Settings")
            }
            .font(AppTypography.labelLarge)
            .foregroundStyle(AppColors.primaryBlue)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .background(AppColors.primaryBlue.opacity(0.1))
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
    }

    // MARK: - Load

    private func loadStatuses() {
        statuses = HealthKitDataType.allCases.compactMap { dataType in
            let status = healthManager.authorizationStatus(for: dataType)
            return (dataType: dataType, status: status)
        }
        isLoading = false
    }
}
