import SwiftUI

struct RecoveryInsightView: View {
    let metric: DailyMetric?

    private var insightText: String {
        guard let metric, let score = metric.recoveryScore else {
            return "Not enough data to generate a recovery insight. Wear your device to sleep tonight for a full analysis."
        }

        let zone = metric.recoveryZone ?? RecoveryZone.from(score: score)
        var parts: [String] = []

        parts.append("Your Recovery is \(Int(score))% (\(zone.label)).")

        // Key factors
        var factors: [String] = []

        if let hrv = metric.hrvRMSSD {
            if hrv > 60 {
                factors.append("strong HRV at \(hrv.msFormatted)")
            } else if hrv < 30 {
                factors.append("low HRV at \(hrv.msFormatted)")
            }
        }

        if let rhr = metric.restingHeartRate {
            if rhr < 55 {
                factors.append("great resting heart rate at \(rhr.bpmFormatted)")
            } else if rhr > 70 {
                factors.append("elevated resting heart rate at \(rhr.bpmFormatted)")
            }
        }

        if let sleepPerf = metric.sleepPerformance {
            if sleepPerf >= 90 {
                factors.append("excellent sleep at \(sleepPerf.formattedPercentage)")
            } else if sleepPerf < 70 {
                factors.append("insufficient sleep at \(sleepPerf.formattedPercentage)")
            }
        }

        if !factors.isEmpty {
            parts.append("Key factors: " + factors.joined(separator: ", ") + ".")
        }

        switch zone {
        case .green:
            parts.append("Your body is well-recovered. Consider pushing for a high-intensity workout today.")
        case .yellow:
            parts.append("Your body is moderately recovered. A steady, moderate effort is recommended.")
        case .red:
            parts.append("Your body needs rest. Focus on low-intensity activity and prioritize sleep tonight.")
        }

        return parts.joined(separator: " ")
    }

    private var keyFactors: [(String, String, Color)] {
        guard let metric else { return [] }
        var result: [(String, String, Color)] = []

        if let hrv = metric.hrvRMSSD {
            let color: Color = hrv > 40 ? AppColors.recoveryGreen : AppColors.recoveryRed
            result.append(("waveform.path.ecg", "HRV: \(hrv.msFormatted)", color))
        }

        if let rhr = metric.restingHeartRate {
            let color: Color = rhr < 65 ? AppColors.recoveryGreen : AppColors.recoveryRed
            result.append(("heart.fill", "RHR: \(rhr.bpmFormatted)", color))
        }

        if let sleepPerf = metric.sleepPerformance {
            let color: Color = sleepPerf >= 85 ? AppColors.recoveryGreen : (sleepPerf >= 70 ? AppColors.recoveryYellow : AppColors.recoveryRed)
            result.append(("moon.fill", "Sleep: \(sleepPerf.formattedPercentage)", color))
        }

        return result
    }

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
            // Insight header
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "brain.head.profile")
                    .font(.title3)
                    .foregroundStyle(AppColors.primaryBlue)

                Text("AI Insight")
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.primaryBlue)
            }

            // Insight text
            Text(insightText)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .lineSpacing(4)

            // Key factors
            if !keyFactors.isEmpty {
                Divider()
                    .overlay(AppColors.backgroundTertiary)

                VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                    Text("Key Factors")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textTertiary)

                    HStack(spacing: AppTheme.spacingMD) {
                        ForEach(keyFactors, id: \.1) { icon, label, color in
                            HStack(spacing: AppTheme.spacingXS) {
                                Image(systemName: icon)
                                    .font(.caption)
                                    .foregroundStyle(color)
                                Text(label)
                                    .font(AppTypography.caption)
                                    .foregroundStyle(AppColors.textPrimary)
                            }
                        }
                    }
                }
            }

            // Quick actions
            Divider()
                .overlay(AppColors.backgroundTertiary)

            HStack(spacing: AppTheme.spacingSM) {
                NavigationLink(destination: SleepDetailView()) {
                    quickActionButton(title: "See Sleep Details", icon: "moon.fill")
                }
                .buttonStyle(.plain)

                NavigationLink(destination: StrainTrendView()) {
                    quickActionButton(title: "View Strain History", icon: "flame.fill")
                }
                .buttonStyle(.plain)
            }
        }
        .cardStyle()
    }

    // MARK: - Quick Action Button

    private func quickActionButton(title: String, icon: String) -> some View {
        HStack(spacing: AppTheme.spacingXS) {
            Image(systemName: icon)
                .font(.caption)
            Text(title)
                .font(AppTypography.labelSmall)
        }
        .foregroundStyle(AppColors.primaryBlue)
        .padding(.horizontal, AppTheme.spacingSM)
        .padding(.vertical, AppTheme.spacingXS + 2)
        .background(AppColors.primaryBlue.opacity(0.12))
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
    }
}

#Preview {
    NavigationStack {
        RecoveryInsightView(metric: nil)
            .padding()
            .background(AppColors.backgroundPrimary)
    }
}
