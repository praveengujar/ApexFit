import SwiftUI

struct CircularGaugeView: View {
    let value: Double
    let maxValue: Double
    let label: String
    let unit: String
    let color: Color
    let size: GaugeSize
    var overrideDimension: CGFloat? = nil
    var overrideLineWidth: CGFloat? = nil
    var showCenterText: Bool = true

    @State private var animatedProgress: Double = 0

    enum GaugeSize {
        case large, medium, small

        var dimension: CGFloat {
            switch self {
            case .large: return AppTheme.gaugeSizeLarge
            case .medium: return AppTheme.gaugeSizeMedium
            case .small: return AppTheme.gaugeSizeSmall
            }
        }

        var lineWidth: CGFloat {
            switch self {
            case .large: return AppTheme.gaugeLineWidth
            case .medium, .small: return AppTheme.gaugeLineWidthSmall
            }
        }

        var valueFont: Font {
            switch self {
            case .large: return AppTypography.metricLarge
            case .medium: return AppTypography.metricSmall
            case .small: return AppTypography.metricSmall
            }
        }

        var labelFont: Font {
            switch self {
            case .large: return AppTypography.labelLarge
            case .medium, .small: return AppTypography.labelSmall
            }
        }
    }

    private var effectiveDimension: CGFloat { overrideDimension ?? size.dimension }
    private var effectiveLineWidth: CGFloat { overrideLineWidth ?? size.lineWidth }

    private var progress: Double {
        guard maxValue > 0 else { return 0 }
        return (value / maxValue).clamped(to: 0...1)
    }

    var body: some View {
        ZStack {
            // Background arc
            Circle()
                .trim(from: 0, to: 0.75)
                .stroke(
                    color.opacity(0.2),
                    style: StrokeStyle(lineWidth: effectiveLineWidth, lineCap: .round)
                )
                .rotationEffect(.degrees(135))

            // Progress arc
            Circle()
                .trim(from: 0, to: animatedProgress * 0.75)
                .stroke(
                    color,
                    style: StrokeStyle(lineWidth: effectiveLineWidth, lineCap: .round)
                )
                .rotationEffect(.degrees(135))
                .animation(AppTheme.animationSlow, value: animatedProgress)

            // Center text
            if showCenterText {
                VStack(spacing: 2) {
                    Text(displayValue)
                        .font(size.valueFont)
                        .foregroundStyle(color)
                        .contentTransition(.numericText())

                    Text(label)
                        .font(size.labelFont)
                        .foregroundStyle(AppColors.textSecondary)
                }
            }
        }
        .frame(width: effectiveDimension, height: effectiveDimension)
        .onAppear {
            animatedProgress = progress
        }
        .onChange(of: value) { _, _ in
            animatedProgress = progress
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("\(label): \(displayValue)\(unit)")
    }

    private var displayValue: String {
        if value == 0 && maxValue > 0 {
            return "--"
        }
        if maxValue <= 21 {
            return value.formattedOneDecimal
        }
        return value.formattedNoDecimal
    }
}
