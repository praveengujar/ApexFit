import SwiftUI

struct StickyGaugeHeader: View {
    let metric: DailyMetric?

    var body: some View {
        HStack(spacing: 0) {
            miniGaugeItem(
                value: metric?.sleepPerformance ?? 0,
                maxValue: 100,
                label: "SLEEP",
                color: AppColors.sleepDeep
            )

            miniGaugeItem(
                value: metric?.recoveryScore ?? 0,
                maxValue: 99,
                label: "RECOVERY",
                color: recoveryColor
            )

            miniGaugeItem(
                value: metric?.strainScore ?? 0,
                maxValue: 21,
                label: "STRAIN",
                color: AppColors.primaryBlue
            )
        }
        .padding(.vertical, AppTheme.spacingSM)
        .padding(.horizontal, AppTheme.spacingMD)
        .background(
            AppColors.backgroundPrimary
                .opacity(0.95)
        )
    }

    // MARK: - Mini Gauge Item

    private func miniGaugeItem(
        value: Double,
        maxValue: Double,
        label: String,
        color: Color
    ) -> some View {
        HStack(spacing: AppTheme.spacingSM) {
            MiniProgressRing(
                progress: maxValue > 0 ? (value / maxValue).clamped(to: 0...1) : 0,
                color: color
            )

            Text(label)
                .font(AppTypography.labelSmall)
                .foregroundStyle(AppColors.textSecondary)
        }
        .frame(maxWidth: .infinity)
    }

    // MARK: - Recovery Color

    private var recoveryColor: Color {
        if let zone = metric?.recoveryZone {
            return AppColors.recoveryColor(for: zone)
        }
        if let score = metric?.recoveryScore {
            return AppColors.recoveryColor(for: score)
        }
        return AppColors.textTertiary
    }
}

// MARK: - Mini Progress Ring

private struct MiniProgressRing: View {
    let progress: Double
    let color: Color

    @State private var animatedProgress: Double = 0

    private let diameter: CGFloat = 24
    private let strokeWidth: CGFloat = 3

    var body: some View {
        ZStack {
            Circle()
                .stroke(
                    color.opacity(0.2),
                    style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round)
                )

            Circle()
                .trim(from: 0, to: animatedProgress)
                .stroke(
                    color,
                    style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round)
                )
                .rotationEffect(.degrees(-90))
                .animation(AppTheme.animationDefault, value: animatedProgress)
        }
        .frame(width: diameter, height: diameter)
        .onAppear {
            animatedProgress = progress
        }
        .onChange(of: progress) { _, newValue in
            animatedProgress = newValue
        }
    }
}

#Preview {
    ZStack {
        AppColors.backgroundPrimary.ignoresSafeArea()
        VStack {
            StickyGaugeHeader(metric: nil)
            Spacer()
        }
    }
}
