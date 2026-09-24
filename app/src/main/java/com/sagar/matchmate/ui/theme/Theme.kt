package com.sagar.matchmate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MatchMateLightColorScheme = lightColorScheme(
    primary = MatchMatePink,
    onPrimary = MatchMateSurface,

    primaryContainer = MatchMatePinkSoft,
    onPrimaryContainer = MatchMatePinkDark,

    secondary = MatchMateYellow,
    onSecondary = MatchMateTextPrimary,

    secondaryContainer = MatchMateYellowSoft,
    onSecondaryContainer = MatchMateTextPrimary,

    background = MatchMateBackground,
    onBackground = MatchMateTextPrimary,

    surface = MatchMateSurface,
    onSurface = MatchMateTextPrimary,

    surfaceVariant = MatchMatePinkSoft,
    onSurfaceVariant = MatchMateTextSecondary,

    outline = MatchMateDivider,

    error = MatchMateRed,
    onError = MatchMateSurface,
)

@Composable
fun MatchMateTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MatchMateLightColorScheme,
        typography = Typography,
        content = content
    )
}