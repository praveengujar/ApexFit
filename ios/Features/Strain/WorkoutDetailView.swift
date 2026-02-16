import SwiftUI

struct WorkoutDetailView: View {
    let workout: WorkoutRecord

    private var zoneMinutes: [Double] {
        [
            workout.zone1Minutes,
            workout.zone2Minutes,
            workout.zone3Minutes,
            workout.zone4Minutes,
            workout.zone5Minutes,
        ]
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingLG) {
                headerSection
                strainGaugeSection
                statsGridSection
                hrZoneSection
                zoneTimeBreakdownSection
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle(workout.workoutName)
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Text(workout.workoutName)
                .font(AppTypography.heading1)
                .foregroundStyle(AppColors.textPrimary)

            HStack(spacing: AppTheme.spacingMD) {
                Label(workout.formattedDuration, systemImage: "clock")
                Label(workout.startDate.formatted(.dateTime.month(.abbreviated).day().hour().minute()), systemImage: "calendar")
            }
            .font(AppTypography.bodySmall)
            .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingSM)
    }

    // MARK: - Strain Gauge

    private var strainGaugeSection: some View {
        VStack(spacing: AppTheme.spacingSM) {
            CircularGaugeView(
                value: workout.strainScore,
                maxValue: 21,
                label: "Workout Strain",
                unit: "",
                color: AppColors.primaryBlue,
                size: .medium
            )
        }
        .frame(maxWidth: .infinity)
    }

    // MARK: - Stats Grid

    private var statsGridSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Workout Stats")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingSM) {
                MetricTile(
                    title: "Avg Heart Rate",
                    value: workout.averageHeartRate.map { $0.bpmFormatted } ?? "--",
                    icon: "heart.fill",
                    color: AppColors.recoveryRed
                )
                MetricTile(
                    title: "Max Heart Rate",
                    value: workout.maxHeartRate.map { $0.bpmFormatted } ?? "--",
                    icon: "heart.circle.fill",
                    color: AppColors.zone5
                )
                MetricTile(
                    title: "Calories",
                    value: "\(Int(workout.activeCalories)) cal",
                    icon: "flame.fill",
                    color: AppColors.zone4
                )
                MetricTile(
                    title: "Distance",
                    value: distanceFormatted,
                    icon: "figure.run",
                    color: AppColors.teal
                )
            }
        }
    }

    // MARK: - HR Zone Breakdown

    private var hrZoneSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("HR Zone Breakdown")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            HRZoneBreakdownView(zoneMinutes: zoneMinutes)
        }
    }

    // MARK: - Zone Time Breakdown

    private var zoneTimeBreakdownSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Zone Time Breakdown")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            VStack(spacing: 0) {
                ForEach(1...5, id: \.self) { zone in
                    zoneRow(zone: zone)

                    if zone < 5 {
                        Divider()
                            .overlay(AppColors.backgroundTertiary)
                    }
                }
            }
            .cardStyle()
        }
    }

    private func zoneRow(zone: Int) -> some View {
        let minutes = zoneMinutes[zone - 1]
        let total = workout.totalZoneMinutes
        let percentage = total > 0 ? (minutes / total) * 100 : 0
        let zoneName = zoneLabel(for: zone)

        return HStack {
            Circle()
                .fill(AppColors.strainZoneColor(zone))
                .frame(width: 10, height: 10)

            Text("Zone \(zone)")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
                .frame(width: 56, alignment: .leading)

            Text(zoneName)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)

            Spacer()

            Text("\(Int(minutes))m")
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.textPrimary)

            Text("(\(percentage.formattedNoDecimal)%)")
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textTertiary)
                .frame(width: 44, alignment: .trailing)
        }
        .padding(.vertical, AppTheme.spacingSM)
    }

    // MARK: - Helpers

    private var distanceFormatted: String {
        guard let meters = workout.distanceMeters, meters > 0 else { return "--" }
        let km = meters / 1000.0
        if km >= 1 {
            return "\(km.formattedOneDecimal) km"
        }
        return "\(Int(meters)) m"
    }

    private func zoneLabel(for zone: Int) -> String {
        switch zone {
        case 1: return "Warm-Up"
        case 2: return "Fat Burn"
        case 3: return "Aerobic"
        case 4: return "Threshold"
        case 5: return "Anaerobic"
        default: return ""
        }
    }
}

#Preview {
    NavigationStack {
        WorkoutDetailView(
            workout: WorkoutRecord(
                workoutType: "running",
                workoutName: "Morning Run",
                startDate: Date().hoursAgo(2),
                endDate: Date().hoursAgo(1),
                strainScore: 12.4
            )
        )
    }
}
