import SwiftUI

struct GlanceDashboardView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity
    @Environment(WatchWorkoutManager.self) private var workoutManager
    @Environment(\.isLuminanceReduced) private var isAOD

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    private var recoveryZone: RecoveryZone {
        RecoveryZone(rawValue: context.recoveryZone) ?? .green
    }

    var body: some View {
        if isAOD {
            aodView
        } else {
            NavigationStack {
                ScrollView {
                    VStack(spacing: 12) {
                        recoverySection
                        strainSleepRow
                        stressSection
                        sleepPlannerCard
                        actionButtons
                    }
                    .padding(.horizontal, 4)
                }
                .navigationTitle("Zyva")
                .navigationBarTitleDisplayMode(.inline)
            }
        }
    }

    // MARK: - AOD

    private var aodView: some View {
        VStack(spacing: 8) {
            Text(context.recoveryScore > 0 ? context.recoveryScore.formattedNoDecimal + "%" : "--")
                .font(.system(size: 36, weight: .bold, design: .rounded))
                .foregroundStyle(AppColors.recoveryColor(for: recoveryZone).opacity(0.6))
            Text("Strain: " + context.currentDayStrain.formattedOneDecimal)
                .font(.system(size: 15, weight: .medium))
                .foregroundStyle(Color.white.opacity(0.4))
        }
    }

    // MARK: - Recovery

    private var recoverySection: some View {
        NavigationLink(destination: RecoveryDetailWatchView()) {
            VStack(spacing: 4) {
                CircularGaugeView(
                    value: context.recoveryScore,
                    maxValue: 99,
                    label: "Recovery",
                    unit: "%",
                    color: AppColors.recoveryColor(for: recoveryZone),
                    size: .small
                )
                Text(recoveryZone.label)
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(AppColors.recoveryColor(for: recoveryZone))
            }
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(.plain)
    }

    // MARK: - Strain + Sleep

    private var strainSleepRow: some View {
        HStack(spacing: 8) {
            NavigationLink(destination: StrainDetailWatchView()) {
                VStack(spacing: 4) {
                    Text(context.currentDayStrain.formattedOneDecimal)
                        .font(.system(size: 24, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.primaryBlue)
                    Text("/ \(context.strainTargetHigh.formattedOneDecimal)")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundStyle(AppColors.textTertiary)
                    Text("Strain")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundStyle(AppColors.textSecondary)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)

            NavigationLink(destination: SleepSummaryWatchView()) {
                VStack(spacing: 4) {
                    Text(context.sleepPerformance > 0 ? context.sleepPerformance.formattedNoDecimal + "%" : "--")
                        .font(.system(size: 24, weight: .bold, design: .rounded))
                        .foregroundStyle(AppColors.sleepDeep)
                    Text((Double(context.sleepDurationMinutes) / 60.0).formattedHoursMinutes)
                        .font(.system(size: 11, weight: .medium))
                        .foregroundStyle(AppColors.textTertiary)
                    Text("Sleep")
                        .font(.system(size: 11, weight: .medium))
                        .foregroundStyle(AppColors.textSecondary)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)
        }
    }

    // MARK: - Stress

    private var stressSection: some View {
        NavigationLink(destination: StressWatchView()) {
            HStack {
                Text("Stress")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(AppColors.textSecondary)
                Spacer()
                Text(stressLabel)
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundStyle(stressColor)
                GeometryReader { geo in
                    ZStack(alignment: .leading) {
                        Capsule().fill(AppColors.backgroundTertiary)
                        Capsule().fill(stressColor)
                            .frame(width: geo.size.width * (context.currentStressScore / 3.0).clamped(to: 0...1))
                    }
                }
                .frame(width: 60, height: 6)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 10)
            .background(AppColors.backgroundSecondary)
            .clipShape(RoundedRectangle(cornerRadius: 10))
        }
        .buttonStyle(.plain)
    }

    private var stressLabel: String {
        switch context.currentStressScore {
        case 0..<0.5: return "Low"
        case 0.5..<1.5: return "Moderate"
        case 1.5..<2.5: return "High"
        default: return "Very High"
        }
    }

    private var stressColor: Color {
        switch context.currentStressScore {
        case 0..<1.0: return AppColors.recoveryGreen
        case 1.0..<2.0: return AppColors.recoveryYellow
        default: return AppColors.recoveryRed
        }
    }

    // MARK: - Sleep Planner

    private var sleepPlannerCard: some View {
        Group {
            if let bedtime = context.recommendedBedtime {
                HStack {
                    Image(systemName: "bed.double.fill")
                        .foregroundStyle(AppColors.sleepDeep)
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Bed by \(bedtime.formatted(date: .omitted, time: .shortened))")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundStyle(AppColors.textPrimary)
                        Text("Need: \((Double(context.sleepNeedMinutes) / 60.0).formattedHoursMinutes)")
                            .font(.system(size: 11))
                            .foregroundStyle(AppColors.textSecondary)
                    }
                    Spacer()
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 10)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
    }

    // MARK: - Actions

    private var actionButtons: some View {
        VStack(spacing: 8) {
            NavigationLink(destination: WorkoutStartView()) {
                Label("Start Workout", systemImage: "figure.run")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(AppColors.teal.opacity(0.3))
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)

            NavigationLink(destination: QuickJournalView()) {
                Label("Quick Journal", systemImage: "note.text")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(AppColors.textSecondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                    .background(AppColors.backgroundSecondary)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)
        }
    }
}
