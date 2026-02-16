import SwiftUI

/// Gradient bar component for longevity metrics showing orange-to-green scale
/// with dual triangle markers for 6-month and 30-day averages, plus impact label.
struct LongevityGradientBar: View {
    let range: ClosedRange<Double>
    let sixMonthAvg: Double
    let thirtyDayAvg: Double
    let deltaYears: Double
    let isHigherBetter: Bool
    let rangeMinLabel: String
    let rangeMaxLabel: String

    var body: some View {
        HStack(spacing: AppTheme.spacingSM) {
            VStack(spacing: 2) {
                // 6-month marker label
                markerLabel(value: sixMonthAvg, isAbove: true)

                // Gradient bar
                GeometryReader { geo in
                    ZStack(alignment: .leading) {
                        // Segmented gradient bar
                        HStack(spacing: 1.5) {
                            ForEach(0..<20, id: \.self) { i in
                                let fraction = Double(i) / 20.0
                                RoundedRectangle(cornerRadius: 2)
                                    .fill(segmentColor(at: fraction))
                                    .frame(height: 12)
                            }
                        }

                        // 6-month marker (triangle above)
                        markerTriangle(fraction: markerFraction(sixMonthAvg), isAbove: true, geo: geo)

                        // 30-day marker (triangle below)
                        markerTriangle(fraction: markerFraction(thirtyDayAvg), isAbove: false, geo: geo)
                    }
                }
                .frame(height: 24)

                // 30-day marker label
                markerLabel(value: thirtyDayAvg, isAbove: false)

                // Range labels
                HStack {
                    Text(rangeMinLabel)
                        .font(.system(size: 10, weight: .medium))
                        .foregroundStyle(AppColors.longevityOrange)
                    Spacer()
                    Text(rangeMaxLabel)
                        .font(.system(size: 10, weight: .medium))
                        .foregroundStyle(AppColors.longevityGreen)
                }
            }

            // Delta years label
            deltaYearsView
                .frame(width: 48)
        }
    }

    // MARK: - Delta Years

    private var deltaYearsView: some View {
        VStack(spacing: 0) {
            let prefix = deltaYears >= 0.05 ? "+" : (deltaYears <= -0.05 ? "" : "")
            Text("\(prefix)\(String(format: "%.1f", deltaYears))")
                .font(.system(size: 16, weight: .bold, design: .rounded))
                .foregroundStyle(deltaColor)
            Text("years")
                .font(.system(size: 11))
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    private var deltaColor: Color {
        if abs(deltaYears) < 0.05 { return AppColors.textTertiary }
        // For longevity: negative delta = younger = good (green)
        return deltaYears < 0 ? AppColors.longevityGreen : AppColors.longevityOrange
    }

    // MARK: - Helpers

    private func segmentColor(at fraction: Double) -> Color {
        if isHigherBetter {
            // Left = orange (bad), right = green (good)
            return Color(
                red: 1.0 - fraction * 0.6,
                green: 0.3 + fraction * 0.6,
                blue: 0.0 + fraction * 0.1
            )
        } else {
            // Inverted: left = green (good), right = orange (bad) — for RHR
            let inv = 1.0 - fraction
            return Color(
                red: 1.0 - inv * 0.6,
                green: 0.3 + inv * 0.6,
                blue: 0.0 + inv * 0.1
            )
        }
    }

    private func markerFraction(_ value: Double) -> CGFloat {
        let lower = range.lowerBound
        let upper = range.upperBound
        guard upper > lower else { return 0 }
        return CGFloat((value - lower) / (upper - lower)).clamped(to: 0...1)
    }

    private func markerTriangle(fraction: CGFloat, isAbove: Bool, geo: GeometryProxy) -> some View {
        Image(systemName: isAbove ? "arrowtriangle.down.fill" : "arrowtriangle.up.fill")
            .font(.system(size: 8))
            .foregroundStyle(AppColors.textSecondary)
            .offset(
                x: geo.size.width * fraction - 4,
                y: isAbove ? -3 : 15
            )
    }

    @ViewBuilder
    private func markerLabel(value: Double, isAbove: Bool) -> some View {
        // Only show labels if values differ enough
        EmptyView()
    }
}
