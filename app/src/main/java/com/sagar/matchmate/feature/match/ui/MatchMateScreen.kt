package com.sagar.matchmate.feature.match.ui

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
import com.sagar.matchmate.ui.theme.MatchMateTextTertiary
import com.sagar.matchmate.ui.theme.MatchMateYellowSoft

@Composable
fun MatchMateScreen(
    viewModel: MatchMateViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(
            MatchMateIntent.LoadInitialMatches
        )
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MatchMateEffect.ShowSnackbar -> {
                    snackbarHostState.showLatestSnackbar(
                        message = effect.message
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MatchMateBackground,
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

            MatchMateHeader(
                selectedTab = uiState.selectedTab,
                isRefreshing = uiState.pagination.isRefreshing,
                onRefresh = {
                    viewModel.sendIntent(
                        MatchMateIntent.Refresh
                    )
                }
            )

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

            GenderFilters(
                selectedFilter = uiState.genderFilter,
                onFilterSelected = { filter ->
                    viewModel.sendIntent(
                        MatchMateIntent.SelectGenderFilter(filter)
                    )
                }
            )

            when {
                uiState.pagination.isInitialLoading &&
                        uiState.matches.isEmpty() -> {

                    MatchSkeletonList()
                }

                uiState.initialError != null &&
                        uiState.matches.isEmpty() -> {

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

                else -> {
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
}

@Composable
private fun MatchMateHeader(
    selectedTab: MatchTab,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val title = when (selectedTab) {
        MatchTab.DISCOVER -> "Discover"
        MatchTab.ACCEPTED -> "Your Matches"
        MatchTab.DECLINED -> "Passed"
    }

    val subtitle = when (selectedTab) {
        MatchTab.DISCOVER -> "Find someone who feels right"
        MatchTab.ACCEPTED -> "People you're interested in"
        MatchTab.DECLINED -> "People you've passed on"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 12.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MatchMateTextPrimary
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MatchMateTextSecondary
            )
        }

        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = MatchMateSurface,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MatchMateDivider
            )
        ) {
            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = if (isRefreshing) {
                        MatchMateTextTertiary
                    } else {
                        MatchMatePink
                    }
                )
            }
        }
    }
}
@Composable
private fun MatchTabs(
    selectedTab: MatchTab,
    acceptedCount: Int,
    declinedCount: Int,
    onTabSelected: (MatchTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            )
            .background(
                color = MatchMatePinkSoft.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        ModernTab(
            modifier = Modifier.weight(1f),
            selected = selectedTab == MatchTab.DISCOVER,
            text = "Discover",
            onClick = {
                onTabSelected(MatchTab.DISCOVER)
            }
        )

        ModernTab(
            modifier = Modifier.weight(1f),
            selected = selectedTab == MatchTab.ACCEPTED,
            text = if (acceptedCount > 0) {
                "Accepted $acceptedCount"
            } else {
                "Accepted"
            },
            onClick = {
                onTabSelected(MatchTab.ACCEPTED)
            }
        )

        ModernTab(
            modifier = Modifier.weight(1f),
            selected = selectedTab == MatchTab.DECLINED,
            text = if (declinedCount > 0) {
                "Declined $declinedCount"
            } else {
                "Declined"
            },
            onClick = {
                onTabSelected(MatchTab.DECLINED)
            }
        )
    }
}

@Composable
private fun ModernTab(
    modifier: Modifier,
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            MatchMateSurface
        } else {
            Color.Transparent
        },
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    }
                ),
                color = if (selected) {
                    MatchMatePinkDark
                } else {
                    MatchMateTextSecondary
                }
            )
        }
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
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MatchMateSurface,
                    labelColor = MatchMateTextSecondary,
                    selectedContainerColor = MatchMatePink,
                    selectedLabelColor = MatchMateSurface
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
                            GenderFilter.ALL -> "Everyone"
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
        shape = RoundedCornerShape(12.dp),
        color = MatchMateYellowSoft
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE7A900))
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Offline · Showing saved matches",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MatchMateTextPrimary
            )
        }
    }
}


@Composable
private fun MatchSkeletonList() {

    val infiniteTransition = rememberInfiniteTransition(
        label = "skeleton"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(3) {
            MatchSkeletonCard(
                alpha = alpha
            )
        }
    }
}

@Composable
private fun MatchSkeletonCard(
    alpha: Float
) {
    val skeletonColor = MatchMateDivider.copy(
        alpha = alpha
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MatchMateSurface)
            .border(
                width = 1.dp,
                color = MatchMateDivider,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(bottom = 16.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(skeletonColor)
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonColor)
            )

            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonColor)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(skeletonColor)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(skeletonColor)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(skeletonColor)
                )
            }
        }
    }
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
                shape = CircleShape,
                color = MatchMateErrorSoft
            ) {
                Text(
                    text = "!",
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MatchMateError
                )
            }

            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MatchMateTextPrimary
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MatchMateTextSecondary
            )

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
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

private suspend fun SnackbarHostState.showLatestSnackbar(
    message: String
) {
    currentSnackbarData?.dismiss()

    showSnackbar(
        message = message,
        duration = SnackbarDuration.Short
    )
}