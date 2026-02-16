import HealthKit

enum HealthKitDataType: String, CaseIterable {
    // Vital Signs
    case heartRate
    case heartRateVariabilitySDNN
    case restingHeartRate
    case respiratoryRate
    case oxygenSaturation
    case bodyTemperature
    case vo2Max
    case walkingHeartRateAverage

    // Activity
    case stepCount
    case activeEnergyBurned
    case basalEnergyBurned
    case appleExerciseTime
    case distanceWalkingRunning
    case distanceCycling
    case distanceSwimming

    // Body
    case bodyMass
    case leanBodyMass
    case bodyFatPercentage
    case height

    // Sleep
    case sleepAnalysis

    // Workouts
    case workout

    var quantityTypeIdentifier: HKQuantityTypeIdentifier? {
        switch self {
        case .heartRate: return .heartRate
        case .heartRateVariabilitySDNN: return .heartRateVariabilitySDNN
        case .restingHeartRate: return .restingHeartRate
        case .respiratoryRate: return .respiratoryRate
        case .oxygenSaturation: return .oxygenSaturation
        case .bodyTemperature: return .bodyTemperature
        case .vo2Max: return .vo2Max
        case .walkingHeartRateAverage: return .walkingHeartRateAverage
        case .stepCount: return .stepCount
        case .activeEnergyBurned: return .activeEnergyBurned
        case .basalEnergyBurned: return .basalEnergyBurned
        case .appleExerciseTime: return .appleExerciseTime
        case .distanceWalkingRunning: return .distanceWalkingRunning
        case .distanceCycling: return .distanceCycling
        case .distanceSwimming: return .distanceSwimming
        case .bodyMass: return .bodyMass
        case .leanBodyMass: return .leanBodyMass
        case .bodyFatPercentage: return .bodyFatPercentage
        case .height: return .height
        default: return nil
        }
    }

    var categoryTypeIdentifier: HKCategoryTypeIdentifier? {
        switch self {
        case .sleepAnalysis: return .sleepAnalysis
        default: return nil
        }
    }

    var sampleType: HKSampleType? {
        if let id = quantityTypeIdentifier {
            return HKQuantityType(id)
        }
        if let id = categoryTypeIdentifier {
            return HKCategoryType(id)
        }
        if self == .workout {
            return HKWorkoutType.workoutType()
        }
        return nil
    }

    var displayName: String {
        switch self {
        case .heartRate: return "Heart Rate"
        case .heartRateVariabilitySDNN: return "HRV (SDNN)"
        case .restingHeartRate: return "Resting Heart Rate"
        case .respiratoryRate: return "Respiratory Rate"
        case .oxygenSaturation: return "Blood Oxygen"
        case .bodyTemperature: return "Body Temperature"
        case .vo2Max: return "VO2 Max"
        case .walkingHeartRateAverage: return "Walking HR Average"
        case .stepCount: return "Steps"
        case .activeEnergyBurned: return "Active Calories"
        case .basalEnergyBurned: return "Resting Calories"
        case .appleExerciseTime: return "Exercise Time"
        case .distanceWalkingRunning: return "Walking + Running Distance"
        case .distanceCycling: return "Cycling Distance"
        case .distanceSwimming: return "Swimming Distance"
        case .bodyMass: return "Weight"
        case .leanBodyMass: return "Lean Body Mass"
        case .bodyFatPercentage: return "Body Fat %"
        case .height: return "Height"
        case .sleepAnalysis: return "Sleep"
        case .workout: return "Workouts"
        }
    }

    static var readTypes: Set<HKObjectType> {
        var types = Set<HKObjectType>()
        for dataType in HealthKitDataType.allCases {
            if let sampleType = dataType.sampleType {
                types.insert(sampleType)
            }
        }
        if let heartbeatSeries = HKSeriesType.heartbeat() as? HKObjectType {
            types.insert(heartbeatSeries)
        }
        return types
    }

    static var backgroundDeliveryTypes: [HealthKitDataType] {
        [.heartRate, .restingHeartRate, .heartRateVariabilitySDNN, .sleepAnalysis, .workout, .stepCount]
    }
}
