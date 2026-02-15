import SwiftUI

struct iOSLiveWorkoutView: View {
    @Bindable var workoutManager: WorkoutManager
    let onEnd: () -> Void

    @State private var selectedTab = 0
    @State private var showEndConfirmation = false

    var body: some View {
        VStack(spacing: 0) {
            // Tab content
            TabView(selection: $selectedTab) {
                heartRatePage.tag(0)
                strainPage.tag(1)
                zoneBreakdownPage.tag(2)
            }
            .tabViewStyle(.page(indexDisplayMode: .always))

            // Controls
            controlsBar
        }
        .background(AppColors.backgroundPrimary)
        .navigationBarBackButtonHidden(true)
        .alert("End Workout?", isPresented: $showEndConfirmation) {
            Button("End", role: .destructive) { onEnd() }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("Are you sure you want to end this workout?")
        }
    }

    // MARK: - Page 1: Heart Rate + Zone

    private var heartRatePage: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Spacer()

            Text("ZONE \(workoutManager.currentZone)")
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(zoneColor)

            Text(workoutManager.currentHeartRate > 0 ? "\(Int(workoutManager.currentHeartRate))" : "--")
                .font(.system(size: 72, weight: .bold, design: .rounded))
                .foregroundStyle(zoneColor)

            Text("BPM")
                .font(.system(size: 16, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)

            // Zone progress bar
            if let boundaries = currentZoneBoundaries {
                zoneProgressBar(boundaries: boundaries)
                    .padding(.horizontal, AppTheme.spacingLG)
            }

            Spacer()

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 28, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
                .padding(.bottom, AppTheme.spacingMD)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(zoneColor.opacity(0.06))
    }

    private func zoneProgressBar(boundaries: (lower: Int, upper: Int)) -> some View {
        VStack(spacing: 4) {
            GeometryReader { geo in
                let range = Double(boundaries.upper - boundaries.lower)
                let progress = range > 0 ? (workoutManager.currentHeartRate - Double(boundaries.lower)) / range : 0
                ZStack(alignment: .leading) {
                    Capsule().fill(zoneColor.opacity(0.2))
                    Capsule().fill(zoneColor)
                        .frame(width: max(0, geo.size.width * progress.clamped(to: 0...1)))
                }
            }
            .frame(height: 6)

            HStack {
                Text("\(boundaries.lower)")
                Spacer()
                Text("\(boundaries.upper) bpm")
            }
            .font(.system(size: 12))
            .foregroundStyle(AppColors.textTertiary)
        }
    }

    // MARK: - Page 2: Strain + Metrics

    private var strainPage: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Spacer()

            Text("WORKOUT STRAIN")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(AppColors.textSecondary)

            Text(workoutManager.currentStrain.formattedOneDecimal)
                .font(.system(size: 64, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.primaryBlue)

            HStack(spacing: AppTheme.spacingLG) {
                metricColumn(value: "\(Int(workoutManager.currentCalories))", label: "Cal")
                metricColumn(value: workoutManager.currentDistance.formattedOneDecimal, label: "km")
            }

            Spacer()

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 28, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
                .padding(.bottom, AppTheme.spacingMD)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private func metricColumn(value: String, label: String) -> some View {
        VStack(spacing: 2) {
            Text(value)
                .font(.system(size: 24, weight: .semibold, design: .rounded))
                .foregroundStyle(AppColors.textPrimary)
            Text(label)
                .font(.system(size: 13))
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    // MARK: - Page 3: Zone Breakdown

    private var zoneBreakdownPage: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Spacer()

            Text("ZONE BREAKDOWN")
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(AppColors.textSecondary)

            let totalMin = workoutManager.zone1Minutes + workoutManager.zone2Minutes + workoutManager.zone3Minutes + workoutManager.zone4Minutes + workoutManager.zone5Minutes

            VStack(spacing: AppTheme.spacingSM) {
                zoneBar(zone: 5, minutes: workoutManager.zone5Minutes, total: totalMin, color: AppColors.zone5)
                zoneBar(zone: 4, minutes: workoutManager.zone4Minutes, total: totalMin, color: AppColors.zone4)
                zoneBar(zone: 3, minutes: workoutManager.zone3Minutes, total: totalMin, color: AppColors.zone3)
                zoneBar(zone: 2, minutes: workoutManager.zone2Minutes, total: totalMin, color: AppColors.zone2)
                zoneBar(zone: 1, minutes: workoutManager.zone1Minutes, total: totalMin, color: AppColors.zone1)
            }
            .padding(.horizontal, AppTheme.spacingLG)

            HStack {
                Text("Avg: \(workoutManager.averageHeartRate > 0 ? "\(Int(workoutManager.averageHeartRate)) bpm" : "--")")
                Spacer()
                Text("Max: \(workoutManager.maxHeartRateReading > 0 ? "\(Int(workoutManager.maxHeartRateReading)) bpm" : "--")")
            }
            .font(.system(size: 13, weight: .medium))
            .foregroundStyle(AppColors.textTertiary)
            .padding(.horizontal, AppTheme.spacingLG)

            Spacer()

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 28, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
                .padding(.bottom, AppTheme.spacingMD)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private func zoneBar(zone: Int, minutes: Double, total: Double, color: Color) -> some View {
        HStack(spacing: 8) {
            Text("Z\(zone)")
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(color)
                .frame(width: 28, alignment: .leading)

            GeometryReader { geo in
                let fraction = total > 0 ? minutes / total : 0
                ZStack(alignment: .leading) {
                    Capsule().fill(color.opacity(0.2))
                    Capsule().fill(color)
                        .frame(width: max(0, geo.size.width * fraction))
                }
            }
            .frame(height: 10)

            Text("\(Int(minutes))m")
                .font(.system(size: 13))
                .foregroundStyle(AppColors.textTertiary)
                .frame(width: 36, alignment: .trailing)
        }
    }

    // MARK: - Controls Bar

    private var controlsBar: some View {
        HStack(spacing: AppTheme.spacingMD) {
            if workoutManager.isPaused {
                Button {
                    workoutManager.resumeWorkout()
                } label: {
                    Label("Resume", systemImage: "play.fill")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(AppColors.recoveryGreen)
                        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .buttonStyle(.plain)
            } else {
                Button {
                    workoutManager.pauseWorkout()
                } label: {
                    Label("Pause", systemImage: "pause.fill")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 14)
                        .background(AppColors.recoveryYellow)
                        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .buttonStyle(.plain)
            }

            Button {
                showEndConfirmation = true
            } label: {
                Label("End", systemImage: "stop.fill")
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(AppColors.recoveryRed)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.vertical, AppTheme.spacingSM)
        .background(AppColors.backgroundCard)
    }

    // MARK: - Helpers

    private var zoneColor: Color {
        AppColors.strainZoneColor(workoutManager.currentZone)
    }

    private var currentZoneBoundaries: (lower: Int, upper: Int)? {
        workoutManager.zoneBoundaries
            .first { $0.zone == workoutManager.currentZone }
            .map { (lower: $0.lower, upper: $0.upper) }
    }
}
