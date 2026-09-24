package com.sagar.matchmate.di

import android.content.Context
import androidx.room.Room
import com.sagar.matchmate.core.network.NetworkMonitor
import com.sagar.matchmate.data.local.database.MatchDatabase
import com.sagar.matchmate.data.remote.RetrofitProvider
import com.sagar.matchmate.data.repository.MatchRepositoryImpl
import com.sagar.matchmate.domain.repository.MatchRepository

class AppContainer(
    context: Context
) {
    private val database: MatchDatabase by lazy {
        Room.databaseBuilder(
                context,
                MatchDatabase::class.java,
                "match_mate.db"
            ).fallbackToDestructiveMigration()
            .build()
    }
    private val randomUserApi by lazy {
        RetrofitProvider.randomUserApi
    }
    val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(context)
    }
    val matchRepository: MatchRepository by lazy {
        MatchRepositoryImpl(
            api = randomUserApi,
            database = database
        )
    }
}