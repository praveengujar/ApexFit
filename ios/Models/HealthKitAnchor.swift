import Foundation
import SwiftData

@Model
final class HealthKitAnchor {
    var id: UUID
    var dataTypeIdentifier: String
    var anchorData: Data?
    var lastQueryDate: Date
    var createdAt: Date

    init(dataTypeIdentifier: String) {
        self.id = UUID()
        self.dataTypeIdentifier = dataTypeIdentifier
        self.lastQueryDate = Date()
        self.createdAt = Date()
    }
}
