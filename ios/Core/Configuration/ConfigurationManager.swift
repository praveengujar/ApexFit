import Foundation

final class ConfigurationManager {
    static let shared = ConfigurationManager()

    let config: ScoringConfig

    private init() {
        guard let url = Bundle.main.url(forResource: "ScoringConfig", withExtension: "json"),
              let data = try? Data(contentsOf: url),
              let config = try? JSONDecoder().decode(ScoringConfig.self, from: data) else {
            fatalError("ScoringConfig.json missing or invalid — this is a developer error.")
        }
        self.config = config
    }
}
