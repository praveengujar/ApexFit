import Foundation
import ApexFitShared

// MARK: - RecoveryZone Mapping

extension ApexFitShared.RecoveryZone {
    /// Convert KMP RecoveryZone to iOS RecoveryZone.
    var toiOS: RecoveryZone {
        switch self {
        case .green: return .green
        case .yellow: return .yellow
        case .red: return .red
        default: return .red
        }
    }
}

extension RecoveryZone {
    /// Convert iOS RecoveryZone to KMP RecoveryZone.
    var toKMP: ApexFitShared.RecoveryZone {
        switch self {
        case .green: return .green
        case .yellow: return .yellow
        case .red: return .red
        }
    }
}

// MARK: - StrainZone Mapping

extension ApexFitShared.StrainZone {
    var toiOS: StrainZone {
        switch self {
        case .light: return .light
        case .moderate: return .moderate
        case .high: return .high
        case .overreaching: return .overreaching
        default: return .light
        }
    }
}

// MARK: - BiologicalSex Mapping

extension ApexFitShared.BiologicalSex {
    var toiOS: BiologicalSex {
        switch self {
        case .male: return .male
        case .female: return .female
        case .other: return .other
        case .notSet: return .notSet
        default: return .notSet
        }
    }
}

extension BiologicalSex {
    var toKMP: ApexFitShared.BiologicalSex {
        switch self {
        case .male: return .male
        case .female: return .female
        case .other: return .other
        case .notSet: return .notSet
        }
    }
}

// MARK: - BaselineMetricType Mapping

extension ApexFitShared.BaselineMetricType {
    var toiOS: BaselineMetricType {
        switch self {
        case .hrv: return .hrv
        case .restingHeartRate: return .restingHeartRate
        case .respiratoryRate: return .respiratoryRate
        case .spo2: return .spo2
        case .skinTemperature: return .skinTemperature
        case .sleepDuration: return .sleepDuration
        case .sleepPerformance: return .sleepPerformance
        case .strain: return .strain
        case .steps: return .steps
        case .deepSleepPercentage: return .deepSleepPercentage
        case .remSleepPercentage: return .remSleepPercentage
        default: return .hrv
        }
    }
}

extension BaselineMetricType {
    var toKMP: ApexFitShared.BaselineMetricType {
        switch self {
        case .hrv: return .hrv
        case .restingHeartRate: return .restingHeartRate
        case .respiratoryRate: return .respiratoryRate
        case .spo2: return .spo2
        case .skinTemperature: return .skinTemperature
        case .sleepDuration: return .sleepDuration
        case .sleepPerformance: return .sleepPerformance
        case .strain: return .strain
        case .steps: return .steps
        case .deepSleepPercentage: return .deepSleepPercentage
        case .remSleepPercentage: return .remSleepPercentage
        }
    }
}

// MARK: - SleepStageType Mapping

extension SleepStageType {
    var toKMP: ApexFitShared.SleepStageType {
        switch self {
        case .awake: return .awake
        case .light: return .light
        case .deep: return .deep
        case .rem: return .rem
        case .inBed: return .inBed
        }
    }
}

// MARK: - MaxHRSource Mapping

extension MaxHRSource {
    var toKMP: ApexFitShared.MaxHRSource {
        switch self {
        case .userInput: return .userInput
        case .observed: return .observed
        case .ageEstimate: return .ageEstimate
        }
    }
}

// MARK: - UnitSystem Mapping

extension UnitSystem {
    var toKMP: ApexFitShared.UnitSystem {
        switch self {
        case .metric: return .metric
        case .imperial: return .imperial
        }
    }
}
