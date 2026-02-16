import SwiftUI
import SwiftData
import HealthKit

struct AddActivityView: View {
    @Environment(\.modelContext) private var modelContext
    @Environment(\.dismiss) private var dismiss

    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    // MARK: - Form State

    @State private var selectedType: HKWorkoutActivityType = .running
    @State private var activityName: String = "Running"
    @State private var startDate = Date().addingTimeInterval(-3600)
    @State private var endDate = Date()
    @State private var caloriesText: String = ""
    @State private var distanceText: String = ""
    @State private var rpe: Int = 5
    @State private var showOptionalFields = false
    @State private var isSaving = false

    private var userProfile: UserProfile? { profiles.first }
    private var maxHR: Int { userProfile?.estimatedMaxHR ?? 190 }

    private var durationMinutes: Double {
        max(0, endDate.timeIntervalSince(startDate) / 60.0)
    }

    // MARK: - Workout Types

    private static let workoutTypes: [(type: HKWorkoutActivityType, name: String, icon: String)] = [
        (.running, "Run", "figure.run"),
        (.cycling, "Cycle", "figure.outdoor.cycle"),
        (.swimming, "Swim", "figure.pool.swim"),
        (.traditionalStrengthTraining, "Strength", "dumbbell.fill"),
        (.highIntensityIntervalTraining, "HIIT", "flame.fill"),
        (.yoga, "Yoga", "figure.yoga"),
        (.walking, "Walk", "figure.walk"),
        (.hiking, "Hike", "figure.hiking"),
        (.other, "Other", "figure.mixed.cardio"),
    ]

    // MARK: - Body

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    activityTypePicker
                    timeSection
                    optionalFieldsSection
                    rpeSection
                    saveButton
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Add Activity")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                        .foregroundStyle(AppColors.teal)
                }
            }
        }
    }

    // MARK: - Activity Type Picker

    private var activityTypePicker: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("ACTIVITY TYPE")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
                .tracking(0.5)

            LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible()), GridItem(.flexible())], spacing: AppTheme.spacingSM) {
                ForEach(Self.workoutTypes, id: \.type.rawValue) { item in
                    activityTypeCell(type: item.type, name: item.name, icon: item.icon)
                }
            }
        }
    }

    private func activityTypeCell(type: HKWorkoutActivityType, name: String, icon: String) -> some View {
        let isSelected = selectedType == type
        return Button {
            selectedType = type
            activityName = type.displayName
        } label: {
            VStack(spacing: 6) {
                Image(systemName: icon)
                    .font(.system(size: 22))
                    .foregroundStyle(isSelected ? AppColors.teal : AppColors.textSecondary)
                Text(name)
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(isSelected ? AppColors.textPrimary : AppColors.textSecondary)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, AppTheme.spacingSM)
            .background(isSelected ? AppColors.teal.opacity(0.15) : AppColors.backgroundTertiary)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
            .overlay(
                RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall)
                    .stroke(isSelected ? AppColors.teal : Color.clear, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }

    // MARK: - Time Section

    private var timeSection: some View {
        VStack(spacing: AppTheme.spacingSM) {
            HStack {
                Text("START")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
                Spacer()
                DatePicker("", selection: $startDate)
                    .labelsHidden()
                    .colorScheme(.dark)
            }
            .padding(AppTheme.spacingSM)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

            HStack {
                Text("END")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
                Spacer()
                DatePicker("", selection: $endDate)
                    .labelsHidden()
                    .colorScheme(.dark)
            }
            .padding(AppTheme.spacingSM)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

            HStack {
                Text("DURATION")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.textSecondary)
                Spacer()
                Text(formattedDuration)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)
            }
            .padding(AppTheme.spacingSM)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
        }
    }

    private var formattedDuration: String {
        let hours = Int(durationMinutes) / 60
        let mins = Int(durationMinutes) % 60
        if hours > 0 { return "\(hours)h \(mins)m" }
        return "\(mins)m"
    }

    // MARK: - Optional Fields

    private var optionalFieldsSection: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Button {
                withAnimation { showOptionalFields.toggle() }
            } label: {
                HStack {
                    Text("OPTIONAL DETAILS")
                        .font(AppTypography.labelSmall)
                        .foregroundStyle(AppColors.textSecondary)
                    Spacer()
                    Image(systemName: showOptionalFields ? "chevron.up" : "chevron.down")
                        .font(.system(size: 12))
                        .foregroundStyle(AppColors.textTertiary)
                }
            }
            .buttonStyle(.plain)

            if showOptionalFields {
                VStack(spacing: AppTheme.spacingSM) {
                    formRow(label: "ACTIVITY NAME") {
                        TextField("Name", text: $activityName)
                            .font(AppTypography.bodyMedium)
                            .foregroundStyle(AppColors.textPrimary)
                            .multilineTextAlignment(.trailing)
                    }

                    formRow(label: "CALORIES") {
                        TextField("Optional", text: $caloriesText)
                            .font(AppTypography.bodyMedium)
                            .foregroundStyle(AppColors.textPrimary)
                            .multilineTextAlignment(.trailing)
                            .keyboardType(.numberPad)
                    }

                    if showsDistance {
                        formRow(label: distanceLabel) {
                            TextField("Optional", text: $distanceText)
                                .font(AppTypography.bodyMedium)
                                .foregroundStyle(AppColors.textPrimary)
                                .multilineTextAlignment(.trailing)
                                .keyboardType(.decimalPad)
                        }
                    }
                }
            }
        }
    }

    private func formRow<Content: View>(label: String, @ViewBuilder content: () -> Content) -> some View {
        HStack {
            Text(label)
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            content()
        }
        .padding(AppTheme.spacingSM)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
    }

    private var showsDistance: Bool {
        [.running, .cycling, .walking, .hiking, .swimming].contains(selectedType)
    }

    private var distanceLabel: String {
        userProfile?.preferredUnits == .imperial ? "DISTANCE (MI)" : "DISTANCE (KM)"
    }

    // MARK: - RPE Section

    private var rpeSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("HOW HARD WAS IT?")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
                .tracking(0.5)

            VStack(spacing: 4) {
                HStack {
                    Text("Easy")
                        .font(.system(size: 11))
                        .foregroundStyle(AppColors.textTertiary)
                    Spacer()
                    Text("RPE: \(rpe)")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(rpeColor)
                    Spacer()
                    Text("Max")
                        .font(.system(size: 11))
                        .foregroundStyle(AppColors.textTertiary)
                }

                Slider(value: rpeBinding, in: 1...10, step: 1)
                    .tint(rpeColor)
            }
            .padding(AppTheme.spacingSM)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
        }
    }

    private var rpeBinding: Binding<Double> {
        Binding(
            get: { Double(rpe) },
            set: { rpe = Int($0) }
        )
    }

    private var rpeColor: Color {
        switch rpe {
        case 1...3: return AppColors.recoveryGreen
        case 4...6: return AppColors.recoveryYellow
        case 7...8: return AppColors.zone4
        default: return AppColors.recoveryRed
        }
    }

    // MARK: - Save Button

    private var saveButton: some View {
        Button {
            Task { await saveActivity() }
        } label: {
            HStack {
                if isSaving {
                    ProgressView()
                        .tint(.white)
                } else {
                    Text("SAVE ACTIVITY")
                        .font(AppTypography.labelLarge)
                }
            }
            .foregroundStyle(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, AppTheme.spacingMD)
            .background(durationMinutes > 0 ? AppColors.teal : AppColors.textTertiary)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
        .disabled(durationMinutes <= 0 || isSaving)
    }

    // MARK: - Save Logic

    @MainActor
    private func saveActivity() async {
        isSaving = true
        defer { isSaving = false }

        let strainEngine = StrainEngine(maxHeartRate: maxHR)
        let queryService = HealthKitQueryService()

        // Try to fetch real HR data from HealthKit for the time window
        var strainResult: StrainResult?
        var avgHR: Double?
        var peakHR: Double?

        do {
            let hrSamples = try await queryService.fetchHeartRateSamples(from: startDate, to: endDate)
            if !hrSamples.isEmpty {
                strainResult = strainEngine.computeWorkoutStrain(from: hrSamples)
                avgHR = hrSamples.map(\.1).reduce(0, +) / Double(hrSamples.count)
                peakHR = hrSamples.map(\.1).max()
            }
        } catch {
            // No HR data available — will use RPE estimation
        }

        // If no real HR data, estimate from RPE
        if strainResult == nil {
            strainResult = estimateStrainFromRPE()
            let estimatedBPM = estimatedHRPercent(from: rpe) * Double(maxHR)
            avgHR = estimatedBPM
            peakHR = estimatedBPM * 1.1
        }

        let strain = strainResult?.strain ?? 0

        // Create WorkoutRecord
        let record = WorkoutRecord(
            workoutType: String(selectedType.rawValue),
            workoutName: activityName,
            startDate: startDate,
            endDate: endDate,
            strainScore: strain
        )

        record.averageHeartRate = avgHR
        record.maxHeartRate = peakHR
        record.zone1Minutes = strainResult?.zone1Minutes ?? 0
        record.zone2Minutes = strainResult?.zone2Minutes ?? 0
        record.zone3Minutes = strainResult?.zone3Minutes ?? 0
        record.zone4Minutes = strainResult?.zone4Minutes ?? 0
        record.zone5Minutes = strainResult?.zone5Minutes ?? 0

        if let cal = Double(caloriesText) {
            record.activeCalories = cal
        }

        if let dist = Double(distanceText) {
            let isImperial = userProfile?.preferredUnits == .imperial
            record.distanceMeters = isImperial ? dist * 1609.34 : dist * 1000.0
        }

        // Muscular load for strength workouts
        if MuscularLoadEngine.isStrengthWorkout(selectedType) {
            if let avg = avgHR, let peak = peakHR {
                let loadResult = MuscularLoadEngine.computeLoad(
                    workoutType: selectedType,
                    durationMinutes: durationMinutes,
                    averageHeartRate: avg,
                    maxHeartRateDuringWorkout: peak,
                    userMaxHeartRate: Double(maxHR),
                    bodyWeightKG: userProfile?.weightKG,
                    rpe: rpe
                )
                record.muscularLoad = loadResult.load
                record.isStrengthWorkout = true
            }
        }

        // Find or create DailyMetric for the workout date
        let workoutDay = Calendar.current.startOfDay(for: startDate)
        let dailyMetric: DailyMetric
        if let existing = metrics.first(where: { Calendar.current.isDate($0.date, inSameDayAs: workoutDay) }) {
            dailyMetric = existing
        } else {
            dailyMetric = DailyMetric(date: workoutDay)
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
        dismiss()
    }

    // MARK: - Strain Estimation

    private func estimateStrainFromRPE() -> StrainResult {
        let estimatedBPM = estimatedHRPercent(from: rpe) * Double(maxHR)
        let sampleInterval: Double = 30 // seconds
        let totalSamples = Int(durationMinutes * 60 / sampleInterval)

        var samples: [HeartRateSample] = []
        for i in 0..<max(1, totalSamples) {
            let timestamp = startDate.addingTimeInterval(Double(i) * sampleInterval)
            samples.append(HeartRateSample(timestamp: timestamp, bpm: estimatedBPM, durationSeconds: sampleInterval))
        }

        let engine = StrainEngine(maxHeartRate: maxHR)
        return engine.computeStrain(from: samples)
    }

    private func estimatedHRPercent(from rpe: Int) -> Double {
        switch rpe {
        case 1...3: return 0.55
        case 4...5: return 0.65
        case 6...7: return 0.75
        case 8...9: return 0.85
        default: return 0.95
        }
    }
}
