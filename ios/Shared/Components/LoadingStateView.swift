import SwiftUI

// MARK: - Loading State

struct LoadingStateView: View {
    let message: String

    init(message: String = "Loading...") {
        self.message = message
    }

    var body: some View {
        VStack(spacing: AppTheme.spacingMD) {
            ProgressView()
                .progressViewStyle(.circular)
                .tint(AppColors.primaryBlue)
                .scaleEffect(1.2)
            Text(message)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingXXL)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Loading: \(message)")
    }
}

// MARK: - Empty State

struct EmptyStateView: View {
    let icon: String
    let title: String
    let message: String

    var body: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 48))
                .foregroundStyle(AppColors.textTertiary)

            Text(title)
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Text(message)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, AppTheme.spacingLG)
        .padding(.vertical, AppTheme.spacingXXL)
        .accessibilityElement(children: .combine)
    }
}

// MARK: - Error State

struct ErrorStateView: View {
    let icon: String
    let message: String
    let retryAction: (() -> Void)?

    init(
        icon: String = "exclamationmark.triangle",
        message: String,
        retryAction: (() -> Void)? = nil
    ) {
        self.icon = icon
        self.message = message
        self.retryAction = retryAction
    }

    var body: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 48))
                .foregroundStyle(AppColors.recoveryRed)

            Text("Something Went Wrong")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Text(message)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)

            if let retry = retryAction {
                Button {
                    retry()
                } label: {
                    HStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "arrow.clockwise")
                        Text("Try Again")
                    }
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(.white)
                    .padding(.horizontal, AppTheme.spacingLG)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.primaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
                }
                .padding(.top, AppTheme.spacingSM)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, AppTheme.spacingLG)
        .padding(.vertical, AppTheme.spacingXXL)
        .accessibilityElement(children: .combine)
    }
}
