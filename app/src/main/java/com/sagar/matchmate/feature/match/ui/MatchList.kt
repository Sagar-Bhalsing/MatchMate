package com.sagar.matchmate.feature.match.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.feature.match.model.PaginationConfig
import com.sagar.matchmate.feature.match.model.PaginationState
import com.sagar.matchmate.feature.match.MatchTab
import com.sagar.matchmate.ui.theme.MatchMateError
import com.sagar.matchmate.ui.theme.MatchMateErrorSoft
import com.sagar.matchmate.ui.theme.MatchMatePink
import com.sagar.matchmate.ui.theme.MatchMatePinkSoft
import com.sagar.matchmate.ui.theme.MatchMateSurface
import com.sagar.matchmate.ui.theme.MatchMateTextPrimary
import com.sagar.matchmate.ui.theme.MatchMateTextSecondary
import com.sagar.matchmate.ui.theme.MatchMateGreen
import com.sagar.matchmate.ui.theme.MatchMateGreenSoft
import com.sagar.matchmate.ui.theme.MatchMateRed
import com.sagar.matchmate.ui.theme.MatchMateRedSoft
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun MatchList(
    matches: List<Match>,
    pagination: PaginationState,
    selectedTab: MatchTab,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onRetryNextPage: () -> Unit,
    onLoadNextPage: () -> Unit
) {
    val discoverListState = rememberLazyListState()
    val acceptedListState = rememberLazyListState()
    val declinedListState = rememberLazyListState()

    val listState = when (selectedTab) {
        MatchTab.DISCOVER -> discoverListState
        MatchTab.ACCEPTED -> acceptedListState
        MatchTab.DECLINED -> declinedListState
    }
    /*
     * Trigger pagination only for Discover.
     *
     * Accepted and Declined are locally filtered
     * views of the already loaded matches, so they
     * should not trigger additional API calls.
     */
    LaunchedEffect(
        listState,
        matches.size,
        selectedTab
    ) {

        if (selectedTab != MatchTab.DISCOVER) {
            return@LaunchedEffect
        }

        snapshotFlow {

            val layoutInfo = listState.layoutInfo

            layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?.index ?: -1
        }
            .distinctUntilChanged()
            .filter { lastVisibleIndex ->

                matches.isNotEmpty() && pagination.hasMore && !pagination.isLoadingNextPage && lastVisibleIndex >=
                        matches.lastIndex -
                        PaginationConfig.PREFETCH_DISTANCE
            }
            .collect {
                onLoadNextPage()
            }
    }

    /*
     * Empty state
     */
    if (matches.isEmpty()) {

        EmptyMatchesState(
            selectedTab = selectedTab
        )

        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),

        state = listState,

        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 24.dp
        ),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = matches,
            key = { it.id }
        ) { match ->

            MatchCard(
                match = match,

                onAccept = {
                    onAccept(match.id)
                },

                onDecline = {
                    onDecline(match.id)
                }
            )
        }

        /*
         * Pagination error
         */
        if (
            selectedTab == MatchTab.DISCOVER &&
            pagination.nextPageError != null
        ) {

            item(
                key = "pagination_error"
            ) {

                PaginationError(
                    message = "Unable to refresh matches",
                    onRetry = onRetryNextPage
                )
            }
        }

        /*
         * Pagination loader
         */
        if (
            selectedTab == MatchTab.DISCOVER &&
            pagination.isLoadingNextPage
        ) {

            item(
                key = "pagination_loader"
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,

                        verticalArrangement = Arrangement.spacedBy(
                            8.dp
                        )
                    ) {

                        CircularProgressIndicator(
                            color = MatchMatePink,
                            strokeWidth = 2.5.dp
                        )

                        Text(
                            text = "Finding more matches...",

                            style = MaterialTheme.typography.bodySmall,

                            color = MatchMateTextSecondary
                        )
                    }
                }
            }
        }

        /*
         * End of results
         */
        if (
            selectedTab == MatchTab.DISCOVER &&
            !pagination.hasMore &&
            !pagination.isLoadingNextPage
        ) {

            item(
                key = "end_of_results"
            ) {

                EndOfResults()
            }
        }
    }
}

@Composable
private fun EmptyMatchesState(
    selectedTab: MatchTab
) {
    val title = when (selectedTab) {
        MatchTab.DISCOVER ->
            "No matches found"

        MatchTab.ACCEPTED ->
            "No accepted matches yet"

        MatchTab.DECLINED ->
            "No declined matches"
    }

    val description = when (selectedTab) {
        MatchTab.DISCOVER ->
            "Try changing your gender filter."

        MatchTab.ACCEPTED ->
            "Accept someone from Discover and they'll appear here."

        MatchTab.DECLINED ->
            "People you decline will appear here."
    }

    val accentColor = when (selectedTab) {
        MatchTab.DISCOVER -> MatchMatePink
        MatchTab.ACCEPTED -> MatchMateGreen
        MatchTab.DECLINED -> MatchMateRed
    }

    val accentBackground = when (selectedTab) {
        MatchTab.DISCOVER -> MatchMatePinkSoft
        MatchTab.ACCEPTED -> MatchMateGreenSoft
        MatchTab.DECLINED -> MatchMateRedSoft
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(
                12.dp
            )
        ) {

            /*
             * Simple visual accent.
             *
             * We can replace this later with a
             * proper illustration/icon if desired.
             */
            Surface(
                shape = MaterialTheme.shapes.large,
                color = accentBackground
            ) {

                Box(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
                ) {

                    Text(
                        text = when (selectedTab) {
                            MatchTab.DISCOVER -> "♥"
                            MatchTab.ACCEPTED -> "✓"
                            MatchTab.DECLINED -> "×"
                        },

                        style = MaterialTheme.typography.headlineSmall,

                        color = accentColor
                    )
                }
            }

            Text(
                text = title,

                style = MaterialTheme.typography.titleMedium,

                color = MatchMateTextPrimary,

                textAlign = TextAlign.Center
            )

            Text(
                text = description,

                style = MaterialTheme.typography.bodyMedium,

                color = MatchMateTextSecondary,

                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PaginationError(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.spacedBy(
            10.dp
        )
    ) {

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MatchMateErrorSoft
        ) {

            Text(
                text = message,

                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),

                style = MaterialTheme.typography.bodySmall,

                color = MatchMateError,

                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onRetry,

            colors = ButtonDefaults.buttonColors(
                containerColor = MatchMatePink,

                contentColor = MatchMateSurface
            )
        ) {
            Text("Retry")
        }
    }
}

@Composable
private fun EndOfResults() {
    Text(
        text = "You've reached the end of your matches",

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 20.dp
            ),

        textAlign = TextAlign.Center,

        style = MaterialTheme.typography.bodySmall,

        color = MatchMateTextSecondary
    )
}