import SwiftUI
import Charts

struct MetricSparkline: View {
    let dataPoints: [(Date, Double)]
    let color: Color

    var body: some View {
        Group {
            if dataPoints.isEmpty {
                Rectangle()
                    .fill(AppColors.backgroundTertiary)
                    .frame(height: 40)
                    .clipShape(RoundedRectangle(cornerRadius: 4))
            } else {
                Chart {
                    ForEach(Array(dataPoints.enumerated()), id: \.offset) { _, point in
                        LineMark(
                            x: .value("Date", point.0),
                            y: .value("Value", point.1)
                        )
                        .foregroundStyle(color)
                        .interpolationMethod(.catmullRom)

                        AreaMark(
                            x: .value("Date", point.0),
                            y: .value("Value", point.1)
                        )
                        .foregroundStyle(
                            LinearGradient(
                                colors: [color.opacity(0.2), color.opacity(0.0)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                        .interpolationMethod(.catmullRom)
                    }
                }
                .chartXAxis(.hidden)
                .chartYAxis(.hidden)
                .chartLegend(.hidden)
                .frame(height: 40)
            }
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Sparkline with \(dataPoints.count) data points")
    }
}
