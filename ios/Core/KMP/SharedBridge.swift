import Foundation
import ZyvaShared

// MARK: - Date <-> Epoch Millis Conversions

extension Date {
    var epochMillis: Int64 {
        Int64(timeIntervalSince1970 * 1000)
    }

    init(epochMillis: Int64) {
        self.init(timeIntervalSince1970: TimeInterval(epochMillis) / 1000.0)
    }
}

// MARK: - KMP Config Provider

enum KMPConfigProvider {
    private static var _config: ZyvaShared.ScoringConfig?

    static var scoringConfig: ZyvaShared.ScoringConfig {
        if let config = _config { return config }
        guard let url = Bundle.main.url(forResource: "ScoringConfig", withExtension: "json"),
              let json = try? String(contentsOf: url, encoding: .utf8)
        else {
            fatalError("ScoringConfig.json not found in bundle")
        }
        let config = ConfigLoader.shared.loadScoringConfig(jsonString: json)
        _config = config
        return config
    }
}

// MARK: - KotlinDouble / KotlinInt / KotlinLong Helpers

extension Optional where Wrapped == Double {
    /// Convert Swift Double? to KotlinDouble? for KMP interop.
    var kotlinDouble: KotlinDouble? {
        self.map { KotlinDouble(value: $0) }
    }
}

extension KotlinDouble {
    var swiftDouble: Double {
        doubleValue
    }
}

extension Optional where Wrapped == KotlinDouble {
    var swiftOptionalDouble: Double? {
        self?.doubleValue
    }
}
