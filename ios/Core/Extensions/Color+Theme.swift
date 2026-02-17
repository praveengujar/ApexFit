import SwiftUI

extension ShapeStyle where Self == Color {
    static var appBackground: Color { AppColors.backgroundPrimary }
    static var appSecondaryBackground: Color { AppColors.backgroundSecondary }
    static var appCardBackground: Color { AppColors.backgroundCard }
    static var appPrimaryText: Color { AppColors.textPrimary }
    static var appSecondaryText: Color { AppColors.textSecondary }
    static var appAccent: Color { AppColors.primaryBlue }
}

extension View {
    func recoveryGradient(zone: RecoveryZone) -> some View {
        self.foregroundStyle(
            LinearGradient(
                colors: [AppColors.recoveryColor(for: zone), AppColors.recoveryColor(for: zone).opacity(0.7)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
    }
}
