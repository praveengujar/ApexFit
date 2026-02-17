import Foundation

struct APIEndpoint {
    let path: String
    let method: HTTPMethod
    let body: Encodable?
    let queryParameters: [String: String]?

    init(path: String, method: HTTPMethod = .GET, body: Encodable? = nil, queryParameters: [String: String]? = nil) {
        self.path = path
        self.method = method
        self.body = body
        self.queryParameters = queryParameters
    }
}

// MARK: - User Endpoints
extension APIEndpoint {
    static func getProfile() -> APIEndpoint {
        APIEndpoint(path: "/api/v1/users/me")
    }

    static func updateProfile(body: APIModels.UserProfileUpdate) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/users/me", method: .PUT, body: body)
    }
}

// MARK: - Metrics Endpoints
extension APIEndpoint {
    static func syncMetrics(body: APIModels.MetricsSyncRequest) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/metrics/sync", method: .POST, body: body)
    }

    static func getDailyMetrics(from: Date, to: Date) -> APIEndpoint {
        let formatter = ISO8601DateFormatter()
        return APIEndpoint(
            path: "/api/v1/metrics/daily",
            queryParameters: [
                "from": formatter.string(from: from),
                "to": formatter.string(from: to)
            ]
        )
    }
}

// MARK: - Recovery Endpoints
extension APIEndpoint {
    static func getRecoveryHistory(days: Int) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/recovery/history", queryParameters: ["days": "\(days)"])
    }
}

// MARK: - Strain Endpoints
extension APIEndpoint {
    static func getStrainHistory(days: Int) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/strain/history", queryParameters: ["days": "\(days)"])
    }
}

// MARK: - Sleep Endpoints
extension APIEndpoint {
    static func getSleepHistory(days: Int) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/sleep/history", queryParameters: ["days": "\(days)"])
    }
}

// MARK: - Workout Endpoints
extension APIEndpoint {
    static func syncWorkouts(body: APIModels.WorkoutSyncRequest) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/workouts/sync", method: .POST, body: body)
    }
}

// MARK: - Journal Endpoints
extension APIEndpoint {
    static func submitJournal(body: APIModels.JournalSubmission) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/journal", method: .POST, body: body)
    }

    static func getJournalImpacts() -> APIEndpoint {
        APIEndpoint(path: "/api/v1/journal/impacts")
    }
}

// MARK: - Coach Endpoints
extension APIEndpoint {
    static func sendCoachMessage(body: APIModels.CoachMessageRequest) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/coach/message", method: .POST, body: body)
    }
}

// MARK: - Teams Endpoints
extension APIEndpoint {
    static func getTeamLeaderboard(teamId: String) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/teams/\(teamId)/leaderboard")
    }
}

// MARK: - Notification Endpoints
extension APIEndpoint {
    static func registerDeviceToken(body: APIModels.DeviceTokenRegistration) -> APIEndpoint {
        APIEndpoint(path: "/api/v1/notifications/device", method: .POST, body: body)
    }
}
