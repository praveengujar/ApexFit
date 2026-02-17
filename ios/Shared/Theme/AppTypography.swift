import SwiftUI

enum AppTypography {
    // MARK: - Metric Display
    static let metricLarge = Font.system(size: 48, weight: .bold, design: .rounded)
    static let metricMedium = Font.system(size: 36, weight: .bold, design: .rounded)
    static let metricSmall = Font.system(size: 24, weight: .semibold, design: .rounded)

    // MARK: - Headings
    static let heading1 = Font.system(size: 28, weight: .bold)
    static let heading2 = Font.system(size: 22, weight: .bold)
    static let heading3 = Font.system(size: 17, weight: .semibold)

    // MARK: - Body
    static let bodyLarge = Font.system(size: 17, weight: .regular)
    static let bodyMedium = Font.system(size: 15, weight: .regular)
    static let bodySmall = Font.system(size: 13, weight: .regular)

    // MARK: - Labels
    static let labelLarge = Font.system(size: 15, weight: .semibold)
    static let labelMedium = Font.system(size: 13, weight: .semibold)
    static let labelSmall = Font.system(size: 11, weight: .semibold)

    // MARK: - Captions
    static let caption = Font.system(size: 12, weight: .regular)
    static let captionBold = Font.system(size: 12, weight: .semibold)
}
