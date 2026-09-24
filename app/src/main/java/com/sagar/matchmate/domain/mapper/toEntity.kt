package com.sagar.matchmate.domain.mapper


import com.sagar.matchmate.data.local.entity.MatchEntity
import com.sagar.matchmate.data.remote.dto.RandomUserDto
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.domain.model.MatchStatus

/**
 * Converts a RandomUser API response into a Room entity.
 *
 * The status is intentionally supplied from outside.
 *
 * This is important because the API knows nothing about
 * our local Accept/Decline state.
 */
fun MatchEntity.toDomain(): Match = Match(
    id = id,
    gender = gender,
    firstName = firstName,
    lastName = lastName,
    age = age,
    city = city,
    state = state,
    country = country,
    imageUrl = imageUrl,
    email = email,
    phone = phone,
    status = status.toMatchStatus()
)

fun String.toMatchStatus(): MatchStatus {

    return when (this) {

        MatchStatus.ACCEPTED.name ->
            MatchStatus.ACCEPTED

        MatchStatus.DECLINED.name ->
            MatchStatus.DECLINED

        else ->
            MatchStatus.PENDING
    }
}

fun RandomUserDto.toEntity(
    page: Int,
    status: MatchStatus = MatchStatus.PENDING,
    now: Long
): MatchEntity = MatchEntity(
    id = login.uuid,
    gender = gender,
    firstName = name.first,
    lastName = name.last,
    age = dateOfBirth.age,
    city = location.city,
    state = location.state,
    country = location.country,
    imageUrl = picture.large,
    email = email,
    phone = phone,
    status = status.name,
    page = page,
    updatedAt = now
)