import SwiftUI
import SwiftData

struct JournalSetupEditView: View {
    @Environment(\.modelContext) private var modelContext
    @Environment(\.dismiss) private var dismiss
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var selectedIDs: Set<String> = []

    private var userProfile: UserProfile? {
        profiles.first
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingLG) {
                headerInfo
                categorySections
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Edit Behaviors")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Save") {
                    saveSelection()
                }
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.primaryBlue)
            }
        }
        .onAppear {
            if let profile = userProfile {
                selectedIDs = Set(profile.selectedJournalBehaviorIDs)
            }
        }
    }

    // MARK: - Header Info

    private var headerInfo: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "checklist")
                .font(.system(size: 32))
                .foregroundStyle(AppColors.primaryBlue)
            Text("Choose which behaviors to track daily. These will appear in your journal and be analyzed for their impact on your recovery.")
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)
        }
        .padding(.vertical, AppTheme.spacingSM)
    }

    // MARK: - Category Sections

    private var categorySections: some View {
        ForEach(BehaviorCategory.allCases) { category in
            VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
                categorySectionHeader(category)
                ForEach(JournalBehaviorCatalog.behaviors(for: category)) { behavior in
                    behaviorToggleRow(behavior)
                }
            }
        }
    }

    private func categorySectionHeader(_ category: BehaviorCategory) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            Image(systemName: category.icon)
                .font(.caption)
                .foregroundStyle(categoryColor(for: category))
            Text(category.rawValue)
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)
        }
        .padding(.top, AppTheme.spacingSM)
    }

    private func behaviorToggleRow(_ behavior: JournalBehavior) -> some View {
        let isSelected = selectedIDs.contains(behavior.id)

        return Button {
            withAnimation(AppTheme.animationDefault) {
                if isSelected {
                    selectedIDs.remove(behavior.id)
                } else {
                    selectedIDs.insert(behavior.id)
                }
            }
        } label: {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: behavior.icon)
                    .font(.system(size: 14))
                    .foregroundStyle(categoryColor(for: behavior.category))
                    .frame(width: 28, height: 28)
                    .background(categoryColor(for: behavior.category).opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: 6))

                VStack(alignment: .leading, spacing: 2) {
                    Text(behavior.name)
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textPrimary)
                    Text(responseTypeLabel(behavior.responseType))
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                }

                Spacer()

                Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                    .font(.title3)
                    .foregroundStyle(isSelected ? AppColors.primaryBlue : AppColors.textTertiary)
            }
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        }
        .buttonStyle(.plain)
    }

    // MARK: - Helpers

    private func categoryColor(for category: BehaviorCategory) -> Color {
        switch category {
        case .nutrition: return AppColors.recoveryGreen
        case .activity: return AppColors.primaryBlue
        case .lifestyle: return AppColors.lavender
        case .mental: return AppColors.teal
        case .substances: return AppColors.recoveryYellow
        }
    }

    private func responseTypeLabel(_ type: JournalResponseType) -> String {
        switch type {
        case .toggle: return "Yes / No"
        case .numeric: return "Number"
        case .scale: return "Scale"
        }
    }

    private func saveSelection() {
        guard let profile = userProfile else { return }
        profile.selectedJournalBehaviorIDs = Array(selectedIDs)
        profile.updatedAt = Date()
        try? modelContext.save()
        dismiss()
    }
}
