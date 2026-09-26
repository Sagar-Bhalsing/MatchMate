package com.sagar.matchmate.feature.match.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.feature.match.MatchTab
import com.sagar.matchmate.feature.match.model.PaginationConfig
import com.sagar.matchmate.feature.match.model.PaginationState
import com.sagar.matchmate.ui.theme.MatchMateDivider
import com.sagar.matchmate.ui.theme.MatchMateError
import com.sagar.matchmate.ui.theme.MatchMateErrorSoft
import com.sagar.matchmate.ui.theme.MatchMateGreen
import com.sagar.matchmate.ui.theme.MatchMateGreenSoft
import com.sagar.matchmate.ui.theme.MatchMatePink
import com.sagar.matchmate.ui.theme.MatchMatePinkSoft
import com.sagar.matchmate.ui.theme.MatchMateRed
import com.sagar.matchmate.ui.theme.MatchMateRedSoft
import com.sagar.matchmate.ui.theme.MatchMateSurface
import com.sagar.matchmate.ui.theme.MatchMateTextPrimary
import com.sagar.matchmate.ui.theme.MatchMateTextSecondary
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
    LaunchedEffect(
        listState,
        matches.size,
        selectedTab
    ) {

        if (selectedTab != MatchTab.DISCOVER) {
            return@LaunchedEffect
        }

        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo
                .lastOrNull()
                ?.index ?: -1
        }
            .distinctUntilChanged()
            .filter { lastVisibleIndex ->
                matches.isNotEmpty() &&
                        pagination.hasMore &&
                        !pagination.isLoadingNextPage &&
                        lastVisibleIndex >=
                        matches.lastIndex -
                        PaginationConfig.PREFETCH_DISTANCE
            }
            .collect {
                onLoadNextPage()
            }
    }

    if (matches.isEmpty()) {
        EmptyMatchesState(
            selectedTab = selectedTab
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(
            items = matches,
            key = { it.id }
        ) { match ->

            MatchListItem {

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
        }
        if (
            selectedTab == MatchTab.DISCOVER &&
            pagination.nextPageError != null
        ) {

            item(
                key = "pagination_error"
            ) {

                PaginationError(
                    onRetry = onRetryNextPage
                )
            }
        }
        if (
            selectedTab == MatchTab.DISCOVER &&
            pagination.isLoadingNextPage
        ) {

            item(
                key = "pagination_loader"
            ) {

                PaginationSkeleton()
            }
        }
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
private fun MatchListItem(
    content: @Composable () -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = true,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 350,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 350,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { it / 12 }
        )
    ) {
        content()
    }
}

@Composable
private fun PaginationSkeleton() {

    val transition = rememberInfiniteTransition(
        label = "paginationSkeleton"
    )

    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(
                durationMillis = 850,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SkeletonMatchCard(alpha = alpha)

        SkeletonMatchCard(alpha = alpha)
    }
}

@Composable
private fun SkeletonMatchCard(
    alpha: Float
) {

    val skeletonColor = MatchMateDivider.copy(
        alpha = alpha
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MatchMateSurface)
            .border(
                width = 1.dp,
                color = MatchMateDivider,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(skeletonColor)
        )
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonColor)
            )
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(15.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(skeletonColor)
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(skeletonColor)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(skeletonColor)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(skeletonColor)
                )
            }
        }
    }
}

@Composable
private fun EmptyMatchesState(
    selectedTab: MatchTab
) {

    val config = when (selectedTab) {

        MatchTab.DISCOVER -> EmptyStateConfig(
            icon = Icons.Default.SearchOff,
            title = "No matches yet",
            description = "Try another filter or check back later.",
            accent = MatchMatePink,
            background = MatchMatePinkSoft
        )

        MatchTab.ACCEPTED -> EmptyStateConfig(
            icon = Icons.Default.FavoriteBorder,
            title = "No accepted matches",
            description = "When you find someone interesting, they'll appear here.",
            accent = MatchMateGreen,
            background = MatchMateGreenSoft
        )

        MatchTab.DECLINED -> EmptyStateConfig(
            icon = Icons.Default.Close,
            title = "No passed matches",
            description = "People you pass on will appear here.",
            accent = MatchMateRed,
            background = MatchMateRedSoft
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = config.background
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = config.icon,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = config.accent
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = config.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MatchMateTextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = config.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MatchMateTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class EmptyStateConfig(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val accent: Color,
    val background: Color
)
@Composable
private fun PaginationError(
    onRetry: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 4.dp
            ),
        shape = RoundedCornerShape(16.dp),
        color = MatchMateErrorSoft
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = MatchMateSurface
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MatchMateError
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {

                Text(
                    text = "Couldn't load more",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MatchMateTextPrimary
                )

                Text(
                    text = "Check your connection and try again.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchMateTextSecondary
                )
            }

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(
                    horizontal = 12.dp
                ),
                border = BorderStroke(
                    1.dp,
                    MatchMateError.copy(alpha = 0.35f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MatchMateError
                )
            ) {
                Text(
                    text = "Retry",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EndOfResults() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 22.dp,
                horizontal = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MatchMateDivider)
        )

        Text(
            text = "You're all caught up",
            modifier = Modifier.padding(
                horizontal = 12.dp
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MatchMateTextSecondary
        )

        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MatchMateDivider)
        )
    }
}