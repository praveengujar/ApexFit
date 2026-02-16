import HealthKit

struct DeviceCapabilities {
    var hasHeartRate: Bool
    var hasSleepStages: Bool
    var hasHRVBeatToBeat: Bool
    var hasSpO2: Bool
    var hasVO2Max: Bool
    var hasRespiratoryRate: Bool
    var capabilityLevel: CapabilityLevel
    var limitationMessages: [String]
}

enum CapabilityLevel: String {
    case excellent
    case good
    case basic
    case limited

    var description: String {
        switch self {
        case .excellent: return "Full feature support"
        case .good: return "Most features supported"
        case .basic: return "Core features available"
        case .limited: return "Limited feature support"
        }
    }
}

struct DeviceCapabilityService {
    private let healthStore = HealthKitManager.shared.healthStore

    func assessCapabilities() async -> DeviceCapabilities {
        let hasHeartRate = checkAuthorization(for: .heartRate)
        let hasSleepStages = checkAuthorization(for: .sleepAnalysis)
        let hasHRVBeatToBeat = checkHeartbeatSeriesAvailability()
        let hasSpO2 = checkAuthorization(for: .oxygenSaturation)
        let hasVO2Max = checkAuthorization(for: .vo2Max)
        let hasRespiratoryRate = checkAuthorization(for: .respiratoryRate)

        var messages: [String] = []
        if !hasHRVBeatToBeat {
            messages.append("RMSSD unavailable — using SDNN for HRV. Recovery accuracy may vary.")
        }
        if !hasSpO2 {
            messages.append("Blood oxygen data unavailable. SpO2 will not factor into recovery.")
        }
        if !hasSleepStages {
            messages.append("Sleep stage data unavailable. Sleep analysis will be limited.")
        }

        let level: CapabilityLevel
        let trueCount = [hasHeartRate, hasSleepStages, hasHRVBeatToBeat, hasSpO2, hasVO2Max, hasRespiratoryRate].filter { $0 }.count
        switch trueCount {
        case 5...6: level = .excellent
        case 3...4: level = .good
        case 2: level = .basic
        default: level = .limited
        }

        return DeviceCapabilities(
            hasHeartRate: hasHeartRate,
            hasSleepStages: hasSleepStages,
            hasHRVBeatToBeat: hasHRVBeatToBeat,
            hasSpO2: hasSpO2,
            hasVO2Max: hasVO2Max,
            hasRespiratoryRate: hasRespiratoryRate,
            capabilityLevel: level,
            limitationMessages: messages
        )
    }

    private func checkAuthorization(for type: HKQuantityTypeIdentifier) -> Bool {
        let quantityType = HKQuantityType(type)
        return healthStore.authorizationStatus(for: quantityType) != .notDetermined
    }

    private func checkAuthorization(for type: HKCategoryTypeIdentifier) -> Bool {
        let categoryType = HKCategoryType(type)
        return healthStore.authorizationStatus(for: categoryType) != .notDetermined
    }

    private func checkHeartbeatSeriesAvailability() -> Bool {
        let seriesType = HKSeriesType.heartbeat()
        return healthStore.authorizationStatus(for: seriesType) != .notDetermined
    }
}
