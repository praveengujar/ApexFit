import SwiftUI

struct WorkoutCard: View {
    let workout: WorkoutRecord

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            // Zone indicator
            RoundedRectangle(cornerRadius: 3)
                .fill(AppColors.strainZoneColor(workout.primaryZone))
                .frame(width: 6)

            VStack(alignment: .leading, spacing: AppTheme.spacingXS) {
                Text(workout.workoutName)
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)

                HStack(spacing: AppTheme.spacingMD) {
                    Label(workout.formattedDuration, systemImage: "clock")
                    if let avgHR = workout.averageHeartRate {
                        Label("\(Int(avgHR)) BPM", systemImage: "heart.fill")
                    }
                    Label("\(Int(workout.activeCalories)) cal", systemImage: "flame.fill")
                }
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
            }

            Spacer()

            VStack(alignment: .trailing, spacing: 2) {
                Text(workout.strainScore.formattedOneDecimal)
                    .font(AppTypography.metricSmall)
                    .foregroundStyle(AppColors.primaryBlue)
                Text("Strain")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(workout.workoutName), \(workout.formattedDuration), strain \(workout.strainScore.formattedOneDecimal)")
    }
}
