package com.sagar.matchmate.domain.model

/**
 * Represents a matrimonial match inside the domain layer.
 *
 * The domain layer does not know whether this data came from
 * Retrofit, Room, cache, or any other source.
 */
data class Match(
    val id: String,
    val gender: String?,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageUrl: String,
    val email: String?,
    val phone: String?,
    val status: MatchStatus
)