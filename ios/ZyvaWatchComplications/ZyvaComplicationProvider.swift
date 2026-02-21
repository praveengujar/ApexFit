import WidgetKit
import SwiftUI

struct ZyvaComplicationProvider: TimelineProvider {
    typealias Entry = ZyvaComplicationEntry

    private let suiteName = "group.com.zyva.shared"

    func placeholder(in context: Context) -> ZyvaComplicationEntry {
        .placeholder
    }

    func getSnapshot(in context: Context, completion: @escaping (ZyvaComplicationEntry) -> Void) {
        completion(currentEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<ZyvaComplicationEntry>) -> Void) {
        let entry = currentEntry()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }

    private func currentEntry() -> ZyvaComplicationEntry {
        guard let defaults = UserDefaults(suiteName: suiteName) else {
            return .empty
        }

        return ZyvaComplicationEntry(
            date: Date(),
            recoveryScore: defaults.double(forKey: "complication_recovery_score"),
            recoveryZone: defaults.string(forKey: "complication_recovery_zone") ?? "green",
            strainScore: defaults.double(forKey: "complication_strain_score"),
            strainTarget: max(defaults.double(forKey: "complication_strain_target"), 14.0),
            sleepPerformance: defaults.double(forKey: "complication_sleep_performance"),
            sleepDuration: defaults.string(forKey: "complication_sleep_duration") ?? "--"
        )
    }
}
