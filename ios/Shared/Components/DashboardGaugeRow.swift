import SwiftUI

struct DashboardGaugeRow: View {
    let metric: DailyMetric?
    var collapseProgress: CGFloat = 0
    var onSleepTap: () -> Void = {}
    var onRecoveryTap: () -> Void = {}
    var onStrainTap: () -> Void = {}

    // Expanded → Compact dimensions
    private let expandedSize: CGFloat = 120
    private let compactSize: CGFloat = 36
    private let expandedStroke: CGFloat = 8
    private let compactStroke: CGFloat = 4

    private var gaugeDimension: CGFloat {
        lerp(expandedSize, compactSize, collapseProgress)
    }

    private var gaugeStroke: CGFloat {
        lerp(expandedStroke, compactStroke, collapseProgress)
    }

    private var labelOpacity: Double {
        // Fade out labels in the first half of collapse
        Double(max(0, 1 - collapseProgress * 2.5))
    }

    private var isCompact: Bool { collapseProgress > 0.6 }

    var body: some View {
        HStack(spacing: 0) {
            gaugeItem(
                value: metric?.sleepPerformance ?? 0,
                maxValue: 100,
                label: "SLEEP",
                unit: "%",
                color: AppColors.sleepDeep,
                action: onSleepTap
            )

            gaugeItem(
                value: metric?.recoveryScore ?? 0,
                maxValue: 99,
                label: "RECOVERY",
                unit: "%",
                color: recoveryColor,
                action: onRecoveryTap
            )

            gaugeItem(
                value: metric?.strainScore ?? 0,
                maxValue: 21,
                label: "STRAIN",
                unit: "",
                color: AppColors.primaryBlue,
                action: onStrainTap
            )
        }
        .padding(.vertical, lerp(AppTheme.spacingSM, AppTheme.spacingXS, collapseProgress))
        .padding(.horizontal, isCompact ? AppTheme.spacingSM : 0)
        .background(
            isCompact
                ? AppColors.backgroundPrimary.opacity(0.95)
                : Color.clear
        )
    }

    // MARK: - Gauge Item

    private func gaugeItem(
        value: Double,
        maxValue: Double,
        label: String,
        unit: String,
        color: Color,
        action: @escaping () -> Void
    ) -> some View {
        Group {
            if isCompact {
                // Compact: horizontal ring + value + label
                compactItem(value: value, maxValue: maxValue, label: label, unit: unit, color: color, action: action)
            } else {
                // Expanded: vertical ring + label button
                expandedItem(value: value, maxValue: maxValue, label: label, unit: unit, color: color, action: action)
            }
        }
        .frame(maxWidth: .infinity)
    }

    private func expandedItem(
        value: Double,
        maxValue: Double,
        label: String,
        unit: String,
        color: Color,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            VStack(spacing: AppTheme.spacingSM) {
                CircularGaugeView(
                    value: value,
                    maxValue: maxValue,
                    label: unit,
                    unit: unit,
                    color: color,
                    size: .medium,
                    overrideDimension: gaugeDimension,
                    overrideLineWidth: gaugeStroke,
                    showCenterText: collapseProgress < 0.5
                )

                Text(label)
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
                    .opacity(labelOpacity)
                    .frame(height: labelOpacity > 0.01 ? nil : 0)
                    .clipped()
            }
        }
        .buttonStyle(.plain)
    }

    private func compactItem(
        value: Double,
        maxValue: Double,
        label: String,
        unit: String,
        color: Color,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack(spacing: AppTheme.spacingSM) {
                CircularGaugeView(
                    value: value,
                    maxValue: maxValue,
                    label: "",
                    unit: unit,
                    color: color,
                    size: .small,
                    overrideDimension: compactSize,
                    overrideLineWidth: compactStroke,
                    showCenterText: false
                )

                VStack(alignment: .leading, spacing: 0) {
                    Text(formattedValue(value, maxValue: maxValue))
                        .font(.system(size: 14, weight: .bold, design: .rounded))
                        .foregroundStyle(color)
                    Text(label)
                        .font(.system(size: 9, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                }
            }
        }
        .buttonStyle(.plain)
    }

    // MARK: - Helpers

    private var recoveryColor: Color {
        if let zone = metric?.recoveryZone {
            return AppColors.recoveryColor(for: zone)
        }
        if let score = metric?.recoveryScore {
            return AppColors.recoveryColor(for: score)
        }
        return AppColors.textTertiary
    }

    private func formattedValue(_ value: Double, maxValue: Double) -> String {
        if value == 0 && maxValue > 0 { return "--" }
        if maxValue <= 21 { return value.formattedOneDecimal }
        return value.formattedNoDecimal
    }

    private func lerp(_ a: CGFloat, _ b: CGFloat, _ t: CGFloat) -> CGFloat {
        a + (b - a) * t.clamped(to: 0...1)
    }
}

#Preview {
    ZStack {
        AppColors.backgroundPrimary.ignoresSafeArea()
        VStack(spacing: 20) {
            DashboardGaugeRow(metric: nil, collapseProgress: 0)
            Divider()
            DashboardGaugeRow(metric: nil, collapseProgress: 1)
        }
    }
}
