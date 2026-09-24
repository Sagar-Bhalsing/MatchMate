package com.sagar.matchmate.feature.match

import com.sagar.matchmate.core.mvi.UiIntent

sealed interface MatchMateIntent : UiIntent {

    data object LoadInitialMatches : MatchMateIntent

    data object LoadNextPage : MatchMateIntent

    data object RetryNextPage : MatchMateIntent

    data object Refresh : MatchMateIntent

    data class AcceptMatch(
        val matchId: String
    ) : MatchMateIntent

    data class DeclineMatch(
        val matchId: String
    ) : MatchMateIntent

    data class SelectTab(
        val tab: MatchTab
    ) : MatchMateIntent

    data class SelectGenderFilter(
        val filter: GenderFilter
    ) : MatchMateIntent

    data class UndoMatchAction(
        val matchId: String
    ) : MatchMateIntent
}