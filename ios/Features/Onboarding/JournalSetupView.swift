import SwiftUI
import SwiftData

// MARK: - Journal Behavior Model

struct JournalBehaviorDTO: Codable, Identifiable, Hashable {
    let id: String
    let name: String
    let category: String
    let responseType: String
    let options: [String]?
}

struct JournalSetupView: View {
    @Bindable var profile: UserProfile
    var onContinue: () -> Void

    @State private var behaviors: [JournalBehaviorDTO] = []
    @State private var selectedIDs: Set<String> = []
    @State private var expandedCategories: Set<String> = []

    private var categories: [String] {
        let ordered = ["Lifestyle", "Nutrition", "Sleep Hygiene", "Recovery Practices"]
        let allCategories = Set(behaviors.map(\.category))
        return ordered.filter { allCategories.contains($0) }
    }

    private func behaviors(in category: String) -> [JournalBehaviorDTO] {
        behaviors.filter { $0.category == category }
    }

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Header
                VStack(spacing: AppTheme.spacingSM) {
                    Image(systemName: "book.circle.fill")
                        .font(.system(size: 56))
                        .foregroundStyle(AppColors.teal)
                        .padding(.top, AppTheme.spacingLG)

                    Text("Daily Journal")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("Select behaviors to track daily. These help our AI find patterns that affect your recovery.")
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingLG)

                    // Selection count
                    Text("\(selectedIDs.count) selected")
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.primaryBlue)
                        .padding(.top, AppTheme.spacingXS)
                }
                .padding(.bottom, AppTheme.spacingMD)

                // Category list
                ScrollView {
                    VStack(spacing: AppTheme.spacingMD) {
                        ForEach(categories, id: \.self) { category in
                            CategorySection(
                                category: category,
                                behaviors: behaviors(in: category),
                                selectedIDs: $selectedIDs,
                                isExpanded: expandedCategories.contains(category),
                                onToggleExpand: {
                                    withAnimation(AppTheme.animationDefault) {
                                        if expandedCategories.contains(category) {
                                            expandedCategories.remove(category)
                                        } else {
                                            expandedCategories.insert(category)
                                        }
                                    }
                                }
                            )
                        }
                    }
                    .padding(.horizontal, AppTheme.spacingLG)
                    .padding(.bottom, AppTheme.spacingXL + 80) // Extra padding for button
                }

                // Continue button (pinned to bottom)
                VStack(spacing: 0) {
                    Divider()
                        .overlay(AppColors.backgroundTertiary)

                    Button(action: saveAndContinue) {
                        Text(selectedIDs.isEmpty ? "Skip for now" : "Continue")
                            .font(AppTypography.heading3)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget + 8)
                            .background(AppColors.primaryBlue)
                            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                    }
                    .padding(.horizontal, AppTheme.spacingLG)
                    .padding(.vertical, AppTheme.spacingMD)
                }
                .background(AppColors.backgroundPrimary)
            }
        }
        .onAppear {
            loadBehaviors()
            selectedIDs = Set(profile.selectedJournalBehaviorIDs)
            // Expand all categories by default
            expandedCategories = Set(categories)
        }
    }

    // MARK: - Data Loading

    private func loadBehaviors() {
        guard let url = Bundle.main.url(forResource: "JournalBehaviors", withExtension: "json") else {
            behaviors = Self.previewBehaviors
            return
        }
        do {
            let data = try Data(contentsOf: url)
            behaviors = try JSONDecoder().decode([JournalBehaviorDTO].self, from: data)
        } catch {
            behaviors = Self.previewBehaviors
        }
    }

    // MARK: - Actions

    private func saveAndContinue() {
        profile.selectedJournalBehaviorIDs = Array(selectedIDs)
        profile.updatedAt = Date()
        onContinue()
    }

    // MARK: - Preview Data

    static let previewBehaviors: [JournalBehaviorDTO] = [
        // Lifestyle
        JournalBehaviorDTO(id: "alcohol", name: "Alcohol", category: "Lifestyle", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "caffeine", name: "Caffeine", category: "Lifestyle", responseType: "numeric", options: nil),
        JournalBehaviorDTO(id: "cannabis", name: "Cannabis", category: "Lifestyle", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "screen_time_late", name: "Late Screen Time", category: "Lifestyle", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "sexual_activity", name: "Sexual Activity", category: "Lifestyle", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "stress_level", name: "Stress Level", category: "Lifestyle", responseType: "scale", options: ["Low", "Moderate", "High", "Very High"]),
        // Nutrition
        JournalBehaviorDTO(id: "hydration", name: "Hydration", category: "Nutrition", responseType: "numeric", options: nil),
        JournalBehaviorDTO(id: "late_meal", name: "Late Meal", category: "Nutrition", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "fasting", name: "Intermittent Fasting", category: "Nutrition", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "supplements", name: "Supplements Taken", category: "Nutrition", responseType: "toggle", options: nil),
        // Sleep Hygiene
        JournalBehaviorDTO(id: "sleep_mask", name: "Sleep Mask", category: "Sleep Hygiene", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "blue_light_glasses", name: "Blue-Light Glasses", category: "Sleep Hygiene", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "mouth_tape", name: "Mouth Tape", category: "Sleep Hygiene", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "room_temp", name: "Cool Room Temp", category: "Sleep Hygiene", responseType: "toggle", options: nil),
        // Recovery Practices
        JournalBehaviorDTO(id: "ice_bath", name: "Ice Bath / Cold Plunge", category: "Recovery Practices", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "sauna", name: "Sauna", category: "Recovery Practices", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "stretching", name: "Stretching / Mobility", category: "Recovery Practices", responseType: "toggle", options: nil),
        JournalBehaviorDTO(id: "massage", name: "Massage / Bodywork", category: "Recovery Practices", responseType: "toggle", options: nil),
    ]
}

// MARK: - Category Section

private struct CategorySection: View {
    let category: String
    let behaviors: [JournalBehaviorDTO]
    @Binding var selectedIDs: Set<String>
    let isExpanded: Bool
    let onToggleExpand: () -> Void

    private var categoryIcon: String {
        switch category {
        case "Lifestyle": return "figure.socialdance"
        case "Nutrition": return "fork.knife"
        case "Sleep Hygiene": return "moon.stars.fill"
        case "Recovery Practices": return "figure.cooldown"
        default: return "circle.grid.2x2.fill"
        }
    }

    private var categoryColor: Color {
        switch category {
        case "Lifestyle": return AppColors.recoveryYellow
        case "Nutrition": return AppColors.recoveryGreen
        case "Sleep Hygiene": return AppColors.lavender
        case "Recovery Practices": return AppColors.teal
        default: return AppColors.primaryBlue
        }
    }

    private var selectedCount: Int {
        behaviors.filter { selectedIDs.contains($0.id) }.count
    }

    var body: some View {
        VStack(spacing: 0) {
            // Category header
            Button(action: onToggleExpand) {
                HStack(spacing: AppTheme.spacingMD) {
                    Image(systemName: categoryIcon)
                        .font(.system(size: 20))
                        .foregroundStyle(categoryColor)
                        .frame(width: 28, alignment: .center)

                    Text(category)
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.textPrimary)

                    Spacer()

                    if selectedCount > 0 {
                        Text("\(selectedCount)")
                            .font(AppTypography.labelSmall)
                            .foregroundStyle(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 2)
                            .background(categoryColor)
                            .clipShape(Capsule())
                    }

                    Image(systemName: isExpanded ? "chevron.up" : "chevron.down")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(AppColors.textTertiary)
                }
                .padding(AppTheme.cardPadding)
            }
            .buttonStyle(.plain)

            // Behavior toggles
            if isExpanded {
                VStack(spacing: 0) {
                    ForEach(behaviors) { behavior in
                        BehaviorToggleRow(
                            behavior: behavior,
                            isSelected: selectedIDs.contains(behavior.id),
                            accentColor: categoryColor,
                            onToggle: {
                                withAnimation(AppTheme.animationDefault) {
                                    if selectedIDs.contains(behavior.id) {
                                        selectedIDs.remove(behavior.id)
                                    } else {
                                        selectedIDs.insert(behavior.id)
                                    }
                                }
                            }
                        )
                    }
                }
                .padding(.horizontal, AppTheme.cardPadding)
                .padding(.bottom, AppTheme.spacingSM)
            }
        }
        .background(AppColors.backgroundSecondary)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }
}

// MARK: - Behavior Toggle Row

private struct BehaviorToggleRow: View {
    let behavior: JournalBehaviorDTO
    let isSelected: Bool
    let accentColor: Color
    let onToggle: () -> Void

    private var responseIcon: String {
        switch behavior.responseType {
        case "numeric": return "number"
        case "scale": return "slider.horizontal.3"
        default: return "checkmark.circle"
        }
    }

    var body: some View {
        Button(action: onToggle) {
            HStack(spacing: AppTheme.spacingMD) {
                // Selection indicator
                ZStack {
                    RoundedRectangle(cornerRadius: 6)
                        .stroke(isSelected ? accentColor : AppColors.textTertiary, lineWidth: 2)
                        .frame(width: 24, height: 24)

                    if isSelected {
                        RoundedRectangle(cornerRadius: 6)
                            .fill(accentColor)
                            .frame(width: 24, height: 24)

                        Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(.white)
                    }
                }

                VStack(alignment: .leading, spacing: 1) {
                    Text(behavior.name)
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    if let options = behavior.options, !options.isEmpty {
                        Text(options.joined(separator: " / "))
                            .font(AppTypography.caption)
                            .foregroundStyle(AppColors.textTertiary)
                    }
                }

                Spacer()

                Image(systemName: responseIcon)
                    .font(.system(size: 14))
                    .foregroundStyle(AppColors.textTertiary)
            }
            .padding(.vertical, AppTheme.spacingSM)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    let container = try! ModelContainer(for: UserProfile.self, configurations: .init(isStoredInMemoryOnly: true))
    let profile = UserProfile()
    container.mainContext.insert(profile)

    return JournalSetupView(profile: profile) {
        print("Continue")
    }
    .modelContainer(container)
}
