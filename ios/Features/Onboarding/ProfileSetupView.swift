import SwiftUI
import SwiftData

struct ProfileSetupView: View {
    @Bindable var profile: UserProfile
    var onContinue: () -> Void

    @State private var displayName: String = ""
    @State private var dateOfBirth: Date = Calendar.current.date(byAdding: .year, value: -25, to: Date()) ?? Date()
    @State private var hasSetDateOfBirth: Bool = false
    @State private var biologicalSex: BiologicalSex = .notSet
    @State private var heightValue: String = ""
    @State private var weightValue: String = ""
    @State private var preferredUnits: UnitSystem = .imperial
    @FocusState private var focusedField: Field?

    private enum Field: Hashable {
        case name, height, weight
    }

    private var dateOfBirthRange: ClosedRange<Date> {
        let calendar = Calendar.current
        let earliest = calendar.date(byAdding: .year, value: -100, to: Date()) ?? Date()
        let latest = calendar.date(byAdding: .year, value: -13, to: Date()) ?? Date()
        return earliest...latest
    }

    private var canContinue: Bool {
        !displayName.trimmingCharacters(in: .whitespaces).isEmpty
    }

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    // Header
                    VStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "person.crop.circle.badge.plus")
                            .font(.system(size: 56))
                            .foregroundStyle(AppColors.primaryBlue)
                            .padding(.top, AppTheme.spacingLG)

                        Text("Set Up Your Profile")
                            .font(AppTypography.heading1)
                            .foregroundStyle(AppColors.textPrimary)

                        Text("Help us personalize your experience")
                            .font(AppTypography.bodyLarge)
                            .foregroundStyle(AppColors.textSecondary)
                    }
                    .padding(.bottom, AppTheme.spacingSM)

                    // Form fields
                    VStack(spacing: AppTheme.spacingLG) {
                        // Display Name
                        OnboardingTextField(
                            label: "Display Name",
                            placeholder: "Your name",
                            text: $displayName,
                            isRequired: true
                        )
                        .focused($focusedField, equals: .name)

                        // Date of Birth
                        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                            Text("Date of Birth")
                                .font(AppTypography.labelLarge)
                                .foregroundStyle(AppColors.textPrimary)

                            DatePicker(
                                "Date of Birth",
                                selection: $dateOfBirth,
                                in: dateOfBirthRange,
                                displayedComponents: .date
                            )
                            .datePickerStyle(.compact)
                            .labelsHidden()
                            .tint(AppColors.primaryBlue)
                            .padding(AppTheme.spacingMD)
                            .background(AppColors.backgroundSecondary)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                            .onChange(of: dateOfBirth) { _, _ in
                                hasSetDateOfBirth = true
                            }

                            Text("Used to estimate max heart rate and provide age-appropriate insights")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }

                        // Biological Sex
                        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                            Text("Biological Sex")
                                .font(AppTypography.labelLarge)
                                .foregroundStyle(AppColors.textPrimary)

                            Picker("Biological Sex", selection: $biologicalSex) {
                                ForEach(BiologicalSex.allCases, id: \.self) { sex in
                                    Text(sex.displayLabel).tag(sex)
                                }
                            }
                            .pickerStyle(.segmented)

                            Text("Affects recovery and heart rate calculations")
                                .font(AppTypography.caption)
                                .foregroundStyle(AppColors.textTertiary)
                        }

                        // Unit System
                        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                            Text("Preferred Units")
                                .font(AppTypography.labelLarge)
                                .foregroundStyle(AppColors.textPrimary)

                            Picker("Units", selection: $preferredUnits) {
                                ForEach(UnitSystem.allCases, id: \.self) { system in
                                    Text(system.displayLabel).tag(system)
                                }
                            }
                            .pickerStyle(.segmented)
                        }

                        // Height
                        OnboardingTextField(
                            label: preferredUnits == .imperial ? "Height (inches)" : "Height (cm)",
                            placeholder: preferredUnits == .imperial ? "e.g. 70" : "e.g. 178",
                            text: $heightValue,
                            keyboardType: .decimalPad,
                            isRequired: false
                        )
                        .focused($focusedField, equals: .height)

                        // Weight
                        OnboardingTextField(
                            label: preferredUnits == .imperial ? "Weight (lbs)" : "Weight (kg)",
                            placeholder: preferredUnits == .imperial ? "e.g. 165" : "e.g. 75",
                            text: $weightValue,
                            keyboardType: .decimalPad,
                            isRequired: false
                        )
                        .focused($focusedField, equals: .weight)
                    }
                    .padding(.horizontal, AppTheme.spacingLG)

                    // Continue button
                    Button(action: saveAndContinue) {
                        Text("Continue")
                            .font(AppTypography.heading3)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget + 8)
                            .background(canContinue ? AppColors.primaryBlue : AppColors.backgroundTertiary)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                    }
                    .disabled(!canContinue)
                    .padding(.horizontal, AppTheme.spacingLG)
                    .padding(.top, AppTheme.spacingSM)
                    .padding(.bottom, AppTheme.spacingXL)
                }
            }
            .scrollDismissesKeyboard(.interactively)
        }
        .onAppear {
            loadExistingProfile()
        }
        .onTapGesture {
            focusedField = nil
        }
    }

    // MARK: - Actions

    private func loadExistingProfile() {
        displayName = profile.displayName
        biologicalSex = profile.biologicalSex
        preferredUnits = profile.preferredUnits
        if let dob = profile.dateOfBirth {
            dateOfBirth = dob
            hasSetDateOfBirth = true
        }
        if let h = profile.heightCM {
            if preferredUnits == .imperial {
                heightValue = String(format: "%.1f", h / 2.54)
            } else {
                heightValue = String(format: "%.1f", h)
            }
        }
        if let w = profile.weightKG {
            if preferredUnits == .imperial {
                weightValue = String(format: "%.1f", w * 2.20462)
            } else {
                weightValue = String(format: "%.1f", w)
            }
        }
    }

    private func saveAndContinue() {
        focusedField = nil

        profile.displayName = displayName.trimmingCharacters(in: .whitespaces)
        profile.biologicalSex = biologicalSex
        profile.preferredUnits = preferredUnits

        if hasSetDateOfBirth {
            profile.dateOfBirth = dateOfBirth
        }

        // Convert height to cm for storage
        if let heightNum = Double(heightValue) {
            if preferredUnits == .imperial {
                profile.heightCM = heightNum * 2.54
            } else {
                profile.heightCM = heightNum
            }
        }

        // Convert weight to kg for storage
        if let weightNum = Double(weightValue) {
            if preferredUnits == .imperial {
                profile.weightKG = weightNum / 2.20462
            } else {
                profile.weightKG = weightNum
            }
        }

        profile.updatedAt = Date()
        onContinue()
    }
}

// MARK: - Onboarding Text Field

private struct OnboardingTextField: View {
    let label: String
    let placeholder: String
    @Binding var text: String
    var keyboardType: UIKeyboardType = .default
    var isRequired: Bool = false

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack(spacing: AppTheme.spacingXS) {
                Text(label)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                if isRequired {
                    Text("*")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.recoveryRed)
                }

                if !isRequired {
                    Text("(optional)")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                }
            }

            TextField(placeholder, text: $text)
                .font(AppTypography.bodyLarge)
                .keyboardType(keyboardType)
                .padding(AppTheme.spacingMD)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                .foregroundStyle(AppColors.textPrimary)
        }
    }
}

// MARK: - Display Label Extensions

private extension BiologicalSex {
    var displayLabel: String {
        switch self {
        case .male: return "Male"
        case .female: return "Female"
        case .other: return "Other"
        case .notSet: return "Not Set"
        }
    }
}

private extension UnitSystem {
    var displayLabel: String {
        switch self {
        case .metric: return "Metric"
        case .imperial: return "Imperial"
        }
    }
}

#Preview {
    let container = try! ModelContainer(for: UserProfile.self, configurations: .init(isStoredInMemoryOnly: true))
    let profile = UserProfile(displayName: "")
    container.mainContext.insert(profile)

    return ProfileSetupView(profile: profile) {
        print("Continue")
    }
    .modelContainer(container)
}
