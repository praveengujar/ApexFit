import SwiftUI

struct RecoveryDetailWatchView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    private var recoveryZone: RecoveryZone {
        RecoveryZone(rawValue: context.recoveryZone) ?? .green
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                CircularGaugeView(
                    value: context.recoveryScore,
                    maxValue: 99,
                    label: "Recovery",
                    unit: "%",
                    color: AppColors.recoveryColor(for: recoveryZone),
                    size: .small
                )

                Text(recoveryZone.label)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(AppColors.recoveryColor(for: recoveryZone))

                // Contributing factors
                VStack(spacing: 6) {
                    factorRow(label: "HRV", value: context.hrvBaseline.msFormatted, good: true)
                    factorRow(label: "RHR", value: context.restingHeartRateBaseline.bpmFormatted, good: true)
                    factorRow(label: "Sleep", value: context.sleepPerformance.formattedPercentage, good: context.sleepPerformance >= 85)
                }
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))

                // Strain target
                VStack(spacing: 4) {
                    Text("Strain Target")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(AppColors.textSecondary)
                    Text("\(context.strainTargetLow.formattedOneDecimal) - \(context.strainTargetHigh.formattedOneDecimal)")
                        .font(.system(size: 20, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.primaryBlue)
                }
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))

                Text(coachHint)
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 8)
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Recovery")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func factorRow(label: String, value: String, good: Bool) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(AppColors.textPrimary)
            Image(systemName: good ? "arrow.up" : "arrow.down")
                .font(.system(size: 10))
                .foregroundStyle(good ? AppColors.recoveryGreen : AppColors.recoveryRed)
        }
    }

    private var coachHint: String {
        switch recoveryZone {
        case .green: return "Great day to push hard"
        case .yellow: return "Moderate activity recommended"
        case .red: return "Focus on rest and recovery"
        }
    }
}
