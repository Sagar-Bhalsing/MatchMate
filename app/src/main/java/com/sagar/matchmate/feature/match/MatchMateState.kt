package com.sagar.matchmate.feature.match

import com.sagar.matchmate.core.mvi.UiState
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.feature.match.model.PaginationState
enum class MatchTab {
    DISCOVER,
    ACCEPTED,
    DECLINED
}

enum class GenderFilter {
    ALL,
    MEN,
    WOMEN
}

data class MatchMateState(
    val matches: List<Match> = emptyList(),
    val selectedTab: MatchTab = MatchTab.DISCOVER,
    val genderFilter: GenderFilter = GenderFilter.ALL,
    val pagination: PaginationState = PaginationState(),
    val initialError: String? = null,
    val isOffline: Boolean = false,
    val isShowingCachedData: Boolean = false
) : UiState {
    val discoverMatches: List<Match>
        get() = matches
            .filter { it.status.name == "PENDING" }
            .filterByGender(genderFilter)
    val acceptedMatches: List<Match>
        get() = matches
            .filter { it.status.name == "ACCEPTED" }
            .filterByGender(genderFilter)
    val declinedMatches: List<Match>
        get() = matches
            .filter { it.status.name == "DECLINED" }
            .filterByGender(genderFilter)
    val selectedMatches: List<Match>
        get() = when (selectedTab) {
            MatchTab.DISCOVER -> discoverMatches
            MatchTab.ACCEPTED -> acceptedMatches
            MatchTab.DECLINED -> declinedMatches
        }
    val acceptedCount: Int
        get() = matches.count { it.status.name == "ACCEPTED" }

    val declinedCount: Int
        get() = matches.count { it.status.name == "DECLINED" }
    private fun List<Match>.filterByGender(
        filter: GenderFilter
    ): List<Match> {
        return when (filter) {
            GenderFilter.ALL -> this
            GenderFilter.MEN -> filter { it.gender == "male" }
            GenderFilter.WOMEN -> filter { it.gender == "female" }
        }
    }
}