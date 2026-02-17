import SwiftUI
import SwiftData

struct JournalHistoryView: View {
    @Query(sort: \JournalEntry.date, order: .reverse)
    private var entries: [JournalEntry]

    var body: some View {
        NavigationStack {
            Group {
                if entries.isEmpty {
                    EmptyStateView(
                        icon: "book.closed",
                        title: "No Journal Entries",
                        message: "Start logging your daily behaviors to see them here."
                    )
                } else {
                    entryList
                }
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("History")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Entry List

    private var entryList: some View {
        ScrollView {
            LazyVStack(spacing: AppTheme.spacingSM) {
                ForEach(entries, id: \.id) { entry in
                    NavigationLink(destination: JournalEntryDetailView(entry: entry)) {
                        historyRow(for: entry)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
        }
    }

    // MARK: - History Row

    private func historyRow(for entry: JournalEntry) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            VStack(alignment: .leading, spacing: 4) {
                Text(entry.formattedDate)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)
                Text("\(entry.responseCount) response\(entry.responseCount == 1 ? "" : "s")")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Spacer()

            completionBadge(for: entry)

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundStyle(AppColors.textTertiary)
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    private func completionBadge(for entry: JournalEntry) -> some View {
        HStack(spacing: 4) {
            Image(systemName: entry.isComplete ? "checkmark.circle.fill" : "circle")
                .font(.caption)
            Text(entry.isComplete ? "Complete" : "Partial")
                .font(AppTypography.labelSmall)
        }
        .foregroundStyle(entry.isComplete ? AppColors.recoveryGreen : AppColors.textTertiary)
    }
}

// MARK: - Read-Only Detail View

struct JournalEntryDetailView: View {
    let entry: JournalEntry

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingMD) {
                dateHeader
                responsesList
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle(entry.formattedDate)
        .navigationBarTitleDisplayMode(.inline)
    }

    private var dateHeader: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(entry.formattedDate)
                    .font(AppTypography.heading2)
                    .foregroundStyle(AppColors.textPrimary)
                if let completedAt = entry.completedAt {
                    Text("Completed at \(completedAt.hourMinuteString)")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }
            }
            Spacer()
            if entry.isComplete {
                Image(systemName: "checkmark.seal.fill")
                    .font(.title2)
                    .foregroundStyle(AppColors.recoveryGreen)
            }
        }
    }

    private var responsesList: some View {
        LazyVStack(spacing: AppTheme.spacingSM) {
            ForEach(entry.responses, id: \.id) { response in
                responseRow(for: response)
            }
        }
    }

    private func responseRow(for response: JournalResponse) -> some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(response.behaviorName)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)
                Text(response.category)
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
            Spacer()
            Text(response.displayValue)
                .font(AppTypography.heading3)
                .foregroundStyle(displayValueColor(for: response))
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    private func displayValueColor(for response: JournalResponse) -> Color {
        switch response.responseType {
        case .toggle:
            return (response.toggleValue ?? false) ? AppColors.recoveryGreen : AppColors.textSecondary
        case .numeric:
            return AppColors.primaryBlue
        case .scale:
            return AppColors.teal
        }
    }
}
