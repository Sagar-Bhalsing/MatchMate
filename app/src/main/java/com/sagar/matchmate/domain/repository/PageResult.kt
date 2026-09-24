package com.sagar.matchmate.domain.repository

data class PageResult(

    /**
     * Page requested from the API.
     */
    val page: Int,

    /**
     * Number of users returned by the API.
     */
    val itemCount: Int,

    /**
     * Whether another page can be requested.
     */
    val hasMore: Boolean
)