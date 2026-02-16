import SwiftUI
import SwiftData

struct NotificationSettingsView: View {
    @Environment(\.modelContext) private var modelContext
    @Query private var preferences: [NotificationPreference]

    @State private var bedtimeReminderTime = Calendar.current.date(
        from: DateComponents(hour: 22, minute: 0)
    ) ?? Date()
    @State private var isInitialized = false

    var body: some View {
        ScrollView {
            VStack(spacing: AppTheme.spacingSM) {
                ForEach(NotificationType.allCases, id: \.self) { type in
                    notificationRow(for: type)
                }
            }
            .padding(.horizontal, AppTheme.spacingMD)
            .padding(.top, AppTheme.spacingSM)
            .padding(.bottom, AppTheme.spacingXL)
        }
        .background(AppColors.backgroundPrimary)
        .navigationTitle("Notifications")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { initializePreferences() }
    }

    // MARK: - Notification Row

    private func notificationRow(for type: NotificationType) -> some View {
        VStack(alignment: .leading, spacing: AppTheme.spacingSM) {
            HStack(spacing: AppTheme.spacingSM) {
                Image(systemName: notificationIcon(for: type))
                    .font(.system(size: 14))
                    .foregroundStyle(notificationColor(for: type))
                    .frame(width: 32, height: 32)
                    .background(notificationColor(for: type).opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusSmall))

                VStack(alignment: .leading, spacing: 2) {
                    Text(type.label)
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textPrimary)
                    Text(type.description)
                        .font(AppTypography.caption)
                        .foregroundStyle(AppColors.textTertiary)
                }

                Spacer()

                Toggle("", isOn: bindingFor(type))
                    .labelsHidden()
                    .tint(AppColors.primaryBlue)
            }

            // Bedtime reminder custom time picker
            if type == .bedtimeReminder, isEnabled(type) {
                DatePicker(
                    "Reminder Time",
                    selection: $bedtimeReminderTime,
                    displayedComponents: .hourAndMinute
                )
                .datePickerStyle(.compact)
                .font(AppTypography.bodySmall)
                .foregroundStyle(AppColors.textSecondary)
                .tint(AppColors.primaryBlue)
                .onChange(of: bedtimeReminderTime) { _, newValue in
                    saveBedtimeTime(newValue)
                }
            }
        }
        .padding(AppTheme.cardPadding)
        .background(AppColors.backgroundCard)
        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
    }

    // MARK: - Icons & Colors

    private func notificationIcon(for type: NotificationType) -> String {
        switch type {
        case .morningRecovery: return "sunrise.fill"
        case .bedtimeReminder: return "moon.stars.fill"
        case .strainTarget: return "flame.fill"
        case .weeklyReport: return "chart.bar.fill"
        case .healthAlert: return "exclamationmark.heart.fill"
        case .journalReminder: return "book.fill"
        case .coachInsight: return "brain.head.profile"
        }
    }

    private func notificationColor(for type: NotificationType) -> Color {
        switch type {
        case .morningRecovery: return AppColors.recoveryGreen
        case .bedtimeReminder: return AppColors.sleepDeep
        case .strainTarget: return AppColors.recoveryRed
        case .weeklyReport: return AppColors.primaryBlue
        case .healthAlert: return AppColors.recoveryYellow
        case .journalReminder: return AppColors.teal
        case .coachInsight: return AppColors.lavender
        }
    }

    // MARK: - Preference Binding

    private func bindingFor(_ type: NotificationType) -> Binding<Bool> {
        Binding(
            get: { isEnabled(type) },
            set: { newValue in
                togglePreference(type, enabled: newValue)
            }
        )
    }

    private func isEnabled(_ type: NotificationType) -> Bool {
        preference(for: type)?.isEnabled ?? true
    }

    private func preference(for type: NotificationType) -> NotificationPreference? {
        preferences.first { $0.notificationType == type }
    }

    // MARK: - Actions

    private func initializePreferences() {
        guard !isInitialized else { return }
        isInitialized = true

        for type in NotificationType.allCases {
            if preference(for: type) == nil {
                let pref = NotificationPreference(notificationType: type, isEnabled: true)
                modelContext.insert(pref)
            }
        }

        if let bedtimePref = preference(for: .bedtimeReminder), let time = bedtimePref.customTime {
            bedtimeReminderTime = time
        }

        try? modelContext.save()
    }

    private func togglePreference(_ type: NotificationType, enabled: Bool) {
        if let pref = preference(for: type) {
            pref.isEnabled = enabled
            pref.updatedAt = Date()
        } else {
            let pref = NotificationPreference(notificationType: type, isEnabled: enabled)
            modelContext.insert(pref)
        }
        try? modelContext.save()
    }

    private func saveBedtimeTime(_ time: Date) {
        if let pref = preference(for: .bedtimeReminder) {
            pref.customTime = time
            pref.updatedAt = Date()
        }
        try? modelContext.save()
    }
}
