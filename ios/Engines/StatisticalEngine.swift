import Foundation

struct CorrelationResult {
    let behaviorName: String
    let metricName: String
    let effectSize: Double      // Cohen's d
    let pValue: Double
    let isSignificant: Bool     // p < 0.05
    let direction: CorrelationDirection
    let sampleSizeWith: Int
    let sampleSizeWithout: Int
    let meanWith: Double
    let meanWithout: Double
}

enum CorrelationDirection: String {
    case positive   // Behavior improves the metric
    case negative   // Behavior worsens the metric
    case neutral    // No significant effect
}

struct StatisticalEngine {
    /// Perform independent samples t-test comparing metric values
    /// when a behavior is present vs absent.
    static func tTest(
        withBehavior: [Double],
        withoutBehavior: [Double]
    ) -> (tStatistic: Double, pValue: Double)? {
        guard withBehavior.count >= 3, withoutBehavior.count >= 3 else { return nil }

        let n1 = Double(withBehavior.count)
        let n2 = Double(withoutBehavior.count)
        let mean1 = withBehavior.mean
        let mean2 = withoutBehavior.mean
        let var1 = withBehavior.standardDeviation * withBehavior.standardDeviation
        let var2 = withoutBehavior.standardDeviation * withoutBehavior.standardDeviation

        let pooledSE = sqrt(var1 / n1 + var2 / n2)
        guard pooledSE > 0 else { return nil }

        let t = (mean1 - mean2) / pooledSE

        // Welch-Satterthwaite degrees of freedom
        let num = pow(var1 / n1 + var2 / n2, 2)
        let den = pow(var1 / n1, 2) / (n1 - 1) + pow(var2 / n2, 2) / (n2 - 1)
        let df = num / den

        // Approximate p-value using normal distribution for large samples
        let pValue = approximatePValue(t: abs(t), df: df)

        return (t, pValue)
    }

    /// Compute Cohen's d effect size.
    static func cohensD(
        withBehavior: [Double],
        withoutBehavior: [Double]
    ) -> Double? {
        guard withBehavior.count >= 3, withoutBehavior.count >= 3 else { return nil }

        let mean1 = withBehavior.mean
        let mean2 = withoutBehavior.mean
        let sd1 = withBehavior.standardDeviation
        let sd2 = withoutBehavior.standardDeviation
        let n1 = Double(withBehavior.count)
        let n2 = Double(withoutBehavior.count)

        // Pooled standard deviation
        let pooledSD = sqrt(((n1 - 1) * sd1 * sd1 + (n2 - 1) * sd2 * sd2) / (n1 + n2 - 2))
        guard pooledSD > 0 else { return nil }

        return (mean1 - mean2) / pooledSD
    }

    /// Full correlation analysis for a behavior against a metric.
    static func analyzeCorrelation(
        behaviorName: String,
        metricName: String,
        withBehavior: [Double],
        withoutBehavior: [Double],
        higherIsBetter: Bool = true
    ) -> CorrelationResult? {
        guard let testResult = tTest(withBehavior: withBehavior, withoutBehavior: withoutBehavior),
              let effectSize = cohensD(withBehavior: withBehavior, withoutBehavior: withoutBehavior) else {
            return nil
        }

        let meanDiff = withBehavior.mean - withoutBehavior.mean
        let direction: CorrelationDirection
        if !testResult.pValue.isLess(than: 0.05) {
            direction = .neutral
        } else if (higherIsBetter && meanDiff > 0) || (!higherIsBetter && meanDiff < 0) {
            direction = .positive
        } else {
            direction = .negative
        }

        return CorrelationResult(
            behaviorName: behaviorName,
            metricName: metricName,
            effectSize: effectSize,
            pValue: testResult.pValue,
            isSignificant: testResult.pValue < 0.05,
            direction: direction,
            sampleSizeWith: withBehavior.count,
            sampleSizeWithout: withoutBehavior.count,
            meanWith: withBehavior.mean,
            meanWithout: withoutBehavior.mean
        )
    }

    /// Effect size interpretation.
    static func interpretEffectSize(_ d: Double) -> String {
        let absD = abs(d)
        switch absD {
        case ..<0.2: return "Negligible"
        case 0.2..<0.5: return "Small"
        case 0.5..<0.8: return "Medium"
        default: return "Large"
        }
    }

    // MARK: - Private

    /// Approximate two-tailed p-value using the normal distribution.
    private static func approximatePValue(t: Double, df: Double) -> Double {
        // For df > 30, t-distribution approximates normal
        // Using a simple approximation
        let x = abs(t)
        let p = erfc(x / sqrt(2.0))
        return p
    }
}
