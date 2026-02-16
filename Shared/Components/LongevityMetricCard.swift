import SwiftUI

/// Expandable card displaying a single longevity metric with gradient bar,
/// dual average markers, delta years impact, and collapsible insight text.
struct LongevityMetricCard: View {
    let result: LongevityEngine.MetricResult
    let isExpanded: Bool
    let onToggle: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            // Header: metric name + expand chevron
            Button(action: onToggle) {
                HStack {
                    Text(result.id.displayName)
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(AppColors.textPrimary)
                        .tracking(0.5)
                    Spacer()
                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .font(.system(size: 12))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }
            .buttonStyle(.plain)

            // Value display
            HStack(alignment: .firstTextBaseline) {
                Text(LongevityEngine.formatValue(result.sixMonthAvg, for: result.id))
                    .font(.system(size: 20, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                Spacer()
            }

            // Gradient bar
            LongevityGradientBar(
                range: result.id.gradientRange,
                sixMonthAvg: result.sixMonthAvg,
                thirtyDayAvg: result.thirtyDayAvg,
                deltaYears: result.deltaYears,
                isHigherBetter: result.id.isHigherBetter,
                rangeMinLabel: rangeMinLabel,
                rangeMaxLabel: rangeMaxLabel
            )

            // Expandable insight section
            if isExpanded {
                VStack(alignment: .leading, spacing: AppTheme.spacingXS) {
                    Text(result.insightTitle)
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    Text(result.insightBody)
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                        .fixedSize(horizontal: false, vertical: true)

                    HStack {
                        Text("VIEW TREND")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(AppColors.primaryBlue)
                        Image(systemName: "arrow.right")
                            .font(.system(size: 10))
                            .foregroundStyle(AppColors.primaryBlue)
                    }
                    .padding(.top, AppTheme.spacingXS)
                }
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    // MARK: - Range Labels

    private var rangeMinLabel: String {
        let range = result.id.gradientRange
        switch result.id {
        case .sleepConsistency, .leanBodyMass:
            return "\(Int(range.lowerBound))%"
        case .hoursOfSleep:
            return "\(Int(range.lowerBound))h"
        case .hrZones1to3Weekly, .hrZones4to5Weekly, .strengthActivityWeekly:
            return "\(Int(range.lowerBound))h"
        case .dailySteps:
            return "\(Int(range.lowerBound / 1000))K"
        case .vo2Max:
            return "\(Int(range.lowerBound))"
        case .restingHeartRate:
            return "\(Int(range.lowerBound))bpm"
        }
    }

    private var rangeMaxLabel: String {
        let range = result.id.gradientRange
        switch result.id {
        case .sleepConsistency, .leanBodyMass:
            return "\(Int(range.upperBound))%"
        case .hoursOfSleep:
            return "\(Int(range.upperBound))h"
        case .hrZones1to3Weekly, .hrZones4to5Weekly, .strengthActivityWeekly:
            return "\(Int(range.upperBound))h"
        case .dailySteps:
            return "\(Int(range.upperBound / 1000))K"
        case .vo2Max:
            return "\(Int(range.upperBound))"
        case .restingHeartRate:
            return "\(Int(range.upperBound))bpm"
        }
    }
}
