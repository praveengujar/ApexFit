import SwiftUI

struct HealthMonitorCard: View {
    let metricsInRange: Int
    let totalMetrics: Int
    var onTap: () -> Void = {}

    private var outOfRange: Int {
        max(totalMetrics - metricsInRange, 0)
    }

    private var allInRange: Bool {
        outOfRange == 0
    }

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                // MARK: - Title Row
                HStack {
                    Text("HEALTH MONITOR")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    Spacer()

                    Image(systemName: "chevron.right")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                }

                Spacer()

                // MARK: - Status Row
                HStack(spacing: AppTheme.spacingSM) {
                    Image(systemName: statusIcon)
                        .font(.system(size: 20))
                        .foregroundStyle(statusColor)

                    VStack(alignment: .leading, spacing: 2) {
                        Text(statusText)
                            .font(AppTypography.labelMedium)
                            .foregroundStyle(statusColor)

                        Text("\(metricsInRange)/\(totalMetrics) Metrics")
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Health Monitor: \(statusText), \(metricsInRange) of \(totalMetrics) metrics in range")
        .accessibilityAddTraits(.isButton)
    }

    // MARK: - Computed Properties

    private var statusIcon: String {
        allInRange ? "checkmark.circle.fill" : "exclamationmark.triangle.fill"
    }

    private var statusColor: Color {
        allInRange ? AppColors.recoveryGreen : .orange
    }

    private var statusText: String {
        allInRange ? "WITHIN RANGE" : "\(outOfRange) OUT OF RANGE"
    }
}

#Preview {
    ZStack {
        AppColors.backgroundPrimary.ignoresSafeArea()
        HStack(spacing: AppTheme.spacingMD) {
            HealthMonitorCard(metricsInRange: 5, totalMetrics: 5)
            HealthMonitorCard(metricsInRange: 3, totalMetrics: 5)
        }
        .padding()
    }
}
