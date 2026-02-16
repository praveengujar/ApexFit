import SwiftUI

struct MyPlanCard: View {
    var planName: String
    var daysLeft: Int
    var progressPercent: Double
    var onTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            sectionHeader
            planCard
        }
    }

    // MARK: - Section Header (outside the card)

    private var sectionHeader: some View {
        Text("My Plan")
            .font(AppTypography.heading2)
            .foregroundStyle(AppColors.textPrimary)
    }

    // MARK: - Plan Card

    private var planCard: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                planHeader
                daysLeftLabel
                progressSection
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(planName), \(daysLeft) days left, \(Int(progressPercent)) percent accomplished")
        .accessibilityAddTraits(.isButton)
    }

    // MARK: - Plan Header

    private var planHeader: some View {
        HStack {
            Text(planName.uppercased())
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Days Left

    private var daysLeftLabel: some View {
        Text("\(daysLeft) days left")
            .font(AppTypography.bodySmall)
            .foregroundStyle(AppColors.textSecondary)
    }

    // MARK: - Progress Section

    private var progressSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("\(Int(progressPercent))% ACCOMPLISHED")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)

            progressBar
        }
    }

    // MARK: - Progress Bar

    private var progressBar: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                // Track
                RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                    .fill(AppColors.backgroundTertiary)
                    .frame(height: 8)

                // Fill
                RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                    .fill(AppColors.recoveryGreen)
                    .frame(
                        width: geometry.size.width * clampedProgress,
                        height: 8
                    )
            }
        }
        .frame(height: 8)
    }

    // MARK: - Helpers

    private var clampedProgress: CGFloat {
        CGFloat(progressPercent.clamped(to: 0...100) / 100.0)
    }
}
