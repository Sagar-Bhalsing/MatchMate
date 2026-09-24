package com.sagar.matchmate.feature.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sagar.matchmate.core.network.NetworkMonitor
import com.sagar.matchmate.domain.repository.MatchRepository

class MatchMateViewModelFactory(
    private val repository: MatchRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(MatchMateViewModel::class.java)) {
            return MatchMateViewModel(
                repository = repository,
                networkMonitor = networkMonitor
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}