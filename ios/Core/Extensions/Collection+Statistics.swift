import Foundation

extension Collection where Element: BinaryFloatingPoint {
    var mean: Double {
        guard !isEmpty else { return 0 }
        let sum = reduce(0) { $0 + Double($1) }
        return sum / Double(count)
    }

    var standardDeviation: Double {
        guard count > 1 else { return 0 }
        let avg = mean
        let variance = reduce(0) { $0 + pow(Double($1) - avg, 2) } / Double(count - 1)
        return sqrt(variance)
    }

    var median: Double {
        guard !isEmpty else { return 0 }
        let sorted = sorted()
        let mid = count / 2
        if count.isMultiple(of: 2) {
            return (Double(sorted[mid - 1]) + Double(sorted[mid])) / 2.0
        }
        return Double(sorted[mid])
    }

    var min: Double? {
        guard !isEmpty else { return nil }
        return Double(self.min(by: { Double($0) < Double($1) })!)
    }

    var max: Double? {
        guard !isEmpty else { return nil }
        return Double(self.max(by: { Double($0) < Double($1) })!)
    }

    func percentile(_ p: Double) -> Double {
        guard !isEmpty else { return 0 }
        let sorted = map { Double($0) }.sorted()
        let index = (p / 100.0) * Double(sorted.count - 1)
        let lower = Int(index)
        let upper = lower + 1
        let fraction = index - Double(lower)
        if upper >= sorted.count {
            return sorted[lower]
        }
        return sorted[lower] + fraction * (sorted[upper] - sorted[lower])
    }
}

extension Array where Element == (date: Date, bpm: Double) {
    func averageBPM() -> Double {
        guard !isEmpty else { return 0 }
        return map(\.bpm).reduce(0, +) / Double(count)
    }

    func maxBPM() -> Double {
        map(\.bpm).max() ?? 0
    }

    func minBPM() -> Double {
        map(\.bpm).min() ?? 0
    }
}
