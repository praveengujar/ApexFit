import SwiftUI
import AuthenticationServices

struct SignInView: View {
    var onSignIn: (_ firebaseUID: String?) -> Void
    var onSkip: () -> Void

    @State private var isSigningIn = false
    @State private var errorMessage: String?

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                // Header
                VStack(spacing: AppTheme.spacingMD) {
                    Image(systemName: "person.circle.fill")
                        .font(.system(size: 64))
                        .foregroundStyle(AppColors.primaryBlue)

                    Text("Sign In")
                        .font(AppTypography.heading1)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("Sign in to sync your data across devices and unlock all features.")
                        .font(AppTypography.bodyLarge)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingXL)
                }

                Spacer()

                // Benefits list
                VStack(alignment: .leading, spacing: AppTheme.spacingMD) {
                    SignInBenefit(icon: "icloud.fill", text: "Sync data across all your devices")
                    SignInBenefit(icon: "lock.shield.fill", text: "Secure, private health data storage")
                    SignInBenefit(icon: "sparkles", text: "Personalized AI coaching insights")
                }
                .padding(.horizontal, AppTheme.spacingXL)

                Spacer()

                // Error message
                if let errorMessage {
                    Text(errorMessage)
                        .font(AppTypography.bodySmall)
                        .foregroundStyle(AppColors.recoveryRed)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingLG)
                        .padding(.bottom, AppTheme.spacingMD)
                }

                // Sign in with Apple button
                VStack(spacing: AppTheme.spacingMD) {
                    SignInWithAppleButton(.signIn) { request in
                        request.requestedScopes = [.fullName, .email]
                    } onCompletion: { result in
                        handleSignInResult(result)
                    }
                    .signInWithAppleButtonStyle(.white)
                    .frame(height: AppTheme.minimumTapTarget + 8)
                    .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                    .disabled(isSigningIn)
                    .overlay {
                        if isSigningIn {
                            RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium)
                                .fill(Color.black.opacity(0.5))
                            ProgressView()
                                .tint(.white)
                        }
                    }

                    // Skip option
                    Button(action: onSkip) {
                        Text("Skip for now")
                            .font(AppTypography.bodyMedium)
                            .foregroundStyle(AppColors.textSecondary)
                            .frame(maxWidth: .infinity)
                            .frame(height: AppTheme.minimumTapTarget)
                    }
                    .disabled(isSigningIn)
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .padding(.bottom, AppTheme.spacingXL)
            }
        }
    }

    // MARK: - Apple Sign In Handling

    private func handleSignInResult(_ result: Result<ASAuthorization, Error>) {
        switch result {
        case .success(let authorization):
            if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
                let userIdentifier = appleIDCredential.user
                // In production, exchange Apple ID token with Firebase Auth
                // For now, pass the Apple user identifier as a placeholder
                onSignIn(userIdentifier)
            }

        case .failure(let error):
            // ASAuthorizationError.canceled means user dismissed the dialog
            if let authError = error as? ASAuthorizationError,
               authError.code == .canceled {
                // User cancelled -- do nothing
                return
            }
            errorMessage = "Sign in failed: \(error.localizedDescription)"
        }
    }
}

// MARK: - Sign In Benefit Row

private struct SignInBenefit: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 20))
                .foregroundStyle(AppColors.primaryBlue)
                .frame(width: 32, alignment: .center)

            Text(text)
                .font(AppTypography.bodyMedium)
                .foregroundStyle(AppColors.textPrimary)

            Spacer()
        }
    }
}

#Preview {
    SignInView(
        onSignIn: { uid in print("Signed in: \(uid ?? "nil")") },
        onSkip: { print("Skipped") }
    )
}
