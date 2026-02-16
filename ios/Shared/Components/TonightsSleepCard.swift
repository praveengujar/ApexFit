import SwiftUI

struct TonightsSleepCard: View {
    var recommendedBedtime: Date?
    var alarmTime: Date?
    var alarmEnabled: Bool
    var onEditAlarm: () -> Void
    var onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: AppTheme.spacingMD) {
                cardHeader
                sleepTimeColumns
                editAlarmButton
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
        .accessibilityElement(children: .contain)
    }

    // MARK: - Header

    private var cardHeader: some View {
        HStack {
            Text("TONIGHT'S SLEEP")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(AppColors.textSecondary)
        }
    }

    // MARK: - Sleep Time Columns

    private var sleepTimeColumns: some View {
        HStack(spacing: 0) {
            bedtimeColumn
                .frame(maxWidth: .infinity)

            dottedConnector

            alarmColumn
                .frame(maxWidth: .infinity)
        }
    }

    // MARK: - Bedtime Column

    private var bedtimeColumn: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "bed.double.fill")
                .font(.system(size: 18))
                .foregroundStyle(AppColors.textSecondary)

            Text(formattedBedtime)
                .font(AppTypography.metricSmall)
                .foregroundStyle(AppColors.textPrimary)

            Text("RECOMMENDED BEDTIME")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
                .multilineTextAlignment(.center)
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Recommended bedtime \(formattedBedtime)")
    }

    // MARK: - Alarm Column

    private var alarmColumn: some View {
        VStack(spacing: AppTheme.spacingSM) {
            Image(systemName: "alarm.fill")
                .font(.system(size: 18))
                .foregroundStyle(AppColors.textSecondary)

            Text(formattedAlarmTime)
                .font(AppTypography.metricSmall)
                .foregroundStyle(AppColors.textPrimary)

            alarmStatusRow

            Text("EXACT TIME")
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .accessibilityElement(children: .combine)
        .accessibilityLabel("Alarm \(alarmEnabled ? "on" : "off") at \(formattedAlarmTime)")
    }

    // MARK: - Alarm Status Indicator

    private var alarmStatusRow: some View {
        HStack(spacing: AppTheme.spacingXS) {
            Circle()
                .fill(alarmEnabled ? AppColors.recoveryGreen : AppColors.textTertiary)
                .frame(width: 6, height: 6)

            Text(alarmEnabled ? "ALARM ON" : "ALARM OFF")
                .font(AppTypography.labelSmall)
                .foregroundStyle(alarmEnabled ? AppColors.recoveryGreen : AppColors.textTertiary)
        }
    }

    // MARK: - Dotted Connector

    private var dottedConnector: some View {
        DashedLine()
            .stroke(
                AppColors.textTertiary,
                style: StrokeStyle(lineWidth: 1, dash: [4, 3])
            )
            .frame(width: 40, height: 1)
            .padding(.bottom, AppTheme.spacingLG)
    }

    // MARK: - Edit Alarm Button

    private var editAlarmButton: some View {
        Button(action: onEditAlarm) {
            Text("EDIT ALARM")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textPrimary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, AppTheme.spacingSM)
                .background(AppColors.backgroundTertiary)
                .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Edit alarm")
    }

    // MARK: - Formatting

    private var formattedBedtime: String {
        guard let bedtime = recommendedBedtime else { return "--:--" }
        return formatHourMinute(bedtime)
    }

    private var formattedAlarmTime: String {
        guard let alarm = alarmTime else { return "--:--" }
        return formatHourMinute(alarm)
    }

    private func formatHourMinute(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "h:mm"
        return formatter.string(from: date)
    }
}

// MARK: - Dashed Line Shape

private struct DashedLine: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: 0, y: rect.midY))
        path.addLine(to: CGPoint(x: rect.width, y: rect.midY))
        return path
    }
}
