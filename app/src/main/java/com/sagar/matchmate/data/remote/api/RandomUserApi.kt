package com.sagar.matchmate.data.remote.api

import com.sagar.matchmate.data.remote.dto.RandomUserResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserApi {

    @GET("api/")
    suspend fun getUsers(
        @Query("results") results: Int,
        @Query("page") page: Int,
        @Query("seed") seed: String
    ): RandomUserResponseDto
}