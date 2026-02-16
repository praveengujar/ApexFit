import Foundation
import HealthKit
import Observation

@Observable
final class HealthKitManager {
    static let shared = HealthKitManager()

    let healthStore = HKHealthStore()
    var isAuthorized = false
    var authorizationError: HealthKitError?

    private init() {}

    var isHealthKitAvailable: Bool {
        HKHealthStore.isHealthDataAvailable()
    }

    func requestAuthorization() async throws {
        guard isHealthKitAvailable else {
            throw HealthKitError.notAvailable
        }

        let readTypes = HealthKitDataType.readTypes

        do {
            try await healthStore.requestAuthorization(toShare: [], read: readTypes)
            isAuthorized = true
        } catch {
            authorizationError = .authorizationFailed(error.localizedDescription)
            throw HealthKitError.authorizationFailed(error.localizedDescription)
        }
    }

    func authorizationStatus(for type: HealthKitDataType) -> HKAuthorizationStatus {
        guard let sampleType = type.sampleType else { return .notDetermined }
        return healthStore.authorizationStatus(for: sampleType)
    }

    func isDataTypeAuthorized(_ type: HealthKitDataType) -> Bool {
        authorizationStatus(for: type) == .sharingAuthorized
    }

    func setupBackgroundDelivery() {
        for dataType in HealthKitDataType.backgroundDeliveryTypes {
            guard let sampleType = dataType.sampleType else { continue }
            healthStore.enableBackgroundDelivery(for: sampleType, frequency: .immediate) { success, error in
                if let error {
                    print("Background delivery failed for \(dataType.displayName): \(error.localizedDescription)")
                }
            }
        }
    }
}

enum HealthKitError: Error, LocalizedError {
    case notAvailable
    case authorizationFailed(String)
    case queryFailed(String)
    case noData
    case invalidData

    var errorDescription: String? {
        switch self {
        case .notAvailable:
            return "HealthKit is not available on this device."
        case .authorizationFailed(let message):
            return "HealthKit authorization failed: \(message)"
        case .queryFailed(let message):
            return "HealthKit query failed: \(message)"
        case .noData:
            return "No data available."
        case .invalidData:
            return "Invalid data received from HealthKit."
        }
    }
}
