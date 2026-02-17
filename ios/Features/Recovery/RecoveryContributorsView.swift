import SwiftUI

struct RecoveryContributorsView: View {
    let metric: DailyMetric?

    private var contributors: [ContributorData] {
        guard let metric else { return ContributorData.placeholders }

        return [
            ContributorData(
                icon: "waveform.path.ecg",
                name: "HRV",
                value: metric.hrvRMSSD.map { $0.msFormatted } ?? "--",
                score: metric.hrvRMSSD != nil ? contributorScore(for: metric.hrvRMSSD, baseline: 50, higherIsBetter: true) : nil,
                delta: metric.hrvRMSSD.map { deltaString(current: $0, baseline: 50, higherIsBetter: true) },
                deltaPositive: metric.hrvRMSSD.map { $0 >= 50 }
            ),
            ContributorData(
                icon: "heart.fill",
                name: "Resting HR",
                value: metric.restingHeartRate.map { $0.bpmFormatted } ?? "--",
                score: metric.restingHeartRate != nil ? contributorScore(for: metric.restingHeartRate, baseline: 60, higherIsBetter: false) : nil,
                delta: metric.restingHeartRate.map { deltaString(current: $0, baseline: 60, higherIsBetter: false) },
                deltaPositive: metric.restingHeartRate.map { $0 <= 60 }
            ),
            ContributorData(
                icon: "moon.fill",
                name: "Sleep Performance",
                value: metric.sleepPerformance.map { $0.formattedPercentage } ?? "--",
                score: metric.sleepPerformance != nil ? contributorScore(for: metric.sleepPerformance, baseline: 85, higherIsBetter: true) : nil,
                delta: metric.sleepPerformance.map { deltaString(current: $0, baseline: 85, higherIsBetter: true) },
                deltaPositive: metric.sleepPerformance.map { $0 >= 85 }
            ),
            ContributorData(
                icon: "lungs.fill",
                name: "Respiratory Rate",
                value: metric.respiratoryRate.map { "\($0.formattedOneDecimal) brpm" } ?? "--",
                score: metric.respiratoryRate != nil ? contributorScore(for: metric.respiratoryRate, baseline: 15, higherIsBetter: false) : nil,
                delta: metric.respiratoryRate.map { deltaString(current: $0, baseline: 15, higherIsBetter: false) },
                deltaPositive: metric.respiratoryRate.map { $0 <= 15 }
            ),
            ContributorData(
                icon: "drop.fill",
                name: "SpO2",
                value: metric.spo2.map { "\($0.formattedOneDecimal)%" } ?? "--",
                score: metric.spo2 != nil ? contributorScore(for: metric.spo2, baseline: 96, higherIsBetter: true) : nil,
                delta: metric.spo2.map { deltaString(current: $0, baseline: 96, higherIsBetter: true) },
                deltaPositive: metric.spo2.map { $0 >= 96 }
            ),
        ]
    }

    var body: some View {
        VStack(spacing: AppTheme.spacingSM) {
            ForEach(contributors) { contributor in
                contributorRow(contributor)
            }
        }
    }

    // MARK: - Contributor Row

    private func contributorRow(_ contributor: ContributorData) -> some View {
        VStack(spacing: AppTheme.spacingSM) {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: contributor.icon)
                    .font(.body)
                    .foregroundStyle(barColor(for: contributor))
                    .frame(width: 24)

                Text(contributor.name)
                    .font(AppTypography.bodyMedium)
                    .foregroundStyle(AppColors.textPrimary)

                Spacer()

                Text(contributor.value)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                if let delta = contributor.delta {
                    HStack(spacing: 2) {
                        Image(systemName: contributor.deltaPositive == true ? "arrow.up.right" : "arrow.down.right")
                            .font(.caption2)
                        Text(delta)
                            .font(AppTypography.caption)
                    }
                    .foregroundStyle(contributor.deltaPositive == true ? AppColors.recoveryGreen : AppColors.recoveryRed)
                }
            }

            if let score = contributor.score {
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(AppColors.backgroundTertiary)
                            .frame(height: 6)

                        RoundedRectangle(cornerRadius: 4)
                            .fill(barColor(for: contributor))
                            .frame(width: geometry.size.width * (score / 100.0).clamped(to: 0...1), height: 6)
                    }
                }
                .frame(height: 6)
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    // MARK: - Helpers

    private func barColor(for contributor: ContributorData) -> Color {
        guard let positive = contributor.deltaPositive else {
            return AppColors.textTertiary
        }
        return positive ? AppColors.recoveryGreen : AppColors.recoveryRed
    }

    private func contributorScore(for value: Double?, baseline: Double, higherIsBetter: Bool) -> Double {
        guard let value else { return 0 }
        let ratio: Double
        if higherIsBetter {
            ratio = value / baseline
        } else {
            ratio = baseline / value
        }
        return (ratio * 50).clamped(to: 0...100)
    }

    private func deltaString(current: Double, baseline: Double, higherIsBetter: Bool) -> String {
        let diff = current - baseline
        let sign = diff >= 0 ? "+" : ""
        if baseline > 10 {
            return "\(sign)\(diff.formattedOneDecimal)"
        }
        return "\(sign)\(String(format: "%.2f", diff))"
    }
}

// MARK: - Contributor Data

private struct ContributorData: Identifiable {
    let id = UUID()
    let icon: String
    let name: String
    let value: String
    let score: Double?
    let delta: String?
    let deltaPositive: Bool?

    static var placeholders: [ContributorData] {
        [
            ContributorData(icon: "waveform.path.ecg", name: "HRV", value: "--", score: nil, delta: nil, deltaPositive: nil),
            ContributorData(icon: "heart.fill", name: "Resting HR", value: "--", score: nil, delta: nil, deltaPositive: nil),
            ContributorData(icon: "moon.fill", name: "Sleep Performance", value: "--", score: nil, delta: nil, deltaPositive: nil),
            ContributorData(icon: "lungs.fill", name: "Respiratory Rate", value: "--", score: nil, delta: nil, deltaPositive: nil),
            ContributorData(icon: "drop.fill", name: "SpO2", value: "--", score: nil, delta: nil, deltaPositive: nil),
        ]
    }
}

#Preview {
    RecoveryContributorsView(metric: nil)
        .background(AppColors.backgroundPrimary)
}
