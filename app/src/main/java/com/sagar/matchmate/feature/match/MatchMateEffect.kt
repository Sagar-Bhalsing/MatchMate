package com.sagar.matchmate.feature.match

import com.sagar.matchmate.core.mvi.UiEffect
sealed interface MatchMateEffect : UiEffect {
    data class ShowSnackbar(
        val message: String
    ) : MatchMateEffect
}