import SwiftUI

struct DateNavigationHeader: View {
    @Binding var selectedDate: Date
    let streak: Int
    var onProfileTap: (() -> Void)? = nil

    var body: some View {
        HStack {
            // MARK: - Left: Avatar + Streak
            HStack(spacing: AppTheme.spacingSM) {
                Button {
                    onProfileTap?()
                } label: {
                    Image(systemName: "person.crop.circle.fill")
                        .font(.system(size: 36))
                        .foregroundStyle(AppColors.textTertiary)
                }

                HStack(spacing: AppTheme.spacingXS) {
                    Image(systemName: "flame.fill")
                        .font(.system(size: 14))
                        .foregroundStyle(.orange)
                    Text("\(streak)")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textPrimary)
                }
            }

            Spacer()

            // MARK: - Center: Date Navigation
            HStack(spacing: AppTheme.spacingSM) {
                Button {
                    navigateDate(by: -1)
                } label: {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(AppColors.textSecondary)
                        .frame(width: AppTheme.minimumTapTarget, height: AppTheme.minimumTapTarget)
                }

                Text(dateDisplayText)
                    .font(AppTypography.labelMedium)
                    .foregroundStyle(AppColors.textPrimary)
                    .padding(.horizontal, AppTheme.spacingSM)
                    .padding(.vertical, AppTheme.spacingXS)
                    .background(AppColors.backgroundTertiary)
                    .clipShape(Capsule())

                Button {
                    navigateDate(by: 1)
                } label: {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(isFutureBlocked ? AppColors.textTertiary : AppColors.textSecondary)
                        .frame(width: AppTheme.minimumTapTarget, height: AppTheme.minimumTapTarget)
                }
                .disabled(isFutureBlocked)
            }

            Spacer()

            // MARK: - Right: Device Info
            HStack(spacing: AppTheme.spacingXS) {
                Image(systemName: "applewatch")
                    .font(.system(size: 16))
                    .foregroundStyle(AppColors.textSecondary)
            }
        }
        .padding(.horizontal, AppTheme.spacingMD)
        .padding(.vertical, AppTheme.spacingSM)
    }

    // MARK: - Helpers

    private var dateDisplayText: String {
        if selectedDate.isToday {
            return "TODAY"
        }
        return selectedDate.shortDateString.uppercased()
    }

    private var isFutureBlocked: Bool {
        Calendar.current.isDateInToday(selectedDate) ||
        selectedDate > Date()
    }

    private func navigateDate(by days: Int) {
        guard let newDate = Calendar.current.date(byAdding: .day, value: days, to: selectedDate) else { return }
        if newDate.startOfDay <= Date().startOfDay {
            withAnimation(AppTheme.animationDefault) {
                selectedDate = newDate
            }
        }
    }
}

#Preview {
    ZStack {
        AppColors.backgroundPrimary.ignoresSafeArea()
        DateNavigationHeader(
            selectedDate: .constant(Date()),
            streak: 41
        )
    }
}
