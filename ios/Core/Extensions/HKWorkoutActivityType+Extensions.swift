import HealthKit

extension HKWorkoutActivityType {
    var displayName: String {
        switch self {
        case .running: return "Running"
        case .cycling: return "Cycling"
        case .walking: return "Walking"
        case .swimming: return "Swimming"
        case .hiking: return "Hiking"
        case .yoga: return "Yoga"
        case .traditionalStrengthTraining: return "Strength Training"
        case .functionalStrengthTraining: return "Functional Training"
        case .highIntensityIntervalTraining: return "HIIT"
        case .crossTraining: return "CrossFit"
        case .elliptical: return "Elliptical"
        case .rowing: return "Rowing"
        case .stairClimbing: return "Stair Climbing"
        case .pilates: return "Pilates"
        case .dance: return "Dance"
        case .cooldown: return "Cooldown"
        case .coreTraining: return "Core Training"
        case .soccer: return "Soccer"
        case .basketball: return "Basketball"
        case .tennis: return "Tennis"
        case .golf: return "Golf"
        case .martialArts: return "Martial Arts"
        case .boxing: return "Boxing"
        case .jumpRope: return "Jump Rope"
        default: return "Workout"
        }
    }

    var systemImageName: String {
        switch self {
        case .running: return "figure.run"
        case .cycling: return "figure.outdoor.cycle"
        case .walking: return "figure.walk"
        case .swimming: return "figure.pool.swim"
        case .hiking: return "figure.hiking"
        case .yoga: return "figure.yoga"
        case .traditionalStrengthTraining, .functionalStrengthTraining: return "dumbbell.fill"
        case .highIntensityIntervalTraining: return "flame.fill"
        case .crossTraining: return "figure.cross.training"
        case .elliptical: return "figure.elliptical"
        case .rowing: return "figure.rower"
        case .dance: return "figure.dance"
        case .boxing, .martialArts: return "figure.boxing"
        default: return "figure.mixed.cardio"
        }
    }

    var isStrength: Bool {
        switch self {
        case .traditionalStrengthTraining, .functionalStrengthTraining, .coreTraining:
            return true
        default:
            return false
        }
    }

    var isHighIntensity: Bool {
        switch self {
        case .highIntensityIntervalTraining, .crossTraining, .boxing, .martialArts:
            return true
        default:
            return false
        }
    }

    var effectiveMassFactor: Double {
        switch self {
        case .traditionalStrengthTraining: return 0.7
        case .functionalStrengthTraining: return 0.8
        case .highIntensityIntervalTraining: return 0.6
        case .crossTraining: return 0.75
        default: return 0.5
        }
    }
}
