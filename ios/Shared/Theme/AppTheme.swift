import SwiftUI

enum AppTheme {
    // MARK: - Spacing
    static let spacingXS: CGFloat = 4
    static let spacingSM: CGFloat = 8
    static let spacingMD: CGFloat = 16
    static let spacingLG: CGFloat = 24
    static let spacingXL: CGFloat = 32
    static let spacingXXL: CGFloat = 48

    // MARK: - Corner Radius
    static let cornerRadiusSmall: CGFloat = 8
    static let cornerRadiusMedium: CGFloat = 12
    static let cornerRadiusLarge: CGFloat = 16
    static let cornerRadiusXL: CGFloat = 20

    // MARK: - Tap Targets
    static let minimumTapTarget: CGFloat = 44

    // MARK: - Gauge Sizes
    static let gaugeSizeLarge: CGFloat = 200
    static let gaugeSizeMedium: CGFloat = 120
    static let gaugeSizeSmall: CGFloat = 80
    static let gaugeLineWidth: CGFloat = 12
    static let gaugeLineWidthSmall: CGFloat = 8

    // MARK: - Card
    static let cardPadding: CGFloat = 16
    static let cardSpacing: CGFloat = 12

    // MARK: - Animation
    static let animationDefault: Animation = .easeInOut(duration: 0.3)
    static let animationSlow: Animation = .easeInOut(duration: 0.6)
    static let animationSpring: Animation = .spring(response: 0.4, dampingFraction: 0.8)
}

struct CardBackground: ViewModifier {
    func body(content: Content) -> some View {
        content
            .padding(AppTheme.cardPadding)
            .background(AppColors.backgroundCard)
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }
}

extension View {
    func cardStyle() -> some View {
        modifier(CardBackground())
    }
}
