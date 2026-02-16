import SwiftUI
import SwiftData

enum OnboardingStep: Int, CaseIterable {
    case welcome = 0
    case signIn
    case healthKit
    case profile
    case journalSetup
    case complete

    var title: String {
        switch self {
        case .welcome: return "Welcome"
        case .signIn: return "Sign In"
        case .healthKit: return "Health Access"
        case .profile: return "Profile"
        case .journalSetup: return "Journal"
        case .complete: return "Complete"
        }
    }

    /// Steps that appear in the progress indicator (excludes welcome and complete)
    static var progressSteps: [OnboardingStep] {
        [.signIn, .healthKit, .profile, .journalSetup]
    }
}

struct OnboardingFlowView: View {
    @Environment(\.modelContext) private var modelContext
    @Query private var profiles: [UserProfile]

    @State private var currentStep: OnboardingStep = .welcome
    @State private var navigationPath = NavigationPath()

    private var profile: UserProfile {
        if let existing = profiles.first {
            return existing
        }
        let newProfile = UserProfile()
        modelContext.insert(newProfile)
        return newProfile
    }

    var body: some View {
        NavigationStack(path: $navigationPath) {
            ZStack {
                AppColors.backgroundPrimary
                    .ignoresSafeArea()

                VStack(spacing: 0) {
                    // Progress indicator (hidden on welcome and complete)
                    if OnboardingStep.progressSteps.contains(currentStep) {
                        progressIndicator
                            .padding(.top, AppTheme.spacingMD)
                            .padding(.horizontal, AppTheme.spacingXL)
                    }

                    // Step content
                    stepContent
                        .transition(.asymmetric(
                            insertion: .move(edge: .trailing).combined(with: .opacity),
                            removal: .move(edge: .leading).combined(with: .opacity)
                        ))
                }
            }
        }
        .preferredColorScheme(.dark)
    }

    // MARK: - Progress Indicator

    @ViewBuilder
    private var progressIndicator: some View {
        let steps = OnboardingStep.progressSteps
        let currentIndex = steps.firstIndex(of: currentStep) ?? 0

        HStack(spacing: AppTheme.spacingSM) {
            ForEach(Array(steps.enumerated()), id: \.offset) { index, step in
                Capsule()
                    .fill(index <= currentIndex ? AppColors.primaryBlue : AppColors.backgroundTertiary)
                    .frame(height: 4)
                    .animation(AppTheme.animationDefault, value: currentStep)
            }
        }
        .padding(.bottom, AppTheme.spacingSM)
    }

    // MARK: - Step Content

    @ViewBuilder
    private var stepContent: some View {
        switch currentStep {
        case .welcome:
            WelcomeView {
                advanceTo(.signIn)
            }

        case .signIn:
            SignInView(
                onSignIn: { firebaseUID in
                    profile.firebaseUID = firebaseUID
                    profile.updatedAt = Date()
                    advanceTo(.healthKit)
                },
                onSkip: {
                    advanceTo(.healthKit)
                }
            )

        case .healthKit:
            HealthKitPermissionView {
                advanceTo(.profile)
            }

        case .profile:
            ProfileSetupView(profile: profile) {
                advanceTo(.journalSetup)
            }

        case .journalSetup:
            JournalSetupView(profile: profile) {
                advanceTo(.complete)
            }

        case .complete:
            OnboardingCompleteView(profile: profile)
        }
    }

    // MARK: - Navigation

    private func advanceTo(_ step: OnboardingStep) {
        withAnimation(AppTheme.animationDefault) {
            currentStep = step
        }
    }
}

#Preview {
    OnboardingFlowView()
        .modelContainer(for: UserProfile.self, inMemory: true)
        .environment(HealthKitManager.shared)
}
