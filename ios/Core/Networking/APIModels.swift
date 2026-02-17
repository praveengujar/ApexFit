import Foundation

enum APIModels {
    // MARK: - User
    struct UserProfileUpdate: Encodable {
        let displayName: String?
        let dateOfBirth: Date?
        let biologicalSex: String?
        let heightCM: Double?
        let weightKG: Double?
        let maxHeartRate: Int?
        let sleepBaselineHours: Double?
        let preferredUnits: String?
    }

    struct UserProfileResponse: Decodable {
        let id: String
        let firebaseUid: String
        let displayName: String
        let email: String?
        let dateOfBirth: Date?
        let biologicalSex: String
        let createdAt: Date
    }

    // MARK: - Metrics Sync
    struct MetricsSyncRequest: Encodable {
        let date: Date
        let recoveryScore: Double?
        let recoveryZone: String?
        let strainScore: Double
        let sleepPerformance: Double?
        let hrvRmssd: Double?
        let hrvSdnn: Double?
        let restingHeartRate: Double?
        let respiratoryRate: Double?
        let spo2: Double?
        let steps: Int
        let activeCalories: Double
        let vo2Max: Double?
        let sleepDurationHours: Double?
        let sleepNeedHours: Double?
        let workouts: [WorkoutSyncData]
    }

    struct WorkoutSyncData: Encodable {
        let workoutType: String
        let workoutName: String
        let startDate: Date
        let endDate: Date
        let strainScore: Double
        let averageHeartRate: Double?
        let maxHeartRate: Double?
        let activeCalories: Double
        let zone1Minutes: Double
        let zone2Minutes: Double
        let zone3Minutes: Double
        let zone4Minutes: Double
        let zone5Minutes: Double
    }

    struct WorkoutSyncRequest: Encodable {
        let workouts: [WorkoutSyncData]
    }

    struct DailyMetricResponse: Decodable {
        let date: Date
        let recoveryScore: Double?
        let strainScore: Double
        let sleepPerformance: Double?
        let hrvRmssd: Double?
        let restingHeartRate: Double?
        let steps: Int
        let activeCalories: Double
    }

    struct MetricsListResponse: Decodable {
        let metrics: [DailyMetricResponse]
        let pagination: PaginationInfo
    }

    // MARK: - Journal
    struct JournalSubmission: Encodable {
        let date: Date
        let responses: [JournalResponseData]
    }

    struct JournalResponseData: Encodable {
        let behaviorId: String
        let behaviorName: String
        let category: String
        let responseType: String
        let toggleValue: Bool?
        let numericValue: Double?
        let scaleValue: String?
    }

    struct JournalImpactResponse: Decodable {
        let impacts: [BehaviorImpact]
    }

    struct BehaviorImpact: Decodable {
        let behaviorName: String
        let metricName: String
        let effectSize: Double
        let isSignificant: Bool
        let direction: String
        let sampleSize: Int
    }

    // MARK: - Coach
    struct CoachMessageRequest: Encodable {
        let message: String
        let screenContext: String?
        let conversationId: String?
        let includeDataWindow: String?
    }

    struct CoachMessageResponse: Decodable {
        let response: String
        let dataCitations: [DataCitation]?
        let followUpSuggestions: [String]?
        let confidence: Double?
    }

    struct DataCitation: Decodable {
        let metric: String
        let value: Double?
        let baseline: Double?
        let delta: String?
    }

    // MARK: - Teams
    struct LeaderboardResponse: Decodable {
        let teamId: String
        let teamName: String
        let entries: [LeaderboardEntry]
    }

    struct LeaderboardEntry: Decodable {
        let userId: String
        let displayName: String
        let recoveryScore: Double?
        let strainScore: Double?
        let rank: Int
    }

    // MARK: - Notifications
    struct DeviceTokenRegistration: Encodable {
        let token: String
        let platform: String = "ios"
    }

    // MARK: - Common
    struct PaginationInfo: Decodable {
        let page: Int
        let pageSize: Int
        let totalCount: Int
        let hasMore: Bool
    }

    struct ErrorResponse: Decodable {
        let error: String
        let message: String
        let statusCode: Int
    }
}
