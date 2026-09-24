package com.sagar.matchmate.feature.match.model

/**
 * Represents the current pagination state.
 */
data class PaginationState(

    /**
     * Last page that was successfully loaded.
     *
     * 0 means no page has been loaded yet.
     */
    val currentPage: Int = 0,

    /**
     * Next page that should be requested.
     */
    val nextPage: Int = 1,

    /**
     * Number of records requested per page.
     */
    val pageSize: Int = PaginationConfig.DEFAULT_PAGE_SIZE,

    /**
     * Whether the server may contain another page.
     */
    val hasMore: Boolean = true,

    /**
     * Initial page request in progress.
     */
    val isInitialLoading: Boolean = false,

    /**
     * Next page request in progress.
     */
    val isLoadingNextPage: Boolean = false,

    /**
     * Refresh request in progress.
     */
    val isRefreshing: Boolean = false,

    /**
     * Error while loading the next page.
     */
    val nextPageError: String? = null,

    /**
     * Number of successfully loaded pages.
     */
    val loadedPages: Int = 0
) {

    /**
     * Whether any pagination operation is currently running.
     */
    val isAnyPageLoading: Boolean
        get() = isInitialLoading ||
                isLoadingNextPage ||
                isRefreshing
}