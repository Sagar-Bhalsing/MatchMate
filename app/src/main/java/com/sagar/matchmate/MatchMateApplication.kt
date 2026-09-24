package com.sagar.matchmate

import android.app.Application
import com.sagar.matchmate.di.AppContainer

class MatchMateApplication : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}