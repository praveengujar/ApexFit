import Foundation

extension Double {
    var formattedOneDecimal: String {
        String(format: "%.1f", self)
    }

    var formattedNoDecimal: String {
        String(format: "%.0f", self)
    }

    var formattedPercentage: String {
        String(format: "%.0f%%", self)
    }

    var formattedHoursMinutes: String {
        let totalMinutes = Int(self * 60)
        let hours = totalMinutes / 60
        let minutes = totalMinutes % 60
        if hours > 0 {
            return "\(hours)h \(minutes)m"
        }
        return "\(minutes)m"
    }

    func clamped(to range: ClosedRange<Double>) -> Double {
        min(max(self, range.lowerBound), range.upperBound)
    }

    var bpmFormatted: String {
        "\(Int(self)) BPM"
    }

    var msFormatted: String {
        String(format: "%.0f ms", self)
    }
}

import CoreGraphics

extension CGFloat {
    func clamped(to range: ClosedRange<CGFloat>) -> CGFloat {
        Swift.min(Swift.max(self, range.lowerBound), range.upperBound)
    }
}

extension Int {
    var formattedWithComma: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        return formatter.string(from: NSNumber(value: self)) ?? "\(self)"
    }
}
