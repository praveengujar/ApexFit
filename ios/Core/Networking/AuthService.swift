import Foundation
import AuthenticationServices

/// Manages Firebase Auth login and JWT token lifecycle.
@Observable
final class AuthService {
    static let shared = AuthService()

    var isSignedIn = false
    var currentUserID: String?
    var currentEmail: String?
    var jwtToken: String?

    private init() {}

    /// Handle Apple Sign-In credential and exchange for Firebase JWT.
    func signInWithApple(credential: ASAuthorizationAppleIDCredential) async throws {
        guard let identityToken = credential.identityToken,
              let tokenString = String(data: identityToken, encoding: .utf8) else {
            throw AuthError.invalidCredential
        }

        // Exchange Apple ID token for Firebase Auth JWT
        let jwt = try await exchangeTokenWithFirebase(appleIDToken: tokenString)

        jwtToken = jwt
        currentUserID = credential.user
        currentEmail = credential.email
        isSignedIn = true

        // Set token on API client
        await APIClient.shared.setAuthToken(jwt)
    }

    /// Sign in anonymously (skip auth during onboarding).
    func signInAnonymously() async throws {
        // For development: create a temporary session
        isSignedIn = true
        currentUserID = UUID().uuidString
    }

    /// Refresh the JWT token.
    func refreshToken() async throws {
        guard let currentToken = jwtToken else {
            throw AuthError.notSignedIn
        }

        // Exchange refresh token for new JWT
        let newToken = try await refreshFirebaseToken(currentToken: currentToken)
        jwtToken = newToken
        await APIClient.shared.setAuthToken(newToken)
    }

    /// Sign out.
    func signOut() {
        jwtToken = nil
        currentUserID = nil
        currentEmail = nil
        isSignedIn = false
        Task {
            await APIClient.shared.setAuthToken(nil)
        }
    }

    /// Register device token for push notifications.
    func registerDeviceToken(_ token: String) async throws {
        let body = APIModels.DeviceTokenRegistration(token: token)
        try await APIClient.shared.requestVoid(.registerDeviceToken(body: body))
    }

    // MARK: - Private Firebase Communication

    private func exchangeTokenWithFirebase(appleIDToken: String) async throws -> String {
        // In production, this calls Firebase Auth REST API:
        // POST https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp
        // For now, return placeholder
        // TODO: Implement actual Firebase Auth integration
        return "firebase-jwt-\(UUID().uuidString)"
    }

    private func refreshFirebaseToken(currentToken: String) async throws -> String {
        // POST https://securetoken.googleapis.com/v1/token
        // TODO: Implement actual token refresh
        return "firebase-jwt-refreshed-\(UUID().uuidString)"
    }
}

enum AuthError: Error, LocalizedError {
    case invalidCredential
    case notSignedIn
    case tokenExpired
    case firebaseError(String)

    var errorDescription: String? {
        switch self {
        case .invalidCredential: return "Invalid sign-in credential"
        case .notSignedIn: return "Not signed in"
        case .tokenExpired: return "Session expired. Please sign in again."
        case .firebaseError(let msg): return "Authentication error: \(msg)"
        }
    }
}
