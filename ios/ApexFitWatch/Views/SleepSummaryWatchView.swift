import SwiftUI

struct SleepSummaryWatchView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    private var totalMinutes: Int { context.sleepDurationMinutes }

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                VStack(spacing: 4) {
                    Text((Double(totalMinutes) / 60.0).formattedHoursMinutes)
                        .font(.system(size: 28, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.sleepDeep)
                    Text("Performance: \(context.sleepPerformance.formattedPercentage)")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(performanceColor)
                }

                // Sleep stages bar
                sleepStagesBar

                // Stage breakdown
                VStack(spacing: 6) {
                    stageRow("Deep", minutes: context.deepSleepMinutes, color: AppColors.sleepDeep)
                    stageRow("REM", minutes: context.remSleepMinutes, color: AppColors.sleepREM)
                    stageRow("Light", minutes: context.lightSleepMinutes, color: AppColors.sleepLight)
                    stageRow("Awake", minutes: awakeMinutes, color: AppColors.sleepAwake)
                }
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))

                // Sleep Planner
                if let bedtime = context.recommendedBedtime {
                    VStack(spacing: 4) {
                        Text("Tonight")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundStyle(AppColors.textSecondary)
                        Text("Bed by \(bedtime.formatted(date: .omitted, time: .shortened))")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundStyle(AppColors.sleepDeep)
                        Text("Need: \((Double(context.sleepNeedMinutes) / 60.0).formattedHoursMinutes)")
                            .font(.system(size: 12))
                            .foregroundStyle(AppColors.textTertiary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(8)
                    .background(AppColors.backgroundSecondary)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                }
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Sleep")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var sleepStagesBar: some View {
        GeometryReader { geo in
            HStack(spacing: 1) {
                stageSegment(minutes: context.deepSleepMinutes, color: AppColors.sleepDeep, width: geo.size.width)
                stageSegment(minutes: context.lightSleepMinutes, color: AppColors.sleepLight, width: geo.size.width)
                stageSegment(minutes: context.remSleepMinutes, color: AppColors.sleepREM, width: geo.size.width)
                stageSegment(minutes: awakeMinutes, color: AppColors.sleepAwake.opacity(0.5), width: geo.size.width)
            }
            .clipShape(Capsule())
        }
        .frame(height: 10)
        .padding(.horizontal, 4)
    }

    private func stageSegment(minutes: Int, color: Color, width: CGFloat) -> some View {
        let fraction = totalMinutes > 0 ? CGFloat(minutes) / CGFloat(totalMinutes) : 0
        return Rectangle().fill(color).frame(width: max(fraction * width, 0))
    }

    private func stageRow(_ label: String, minutes: Int, color: Color) -> some View {
        HStack {
            Circle().fill(color).frame(width: 8, height: 8)
            Text(label)
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(AppColors.textSecondary)
            Spacer()
            Text("\(minutes / 60)h \(minutes % 60)m")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(AppColors.textPrimary)
        }
    }

    private var awakeMinutes: Int {
        max(0, totalMinutes - context.deepSleepMinutes - context.remSleepMinutes - context.lightSleepMinutes)
    }

    private var performanceColor: Color {
        switch context.sleepPerformance {
        case 85...100: return AppColors.recoveryGreen
        case 70..<85: return AppColors.recoveryYellow
        default: return AppColors.recoveryRed
        }
    }
}
