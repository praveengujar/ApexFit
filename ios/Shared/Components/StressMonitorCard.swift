import SwiftUI

struct StressMonitorCard: View {
    let stressScore: Double?
    let lastUpdated: Date?
    var onTap: () -> Void = {}

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                // MARK: - Title Row
                HStack {
                    Text("STRESS MONITOR")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    Spacer()

                    Image(systemName: "chevron.right")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                }

                Spacer()

                // MARK: - Score + Level
                if let score = stressScore {
                    HStack(spacing: AppTheme.spacingSM) {
                        // Score Badge
                        Text(score.formattedOneDecimal)
                            .font(AppTypography.labelMedium)
                            .foregroundStyle(AppColors.textPrimary)
                            .padding(.horizontal, AppTheme.spacingSM)
                            .padding(.vertical, AppTheme.spacingXS)
                            .background(stressLevel(for: score).color.opacity(0.2))
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

                        VStack(alignment: .leading, spacing: 2) {
                            Text(stressLevel(for: score).label)
                                .font(AppTypography.labelMedium)
                                .foregroundStyle(stressLevel(for: score).color)

                            if let lastUpdated {
                                Text(lastUpdated.hourMinuteString)
                                    .font(AppTypography.caption)
                                    .foregroundStyle(AppColors.textSecondary)
                            }
                        }
                    }
                } else {
                    // No data state
                    HStack(spacing: AppTheme.spacingSM) {
                        Text("--")
                            .font(AppTypography.labelMedium)
                            .foregroundStyle(AppColors.textTertiary)
                            .padding(.horizontal, AppTheme.spacingSM)
                            .padding(.vertical, AppTheme.spacingXS)
                            .background(AppColors.backgroundTertiary)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

                        Text("NO DATA")
                            .font(AppTypography.labelMedium)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(accessibilityText)
        .accessibilityAddTraits(.isButton)
    }

    // MARK: - Stress Levels

    private enum StressLevel {
        case low, medium, high, veryHigh

        var label: String {
            switch self {
            case .low: return "LOW"
            case .medium: return "MEDIUM"
            case .high: return "HIGH"
            case .veryHigh: return "VERY HIGH"
            }
        }

        var color: Color {
            switch self {
            case .low: return AppColors.recoveryGreen
            case .medium: return AppColors.recoveryYellow
            case .high: return .orange
            case .veryHigh: return AppColors.recoveryRed
            }
        }
    }

    private func stressLevel(for score: Double) -> StressLevel {
        switch score {
        case ..<1: return .low
        case 1..<2: return .medium
        case 2..<2.5: return .high
        default: return .veryHigh
        }
    }

    // MARK: - Accessibility

    private var accessibilityText: String {
        if let score = stressScore {
            let level = stressLevel(for: score)
            return "Stress Monitor: \(score.formattedOneDecimal), \(level.label)"
        }
        return "Stress Monitor: No data available"
    }
}

#Preview {
    ZStack {
        AppColors.backgroundPrimary.ignoresSafeArea()
        HStack(spacing: AppTheme.spacingMD) {
            StressMonitorCard(
                stressScore: 1.5,
                lastUpdated: Date()
            )
            StressMonitorCard(
                stressScore: nil,
                lastUpdated: nil
            )
        }
        .padding()
    }
}
