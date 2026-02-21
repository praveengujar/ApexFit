import SwiftData

enum SwiftDataContainer {
    static let schema = Schema([
        UserProfile.self,
        DailyMetric.self,
        WorkoutRecord.self,
        SleepSession.self,
        SleepStage.self,
        JournalEntry.self,
        JournalResponse.self,
        BaselineMetric.self,
        HealthKitAnchor.self,
        NotificationPreference.self,
    ])

    static func create() throws -> ModelContainer {
        let configuration = ModelConfiguration(
            "Zyva",
            schema: schema,
            isStoredInMemoryOnly: false,
            allowsSave: true
        )
        return try ModelContainer(
            for: schema,
            configurations: [configuration]
        )
    }

    static func createPreview() throws -> ModelContainer {
        let configuration = ModelConfiguration(
            "ZyvaPreview",
            schema: schema,
            isStoredInMemoryOnly: true
        )
        return try ModelContainer(
            for: schema,
            configurations: [configuration]
        )
    }
}
