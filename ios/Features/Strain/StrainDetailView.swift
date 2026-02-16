import SwiftUI
import SwiftData

struct StrainDetailView: View {
    @Query(sort: \DailyMetric.date, order: .reverse)
    private var metrics: [DailyMetric]

    private var todayMetric: DailyMetric? {
        metrics.first { $0.date.isToday }
    }

    private var strainScore: Double {
        todayMetric?.strainScore ?? 0
    }

    private var strainZone: StrainZone {
        todayMetric?.strainZone ?? .light
    }

    private var recoveryZone: RecoveryZone {
        todayMetric?.recoveryZone ?? .yellow
    }

    private var strainTarget: ClosedRange<Double> {
        RecoveryEngine.strainTarget(for: recoveryZone)
    }

    private var workouts: [WorkoutRecord] {
        todayMetric?.workouts.sorted { $0.startDate > $1.startDate } ?? []
    }

    private var totalZoneMinutes: [Double] {
        let z1 = workouts.reduce(0) { $0 + $1.zone1Minutes }
        let z2 = workouts.reduce(0) { $0 + $1.zone2Minutes }
        let z3 = workouts.reduce(0) { $0 + $1.zone3Minutes }
        let z4 = workouts.reduce(0) { $0 + $1.zone4Minutes }
        let z5 = workouts.reduce(0) { $0 + $1.zone5Minutes }
        return [z1, z2, z3, z4, z5]
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: AppTheme.spacingLG) {
                    gaugeSection
                    strainTargetBanner
                    hrZoneBreakdownSection
                    activitiesSection
                    trendLink
                }
                .padding(.horizontal, AppTheme.spacingMD)
                .padding(.top, AppTheme.spacingSM)
                .padding(.bottom, AppTheme.spacingXL)
            }
            .background(AppColors.backgroundPrimary)
            .navigationTitle("Strain")
            .navigationBarTitleDisplayMode(.large)
        }
    }

    // MARK: - Gauge Section

    private var gaugeSection: some View {
        VStack(spacing: AppTheme.spacingMD) {
            Text("Today's Strain")
                .font(AppTypography.labelMedium)
                .foregroundStyle(AppColors.textSecondary)

            CircularGaugeView(
                value: strainScore,
                maxValue: 21,
                label: "Strain",
                unit: "",
                color: AppColors.primaryBlue,
                size: .large
            )

            HStack(spacing: AppTheme.spacingSM) {
                Text(strainZone.label)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(strainZoneColor)

                Text("(\(strainScore.formattedOneDecimal) / 21)")
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Text(Date().formatted(.dateTime.weekday(.wide).month(.abbreviated).day()))
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AppTheme.spacingMD)
    }

    // MARK: - Strain Target Banner

    private var strainTargetBanner: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text("Strain Target")
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textSecondary)
                Text("\(strainTarget.lowerBound.formattedOneDecimal) - \(strainTarget.upperBound.formattedOneDecimal)")
                    .font(AppTypography.heading3)
                    .foregroundStyle(AppColors.recoveryColor(for: recoveryZone))
            }

            Spacer()

            VStack(alignment: .trailing, spacing: 2) {
                Text("Based on Recovery")
                    .font(AppTypography.caption)
                    .foregroundStyle(AppColors.textTertiary)

                HStack(spacing: AppTheme.spacingXS) {
                    Circle()
                        .fill(AppColors.recoveryColor(for: recoveryZone))
                        .frame(width: 8, height: 8)
                    Text(recoveryZone.label)
                        .font(AppTypography.labelSmall)
                        .foregroundStyle(AppColors.recoveryColor(for: recoveryZone))
                }
            }
        }
        .cardStyle()
    }

    // MARK: - HR Zone Breakdown Section

    private var hrZoneBreakdownSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("HR Zone Breakdown")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            HRZoneBreakdownView(zoneMinutes: totalZoneMinutes)
        }
    }

    // MARK: - Activities Section

    private var activitiesSection: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            Text("Activities")
                .font(AppTypography.heading3)
                .foregroundStyle(AppColors.textPrimary)

            if workouts.isEmpty {
                HStack {
                    Spacer()
                    VStack(spacing: AppTheme.spacingSM) {
                        Image(systemName: "figure.run")
                            .font(.largeTitle)
                            .foregroundStyle(AppColors.textTertiary)
                        Text("No workouts recorded today")
                            .font(AppTypography.bodyMedium)
                            .foregroundStyle(AppColors.textSecondary)
                    }
                    .padding(.vertical, AppTheme.spacingXL)
                    Spacer()
                }
                .cardStyle()
            } else {
                ForEach(workouts, id: \.id) { workout in
                    NavigationLink(destination: WorkoutDetailView(workout: workout)) {
                        WorkoutCard(workout: workout)
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }

    // MARK: - Trend Link

    private var trendLink: some View {
        NavigationLink(destination: StrainTrendView()) {
            HStack {
                Image(systemName: "chart.bar.fill")
                    .font(.title3)
                    .foregroundStyle(AppColors.primaryBlue)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Strain Trend")
                        .font(AppTypography.heading3)
                        .foregroundStyle(AppColors.textPrimary)
                    Text("View your strain over time")
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.textSecondary)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundStyle(AppColors.textTertiary)
            }
            .cardStyle()
        }
        .buttonStyle(.plain)
    }

    // MARK: - Helpers

    private var strainZoneColor: Color {
        switch strainZone {
        case .light: return AppColors.zone1
        case .moderate: return AppColors.zone2
        case .high: return AppColors.zone4
        case .overreaching: return AppColors.zone5
        }
    }
}

#Preview {
    StrainDetailView()
        .modelContainer(for: DailyMetric.self, inMemory: true)
}
