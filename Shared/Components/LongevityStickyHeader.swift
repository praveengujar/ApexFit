import SwiftUI

/// Compact sticky header for the Longevity dashboard, shown when the hero blob
/// scrolls off screen. Displays years younger/older, ApexFit Age with mini blob,
/// and Pace of Aging.
struct LongevityStickyHeader: View {
    let yearsYoungerOlder: Double
    let apexFitAge: Double
    let paceOfAging: Double

    var body: some View {
        HStack(spacing: 0) {
            // Left: Years younger/older
            VStack(spacing: 2) {
                Text(String(format: "%.1f", abs(yearsYoungerOlder)))
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundStyle(yearsColor)
                Text(yearsYoungerOlder < 0 ? "YEARS YOUNGER" : "YEARS OLDER")
                    .font(.system(size: 8, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
            }
            .frame(maxWidth: .infinity)

            // Center: Mini blob + age
            ZStack {
                TimelineView(.animation(minimumInterval: 1.0 / 15.0)) { timeline in
                    OrganicShape(harmonics: 5, amplitude: 0.08, phase: timeline.date.timeIntervalSinceReferenceDate * 0.3)
                        .fill(
                            RadialGradient(
                                colors: [blobColor.opacity(0.2), blobColor.opacity(0.5), .clear],
                                center: .center,
                                startRadius: 0,
                                endRadius: 40
                            )
                        )
                        .frame(width: 80, height: 80)
                }

                VStack(spacing: 0) {
                    Text(String(format: "%.1f", apexFitAge))
                        .font(.system(size: 20, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    Text("APEXFIT AGE")
                        .font(.system(size: 7, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                        .tracking(0.5)
                }
            }
            .frame(maxWidth: .infinity)

            // Right: Pace of Aging
            VStack(spacing: 2) {
                Text(String(format: "%.1fx", paceOfAging))
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundStyle(paceColor)
                Text("PACE OF AGING")
                    .font(.system(size: 8, weight: .semibold))
                    .foregroundStyle(AppColors.textTertiary)
                    .tracking(0.5)
            }
            .frame(maxWidth: .infinity)
        }
        .padding(.vertical, AppTheme.spacingSM)
        .padding(.horizontal, AppTheme.spacingMD)
        .background(AppColors.backgroundPrimary.opacity(0.95))
    }

    private var yearsColor: Color {
        yearsYoungerOlder < 0 ? AppColors.longevityGreen : AppColors.longevityOrange
    }

    private var blobColor: Color {
        yearsYoungerOlder < 0 ? AppColors.longevityGreen : AppColors.longevityOrange
    }

    private var paceColor: Color {
        if paceOfAging < 0.5 { return AppColors.longevityGreen }
        if paceOfAging < 1.5 { return AppColors.recoveryYellow }
        return AppColors.longevityOrange
    }
}
