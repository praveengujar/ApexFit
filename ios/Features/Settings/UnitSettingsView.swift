import SwiftUI
import SwiftData

struct UnitSettingsView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \UserProfile.createdAt, order: .reverse)
    private var profiles: [UserProfile]

    @State private var selectedUnit: UnitSystem = .metric

    private var userProfile: UserProfile? {
        profiles.first
    }

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                unitPicker
                previewCard
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Units")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            selectedUnit = userProfile?.preferredUnits ?? .metric
        }
    }

    // MARK: - Unit Picker

    private var unitPicker: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Measurement System")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            Picker("Unit System", selection: $selectedUnit) {
                ForEach(UnitSystem.allCases, id: \.self) { unit in
                    Text(unit.rawValue.capitalized).tag(unit)
                }
            }
            .pickerStyle(.segmented)
            .onChange(of: selectedUnit) { _, newValue in
                saveUnit(newValue)
            }
        }
        .cardStyle()
    }

    // MARK: - Preview Card

    private var previewCard: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Label("Preview", systemImage: "eye")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.primaryBlue)

            VStack(spacing: AppTheme.spacingSM) {
                previewRow(
                    label: "Weight",
                    metricValue: "75 kg",
                    imperialValue: "165 lbs",
                    icon: "scalemass.fill"
                )
                previewRow(
                    label: "Distance",
                    metricValue: "5.0 km",
                    imperialValue: "3.1 miles",
                    icon: "figure.run"
                )
                previewRow(
                    label: "Height",
                    metricValue: "180 cm",
                    imperialValue: "5'11\"",
                    icon: "ruler"
                )
                previewRow(
                    label: "Temperature",
                    metricValue: "36.6 C",
                    imperialValue: "97.9 F",
                    icon: "thermometer.medium"
                )
            }
        }
        .cardStyle()
    }

    private func previewRow(label: String, metricValue: String, imperialValue: String, icon: String) -> some View {
        HStack {
            Image(systemName: icon)
                .font(.caption)
                .foregroundStyle(AppColors.textTertiary)
                .frame(width: 20)
            Text(label)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text(selectedUnit == .metric ? metricValue : imperialValue)
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
                .contentTransition(.numericText())
        }
    }

    // MARK: - Save

    private func saveUnit(_ unit: UnitSystem) {
        guard let profile = userProfile else { return }
        profile.preferredUnits = unit
        profile.updatedAt = Date()
        try? modelContext.save()
    }
}
