package com.sagar.matchmate.core.network

/**
 * Sealed class representing API execution states.
 */
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}
