package com.apexfit.shared.network

import com.apexfit.shared.network.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlin.math.pow

class ApexFitApiClient(
    private val baseUrl: String,
    private val tokenProvider: suspend () -> String?,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(this@ApexFitApiClient.json)
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }

    private suspend inline fun <reified T> executeWithRetry(
        maxRetries: Int = 3,
        crossinline block: suspend () -> HttpResponse,
    ): T {
        var lastException: Exception? = null
        for (attempt in 0..maxRetries) {
            try {
                val response = block()
                validateResponse(response)
                return response.body<T>()
            } catch (e: ApiError) {
                throw e
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries) {
                    val delayMs = (2.0.pow(attempt) * 1000).toLong()
                    delay(delayMs)
                }
            }
        }
        throw ApiError.NetworkError(lastException?.message ?: "Unknown error")
    }

    private fun validateResponse(response: HttpResponse) {
        when (response.status.value) {
            in 200..299 -> return
            401 -> throw ApiError.Unauthorized
            403 -> throw ApiError.Forbidden
            404 -> throw ApiError.NotFound
            429 -> throw ApiError.RateLimited
            in 500..599 -> throw ApiError.ServerError(response.status.value)
            else -> throw ApiError.HttpError(response.status.value)
        }
    }

    private suspend fun HttpRequestBuilder.applyAuth() {
        tokenProvider()?.let { bearerAuth(it) }
    }

    // MARK: - User Endpoints

    suspend fun getProfile(): UserProfileResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/users/me") { applyAuth() }
    }

    suspend fun updateProfile(body: UserProfileUpdateDto): UserProfileResponseDto = executeWithRetry {
        client.put("$baseUrl/api/v1/users/me") {
            applyAuth()
            setBody(body)
        }
    }

    // MARK: - Metrics Endpoints

    suspend fun syncMetrics(body: MetricsSyncRequestDto) {
        executeWithRetry<HttpResponse> {
            client.post("$baseUrl/api/v1/metrics/sync") {
                applyAuth()
                setBody(body)
            }
        }
    }

    suspend fun getDailyMetrics(from: String, to: String): MetricsListResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/metrics/daily") {
            applyAuth()
            parameter("from", from)
            parameter("to", to)
        }
    }

    // MARK: - Recovery Endpoints

    suspend fun getRecoveryHistory(days: Int): MetricsListResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/recovery/history") {
            applyAuth()
            parameter("days", days.toString())
        }
    }

    // MARK: - Strain Endpoints

    suspend fun getStrainHistory(days: Int): MetricsListResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/strain/history") {
            applyAuth()
            parameter("days", days.toString())
        }
    }

    // MARK: - Sleep Endpoints

    suspend fun getSleepHistory(days: Int): MetricsListResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/sleep/history") {
            applyAuth()
            parameter("days", days.toString())
        }
    }

    // MARK: - Workout Endpoints

    suspend fun syncWorkouts(body: WorkoutSyncRequestDto) {
        executeWithRetry<HttpResponse> {
            client.post("$baseUrl/api/v1/workouts/sync") {
                applyAuth()
                setBody(body)
            }
        }
    }

    // MARK: - Journal Endpoints

    suspend fun submitJournal(body: JournalSubmissionDto) {
        executeWithRetry<HttpResponse> {
            client.post("$baseUrl/api/v1/journal") {
                applyAuth()
                setBody(body)
            }
        }
    }

    suspend fun getJournalImpacts(): JournalImpactResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/journal/impacts") { applyAuth() }
    }

    // MARK: - Coach Endpoints

    suspend fun sendCoachMessage(body: CoachMessageRequestDto): CoachMessageResponseDto = executeWithRetry {
        client.post("$baseUrl/api/v1/coach/message") {
            applyAuth()
            setBody(body)
        }
    }

    // MARK: - Teams Endpoints

    suspend fun getTeamLeaderboard(teamId: String): LeaderboardResponseDto = executeWithRetry {
        client.get("$baseUrl/api/v1/teams/$teamId/leaderboard") { applyAuth() }
    }

    // MARK: - Notification Endpoints

    suspend fun registerDeviceToken(body: DeviceTokenRegistrationDto) {
        executeWithRetry<HttpResponse> {
            client.post("$baseUrl/api/v1/notifications/device") {
                applyAuth()
                setBody(body)
            }
        }
    }

    fun close() {
        client.close()
    }
}
