package com.sagar.matchmate.feature.match

import com.sagar.matchmate.core.mvi.BaseViewModel
import com.sagar.matchmate.core.network.NetworkMonitor
import com.sagar.matchmate.domain.model.MatchStatus
import com.sagar.matchmate.domain.repository.MatchRepository
import kotlinx.coroutines.flow.collectLatest
 import kotlinx.coroutines.launch

class MatchMateViewModel(
    private val repository: MatchRepository,
    private val networkMonitor: NetworkMonitor
) : BaseViewModel<MatchMateIntent, MatchMateState, MatchMateEffect>(
    initialState = MatchMateState()
) {

    init {
        observeMatches()
        observeNetwork()
        observeIntents()
    }

    private fun observeNetwork() {
        scope.launch {
            networkMonitor.isConnected
                .collectLatest { isConnected ->

                    val wasOffline = uiState.value.isOffline

                    setState {
                        copy(
                            isOffline = !isConnected
                        )
                    }

                    /*
                     * Connection has just returned.
                     */
                    if (isConnected && wasOffline) {
                        syncPendingActions()
                    }
                }
        }
    }
    private suspend fun syncPendingActions() {
        runCatching {
            repository.syncPendingActions()
        }.onFailure { throwable ->

            sendEffect(
                MatchMateEffect.ShowSnackbar(
                    throwable.message ?: "Unable to sync pending changes."
                )
            )
        }
    }
    private fun observeMatches() {
        scope.launch {
            repository.observeMatches()
                .collectLatest { matches ->
                    setState {
                        copy(
                            matches = matches
                        )
                    }
                }
        }
    }

    override suspend fun handleIntent(intent: MatchMateIntent) {
        when (intent) {
            is MatchMateIntent.SelectTab -> selectTab(intent.tab)

            is MatchMateIntent.SelectGenderFilter -> selectGenderFilter(intent.filter)

            is MatchMateIntent.UndoMatchAction -> undoMatchAction(intent.matchId)

            MatchMateIntent.LoadInitialMatches -> {
                loadInitialMatches()
            }

            MatchMateIntent.LoadNextPage -> {
                loadNextPage()
            }

            MatchMateIntent.RetryNextPage -> {
                loadNextPage()
            }

            MatchMateIntent.Refresh -> {
                refresh()
            }

            is MatchMateIntent.AcceptMatch -> {
                updateMatchStatus(
                    matchId = intent.matchId,
                    status = MatchStatus.ACCEPTED
                )
            }

            is MatchMateIntent.DeclineMatch -> {
                updateMatchStatus(
                    matchId = intent.matchId,
                    status = MatchStatus.DECLINED
                )
            }
        }
    }

    private suspend fun loadInitialMatches() {

        val currentState = uiState.value

        if (currentState.pagination.isInitialLoading) {
            return
        }

        setState {
            copy(
                initialError = null,
                pagination = pagination.copy(
                    isInitialLoading = true,
                    nextPageError = null
                )
            )
        }

        runCatching {

            val lastCachedPage = repository.getLastCachedPage()

            val result = repository.loadInitialPage()

            Pair(lastCachedPage, result)

        }.onSuccess { (lastCachedPage, result) ->

            val latestState = uiState.value

            /*
             * If cached pages already existed, don't reset pagination
             * back to page 2 after refreshing page 1.
             *
             * Example:
             * Cached pages = 1,2,3
             * Refresh page 1
             * Next page should still be 4.
             */
            val nextPage = maxOf(
                lastCachedPage,
                result.page
            ) + 1

            setState {
                latestState.copy(
                    initialError = null,
                    isOffline = false,
                    isShowingCachedData = false,
                    pagination = latestState.pagination.copy(
                        currentPage = result.page,
                        nextPage = nextPage,
                        hasMore = result.hasMore,
                        isInitialLoading = false,
                        nextPageError = null,
                        loadedPages = maxOf(
                            lastCachedPage,
                            result.page
                        )
                    )
                )
            }

        }.onFailure { throwable ->

            val latestState = uiState.value
            val hasCachedMatches = latestState.matches.isNotEmpty()

            setState {
                latestState.copy(
                    initialError = if (hasCachedMatches) {
                        null
                    } else {
                        throwable.message ?: "Unable to load matches."
                    },
                    isOffline = hasCachedMatches,
                    isShowingCachedData = hasCachedMatches,
                    pagination = latestState.pagination.copy(
                        isInitialLoading = false
                    )
                )
            }

            if (hasCachedMatches) {
                sendEffect(
                    MatchMateEffect.ShowSnackbar(
                        "You're offline. Showing cached matches."
                    )
                )
            }
        }
    }

    private fun selectTab(tab: MatchTab) {
        setState {
            copy(selectedTab = tab)
        }
    }

    private fun selectGenderFilter(filter: GenderFilter) {
        setState {
            copy(genderFilter = filter)
        }
    }
    private suspend fun loadNextPage() {
        val currentPagination = uiState.value.pagination

        if (
            currentPagination.isLoadingNextPage ||
            currentPagination.isRefreshing ||
            !currentPagination.hasMore
        ) {
            return
        }

        val page = currentPagination.nextPage

        setState {
            copy(
                pagination = pagination.copy(
                    isLoadingNextPage = true,
                    nextPageError = null
                )
            )
        }

        runCatching {
            repository.loadNextPage(
                page = page,
                pageSize = currentPagination.pageSize
            )
        }.onSuccess { result ->

            val latestState = uiState.value

            setState {
                latestState.copy(
                    isOffline = false,
                    isShowingCachedData = false,
                    pagination = latestState.pagination.copy(
                        currentPage = result.page,
                        nextPage = result.page + 1,
                        hasMore = result.hasMore,
                        isLoadingNextPage = false,
                        nextPageError = null,
                        loadedPages = latestState.pagination.loadedPages + 1
                    )
                )
            }
        }.onFailure { throwable ->
            setState {
                copy(
                    pagination = pagination.copy(
                        isLoadingNextPage = false,
                        nextPageError =
                            throwable.message ?: "Unable to load more matches."
                    )
                )
            }
        }
    }

    private suspend fun undoMatchAction(matchId: String) {
        runCatching {
            repository.updateMatchStatus(
                matchId = matchId,
                status = MatchStatus.PENDING
            )
        }.onFailure { throwable ->
            sendEffect(
                MatchMateEffect.ShowSnackbar(
                    throwable.message ?: "Unable to undo action."
                )
            )
        }
    }

    private suspend fun refresh() {
        val currentPagination = uiState.value.pagination

        if (
            currentPagination.isRefreshing ||
            currentPagination.isInitialLoading ||
            currentPagination.isLoadingNextPage
        ) {
            return
        }
        setState {
            copy(
                initialError = null,
                pagination = pagination.copy(
                    isRefreshing = true,
                    nextPageError = null
                )
            )
        }

        runCatching {
            val result = repository.refresh()
            val lastCachedPage = repository.getLastCachedPage()

            Pair(result, lastCachedPage)
        }.onSuccess { (result, lastCachedPage) ->

            val latestState = uiState.value

            setState {
                latestState.copy(
                    isOffline = false,
                    isShowingCachedData = false,
                    pagination = latestState.pagination.copy(
                        currentPage = result.page,
                        nextPage = lastCachedPage + 1,
                        hasMore = result.hasMore,
                        isRefreshing = false,
                        nextPageError = null,
                        loadedPages = lastCachedPage
                    )
                )
            }
        }.onFailure { throwable ->

            setState {
                copy(
                    pagination = pagination.copy(
                        isRefreshing = false
                    ),
                    isOffline = true
                )
            }

            sendEffect(
                MatchMateEffect.ShowSnackbar(
                    "Unable to refresh matches."
                )
            )
        }
    }

    private suspend fun updateMatchStatus(
        matchId: String,
        status: MatchStatus
    ) {
        runCatching {
            repository.updateMatchStatus(
                matchId = matchId,
                status = status
            )
        }.onSuccess {

            val message = when (status) {
                MatchStatus.ACCEPTED -> "Added to Accepted"
                MatchStatus.DECLINED -> "Moved to Declined"
                MatchStatus.PENDING -> "Moved back to Discover"
            }

            sendEffect(
                MatchMateEffect.ShowSnackbar(
                    message = message,
                )
            )

        }.onFailure { throwable ->

            sendEffect(
                MatchMateEffect.ShowSnackbar(
                    throwable.message ?: "Unable to update match."
                )
            )
        }
    }
}