import SwiftUI

struct SleepStageChartView: View {
    let session: SleepSession

    private var totalSleep: Double {
        session.totalSleepMinutes
    }

    private var stages: [(label: String, minutes: Double, percentage: Double, color: Color)] {
        guard totalSleep > 0 else { return [] }

        return [
            ("Light", session.lightSleepMinutes, session.lightSleepPercentage, AppColors.sleepLight),
            ("Deep", session.deepSleepMinutes, session.deepSleepPercentage, AppColors.sleepDeep),
            ("REM", session.remSleepMinutes, session.remSleepPercentage, AppColors.sleepREM),
            ("Awake", session.awakeMinutes, awakePercentage, AppColors.sleepAwake),
        ]
    }

    private var awakePercentage: Double {
        guard totalSleep > 0 else { return 0 }
        return (session.awakeMinutes / (totalSleep + session.awakeMinutes)) * 100
    }

    private var fractions: [Double] {
        let total = session.lightSleepMinutes + session.deepSleepMinutes + session.remSleepMinutes + session.awakeMinutes
        guard total > 0 else { return [] }
        return [
            session.lightSleepMinutes / total,
            session.deepSleepMinutes / total,
            session.remSleepMinutes / total,
            session.awakeMinutes / total,
        ]
    }

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            // Stacked horizontal bar
            stackedBar

            // Stage legend
            stageLegend
        }
        .cardStyle()
    }

    // MARK: - Stacked Bar

    private var stackedBar: some View {
        GeometryReader { geometry in
            HStack(spacing: 1) {
                ForEach(Array(zip(fractions.indices, fractions)), id: \.0) { index, fraction in
                    if fraction > 0 {
                        let stageData = stages[index]
                        RoundedRectangle(cornerRadius: index == 0 ? 4 : (index == stages.count - 1 ? 4 : 0))
                            .fill(stageData.color)
                            .frame(width: max(fraction * (geometry.size.width - CGFloat(fractions.count - 1)), 2))
                    }
                }
            }
        }
        .frame(height: 28)
        .clipShape(RoundedRectangle(cornerRadius: 4))
    }

    // MARK: - Stage Legend

    private var stageLegend: some View {
        VStack(spacing: AppTheme.spacingXS) {
            ForEach(stages, id: \.label) { stage in
                HStack(spacing: AppTheme.spacingSM) {
                    Circle()
                        .fill(stage.color)
                        .frame(width: 10, height: 10)

                    Text(stage.label)
                        .font(AppTypography.bodyMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    Spacer()

                    Text(formatMinutes(stage.minutes))
                        .font(AppTypography.labelMedium)
                        .foregroundStyle(AppColors.textPrimary)

                    Text(stage.percentage.formattedNoDecimal + "%")
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                        .frame(width: 36, alignment: .trailing)
                }
            }
        }
    }

    // MARK: - Helpers

    private func formatMinutes(_ minutes: Double) -> String {
        let hrs = Int(minutes) / 60
        let mins = Int(minutes) % 60
        if hrs > 0 {
            return "\(hrs)h \(mins)m"
        }
        return "\(mins)m"
    }
}

#Preview {
    let session = SleepSession(
        startDate: Date().hoursAgo(8),
        endDate: Date(),
        isMainSleep: true
    )
    session.lightSleepMinutes = 180
    session.deepSleepMinutes = 90
    session.remSleepMinutes = 80
    session.awakeMinutes = 30
    session.totalSleepMinutes = 350

    return SleepStageChartView(session: session)
        .padding()
        .background(AppColors.backgroundPrimary)
}
