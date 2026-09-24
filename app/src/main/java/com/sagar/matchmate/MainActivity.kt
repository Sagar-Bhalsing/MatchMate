package com.sagar.matchmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.sagar.matchmate.feature.match.MatchMateViewModel
import com.sagar.matchmate.feature.match.MatchMateViewModelFactory
import com.sagar.matchmate.feature.match.ui.MatchMateScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MatchMateViewModel by viewModels {
        val container =
            (application as MatchMateApplication).appContainer

        MatchMateViewModelFactory(
            repository = container.matchRepository,
            networkMonitor = container.networkMonitor
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MatchMateScreen(
                viewModel = viewModel
            )
        }
    }
}