import WidgetKit

struct ZyvaComplicationEntry: TimelineEntry {
    let date: Date
    let recoveryScore: Double
    let recoveryZone: String
    let strainScore: Double
    let strainTarget: Double
    let sleepPerformance: Double
    let sleepDuration: String

    static let placeholder = ZyvaComplicationEntry(
        date: Date(),
        recoveryScore: 75,
        recoveryZone: "green",
        strainScore: 10.0,
        strainTarget: 14.0,
        sleepPerformance: 85,
        sleepDuration: "7h 23m"
    )

    static let empty = ZyvaComplicationEntry(
        date: Date(),
        recoveryScore: 0,
        recoveryZone: "green",
        strainScore: 0,
        strainTarget: 14.0,
        sleepPerformance: 0,
        sleepDuration: "--"
    )
}
