package com.sagar.matchmate.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Root response returned by randomuser.me.
 *
 * Example:
 *
 * {
 *   "results": [
 *      {
 *          ...
 *      }
 *   ],
 *   "info": {
 *      ...
 *   }
 * }
 */
data class RandomUserResponseDto(

    @SerializedName("results")
    val results: List<RandomUserDto>,

    @SerializedName("info")
    val info: RandomUserInfoDto
)

/**
 * Represents a single user returned by RandomUser API.
 */
data class RandomUserDto(

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("name")
    val name: RandomUserNameDto,

    @SerializedName("location")
    val location: RandomUserLocationDto,

    @SerializedName("login")
    val login: RandomUserLoginDto,

    @SerializedName("dob")
    val dateOfBirth: RandomUserDobDto,

    @SerializedName("picture")
    val picture: RandomUserPictureDto,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone")
    val phone: String?
)

/**
 * User's name information.
 */
data class RandomUserNameDto(

    @SerializedName("title")
    val title: String?,

    @SerializedName("first")
    val first: String,

    @SerializedName("last")
    val last: String
)

/**
 * User location information.
 */
data class RandomUserLocationDto(

    @SerializedName("city")
    val city: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("country")
    val country: String
)

/**
 * Login information.
 *
 * uuid is the most useful identifier for our local database.
 */
data class RandomUserLoginDto(

    @SerializedName("uuid")
    val uuid: String
)

/**
 * Date of birth information.
 */
data class RandomUserDobDto(

    @SerializedName("date")
    val date: String,

    @SerializedName("age")
    val age: Int
)

/**
 * Profile image URLs.
 */
data class RandomUserPictureDto(

    @SerializedName("large")
    val large: String,

    @SerializedName("medium")
    val medium: String,

    @SerializedName("thumbnail")
    val thumbnail: String
)

/**
 * Metadata returned by RandomUser API.
 */
data class RandomUserInfoDto(

    @SerializedName("seed")
    val seed: String,

    @SerializedName("results")
    val results: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("version")
    val version: String
)