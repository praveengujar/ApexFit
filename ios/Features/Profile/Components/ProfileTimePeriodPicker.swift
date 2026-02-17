import SwiftUI

struct ProfileTimePeriodPicker: View {
    @Binding var selected: ProfileTimePeriod

    var body: some View {
        HStack(spacing: AppTheme.spacingSM) {
            ForEach(ProfileTimePeriod.allCases, id: \.self) { period in
                Button {
                    withAnimation(AppTheme.animationDefault) {
                        selected = period
                    }
                } label: {
                    Text(period.rawValue)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(selected == period ? AppColors.textPrimary : AppColors.textSecondary)
                        .padding(.horizontal, AppTheme.spacingMD)
                        .padding(.vertical, AppTheme.spacingSM)
                        .background(selected == period ? AppColors.primaryBlue : AppColors.backgroundTertiary)
                        .clipShape(Capsule())
                }
            }
        }
    }
}
