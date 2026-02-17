import SwiftUI

struct BreathworkView: View {
    @Environment(WatchConnectivityManager.self) private var connectivity
    @State private var sessionManager = BreathworkSessionManager()

    private var context: WatchApplicationContext {
        connectivity.applicationContext
    }

    var body: some View {
        if sessionManager.isActive {
            activeSessionView
        } else if sessionManager.elapsedTime > 0 && !sessionManager.isActive {
            sessionSummary
        } else {
            startView
        }
    }

    private var startView: some View {
        VStack(spacing: 16) {
            Image(systemName: "wind")
                .font(.system(size: 36))
                .foregroundStyle(AppColors.teal)

            Text("Cyclic Sighing")
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(AppColors.textPrimary)

            Text("4s in · 2s sip · 8s out")
                .font(.system(size: 12))
                .foregroundStyle(AppColors.textTertiary)
                .multilineTextAlignment(.center)

            Button {
                sessionManager.startSession(startingStress: context.currentStressScore)
            } label: {
                Text("Begin")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(AppColors.teal)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 8)
    }

    private var activeSessionView: some View {
        VStack(spacing: 12) {
            Text(sessionManager.currentPhase.rawValue)
                .font(.system(size: 15, weight: .semibold))
                .foregroundStyle(AppColors.teal)

            Circle()
                .fill(AppColors.teal.opacity(0.3))
                .frame(
                    width: 80 * sessionManager.circleScale,
                    height: 80 * sessionManager.circleScale
                )
                .animation(
                    .easeInOut(duration: sessionManager.currentPhase == .exhale ? sessionManager.exhaleDuration : sessionManager.inhaleDuration),
                    value: sessionManager.circleScale
                )

            Text("Cycle \(sessionManager.currentCycle) of \(sessionManager.totalCycles)")
                .font(.system(size: 12))
                .foregroundStyle(AppColors.textSecondary)

            Text(sessionManager.elapsedFormatted)
                .font(.system(size: 15, weight: .medium, design: .monospaced))
                .foregroundStyle(AppColors.textTertiary)

            Button("End Session") { sessionManager.endSession() }
                .font(.system(size: 13))
                .foregroundStyle(AppColors.recoveryRed)
        }
    }

    private var sessionSummary: some View {
        ScrollView {
            VStack(spacing: 12) {
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 36))
                    .foregroundStyle(AppColors.recoveryGreen)

                Text("Session Complete")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(AppColors.textPrimary)

                VStack(spacing: 6) {
                    HStack {
                        Text("Duration").foregroundStyle(AppColors.textSecondary)
                        Spacer()
                        Text(sessionManager.elapsedFormatted).foregroundStyle(AppColors.textPrimary)
                    }
                    HStack {
                        Text("Cycles").foregroundStyle(AppColors.textSecondary)
                        Spacer()
                        Text("\(sessionManager.currentCycle)").foregroundStyle(AppColors.textPrimary)
                    }
                }
                .font(.system(size: 13, weight: .medium))
                .padding(8)
                .background(AppColors.backgroundSecondary)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .padding(.horizontal, 4)
        }
        .navigationTitle("Done")
        .navigationBarTitleDisplayMode(.inline)
    }
}
