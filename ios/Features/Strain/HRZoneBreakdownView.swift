import SwiftUI

struct HRZoneBreakdownView: View {
    let zoneMinutes: [Double]

    private var totalMinutes: Double {
        zoneMinutes.reduce(0, +)
    }

    private var zoneFractions: [Double] {
        guard totalMinutes > 0 else {
            return [0.2, 0.2, 0.2, 0.2, 0.2]
        }
        return zoneMinutes.map { $0 / totalMinutes }
    }

    private let zoneColors: [Color] = [
        AppColors.zone1,
        AppColors.zone2,
        AppColors.zone3,
        AppColors.zone4,
        AppColors.zone5,
    ]

    private let zoneNames = ["Zone 1", "Zone 2", "Zone 3", "Zone 4", "Zone 5"]

    var body: some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            // Stacked horizontal bar
            stackedBar

            // Legend
            legendGrid
        }
        .cardStyle()
    }

    // MARK: - Stacked Bar

    private var stackedBar: some View {
        GeometryReader { geometry in
            HStack(spacing: 1) {
                ForEach(0..<5, id: \.self) { index in
                    let fraction = zoneFractions[index]
                    if fraction > 0 {
                        RoundedRectangle(cornerRadius: index == 0 ? 4 : (index == 4 ? 4 : 0))
                            .fill(zoneColors[index])
                            .frame(width: max(fraction * (geometry.size.width - 4), 2))
                    }
                }
            }
        }
        .frame(height: 24)
        .clipShape(RoundedRectangle(cornerRadius: 4))
    }

    // MARK: - Legend Grid

    private var legendGrid: some View {
        VStack(spacing: AppTheme.spacingXS) {
            ForEach(0..<5, id: \.self) { index in
                HStack(spacing: AppTheme.spacingSM) {
                    Circle()
                        .fill(zoneColors[index])
                        .frame(width: 8, height: 8)

                    Text(zoneNames[index])
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textSecondary)
                        .frame(width: 52, alignment: .leading)

                    Spacer()

                    Text("\(Int(zoneMinutes[index]))m")
                        .font(AppTypography.labelSmall)
                        .foregroundStyle(AppColors.textPrimary)

                    Text(percentageText(for: index))
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                        .frame(width: 40, alignment: .trailing)
                }
            }
        }
    }

    // MARK: - Helpers

    private func percentageText(for index: Int) -> String {
        guard totalMinutes > 0 else { return "0%" }
        let pct = (zoneMinutes[index] / totalMinutes) * 100
        return pct.formattedNoDecimal + "%"
    }
}

#Preview {
    HRZoneBreakdownView(zoneMinutes: [12, 18, 25, 15, 5])
        .padding()
        .background(AppColors.backgroundPrimary)
}
