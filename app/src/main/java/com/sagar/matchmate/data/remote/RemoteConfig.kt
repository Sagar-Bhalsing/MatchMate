package com.sagar.matchmate.data.remote

object RemoteConfig {

    const val BASE_URL = "https://randomuser.me/"

    /**
     * Fixed seed gives us a deterministic dataset.
     */
    const val MATCH_SEED = "matchmate"

    /**
     * Number of profiles requested per page.
     */
    const val PAGE_SIZE = 10

    /**
     * Maximum number of pages for this assignment.
     *
     * 5 × 10 = 50 profiles.
     */
    const val MAX_PAGES = 5
}