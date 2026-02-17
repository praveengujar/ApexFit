import SwiftUI

struct AchievementCardsView: View {
    let level: Int
    let greenRecoveryCount: Int
    let apexFitAge: Double
    let yearsYoungerOlder: Double

    var body: some View {
        HStack(spacing: AppTheme.spacingSM) {
            // Level Card
            VStack(spacing: AppTheme.spacingSM) {
                ZStack {
                    Circle()
                        .fill(AppColors.recoveryGreen.opacity(0.2))
                        .frame(width: 44, height: 44)
                    Text("\(level)")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundStyle(AppColors.recoveryGreen)
                }

                Text("LEVEL")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(AppColors.textSecondary)
                    .tracking(1)

                Text("\(greenRecoveryCount) Green Recoveries")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
            .cardStyle()

            // ApexFit Age Card
            VStack(spacing: AppTheme.spacingSM) {
                ZStack {
                    Circle()
                        .fill(AppColors.longevityGreen.opacity(0.2))
                        .frame(width: 44, height: 44)
                    Text(apexFitAge.formattedNoDecimal)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundStyle(AppColors.longevityGreen)
                }

                Text("APEXFIT AGE")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(AppColors.textSecondary)
                    .tracking(1)

                let diff = abs(yearsYoungerOlder)
                let label = yearsYoungerOlder <= 0 ? "younger" : "older"
                Text("\(diff.formattedOneDecimal) yrs \(label)")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
            .frame(maxWidth: .infinity)
            .cardStyle()
        }
    }
}
