package com.sagar.matchmate.domain.model

enum class MatchStatus {

    /**
     * User hasn't made a decision yet.
     */
    PENDING,

    /**
     * User accepted this match.
     */
    ACCEPTED,

    /**
     * User declined this match.
     */
    DECLINED
}