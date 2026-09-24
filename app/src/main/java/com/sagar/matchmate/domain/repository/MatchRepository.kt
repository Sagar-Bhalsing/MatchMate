package com.sagar.matchmate.domain.repository

import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.domain.model.MatchStatus
import kotlinx.coroutines.flow.Flow

interface MatchRepository {

    /**
     * Observe locally cached matches.
     *
     * Room is the source of truth for the UI.
     */
    fun observeMatches(): Flow<List<Match>>
    suspend fun getLastCachedPage(): Int

    /**
     * Load the first page.
     */
    suspend fun loadInitialPage(): PageResult

    /**
     * Load a specific page.
     *
     * The ViewModel controls WHICH page should be loaded.
     * The Repository controls HOW that page is fetched
     * and persisted.
     */
    suspend fun loadNextPage(
        page: Int,
        pageSize: Int
    ): PageResult

    /**
     * Refresh page 1 while preserving local decisions.
     */
    suspend fun refresh(): PageResult

    /**
     * Update the local Accept/Decline decision.
     */
    suspend fun updateMatchStatus(
        matchId: String,
        status: MatchStatus
    )

    /**
     * Synchronize locally queued actions when a real
     * backend mutation API is available.
     */
    suspend fun syncPendingActions()
}