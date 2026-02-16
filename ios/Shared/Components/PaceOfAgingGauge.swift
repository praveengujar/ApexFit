import SwiftUI

/// Horizontal scale gauge showing Pace of Aging from -1.0x to 3.0x.
/// Matches the WHOOP healthspan Pace of Aging visualization with tick marks
/// and a position indicator.
struct PaceOfAgingGauge: View {
    let pace: Double

    private let minPace: Double = -1.0
    private let maxPace: Double = 3.0

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack {
                Text("PACE OF AGING")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(AppColors.textPrimary)
                    .tracking(0.5)

                Spacer()

                HStack(spacing: 4) {
                    Text(String(format: "%.1fx", pace))
                        .font(.system(size: 24, weight: .bold, design: .rounded))
                        .foregroundStyle(paceColor)
                }
            }

            HStack(spacing: 2) {
                Circle()
                    .fill(AppColors.textTertiary)
                    .frame(width: 8, height: 8)
                Text("Slow")
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
                Spacer()
                Text("Fast")
                    .font(.system(size: 12))
                    .foregroundStyle(AppColors.textTertiary)
                Image(systemName: "flame.fill")
                    .font(.system(size: 10))
                    .foregroundStyle(AppColors.textTertiary)
            }

            // Scale bar with tick marks
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    // Tick marks background
                    HStack(spacing: 0) {
                        ForEach(0..<40, id: \.self) { i in
                            Rectangle()
                                .fill(AppColors.textTertiary.opacity(0.3))
                                .frame(width: 1.5, height: 16)
                            if i < 39 {
                                Spacer(minLength: 0)
                            }
                        }
                    }

                    // Indicator (two white vertical bars)
                    let fraction = CGFloat((pace - minPace) / (maxPace - minPace)).clamped(to: 0...1)
                    HStack(spacing: 2) {
                        Rectangle().fill(.white).frame(width: 2.5, height: 22)
                        Rectangle().fill(.white).frame(width: 2.5, height: 22)
                    }
                    .offset(x: geo.size.width * fraction - 3.5)
                }
            }
            .frame(height: 22)

            // Scale labels
            HStack {
                Text("-1.0x")
                Spacer()
                Text("1.0x")
                Spacer()
                Text("3.0x")
            }
            .font(.system(size: 11))
            .foregroundStyle(AppColors.textTertiary)
        }
    }

    private var paceColor: Color {
        if pace < 0.5 { return AppColors.longevityGreen }
        if pace < 1.5 { return AppColors.recoveryYellow }
        return AppColors.longevityOrange
    }
}
