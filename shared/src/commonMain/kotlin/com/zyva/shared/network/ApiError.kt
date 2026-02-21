package com.zyva.shared.network

sealed class ApiError : Exception() {
    data object InvalidUrl : ApiError()
    data object InvalidResponse : ApiError()
    data object Unauthorized : ApiError()
    data object Forbidden : ApiError()
    data object NotFound : ApiError()
    data object RateLimited : ApiError()
    data class ServerError(val code: Int) : ApiError()
    data class HttpError(val code: Int) : ApiError()
    data class NetworkError(val reason: String) : ApiError()
    data class DecodingError(val reason: String) : ApiError()

    override val message: String
        get() = when (this) {
            is InvalidUrl -> "Invalid URL"
            is InvalidResponse -> "Invalid response from server"
            is Unauthorized -> "Authentication required. Please sign in again."
            is Forbidden -> "Access denied"
            is NotFound -> "Resource not found"
            is RateLimited -> "Too many requests. Please try again later."
            is ServerError -> "Server error ($code)"
            is HttpError -> "HTTP error ($code)"
            is NetworkError -> "Network error: $reason"
            is DecodingError -> "Data error: $reason"
        }
}
