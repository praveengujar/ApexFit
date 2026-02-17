import SwiftUI

enum AppColors {
    // MARK: - Recovery Zones
    static let recoveryGreen = Color(hex: "#16EC06")
    static let recoveryYellow = Color(hex: "#FFDE00")
    static let recoveryRed = Color(hex: "#FF0026")

    // MARK: - Sleep Stages
    static let sleepLight = Color(hex: "#7B8CDE")
    static let sleepDeep = Color(hex: "#4A3FB5")
    static let sleepREM = Color(hex: "#00F19F")
    static let sleepAwake = Color.white

    // MARK: - Strain Zones
    static let zone1 = Color(hex: "#4A90D9")
    static let zone2 = Color(hex: "#16EC06")
    static let zone3 = Color(hex: "#FFDE00")
    static let zone4 = Color(hex: "#FF8C00")
    static let zone5 = Color(hex: "#FF0026")

    // MARK: - Longevity
    static let longevityGreen = Color(hex: "#00E676")
    static let longevityOrange = Color(hex: "#FF6D00")
    static let longevityBlobGlow = Color(hex: "#00C853")

    // MARK: - UI Colors
    static let primaryBlue = Color(hex: "#0A84FF")
    static let teal = Color(hex: "#00F19F")
    static let deepPurple = Color(hex: "#4A3FB5")
    static let lavender = Color(hex: "#7B8CDE")

    // MARK: - Backgrounds
    static let backgroundPrimary = Color(hex: "#000000")
    static let backgroundSecondary = Color(hex: "#1C1C1E")
    static let backgroundTertiary = Color(hex: "#2C2C2E")
    static let backgroundCard = Color(hex: "#1C1C1E")

    // MARK: - Text
    static let textPrimary = Color.white
    static let textSecondary = Color(hex: "#8E8E93")
    static let textTertiary = Color(hex: "#636366")

    // MARK: - Helpers
    static func recoveryColor(for zone: RecoveryZone) -> Color {
        switch zone {
        case .green: return recoveryGreen
        case .yellow: return recoveryYellow
        case .red: return recoveryRed
        }
    }

    static func recoveryColor(for score: Double) -> Color {
        recoveryColor(for: RecoveryZone.from(score: score))
    }

    static func strainZoneColor(_ zone: Int) -> Color {
        switch zone {
        case 1: return zone1
        case 2: return zone2
        case 3: return zone3
        case 4: return zone4
        case 5: return zone5
        default: return zone1
        }
    }

    static func sleepStageColor(_ stage: SleepStageType) -> Color {
        switch stage {
        case .awake: return sleepAwake
        case .light: return sleepLight
        case .deep: return sleepDeep
        case .rem: return sleepREM
        case .inBed: return Color(hex: "#3A3A3C")
        }
    }
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
