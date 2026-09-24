package com.sagar.matchmate.feature.match.model

object PaginationConfig {

    /**
     * Number of users requested per API page.
     */
    const val DEFAULT_PAGE_SIZE = 10

    /**
     * Number of items from the bottom of the list at which
     * we start loading the next page.
     *
     * Example:
     *
     * If the list contains 10 items and the user reaches
     * item 7, page 2 can start loading.
     */
    const val PREFETCH_DISTANCE = 3
}