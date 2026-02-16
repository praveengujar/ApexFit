import Foundation
import SwiftData

@Model
final class SleepStage {
    var id: UUID
    var stageType: SleepStageType
    var startDate: Date
    var endDate: Date
    var durationMinutes: Double

    var sleepSession: SleepSession?

    init(stageType: SleepStageType, startDate: Date, endDate: Date) {
        self.id = UUID()
        self.stageType = stageType
        self.startDate = startDate
        self.endDate = endDate
        self.durationMinutes = endDate.timeIntervalSince(startDate) / 60.0
    }
}

enum SleepStageType: String, Codable, CaseIterable {
    case awake
    case light
    case deep
    case rem
    case inBed

    var label: String {
        switch self {
        case .awake: return "Awake"
        case .light: return "Light"
        case .deep: return "Deep (SWS)"
        case .rem: return "REM"
        case .inBed: return "In Bed"
        }
    }

    var sortOrder: Int {
        switch self {
        case .awake: return 0
        case .light: return 1
        case .deep: return 2
        case .rem: return 3
        case .inBed: return 4
        }
    }
}
