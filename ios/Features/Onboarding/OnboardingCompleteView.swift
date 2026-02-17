import SwiftUI
import SwiftData

struct OnboardingCompleteView: View {
    @Bindable var profile: UserProfile

    @State private var checkmarkAppeared = false
    @State private var contentAppeared = false
    @State private var buttonAppeared = false

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                // Animated checkmark
                ZStack {
                    Circle()
                        .fill(AppColors.recoveryGreen.opacity(0.15))
                        .frame(width: 140, height: 140)
                        .scaleEffect(checkmarkAppeared ? 1 : 0.3)
                        .opacity(checkmarkAppeared ? 1 : 0)

                    Circle()
                        .fill(AppColors.recoveryGreen.opacity(0.3))
                        .frame(width: 110, height: 110)
                        .scaleEffect(checkmarkAppeared ? 1 : 0.3)
                        .opacity(checkmarkAppeared ? 1 : 0)

                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 80))
                        .foregroundStyle(AppColors.recoveryGreen)
                        .scaleEffect(checkmarkAppeared ? 1 : 0)
                        .rotationEffect(checkmarkAppeared ? .degrees(0) : .degrees(-90))
                }
                .padding(.bottom, AppTheme.spacingXL)

                // Title
                VStack(spacing: AppTheme.spacingSM) {
                    Text("You're All Set!")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("ApexFit is ready to help you optimize your performance.")
                        .font(AppTypography.bodyLarge)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingXL)
                }
                .opacity(contentAppeared ? 1 : 0)
                .offset(y: contentAppeared ? 0 : 20)

                Spacer()
                    .frame(height: AppTheme.spacingXXL)

                // Configuration summary
                VStack(spacing: AppTheme.spacingMD) {
                    Text("What's configured")
                        .font(AppTypography.labelLarge)
                        .foregroundStyle(AppColors.textSecondary)

                    VStack(spacing: AppTheme.spacingSM) {
                        if !profile.displayName.isEmpty {
                            SummaryRow(icon: "person.fill", text: "Profile: \(profile.displayName)")
                        }

                        if profile.firebaseUID != nil {
                            SummaryRow(icon: "checkmark.shield.fill", text: "Apple Sign In connected")
                        } else {
                            SummaryRow(icon: "person.crop.circle.badge.questionmark", text: "Signed in as guest")
                        }

                        SummaryRow(
                            icon: "heart.fill",
                            text: "HealthKit: \(HealthKitManager.shared.isAuthorized ? "Connected" : "Not connected")"
                        )

                        if !profile.selectedJournalBehaviorIDs.isEmpty {
                            SummaryRow(
                                icon: "book.fill",
                                text: "Journal: \(profile.selectedJournalBehaviorIDs.count) behaviors tracked"
                            )
                        }

                        if profile.dateOfBirth != nil, let age = profile.age {
                            SummaryRow(icon: "calendar", text: "Age: \(age) years old")
                        }
                    }
                    .padding(AppTheme.cardPadding)
                    .background(AppColors.backgroundSecondary)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .opacity(contentAppeared ? 1 : 0)

                Spacer()

                // Start button
                Button(action: completeOnboarding) {
                    HStack(spacing: AppTheme.spacingSM) {
                        Text("Start Using ApexFit")
                        Image(systemName: "arrow.right")
                    }
                    .font(AppTypography.heading3)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: AppTheme.minimumTapTarget + 8)
                    .background(
                        LinearGradient(
                            colors: [AppColors.primaryBlue, AppColors.teal],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .padding(.bottom, AppTheme.spacingXL)
                .opacity(buttonAppeared ? 1 : 0)
                .offset(y: buttonAppeared ? 0 : 30)
            }
        }
        .onAppear {
            withAnimation(.spring(response: 0.6, dampingFraction: 0.7).delay(0.1)) {
                checkmarkAppeared = true
            }
            withAnimation(.easeOut(duration: 0.5).delay(0.4)) {
                contentAppeared = true
            }
            withAnimation(.easeOut(duration: 0.5).delay(0.7)) {
                buttonAppeared = true
            }
        }
    }

    // MARK: - Actions

    private func completeOnboarding() {
        profile.hasCompletedOnboarding = true
        profile.updatedAt = Date()
    }
}

// MARK: - Summary Row

private struct SummaryRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 16))
                .foregroundStyle(AppColors.primaryBlue)
                .frame(width: 24, alignment: .center)

            Text(text)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()

            Image(systemName: "checkmark")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(AppColors.recoveryGreen)
        }
    }
}

#Preview {
    let container = try! ModelContainer(for: UserProfile.self, configurations: .init(isStoredInMemoryOnly: true))
    let profile = UserProfile(displayName: "John")
    profile.selectedJournalBehaviorIDs = ["alcohol", "caffeine", "ice_bath"]
    profile.dateOfBirth = Calendar.current.date(byAdding: .year, value: -28, to: Date())
    container.mainContext.insert(profile)

    return OnboardingCompleteView(profile: profile)
        .modelContainer(container)
        .environment(HealthKitManager.shared)
}
