import SwiftUI

struct StrainDetailWatchView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                CircularGaugeView(
                    value: context.currentDayStrain,
                    maxValue: 21,
                    label: "Strain",
                    unit: "",
                    color: strainColor,
                    size: .small
                )

                Text("Target: \(context.strainTargetLow.formattedOneDecimal) - \(context.strainTargetHigh.formattedOneDecimal)")
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(AppColors.textSecondary)

                // Progress bar
                VStack(spacing: 4) {
                    GeometryReader { geo in
                        ZStack(alignment: .leading) {
                            Capsule().fill(AppColors.backgroundTertiary)
                            let lowFraction = context.strainTargetLow / 21.0
                            let highFraction = context.strainTargetHigh / 21.0
                            Capsule()
                                .fill(AppColors.primaryBlue.opacity(0.2))
                                .frame(width: (highFraction - lowFraction) * geo.size.width)
                                .offset(x: lowFraction * geo.size.width)
                            Capsule().fill(strainColor)
                                .frame(width: (context.currentDayStrain / 21.0) * geo.size.width)
                        }
                    }
                    .frame(height: 8)

                    HStack {
                        Text("0").font(.system(size: 10)).foregroundStyle(AppColors.textTertiary)
                        Spacer()
                        Text("21").font(.system(size: 10)).foregroundStyle(AppColors.textTertiary)
                    }
                }
                .padding(.horizontal, 4)

                Text(strainStatusText)
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
                    .multilineTextAlignment(.center)

                NavigationLink(destination: WorkoutStartView()) {
                    Label("Start Workout", systemImage: "figure.run")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                        .background(AppColors.teal.opacity(0.3))
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Strain")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var strainColor: Color {
        switch context.currentDayStrain {
        case 0..<8: return AppColors.zone1
        case 8..<14: return AppColors.zone3
        case 14..<18: return AppColors.zone4
        default: return AppColors.zone5
        }
    }

    private var strainStatusText: String {
        if context.currentDayStrain < context.strainTargetLow {
            return "Below target. Keep moving!"
        } else if context.currentDayStrain <= context.strainTargetHigh {
            return "In target range. Great work!"
        } else {
            return "Above target. Consider resting."
        }
    }
}
