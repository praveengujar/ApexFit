import SwiftUI

struct BehaviorRow: View {
    let behavior: JournalBehavior
    @Binding var value: ResponseValue

    var body: some View {
        HStack(spacing: AppTheme.spacingSM) {
            categoryIcon
            behaviorLabel
            Spacer()
            responseControl
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
        .accessibilityElement(children: .combine)
    }

    // MARK: - Category Icon

    private var categoryIcon: some View {
        Image(systemName: behavior.category.icon)
            .font(.system(size: 14))
            .foregroundStyle(categoryColor)
            .frame(width: 32, height: 32)
            .background(categoryColor.opacity(0.15))
            .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))
    }

    // MARK: - Behavior Label

    private var behaviorLabel: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(behavior.name)
                .font(AppTypography.labelLarge)
                .foregroundStyle(AppColors.textPrimary)
            Text(behavior.category.rawValue)
                .font(AppTypography.caption)
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    // MARK: - Response Control

    @ViewBuilder
    private var responseControl: some View {
        switch behavior.responseType {
        case .toggle:
            toggleControl
        case .numeric:
            numericControl
        case .scale:
            scaleControl
        }
    }

    // MARK: - Toggle

    private var toggleControl: some View {
        Toggle("", isOn: toggleBinding)
            .labelsHidden()
            .tint(AppColors.primaryBlue)
    }

    private var toggleBinding: Binding<Bool> {
        Binding(
            get: {
                if case .toggle(let val) = value { return val }
                return false
            },
            set: { value = .toggle($0) }
        )
    }

    // MARK: - Numeric (Stepper)

    private var numericControl: some View {
        HStack(spacing: AppTheme.spacingSM) {
            Button {
                let current = numericAmount
                if current > 0 {
                    value = .numeric(current - 1)
                }
            } label: {
                Image(systemName: "minus.circle.fill")
                    .font(.title3)
                    .foregroundStyle(AppColors.textSecondary)
            }
            .buttonStyle(.plain)

            Text("\(Int(numericAmount))")
                .font(AppTypography.metricSmall)
                .foregroundStyle(AppColors.textPrimary)
                .frame(minWidth: 28)
                .contentTransition(.numericText())

            Button {
                value = .numeric(numericAmount + 1)
            } label: {
                Image(systemName: "plus.circle.fill")
                    .font(.title3)
                    .foregroundStyle(AppColors.primaryBlue)
            }
            .buttonStyle(.plain)
        }
    }

    private var numericAmount: Double {
        if case .numeric(let val) = value { return val }
        return 0
    }

    // MARK: - Scale (Segmented Picker)

    private var scaleControl: some View {
        Picker("", selection: scaleBinding) {
            ForEach(behavior.scaleOptions, id: \.self) { option in
                Text(option)
                    .tag(option)
            }
        }
        .pickerStyle(.segmented)
        .frame(maxWidth: 200)
    }

    private var scaleBinding: Binding<String> {
        Binding(
            get: {
                if case .scale(let val) = value { return val }
                return behavior.scaleOptions.first ?? "None"
            },
            set: { value = .scale($0) }
        )
    }

    // MARK: - Category Color

    private var categoryColor: Color {
        switch behavior.category {
        case .nutrition: return AppColors.recoveryGreen
        case .activity: return AppColors.primaryBlue
        case .lifestyle: return AppColors.lavender
        case .mental: return AppColors.teal
        case .substances: return AppColors.recoveryYellow
        }
    }
}
