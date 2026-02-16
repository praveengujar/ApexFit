import SwiftUI
import SwiftData
import HealthKit

struct StartActivityView: View {
    @Environment(\.modelContext) private var modelContext
    @Environment(\.dismiss) private var dismiss

    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    @State private var workoutManager = WorkoutManager()
    @State private var selectedType: HKWorkoutActivityType?
    @State private var countdown: Int?
    @State private var showLiveWorkout = false
    @State private var showWorkoutSummary = false
    @State private var completedWorkout: WorkoutRecord?

    private var userProfile: UserProfile? { profiles.first }
    private var maxHR: Int { userProfile?.estimatedMaxHR ?? 190 }

    private var currentDayStrain: Double {
        let today = Calendar.current.startOfDay(for: Date())
        return metrics.first { Calendar.current.isDate($0.date, inSameDayAs: today) }?.strainScore ?? 0
    }

    private let workoutTypes: [WorkoutTypeItem] = [
        WorkoutTypeItem(name: "Run", icon: "figure.run", type: .running),
        WorkoutTypeItem(name: "Cycle", icon: "figure.outdoor.cycle", type: .cycling),
        WorkoutTypeItem(name: "Swim", icon: "figure.pool.swim", type: .swimming),
        WorkoutTypeItem(name: "Strength", icon: "dumbbell.fill", type: .traditionalStrengthTraining),
        WorkoutTypeItem(name: "HIIT", icon: "flame.fill", type: .highIntensityIntervalTraining),
        WorkoutTypeItem(name: "Yoga", icon: "figure.yoga", type: .yoga),
        WorkoutTypeItem(name: "Walk", icon: "figure.walk", type: .walking),
        WorkoutTypeItem(name: "Hike", icon: "figure.hiking", type: .hiking),
        WorkoutTypeItem(name: "Other", icon: "figure.mixed.cardio", type: .other),
    ]

    var body: some View {
        NavigationStack {
            Group {
                if let count = countdown {
                    countdownView(count: count)
                } else if showLiveWorkout {
                    iOSLiveWorkoutView(
                        workoutManager: workoutManager,
                        onEnd: { endWorkout() }
                    )
                } else {
                    typeSelectionGrid
                }
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle(showLiveWorkout ? workoutManager.selectedWorkoutType.displayName : "Start Activity")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if !showLiveWorkout {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Cancel") { dismiss() }
                            .foregroundStyle(AppColors.teal)
                    }
                }
            }
            .navigationDestination(isPresented: $showWorkoutSummary) {
                if let workout = completedWorkout {
                    WorkoutDetailView(workout: workout)
                }
            }
        }
    }

    // MARK: - Type Selection Grid

    private var typeSelectionGrid: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingLG) {
                Text("Choose your workout")
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.textPrimary)

                LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingMD) {
                    ForEach(workoutTypes) { item in
                        Button {
                            startCountdown(type: item.type)
                        } label: {
                            VStack(spacing: AppTheme.spacingSM) {
                                Image(systemName: item.icon)
                                    .font(.system(size: 28))
                                    .foregroundStyle(AppColors.teal)
                                    .frame(width: 56, height: 56)
                                    .background(AppColors.teal.opacity(0.12))
                                    .clipShape(Circle())
                                Text(item.name)
                                    .font(AppTypography.labelMedium)
                                    .foregroundStyle(AppColors.textPrimary)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, AppTheme.spacingMD)
                            .background(AppColors.backgroundCard)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingLG)
        }
    }

    // MARK: - Countdown

    private func countdownView(count: Int) -> some View {
        VStack {
            Spacer()
            Text("\(count)")
                .font(.system(size: 80, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.teal)
            Text("Get ready!")
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    private func startCountdown(type: HKWorkoutActivityType) {
        selectedType = type
        countdown = 3

        workoutManager.configure(
            maxHeartRate: maxHR,
            dayStrainBefore: currentDayStrain
        )

        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
            if let current = countdown, current > 1 {
                countdown = current - 1
            } else {
                timer.invalidate()
                countdown = nil
                showLiveWorkout = true
                workoutManager.startWorkout(type: type)
            }
        }
    }

    // MARK: - End Workout

    private func endWorkout() {
        workoutManager.endWorkout()
        Task { @MainActor in

            let record = WorkoutRecord(
                workoutType: String(workoutManager.selectedWorkoutType.rawValue),
                workoutName: workoutManager.selectedWorkoutType.displayName,
                startDate: Date().addingTimeInterval(-workoutManager.elapsedTime),
                endDate: Date(),
                strainScore: workoutManager.currentStrain
            )
            record.averageHeartRate = workoutManager.averageHeartRate > 0 ? workoutManager.averageHeartRate : nil
            record.maxHeartRate = workoutManager.maxHeartRateReading > 0 ? workoutManager.maxHeartRateReading : nil
            record.activeCalories = workoutManager.currentCalories
            record.distanceMeters = workoutManager.currentDistance > 0 ? workoutManager.currentDistance * 1000 : nil
            record.zone1Minutes = workoutManager.zone1Minutes
            record.zone2Minutes = workoutManager.zone2Minutes
            record.zone3Minutes = workoutManager.zone3Minutes
            record.zone4Minutes = workoutManager.zone4Minutes
            record.zone5Minutes = workoutManager.zone5Minutes

            // Muscular load for strength workouts
            if MuscularLoadEngine.isStrengthWorkout(workoutManager.selectedWorkoutType) {
                if let avg = record.averageHeartRate, let peak = record.maxHeartRate {
                    let loadResult = MuscularLoadEngine.computeLoad(
                        workoutType: workoutManager.selectedWorkoutType,
                        durationMinutes: record.durationMinutes,
                        averageHeartRate: avg,
                        maxHeartRateDuringWorkout: peak,
                        userMaxHeartRate: Double(maxHR),
                        bodyWeightKG: userProfile?.weightKG
                    )
                    record.muscularLoad = loadResult.load
                    record.isStrengthWorkout = true
                }
            }

            // Attach to DailyMetric
            let today = Calendar.current.startOfDay(for: Date())
            let dailyMetric: DailyMetric
            if let existing = metrics.first(where: { Calendar.current.isDate($0.date, inSameDayAs: today) }) {
                dailyMetric = existing
            } else {
                dailyMetric = DailyMetric(date: today)
                if let profile = userProfile {
                    profile.dailyMetrics.append(dailyMetric)
                }
                modelContext.insert(dailyMetric)
            }

            dailyMetric.workouts.append(record)
            dailyMetric.workoutCount = dailyMetric.workouts.count
            dailyMetric.strainScore = dailyMetric.workouts.map(\.strainScore).reduce(0, +)
            dailyMetric.peakStrain = dailyMetric.workouts.map(\.strainScore).max() ?? 0

            try? modelContext.save()

            completedWorkout = record
            showLiveWorkout = false
            showWorkoutSummary = true
        }
    }
}

// MARK: - Workout Type Item (shared with watch)

struct WorkoutTypeItem: Identifiable {
    let id = UUID()
    let name: String
    let icon: String
    let type: HKWorkoutActivityType
}
