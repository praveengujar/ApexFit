import SwiftUI

struct JournalWeekCard: View {
    var journalEntries: [JournalEntry]
    var currentDate: Date
    var onTap: () -> Void
    var onInsightsTap: () -> Void

    private let calendar = Calendar.current
    private let dayAbbreviations = ["SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"]

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: AppTheme.spacingMD) {
                cardHeader
                weekRow
                insightsButton
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .contain)
    }

    // MARK: - Header

    private var cardHeader: some View {
        HStack {
            Text("MY JOURNAL")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Week Row

    private var weekRow: some View {
        HStack(spacing: 0) {
            ForEach(0..<7, id: \.self) { index in
                dayColumn(for: index)
                    .frame(maxWidth: .infinity)
            }
        }
        .padding(.vertical, AppTheme.spacingSM)
    }

    // MARK: - Day Column

    @ViewBuilder
    private func dayColumn(for weekdayIndex: Int) -> some View {
        let date = dateForWeekday(weekdayIndex)
        let entry = entryForDate(date)
        let isToday = calendar.isDateInToday(date)
        let isComplete = entry?.isComplete ?? false
        let isFuture = date > currentDate

        VStack(spacing: AppTheme.spacingSM) {
            Text(dayAbbreviations[weekdayIndex])
                .font(AppTypography.labelSmall)
                .foregroundStyle(isToday ? AppColors.textPrimary : AppColors.textSecondary)

            ZStack {
                if isComplete {
                    Circle()
                        .fill(AppColors.recoveryGreen)
                        .frame(width: isToday ? 28 : 24, height: isToday ? 28 : 24)

                    Image(systemName: "checkmark")
                        .font(.system(size: isToday ? 12 : 10, weight: .bold))
                        .foregroundStyle(.white)
                } else {
                    Circle()
                        .stroke(
                            isFuture ? AppColors.textTertiary.opacity(0.4) : AppColors.textTertiary,
                            lineWidth: 1.5
                        )
                        .frame(width: isToday ? 28 : 24, height: isToday ? 28 : 24)
                }
            }
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(dayAbbreviations[weekdayIndex]), \(isComplete ? "complete" : isFuture ? "upcoming" : "incomplete")")
    }

    // MARK: - Insights Button

    private var insightsButton: some View {
        Button(action: onInsightsTap) {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: "lightbulb.fill")
                    .font(.system(size: 14))
                    .foregroundStyle(AppColors.textPrimary)

                Text("RECOVERY INSIGHTS")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, AppTheme.spacingSM)
            .background(AppColors.backgroundTertiary)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Recovery insights")
    }

    // MARK: - Helpers

    /// Returns the date for a given weekday index (0=Sun ... 6=Sat) within the current week.
    private func dateForWeekday(_ weekdayIndex: Int) -> Date {
        let startOfWeek = calendar.dateInterval(of: .weekOfYear, for: currentDate)?.start ?? currentDate
        return calendar.date(byAdding: .day, value: weekdayIndex, to: startOfWeek) ?? currentDate
    }

    /// Finds the journal entry whose date matches the given day.
    private func entryForDate(_ date: Date) -> JournalEntry? {
        let startOfTargetDay = calendar.startOfDay(for: date)
        return journalEntries.first { entry in
            calendar.isDate(entry.date, inSameDayAs: startOfTargetDay)
        }
    }
}
