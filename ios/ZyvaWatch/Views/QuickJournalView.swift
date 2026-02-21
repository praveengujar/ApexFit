import SwiftUI

struct QuickJournalView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity

    @State private var entries: [(id: String, name: String, isOn: Bool)] = [
        ("alcohol", "Alcohol", false),
        ("caffeine", "Caffeine > 2 cups", false),
        ("late_meal", "Late Meal", false),
        ("supplements", "Supplements", false),
        ("screen_time_late", "Screen before bed", false)
    ]
    @State private var submitted = false

    var body: some View {
        if submitted {
            submittedView
        } else {
            journalForm
        }
    }

    private var journalForm: some View {
        ScrollView {
            VStack(spacing: 0) {
                ForEach(entries.indices, id: \.self) { index in
                    Button {
                        entries[index].isOn.toggle()
                        HapticCoachingManager.shared.playClick()
                    } label: {
                        HStack {
                            Text(entries[index].name)
                                .font(.system(size: 14, weight: .medium))
                                .foregroundStyle(AppColors.textPrimary)
                            Spacer()
                            Image(systemName: entries[index].isOn ? "checkmark.circle.fill" : "circle")
                                .foregroundStyle(entries[index].isOn ? AppColors.teal : AppColors.textTertiary)
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 10)
                    }
                    .buttonStyle(.plain)

                    if index < entries.count - 1 {
                        Divider().overlay(AppColors.backgroundTertiary)
                    }
                }
            }
            .background(AppColors.backgroundSecondary)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .padding(.horizontal, 4)

            Button {
                submitJournal()
            } label: {
                Text("Submit")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(AppColors.teal)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 4)
            .padding(.top, 8)

            Text("Full journal on iPhone")
                .font(.system(size: 11))
                .foregroundStyle(AppColors.textTertiary)
                .padding(.top, 4)
        }
        .navigationTitle("Quick Journal")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var submittedView: some View {
        VStack(spacing: 12) {
            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 36))
                .foregroundStyle(AppColors.recoveryGreen)
            Text("Submitted")
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(AppColors.textPrimary)
            Text("Syncing to iPhone...")
                .font(.system(size: 12))
                .foregroundStyle(AppColors.textTertiary)
        }
    }

    private func submitJournal() {
        var journalEntries: [String: Bool] = [:]
        for entry in entries { journalEntries[entry.id] = entry.isOn }
        connectivity.sendQuickJournal(journalEntries)
        submitted = true
        HapticCoachingManager.shared.playNotification()
    }
}
