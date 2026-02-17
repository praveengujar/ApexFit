import SwiftUI

struct WorkoutSummaryView: View {
    @Environment(WatchWorkoutManager.self) private var workoutManager

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 30))
                    .foregroundStyle(AppColors.recoveryGreen)

                Text("Workout Complete")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(AppColors.textPrimary)

                VStack(spacing: 6) {
                    summaryRow("Duration", value: workoutManager.workoutDurationFormatted)
                    summaryRow("Strain", value: workoutManager.currentStrain.formattedOneDecimal)
                    summaryRow("Avg HR", value: "\(Int(workoutManager.averageHeartRate)) bpm")
                    summaryRow("Max HR", value: "\(Int(workoutManager.maxHeartRateReading)) bpm")
                    summaryRow("Calories", value: "\(Int(workoutManager.currentCalories))")
                    if workoutManager.currentDistance > 0 {
                        summaryRow("Distance", value: "\(workoutManager.currentDistance.formattedOneDecimal) km")
                    }
                }
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))

                VStack(spacing: 4) {
                    Text("Zone Time")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                    HStack(spacing: 6) {
                        zonePill(zone: 5, minutes: workoutManager.zone5Minutes)
                        zonePill(zone: 4, minutes: workoutManager.zone4Minutes)
                        zonePill(zone: 3, minutes: workoutManager.zone3Minutes)
                    }
                    HStack(spacing: 6) {
                        zonePill(zone: 2, minutes: workoutManager.zone2Minutes)
                        zonePill(zone: 1, minutes: workoutManager.zone1Minutes)
                    }
                }
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))

                VStack(spacing: 2) {
                    Text("Day Strain")
                        .font(.system(size: 12))
                        .foregroundStyle(AppColors.textSecondary)
                    Text(workoutManager.totalDayStrain.formattedOneDecimal)
                        .font(.system(size: 20, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.primaryBlue)
                }
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Summary")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func summaryRow(_ label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(AppColors.textPrimary)
        }
    }

    private func zonePill(zone: Int, minutes: Double) -> some View {
        HStack(spacing: 2) {
            Text("Z\(zone)").font(.system(size: 10, weight: .bold))
            Text("\(Int(minutes))m").font(.system(size: 10))
        }
        .foregroundStyle(.white)
        .padding(.horizontal, 6)
        .padding(.vertical, 3)
        .background(AppColors.strainZoneColor(zone))
        .clipShape(Capsule())
    }
}
