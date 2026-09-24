package com.sagar.matchmate.data.remote

import com.sagar.matchmate.data.remote.RemoteConfig.BASE_URL
import com.sagar.matchmate.data.remote.api.RandomUserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {
    private val retrofit: Retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }
    val randomUserApi: RandomUserApi by lazy {

        retrofit.create(
            RandomUserApi::class.java
        )
    }
}