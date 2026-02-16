import SwiftUI
import WatchKit

struct LiveWorkoutView: View {
    @Environment(WatchWorkoutManager.self) private var workoutManager
    @Environment(\.isLuminanceReduced) private var isAOD
    @State private var showingControls = false

    var body: some View {
        if isAOD {
            aodView
        } else if showingControls {
            workoutControlsView
        } else {
            TabView {
                heartRatePage
                strainPage
                zoneBreakdownPage
            }
            .tabViewStyle(.verticalPage)
            .onLongPressGesture { showingControls = true }
            .navigationBarBackButtonHidden(true)
        }
    }

    // MARK: - AOD

    private var aodView: some View {
        VStack(spacing: 8) {
            Text(workoutManager.currentHeartRate > 0 ? "\(Int(workoutManager.currentHeartRate))" : "--")
                .font(.system(size: 36, weight: .bold, design: .rounded))
                .foregroundStyle(zoneColor.opacity(0.6))
            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 15, weight: .medium, design: .monospaced))
                .foregroundStyle(Color.white.opacity(0.4))
            Text("Strain: \(workoutManager.currentStrain.formattedOneDecimal)")
                .font(.system(size: 13))
                .foregroundStyle(Color.white.opacity(0.3))
        }
    }

    // MARK: - Page 1: Heart Rate + Zone

    private var heartRatePage: some View {
        VStack(spacing: 8) {
            Text("ZONE \(workoutManager.currentZone)")
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(zoneColor)

            Text(workoutManager.currentHeartRate > 0 ? "\(Int(workoutManager.currentHeartRate))" : "--")
                .font(.system(size: 48, weight: .bold, design: .rounded))
                .foregroundStyle(zoneColor)

            Text("BPM")
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)

            if let boundaries = currentZoneBoundaries {
                VStack(spacing: 2) {
                    GeometryReader { geo in
                        let range = Double(boundaries.upper - boundaries.lower)
                        let progress = range > 0 ? (workoutManager.currentHeartRate - Double(boundaries.lower)) / range : 0
                        ZStack(alignment: .leading) {
                            Capsule().fill(zoneColor.opacity(0.2))
                            Capsule().fill(zoneColor)
                                .frame(width: max(0, geo.size.width * progress.clamped(to: 0...1)))
                        }
                    }
                    .frame(height: 4)
                    HStack {
                        Text("\(boundaries.lower)")
                        Spacer()
                        Text("\(boundaries.upper) bpm")
                    }
                    .font(.system(size: 10))
                    .foregroundStyle(AppColors.textTertiary)
                }
                .padding(.horizontal, 12)
            }

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 17, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(zoneColor.opacity(0.08))
    }

    // MARK: - Page 2: Strain + Metrics

    private var strainPage: some View {
        VStack(spacing: 8) {
            Text("WORKOUT STRAIN")
                .font(.system(size: 11, weight: .bold))
                .foregroundStyle(AppColors.textSecondary)

            Text(workoutManager.currentStrain.formattedOneDecimal)
                .font(.system(size: 40, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.primaryBlue)

            HStack(spacing: 16) {
                VStack(spacing: 2) {
                    Text("\(Int(workoutManager.currentCalories))")
                        .font(.system(size: 18, weight: .semibold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    Text("Cal")
                        .font(.system(size: 11))
                        .foregroundStyle(AppColors.textTertiary)
                }
                VStack(spacing: 2) {
                    Text(workoutManager.currentDistance.formattedOneDecimal)
                        .font(.system(size: 18, weight: .semibold, design: .rounded))
                        .foregroundStyle(AppColors.textPrimary)
                    Text("km")
                        .font(.system(size: 11))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 17, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    // MARK: - Page 3: Zone Breakdown

    private var zoneBreakdownPage: some View {
        VStack(spacing: 6) {
            Text("ZONE BREAKDOWN")
                .font(.system(size: 11, weight: .bold))
                .foregroundStyle(AppColors.textSecondary)

            let totalMin = workoutManager.zone1Minutes + workoutManager.zone2Minutes + workoutManager.zone3Minutes + workoutManager.zone4Minutes + workoutManager.zone5Minutes

            VStack(spacing: 4) {
                zoneBar(zone: 5, minutes: workoutManager.zone5Minutes, total: totalMin, color: AppColors.zone5)
                zoneBar(zone: 4, minutes: workoutManager.zone4Minutes, total: totalMin, color: AppColors.zone4)
                zoneBar(zone: 3, minutes: workoutManager.zone3Minutes, total: totalMin, color: AppColors.zone3)
                zoneBar(zone: 2, minutes: workoutManager.zone2Minutes, total: totalMin, color: AppColors.zone2)
                zoneBar(zone: 1, minutes: workoutManager.zone1Minutes, total: totalMin, color: AppColors.zone1)
            }
            .padding(.horizontal, 8)

            HStack {
                Text("Avg: \(workoutManager.averageHeartRate > 0 ? "\(Int(workoutManager.averageHeartRate))" : "--")")
                Spacer()
                Text("Max: \(workoutManager.maxHeartRateReading > 0 ? "\(Int(workoutManager.maxHeartRateReading))" : "--")")
            }
            .font(.system(size: 11, weight: .medium))
            .foregroundStyle(AppColors.textTertiary)
            .padding(.horizontal, 8)

            Text(workoutManager.workoutDurationFormatted)
                .font(.system(size: 17, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private func zoneBar(zone: Int, minutes: Double, total: Double, color: Color) -> some View {
        HStack(spacing: 4) {
            Text("Z\(zone)")
                .font(.system(size: 11, weight: .semibold))
                .foregroundStyle(color)
                .frame(width: 20, alignment: .leading)
            GeometryReader { geo in
                let fraction = total > 0 ? minutes / total : 0
                ZStack(alignment: .leading) {
                    Capsule().fill(color.opacity(0.2))
                    Capsule().fill(color).frame(width: max(0, geo.size.width * fraction))
                }
            }
            .frame(height: 6)
            Text("\(Int(minutes))m")
                .font(.system(size: 10))
                .foregroundStyle(AppColors.textTertiary)
                .frame(width: 28, alignment: .trailing)
        }
    }

    // MARK: - Controls

    private var workoutControlsView: some View {
        VStack(spacing: 12) {
            if workoutManager.isPaused {
                Button {
                    workoutManager.resumeWorkout()
                    showingControls = false
                } label: {
                    Label("Resume", systemImage: "play.fill")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(AppColors.recoveryGreen)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .buttonStyle(.plain)
            } else {
                Button {
                    workoutManager.pauseWorkout()
                } label: {
                    Label("Pause", systemImage: "pause.fill")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(AppColors.recoveryYellow)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .buttonStyle(.plain)
            }

            Button {
                Task {
                    await workoutManager.endWorkout()
                    showingControls = false
                }
            } label: {
                Label("End Workout", systemImage: "stop.fill")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(AppColors.recoveryRed)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)

            Button("Cancel") { showingControls = false }
                .font(.system(size: 13))
                .foregroundStyle(AppColors.textTertiary)
        }
        .padding(.horizontal, 8)
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
