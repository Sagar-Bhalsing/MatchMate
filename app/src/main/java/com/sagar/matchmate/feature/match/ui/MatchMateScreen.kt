package com.sagar.matchmate.feature.match.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sagar.matchmate.feature.match.GenderFilter
import com.sagar.matchmate.feature.match.MatchMateEffect
import com.sagar.matchmate.feature.match.MatchMateIntent
import com.sagar.matchmate.feature.match.MatchMateViewModel
import com.sagar.matchmate.feature.match.MatchTab
import com.sagar.matchmate.ui.theme.MatchMateBackground
import com.sagar.matchmate.ui.theme.MatchMateDivider
import com.sagar.matchmate.ui.theme.MatchMateError
import com.sagar.matchmate.ui.theme.MatchMateErrorSoft
import com.sagar.matchmate.ui.theme.MatchMatePink
import com.sagar.matchmate.ui.theme.MatchMatePinkDark
import com.sagar.matchmate.ui.theme.MatchMatePinkSoft
import com.sagar.matchmate.ui.theme.MatchMateSurface
import com.sagar.matchmate.ui.theme.MatchMateTextPrimary
import com.sagar.matchmate.ui.theme.MatchMateTextSecondary
import com.sagar.matchmate.ui.theme.MatchMateYellowSoft
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchMateScreen(
    viewModel: MatchMateViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    /*
     * Initial load
     */
    LaunchedEffect(Unit) {
        viewModel.sendIntent(
            MatchMateIntent.LoadInitialMatches
        )
    }

    /*
     * One-time effects
     */
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MatchMateEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MatchMateEffect.ShowSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()

                    launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MatchMateBackground,
        topBar = {
            MatchMateTopBar(
                selectedTab = uiState.selectedTab,
                isRefreshing = uiState.pagination.isRefreshing,
                onRefresh = {
                    viewModel.sendIntent(
                        MatchMateIntent.Refresh
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isOffline) {
                OfflineBanner()
            }
            MatchTabs(
                selectedTab = uiState.selectedTab,
                acceptedCount = uiState.acceptedCount,
                declinedCount = uiState.declinedCount,
                onTabSelected = { tab ->
                    viewModel.sendIntent(
                        MatchMateIntent.SelectTab(tab)
                    )
                }
            )
            // Gender filters
            GenderFilters(
                selectedFilter = uiState.genderFilter,
                onFilterSelected = { filter ->
                    viewModel.sendIntent(
                        MatchMateIntent.SelectGenderFilter(filter)
                    )
                }
            )
            if (
                uiState.pagination.isInitialLoading &&
                uiState.matches.isEmpty()
            ) {
                InitialLoading()
            } else if (
                uiState.initialError != null &&
                uiState.matches.isEmpty()
            ) {
                ErrorState(
                    message = uiState.initialError
                        ?: "Something went wrong.",
                    onRetry = {
                        viewModel.sendIntent(
                            MatchMateIntent.LoadInitialMatches
                        )
                    }
                )
            }
            //Matches
            else {
                MatchList(
                    matches = uiState.selectedMatches,
                    pagination = uiState.pagination,
                    selectedTab = uiState.selectedTab,
                    onAccept = { matchId ->
                        viewModel.sendIntent(
                            MatchMateIntent.AcceptMatch(matchId)
                        )
                    },

                    onDecline = { matchId ->
                        viewModel.sendIntent(
                            MatchMateIntent.DeclineMatch(matchId)
                        )
                    },

                    onRetryNextPage = {
                        viewModel.sendIntent(
                            MatchMateIntent.RetryNextPage
                        )
                    },

                    onLoadNextPage = {
                        viewModel.sendIntent(
                            MatchMateIntent.LoadNextPage
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchMateTopBar(
    selectedTab: MatchTab,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val title = when (selectedTab) {
        MatchTab.DISCOVER -> "Discover Matches"
        MatchTab.ACCEPTED -> "Accepted Matches"
        MatchTab.DECLINED -> "Declined Matches"
    }

    val subtitle = when (selectedTab) {
        MatchTab.DISCOVER ->
            "Find people who might be a great match"

        MatchTab.ACCEPTED ->
            "People you've accepted"

        MatchTab.DECLINED ->
            "People you've declined"
    }

    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MatchMateBackground,
            titleContentColor = MatchMateTextPrimary,
            navigationIconContentColor = MatchMateTextPrimary,
            actionIconContentColor = MatchMateTextPrimary
        ),
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MatchMateTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MatchMateTextSecondary
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MatchMatePink,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh matches",
                        tint = MatchMatePink
                    )
                }
            }
        }
    )
}

@Composable
private fun MatchTabs(
    selectedTab: MatchTab,
    acceptedCount: Int,
    declinedCount: Int,
    onTabSelected: (MatchTab) -> Unit
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MatchMateBackground,
        contentColor = MatchMatePink,
        divider = {
            androidx.compose.material3.HorizontalDivider(
                color = MatchMateDivider
            )
        },
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(selectedTab.ordinal),
                width = 42.dp,
                color = MatchMatePink
            )
        }
    ) {
        Tab(
            selected = selectedTab == MatchTab.DISCOVER,
            onClick = {
                onTabSelected(MatchTab.DISCOVER)
            },
            selectedContentColor = MatchMatePink,
            unselectedContentColor = MatchMateTextSecondary,
            text = {
                Text(
                    text = "Discover"
                )
            }
        )
        Tab(
            selected = selectedTab == MatchTab.ACCEPTED,
            onClick = {
                onTabSelected(MatchTab.ACCEPTED)
            },
            selectedContentColor = MatchMatePink,
            unselectedContentColor = MatchMateTextSecondary,
            text = {
                Text(
                    text = if (acceptedCount > 0) {
                        "Accepted $acceptedCount"
                    } else {
                        "Accepted"
                    }
                )
            }
        )
        Tab(
            selected = selectedTab == MatchTab.DECLINED,
            onClick = {
                onTabSelected(MatchTab.DECLINED)
            },
            selectedContentColor = MatchMatePink,
            unselectedContentColor = MatchMateTextSecondary,
            text = {
                Text(
                    text = if (declinedCount > 0) {
                        "Declined $declinedCount"
                    } else {
                        "Declined"
                    }
                )
            }
        )
    }
}

@Composable
private fun GenderFilters(
    selectedFilter: GenderFilter,
    onFilterSelected: (GenderFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GenderFilter.entries.forEach { filter ->
            val selected = selectedFilter == filter
            FilterChip(
                selected = selected,
                onClick = {
                    onFilterSelected(filter)
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MatchMateSurface,
                    labelColor = MatchMateTextSecondary,
                    selectedContainerColor = MatchMatePinkSoft,
                    selectedLabelColor = MatchMatePinkDark,
                    disabledContainerColor = MatchMateSurface,
                    disabledLabelColor = MatchMateTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = MatchMateDivider,
                    selectedBorderColor = MatchMatePink
                ),
                label = {
                    Text(
                        text = when (filter) {
                            GenderFilter.ALL -> "All"
                            GenderFilter.MEN -> "Men"
                            GenderFilter.WOMEN -> "Women"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun OfflineBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MatchMateYellowSoft
    ) {
        Text(
            text = "You're offline • Showing saved matches",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 9.dp
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MatchMateTextPrimary
        )
    }
}

@Composable
private fun InitialLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(
                color = MatchMatePink,
                strokeWidth = 3.dp
            )
            Text(
                text = "Finding matches...",

                style = MaterialTheme.typography.bodyMedium,

                color = MatchMateTextSecondary
            )
        }
    }
}
private suspend fun SnackbarHostState.showLatestSnackbar(
    message: String
) {
    currentSnackbarData?.dismiss()

    showSnackbar(
        message = message,
        duration = SnackbarDuration.Short
    )
}
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MatchMateErrorSoft
            ) {
                Text(
                    text = "!",
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MatchMateError
                )
            }
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MatchMateTextPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MatchMateTextSecondary
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatchMatePink,
                    contentColor = MatchMateSurface
                )
            ) {
                Text("Try again")
            }
        }
    }
}