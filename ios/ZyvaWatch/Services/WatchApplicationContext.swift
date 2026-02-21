import Foundation

struct WatchApplicationContext: Codable {
    // Recovery
    let recoveryScore: Double
    let recoveryZone: String
    let recoveryTimestamp: Date

    // Strain
    let currentDayStrain: Double
    let strainTargetLow: Double
    let strainTargetHigh: Double
    let strainTimestamp: Date

    // Sleep
    let sleepPerformance: Double
    let sleepDurationMinutes: Int
    let sleepNeedMinutes: Int
    let deepSleepMinutes: Int
    let remSleepMinutes: Int
    let lightSleepMinutes: Int

    // Stress
    let currentStressScore: Double
    let stressTimestamp: Date

    // Healthspan
    let zyvaAge: Double?
    let paceOfAging: String?

    // Sleep Planner
    let recommendedBedtime: Date?
    let sleepDebtMinutes: Int

    // User Profile
    let maxHeartRate: Int
    let restingHeartRateBaseline: Double
    let hrvBaseline: Double
    let baselineSleepNeedMinutes: Int

    // Auth
    let authToken: String
    let tokenExpiry: Date

    // MARK: - Dictionary Conversion

    func toDictionary() -> [String: Any] {
        guard let data = try? JSONEncoder().encode(self),
              let dict = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
            return [:]
        }
        return dict
    }

    static func from(dictionary: [String: Any]) -> WatchApplicationContext? {
        guard let data = try? JSONSerialization.data(withJSONObject: dictionary),
              let context = try? JSONDecoder().decode(WatchApplicationContext.self, from: data) else {
            return nil
        }
        return context
    }

    // MARK: - Defaults

    static let placeholder = WatchApplicationContext(
        recoveryScore: 0,
        recoveryZone: "green",
        recoveryTimestamp: .distantPast,
        currentDayStrain: 0,
        strainTargetLow: 10,
        strainTargetHigh: 14,
        strainTimestamp: .distantPast,
        sleepPerformance: 0,
        sleepDurationMinutes: 0,
        sleepNeedMinutes: 450,
        deepSleepMinutes: 0,
        remSleepMinutes: 0,
        lightSleepMinutes: 0,
        currentStressScore: 0,
        stressTimestamp: .distantPast,
        zyvaAge: nil,
        paceOfAging: nil,
        recommendedBedtime: nil,
        sleepDebtMinutes: 0,
        maxHeartRate: 190,
        restingHeartRateBaseline: 60,
        hrvBaseline: 50,
        baselineSleepNeedMinutes: 480,
        authToken: "",
        tokenExpiry: .distantPast
    )
}
