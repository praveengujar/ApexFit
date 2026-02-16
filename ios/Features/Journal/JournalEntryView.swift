import SwiftUI
import SwiftData

struct JournalEntryView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]
    @Query(sort: \JournalEntry.date, order: .reverse)
    private var entries: [JournalEntry]

    @State private var responseValues: [String: ResponseValue] = [:]
    @State private var isSaving = false
    @State private var showSavedConfirmation = false

    private var userProfile: UserProfile? {
        profiles.first
    }

    private var todayEntry: JournalEntry? {
        entries.first { $0.date.isSameDay(as: Date()) }
    }

    private var selectedBehaviors: [JournalBehavior] {
        guard let profile = userProfile else { return [] }
        return JournalBehaviorCatalog.behaviors(for: profile.selectedJournalBehaviorIDs)
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingMD) {
                    dateHeader
                    behaviorsList
                    saveButton
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Journal")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    NavigationLink(destination: JournalSetupEditView()) {
                        Image(systemName: "slider.horizontal.3")
                            .foregroundStyle(AppColors.textSecondary)
                    }
                }
            }
            .onAppear { loadExistingResponses() }
            .overlay {
                if showSavedConfirmation {
                    savedToast
                }
            }
        }
    }

    // MARK: - Date Header

    private var dateHeader: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(Date().formatted(.dateTime.weekday(.wide)))
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
                Text(Date().formatted(.dateTime.month(.wide).day().year()))
                    .font(AppTypography.heading2)
                    .foregroundStyle(AppColors.textPrimary)
            }
            Spacer()
            if todayEntry != nil {
                Label("Logged", systemImage: "checkmark.circle.fill")
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(AppColors.recoveryGreen)
            }
        }
        .padding(.bottom, AppTheme.spacingSM)
    }

    // MARK: - Behaviors List

    private var behaviorsList: some View {
        Group {
            if selectedBehaviors.isEmpty {
                emptyBehaviorsPrompt
            } else {
                LazyVStack(spacing: AppTheme.spacingSM) {
                    ForEach(selectedBehaviors) { behavior in
                        BehaviorRow(
                            behavior: behavior,
                            value: binding(for: behavior)
                        )
                    }
                }
            }
        }
    }

    private var emptyBehaviorsPrompt: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Image(systemName: "list.bullet.clipboard")
                .font(.system(size: 40))
                .foregroundStyle(AppColors.textTertiary)
            Text("No Behaviors Selected")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)
            Text("Add behaviors to track how your daily habits affect your recovery and performance.")
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)
            NavigationLink(destination: JournalSetupEditView()) {
                Text("Set Up Journal")
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, AppTheme.spacingSM)
                    .background(AppColors.primaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
            }
        }
        .cardStyle()
    }

    // MARK: - Save Button

    private var saveButton: some View {
        Group {
            if !selectedBehaviors.isEmpty {
                Button {
                    saveEntry()
                } label: {
                    HStack(spacing: AppTheme.spacingSM) {
                        if isSaving {
                            ProgressView()
                                .tint(.white)
                        }
                        Text(todayEntry != nil ? "Update Entry" : "Save Entry")
                            .font(AppTypography.labelLarge)
                    }
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(AppColors.primaryBlue)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .disabled(isSaving)
            }
        }
    }

    // MARK: - Saved Toast

    private var savedToast: some View {
        VStack {
            Spacer()
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "checkmark.circle.fill")
                    .foregroundStyle(AppColors.recoveryGreen)
                Text("Journal entry saved")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.vertical, AppTheme.spacingSM)
            .background(AppColors.backgroundTertiary)
            .clipShape(Capsule())
            .padding(.bottom, AppTheme.spacingLG)
        }
        .transition(.move(edge: .bottom).combined(with: .opacity))
        .animation(AppTheme.animationDefault, value: showSavedConfirmation)
    }

    // MARK: - Helpers

    private func binding(for behavior: JournalBehavior) -> Binding<ResponseValue> {
        Binding(
            get: { responseValues[behavior.id] ?? behavior.defaultValue },
            set: { responseValues[behavior.id] = $0 }
        )
    }

    private func loadExistingResponses() {
        guard let entry = todayEntry else { return }
        for response in entry.responses {
            switch response.responseType {
            case .toggle:
                responseValues[response.behaviorID] = .toggle(response.toggleValue ?? false)
            case .numeric:
                responseValues[response.behaviorID] = .numeric(response.numericValue ?? 0)
            case .scale:
                responseValues[response.behaviorID] = .scale(response.scaleValue ?? "None")
            }
        }
    }

    private func saveEntry() {
        isSaving = true

        let entry: JournalEntry
        if let existing = todayEntry {
            entry = existing
            // Remove old responses
            for response in entry.responses {
                modelContext.delete(response)
            }
            entry.responses = []
        } else {
            entry = JournalEntry(date: Date())
            entry.userProfile = userProfile
            modelContext.insert(entry)
        }

        for behavior in selectedBehaviors {
            let value = responseValues[behavior.id] ?? behavior.defaultValue
            let response = JournalResponse(
                behaviorID: behavior.id,
                behaviorName: behavior.name,
                category: behavior.category.rawValue,
                responseType: behavior.responseType
            )

            switch value {
            case .toggle(let boolVal):
                response.toggleValue = boolVal
            case .numeric(let doubleVal):
                response.numericValue = doubleVal
            case .scale(let stringVal):
                response.scaleValue = stringVal
            }

            response.journalEntry = entry
            entry.responses.append(response)
            modelContext.insert(response)
        }

        entry.isComplete = true
        entry.completedAt = Date()

        try? modelContext.save()
        isSaving = false

        withAnimation {
            showSavedConfirmation = true
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            withAnimation {
                showSavedConfirmation = false
            }
        }
    }
}

// MARK: - Response Value

enum ResponseValue {
    case toggle(Bool)
    case numeric(Double)
    case scale(String)
}

// MARK: - Journal Behavior Catalog

struct JournalBehavior: Identifiable {
    let id: String
    let name: String
    let category: BehaviorCategory
    let icon: String
    let responseType: JournalResponseType
    let scaleOptions: [String]

    var defaultValue: ResponseValue {
        switch responseType {
        case .toggle: return .toggle(false)
        case .numeric: return .numeric(0)
        case .scale: return .scale(scaleOptions.first ?? "None")
        }
    }
}

enum BehaviorCategory: String, CaseIterable, Identifiable {
    case nutrition = "Nutrition"
    case activity = "Activity"
    case lifestyle = "Lifestyle"
    case mental = "Mental"
    case substances = "Substances"

    var id: String { rawValue }

    var icon: String {
        switch self {
        case .nutrition: return "fork.knife"
        case .activity: return "figure.run"
        case .lifestyle: return "moon.stars"
        case .mental: return "brain.head.profile"
        case .substances: return "cup.and.saucer"
        }
    }
}

struct JournalBehaviorCatalog {
    static let allBehaviors: [JournalBehavior] = [
        // Nutrition
        JournalBehavior(id: "hydration", name: "Hydration", category: .nutrition, icon: "drop.fill", responseType: .scale, scaleOptions: ["Low", "Moderate", "Good", "Excellent"]),
        JournalBehavior(id: "healthy_eating", name: "Healthy Eating", category: .nutrition, icon: "leaf.fill", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "supplements", name: "Took Supplements", category: .nutrition, icon: "pills.fill", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "protein_goal", name: "Hit Protein Goal", category: .nutrition, icon: "fish.fill", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "water_glasses", name: "Glasses of Water", category: .nutrition, icon: "drop.fill", responseType: .numeric, scaleOptions: []),

        // Activity
        JournalBehavior(id: "stretching", name: "Stretching", category: .activity, icon: "figure.flexibility", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "active_recovery", name: "Active Recovery", category: .activity, icon: "figure.walk", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "steps_10k", name: "10k Steps", category: .activity, icon: "shoeprints.fill", responseType: .toggle, scaleOptions: []),

        // Lifestyle
        JournalBehavior(id: "screen_before_bed", name: "Screen Before Bed", category: .lifestyle, icon: "iphone", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "cold_exposure", name: "Cold Exposure", category: .lifestyle, icon: "snowflake", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "sauna", name: "Sauna / Heat", category: .lifestyle, icon: "thermometer.high", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "nap", name: "Nap", category: .lifestyle, icon: "bed.double.fill", responseType: .toggle, scaleOptions: []),

        // Mental
        JournalBehavior(id: "meditation", name: "Meditation", category: .mental, icon: "brain.head.profile", responseType: .toggle, scaleOptions: []),
        JournalBehavior(id: "stress_level", name: "Stress Level", category: .mental, icon: "waveform.path.ecg", responseType: .scale, scaleOptions: ["Low", "Moderate", "High", "Very High"]),
        JournalBehavior(id: "mood", name: "Mood", category: .mental, icon: "face.smiling", responseType: .scale, scaleOptions: ["Poor", "Okay", "Good", "Great"]),
        JournalBehavior(id: "journaling", name: "Journaling", category: .mental, icon: "book.fill", responseType: .toggle, scaleOptions: []),

        // Substances
        JournalBehavior(id: "alcohol", name: "Alcohol", category: .substances, icon: "wineglass.fill", responseType: .numeric, scaleOptions: []),
        JournalBehavior(id: "caffeine", name: "Caffeine (cups)", category: .substances, icon: "cup.and.saucer.fill", responseType: .numeric, scaleOptions: []),
        JournalBehavior(id: "cannabis", name: "Cannabis", category: .substances, icon: "leaf.fill", responseType: .toggle, scaleOptions: []),
    ]

    static func behaviors(for ids: [String]) -> [JournalBehavior] {
        ids.compactMap { id in
            allBehaviors.first { $0.id == id }
        }
    }

    static func behaviors(for category: BehaviorCategory) -> [JournalBehavior] {
        allBehaviors.filter { $0.category == category }
    }
}
