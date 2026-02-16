import SwiftUI
import HealthKit
import WatchKit

struct WorkoutTypeItem: Identifiable {
    let id = UUID()
    let name: String
    let icon: String
    let type: HKWorkoutActivityType
}

struct WorkoutStartView: View {
    @Environment(WatchWorkoutManager.self) private var workoutManager
    @Environment(WatchConnectivityManager.self) private var connectivity
    @State private var selectedType: HKWorkoutActivityType?
    @State private var countdown: Int?
    @State private var navigateToWorkout = false

    private let workoutTypes: [WorkoutTypeItem] = [
        WorkoutTypeItem(name: "Run (Outdoor)", icon: "figure.run", type: .running),
        WorkoutTypeItem(name: "Cycle", icon: "figure.outdoor.cycle", type: .cycling),
        WorkoutTypeItem(name: "Swim", icon: "figure.pool.swim", type: .swimming),
        WorkoutTypeItem(name: "Strength", icon: "figure.strengthtraining.traditional", type: .traditionalStrengthTraining),
        WorkoutTypeItem(name: "HIIT", icon: "figure.highintensity.intervaltraining", type: .highIntensityIntervalTraining),
        WorkoutTypeItem(name: "Yoga", icon: "figure.yoga", type: .yoga),
        WorkoutTypeItem(name: "Walk", icon: "figure.walk", type: .walking),
        WorkoutTypeItem(name: "Hike", icon: "figure.hiking", type: .hiking),
        WorkoutTypeItem(name: "Other", icon: "figure.mixed.cardio", type: .other),
    ]

    var body: some View {
        Group {
            if let count = countdown {
                countdownView(count: count)
            } else if navigateToWorkout {
                LiveWorkoutView()
            } else {
                typeSelectionList
            }
        }
    }

    private var typeSelectionList: some View {
        List(workoutTypes) { item in
            Button {
                startCountdown(type: item.type)
            } label: {
                HStack(spacing: 10) {
                    Image(systemName: item.icon)
                        .font(.system(size: 18))
                        .foregroundStyle(AppColors.teal)
                        .frame(width: 28)
                    Text(item.name)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(AppColors.textPrimary)
                }
            }
            .listRowBackground(AppColors.backgroundSecondary)
        }
        .navigationTitle("Workout")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func countdownView(count: Int) -> some View {
        VStack {
            Spacer()
            Text("\(count)")
                .font(.system(size: 60, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.teal)
            Spacer()
        }
    }

    private func startCountdown(type: HKWorkoutActivityType) {
        selectedType = type
        countdown = 3

        let context = connectivity.applicationContext
        workoutManager.configure(
            maxHeartRate: context.maxHeartRate,
            dayStrainBefore: context.currentDayStrain
        )

        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
            WKInterfaceDevice.current().play(.click)
            if let current = countdown, current > 1 {
                countdown = current - 1
            } else {
                timer.invalidate()
                countdown = nil
                navigateToWorkout = true
                Task {
                    try? await workoutManager.startWorkout(type: type)
                }
            }
        }
    }
}
