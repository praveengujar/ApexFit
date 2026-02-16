import SwiftUI

// MARK: - Activity Type Enum

enum DashboardActivityType {
    case sleep(SleepSession)
    case workout(WorkoutRecord)
}

// MARK: - Dashboard Activity Card

struct DashboardActivityCard: View {
    let activityType: DashboardActivityType

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            activityBadge
            activityLabel
            Spacer()
            timeRangeColumn
            timelineDots
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .accessibilityElement(children: .combine)
        .accessibilityLabel(accessibilityText)
    }

    // MARK: - Badge (Icon + Value)

    @ViewBuilder
    private var activityBadge: some View {
        HStack(spacing: AppTheme.spacingXS) {
            Image(systemName: badgeIconName)
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(.white)

            Text(badgeValueText)
                .font(AppTypography.labelLarge)
                .foregroundStyle(.white)
        }
        .padding(.horizontal, AppTheme.spacingSM)
        .padding(.vertical, 6)
        .background(AppColors.primaryBlue)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
    }

    // MARK: - Activity Name

    @ViewBuilder
    private var activityLabel: some View {
        Text(activityName)
            .font(AppTypography.labelMedium)
            .foregroundStyle(AppColors.textPrimary)
            .textCase(.uppercase)
    }

    // MARK: - Time Range Column

    @ViewBuilder
    private var timeRangeColumn: some View {
        VStack(alignment: .trailing, spacing: 2) {
            Text(startTimeText)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)

            Text(endTimeText)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Timeline Dots

    @ViewBuilder
    private var timelineDots: some View {
        VStack(spacing: 4) {
            ForEach(0..<3, id: \.self) { _ in
                Circle()
                    .fill(dotColor)
                    .frame(width: 4, height: 4)
            }
        }
        .frame(width: 8)
    }

    // MARK: - Computed Properties

    private var badgeIconName: String {
        switch activityType {
        case .sleep:
            return "moon.fill"
        case .workout(let workout):
            return workoutIconName(for: workout.workoutType)
        }
    }

    private var badgeValueText: String {
        switch activityType {
        case .sleep(let session):
            let hours = Int(session.totalSleepMinutes) / 60
            let minutes = Int(session.totalSleepMinutes) % 60
            return "\(hours):\(String(format: "%02d", minutes))"
        case .workout(let workout):
            return workout.strainScore.formattedOneDecimal
        }
    }

    private var activityName: String {
        switch activityType {
        case .sleep:
            return "Sleep"
        case .workout(let workout):
            return workout.workoutName
        }
    }

    private var startTimeText: String {
        switch activityType {
        case .sleep(let session):
            return formatTimeWithDay(session.startDate)
        case .workout(let workout):
            return formatTimeWithDay(workout.startDate)
        }
    }

    private var endTimeText: String {
        switch activityType {
        case .sleep(let session):
            return session.endDate.hourMinuteString
        case .workout(let workout):
            return workout.endDate.hourMinuteString
        }
    }

    private var dotColor: Color {
        switch activityType {
        case .sleep:
            return .white
        case .workout:
            return AppColors.primaryBlue
        }
    }

    private var accessibilityText: String {
        switch activityType {
        case .sleep(let session):
            return "Sleep, \(session.formattedDuration), \(startTimeText) to \(endTimeText)"
        case .workout(let workout):
            return "\(workout.workoutName), strain \(workout.strainScore.formattedOneDecimal), \(startTimeText) to \(endTimeText)"
        }
    }

    // MARK: - Helpers

    private func formatTimeWithDay(_ date: Date) -> String {
        let dayAbbrev = date.dayOfWeek
        let time = date.hourMinuteString
        return "[\(dayAbbrev)] \(time)"
    }

    private func workoutIconName(for type: String) -> String {
        let lowered = type.lowercased()
        switch lowered {
        case "running":
            return "figure.run"
        case "cycling":
            return "figure.outdoor.cycle"
        case "swimming":
            return "figure.pool.swim"
        case "tennis":
            return "tennisball.fill"
        case "strength", "strength training", "functional strength training":
            return "dumbbell.fill"
        case "yoga":
            return "figure.yoga"
        case "hiking":
            return "figure.hiking"
        case "walking":
            return "figure.walk"
        case "basketball":
            return "basketball.fill"
        case "soccer":
            return "soccerball"
        default:
            return "figure.mixed.cardio"
        }
    }
}

// MARK: - Convenience Initializers

extension DashboardActivityCard {
    /// Create a card for a sleep session.
    init(session: SleepSession) {
        self.activityType = .sleep(session)
    }

    /// Create a card for a workout.
    init(workout: WorkoutRecord) {
        self.activityType = .workout(workout)
    }
}
