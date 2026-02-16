import SwiftUI
import SwiftData

struct JournalImpactDashboardView: View {
    @Environment(\.modelContext) private var modelContext
    @Query(sort: \JournalEntry.date, order: .reverse)
    private var entries: [JournalEntry]

    @State private var selectedMetric: TargetMetric = .recovery
    @State private var results: [CorrelationResult] = []
    @State private var isLoading = false
    @State private var errorMessage: String?

    private var hasEnoughData: Bool {
        entries.count >= 14
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingMD) {
                    metricPicker
                    if !hasEnoughData {
                        insufficientDataView
                    } else if isLoading {
                        LoadingStateView(message: "Analyzing correlations...")
                    } else if let error = errorMessage {
                        ErrorStateView(
                            icon: "exclamationmark.triangle",
                            message: error,
                            retryAction: { computeResults() }
                        )
                    } else if results.isEmpty {
                        EmptyStateView(
                            icon: "chart.bar.xaxis",
                            title: "No Results Yet",
                            message: "Keep logging journal entries to see how your behaviors impact \(selectedMetric.displayName)."
                        )
                    } else {
                        resultsList
                    }
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Impact Analysis")
            .navigationBarTitleDisplayMode(.large)
            .onAppear { computeResults() }
            .onChange(of: selectedMetric) { _, _ in computeResults() }
        }
    }

    // MARK: - Metric Picker

    private var metricPicker: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Target Metric")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)
            Picker("Metric", selection: $selectedMetric) {
                ForEach(TargetMetric.allCases, id: \.self) { metric in
                    Text(metric.displayName).tag(metric)
                }
            }
            .pickerStyle(.segmented)
        }
        .cardStyle()
    }

    // MARK: - Insufficient Data

    private var insufficientDataView: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Image(systemName: "chart.bar.doc.horizontal")
                .font(.system(size: 40))
                .foregroundStyle(AppColors.textTertiary)
            Text("Need More Data")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)
            Text("Log at least 14 journal entries to see impact analysis. You have \(entries.count) so far.")
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)

            ProgressView(value: Double(entries.count), total: 14)
                .tint(AppColors.primaryBlue)
                .padding(.horizontal, AppTheme.spacingXL)

            Text("\(entries.count) / 14 entries")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textTertiary)
        }
        .cardStyle()
    }

    // MARK: - Results List

    private var resultsList: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Behaviors by Impact on \(selectedMetric.displayName)")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            ForEach(Array(results.enumerated()), id: \.offset) { _, result in
                correlationRow(result)
            }
        }
    }

    private func correlationRow(_ result: CorrelationResult) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            directionIcon(result.direction)

            VStack(alignment: .leading, spacing: 2) {
                Text(behaviorDisplayName(result.behaviorName))
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                HStack(spacing: AppTheme.spacingSM) {
                    effectSizeBadge(result.effectSize)
                    if result.isSignificant {
                        significanceBadge
                    }
                }
            }

            Spacer()

            VStack(alignment: .trailing, spacing: 2) {
                Text(directionLabel(result.direction))
                    .font(AppTypography.labelSmall)
                    .foregroundStyle(directionColor(result.direction))
                Text("n=\(result.sampleSizeWith + result.sampleSizeWithout)")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    // MARK: - Direction Icon

    private func directionIcon(_ direction: CorrelationDirection) -> some View {
        Image(systemName: directionIconName(direction))
            .font(.system(size: 14, weight: .bold))
            .foregroundStyle(directionColor(direction))
            .frame(width: 32, height: 32)
            .background(directionColor(direction).opacity(0.15))
            .clipShape(Circle())
    }

    private func directionIconName(_ direction: CorrelationDirection) -> String {
        switch direction {
        case .positive: return "arrow.up.right"
        case .negative: return "arrow.down.right"
        case .neutral: return "minus"
        }
    }

    private func directionLabel(_ direction: CorrelationDirection) -> String {
        switch direction {
        case .positive: return "Positive"
        case .negative: return "Negative"
        case .neutral: return "Neutral"
        }
    }

    private func directionColor(_ direction: CorrelationDirection) -> Color {
        switch direction {
        case .positive: return AppColors.recoveryGreen
        case .negative: return AppColors.recoveryRed
        case .neutral: return AppColors.textTertiary
        }
    }

    // MARK: - Effect Size Badge

    private func effectSizeBadge(_ effectSize: Double) -> some View {
        let label = StatisticalEngine.interpretEffectSize(effectSize)
        return Text(label)
            .font(AppTypography.labelSmall)
            .foregroundStyle(AppColors.textSecondary)
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(AppColors.backgroundTertiary)
            .clipShape(Capsule())
    }

    private var significanceBadge: some View {
        HStack(spacing: 2) {
            Image(systemName: "checkmark.seal.fill")
                .font(.system(size: 10))
            Text("Significant")
                .font(AppTypography.labelSmall)
        }
        .foregroundStyle(AppColors.teal)
    }

    // MARK: - Helpers

    private func behaviorDisplayName(_ id: String) -> String {
        JournalBehaviorCatalog.allBehaviors.first { $0.id == id }?.name ?? id
    }

    private func computeResults() {
        guard hasEnoughData else { return }
        isLoading = true
        errorMessage = nil

        Task {
            do {
                let service = JournalCorrelationService(modelContext: modelContext)
                let computed = try await service.computeCorrelations(
                    targetMetric: selectedMetric,
                    minimumEntries: 14
                )
                await MainActor.run {
                    results = computed
                    isLoading = false
                }
            } catch {
                await MainActor.run {
                    errorMessage = error.localizedDescription
                    isLoading = false
                }
            }
        }
    }
}
