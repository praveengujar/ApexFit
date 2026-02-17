import SwiftUI

struct StressWatchView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                Text(context.currentStressScore.formattedOneDecimal)
                    .font(.system(size: 40, weight: .bold, design: .rounded))
                    .foregroundStyle(stressColor)

                Text(stressLevel)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(stressColor)

                GeometryReader { geo in
                    ZStack(alignment: .leading) {
                        Capsule().fill(AppColors.backgroundTertiary)
                        Capsule().fill(stressColor)
                            .frame(width: geo.size.width * (context.currentStressScore / 3.0).clamped(to: 0...1))
                    }
                }
                .frame(height: 8)
                .padding(.horizontal, 8)

                HStack {
                    Text("0"); Spacer(); Text("3")
                }
                .font(.system(size: 10))
                .foregroundStyle(AppColors.textTertiary)
                .padding(.horizontal, 8)

                NavigationLink(destination: BreathworkView()) {
                    HStack {
                        Image(systemName: "wind")
                        Text("Start Breathwork")
                    }
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(context.currentStressScore >= 1.5 ? AppColors.teal : AppColors.teal.opacity(0.3))
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Stress")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var stressLevel: String {
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
}
