import SwiftUI
import WidgetKit

// MARK: - Accessory Circular (Recovery)

struct AccessoryCircularView: View {
    let entry: ApexFitComplicationEntry

    private var recoveryZone: RecoveryZone {
        RecoveryZone(rawValue: entry.recoveryZone) ?? .green
    }

    var body: some View {
        Gauge(value: entry.recoveryScore, in: 0...99) {
            Text("R")
        } currentValueLabel: {
            Text(entry.recoveryScore > 0 ? "\(Int(entry.recoveryScore))" : "--")
                .font(.system(size: 20, weight: .bold, design: .rounded))
        }
        .gaugeStyle(.accessoryCircular)
        .tint(AppColors.recoveryColor(for: recoveryZone))
    }
}

// MARK: - Accessory Corner (Strain)

struct AccessoryCornerView: View {
    let entry: ApexFitComplicationEntry

    var body: some View {
        Text(entry.strainScore > 0 ? String(format: "%.1f", entry.strainScore) : "--")
            .font(.system(size: 20, weight: .bold, design: .rounded))
            .widgetLabel {
                Gauge(value: entry.strainScore, in: 0...entry.strainTarget) {
                    Text("Strain")
                }
                .gaugeStyle(.accessoryLinear)
                .tint(AppColors.primaryBlue)
            }
    }
}

// MARK: - Accessory Rectangular (Triple Summary)

struct AccessoryRectangularView: View {
    let entry: ApexFitComplicationEntry

    private var recoveryZone: RecoveryZone {
        RecoveryZone(rawValue: entry.recoveryZone) ?? .green
    }

    var body: some View {
        HStack(spacing: 8) {
            VStack(spacing: 1) {
                Text(entry.recoveryScore > 0 ? "\(Int(entry.recoveryScore))%" : "--")
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.recoveryColor(for: recoveryZone))
                Text("Rec")
                    .font(.system(size: 9))
                    .foregroundStyle(.secondary)
            }
            VStack(spacing: 1) {
                Text(entry.strainScore > 0 ? String(format: "%.1f", entry.strainScore) : "--")
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                Text("Strain")
                    .font(.system(size: 9))
                    .foregroundStyle(.secondary)
            }
            VStack(spacing: 1) {
                Text(entry.sleepPerformance > 0 ? "\(Int(entry.sleepPerformance))%" : "--")
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                Text("Sleep")
                    .font(.system(size: 9))
                    .foregroundStyle(.secondary)
            }
        }
        .frame(maxWidth: .infinity)
    }
}

// MARK: - Accessory Inline

struct AccessoryInlineView: View {
    let entry: ApexFitComplicationEntry

    var body: some View {
        let recovery = entry.recoveryScore > 0 ? "\(Int(entry.recoveryScore))%" : "--"
        let strain = entry.strainScore > 0 ? String(format: "%.1f", entry.strainScore) : "--"
        Text("Recovery: \(recovery) · Strain: \(strain)")
    }
}
