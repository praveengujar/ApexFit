import SwiftUI

/// A full-width metric row used on the dashboard for metrics like HRV, RHR, VO2 Max, HR Zones, Calories, and Steps.
/// Displays an icon, label, current value with optional trend indicator, and baseline comparison.
struct DashboardMetricRow: View {
    let icon: String
    let label: String
    let value: String
    var baseline: String? = nil
    var trendUp: Bool? = nil
    var onTap: (() -> Void)? = nil

    var body: some View {
        Button {
            onTap?()
        } label: {
            HStack(spacing: AppTheme.spacingMD) {
                // MARK: - Icon
                iconView

                // MARK: - Label
                Text(label.uppercased())
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)

                Spacer()

                // MARK: - Value + Trend
                valueColumn
            }
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityLabel(accessibilityText)
    }

    // MARK: - Icon View

    private var iconView: some View {
        Image(systemName: icon)
            .font(.system(size: 15, weight: .medium))
            .foregroundStyle(AppColors.textSecondary)
            .frame(width: 30, height: 30)
            .background(AppColors.backgroundTertiary)
            .clipShape(Circle())
    }

    // MARK: - Value Column

    private var valueColumn: some View {
        VStack(alignment: .trailing, spacing: 2) {
            HStack(spacing: AppTheme.spacingXS) {
                Text(value)
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.textPrimary)

                if let trendUp {
                    trendIndicator(up: trendUp)
                }
            }

            if let baseline {
                Text(baseline)
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
    }

    // MARK: - Trend Indicator

    private func trendIndicator(up: Bool) -> some View {
        Image(systemName: up ? "triangle.fill" : "triangle.fill")
            .font(.system(size: 8, weight: .bold))
            .foregroundStyle(up ? AppColors.recoveryGreen : Color.orange)
            .rotationEffect(up ? .zero : .degrees(180))
    }

    // MARK: - Accessibility

    private var accessibilityText: String {
        var text = "\(label), \(value)"
        if let baseline {
            text += ", baseline \(baseline)"
        }
        if let trendUp {
            text += trendUp ? ", trending up" : ", trending down"
        }
        return text
    }
}

// MARK: - Preview

#Preview("HRV Row - Trend Up") {
    VStack(spacing: AppTheme.spacingSM) {
        DashboardMetricRow(
            icon: "waveform.path.ecg",
            label: "HRV",
            value: "64 ms",
            baseline: "Baseline: 58 ms",
            trendUp: true
        )

        DashboardMetricRow(
            icon: "heart.fill",
            label: "Resting Heart Rate",
            value: "52 bpm",
            baseline: "Baseline: 49 bpm",
            trendUp: false
        )

        DashboardMetricRow(
            icon: "lungs.fill",
            label: "VO2 Max",
            value: "46.2",
            baseline: "Baseline: 45.8",
            trendUp: true
        )

        DashboardMetricRow(
            icon: "flame.fill",
            label: "Calories",
            value: "2,140",
            baseline: nil,
            trendUp: nil
        )

        DashboardMetricRow(
            icon: "figure.walk",
            label: "Steps",
            value: "8,432",
            baseline: "Goal: 10,000"
        )
    }
    .padding(AppTheme.spacingMD)
    .background(AppColors.backgroundPrimary)
}
