package com.sagar.matchmate.feature.match.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.domain.model.MatchStatus
import com.sagar.matchmate.ui.theme.MatchMateBackground
import com.sagar.matchmate.ui.theme.MatchMateDivider
import com.sagar.matchmate.ui.theme.MatchMateGreen
import com.sagar.matchmate.ui.theme.MatchMateGreenSoft
import com.sagar.matchmate.ui.theme.MatchMatePink
import com.sagar.matchmate.ui.theme.MatchMatePinkSoft
import com.sagar.matchmate.ui.theme.MatchMateRed
import com.sagar.matchmate.ui.theme.MatchMateRedSoft
import com.sagar.matchmate.ui.theme.MatchMateSurface
import com.sagar.matchmate.ui.theme.MatchMateTextPrimary
import com.sagar.matchmate.ui.theme.MatchMateTextSecondary
import com.sagar.matchmate.ui.theme.MatchMateYellowSoft

@Composable
fun MatchCard(
    match: Match,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var showContactInfo by remember(match.id) {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MatchMateSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MatchMateDivider
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        ) {

            /*
             * ----------------------------------------------------------------
             * HERO IMAGE
             * ----------------------------------------------------------------
             */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            ) {

                AsyncImage(
                    model = match.imageUrl,
                    contentDescription =
                        "${match.firstName} ${match.lastName}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            )
                        )
                        .background(MatchMatePinkSoft),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.68f)
                                )
                            )
                        )
                )
                StatusChip(
                    status = match.status,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            horizontal = 18.dp,
                            vertical = 18.dp
                        )
                ) {
                    Text(
                        text = "${match.firstName} ${match.lastName}, ${match.age}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${match.city}, ${match.country}",
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                ContactToggle(
                    expanded = showContactInfo,
                    onClick = {
                        showContactInfo = !showContactInfo
                    }
                )
                AnimatedVisibility(
                    visible = showContactInfo,
                    enter = expandVertically(
                        animationSpec = tween(
                            durationMillis = 280,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(180)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(
                            durationMillis = 230,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(140)
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        ContactRow(
                            icon = Icons.Default.Email,
                            value = match.email ?: "Not available"
                        )

                        ContactRow(
                            icon = Icons.Default.Phone,
                            value = match.phone ?: "Not available"
                        )
                    }
                }

                /*
                 * ----------------------------------------------------------------
                 * ACTIONS
                 * ----------------------------------------------------------------
                 */

                if (match.status == MatchStatus.PENDING) {

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDecline,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MatchMateSurface,
                                contentColor = MatchMateTextPrimary
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MatchMateRed.copy(alpha = 0.35f)
                            )
                        ) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp),
                                tint = MatchMateRed
                            )

                            Spacer(
                                modifier = Modifier.width(7.dp)
                            )

                            Text(
                                text = "Pass",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MatchMatePink,
                                contentColor = MatchMateSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(7.dp)
                            )

                            Text(
                                text = "Interested",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ContactToggle(
    expanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (expanded) {
            MatchMatePinkSoft
        } else {
            MatchMateBackground
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (expanded) {
                MatchMatePink.copy(alpha = 0.25f)
            } else {
                MatchMateDivider
            }
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 11.dp
                ),
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
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MatchMatePink
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {

                Text(
                    text = if (expanded) {
                        "Contact information"
                    } else {
                        "View contact information"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MatchMateTextPrimary
                )

                Text(
                    text = if (expanded) {
                        "Tap to hide details"
                    } else {
                        "Email & phone"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MatchMateTextSecondary
                )
            }

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.ExpandLess
                } else {
                    Icons.Default.ExpandMore
                },
                contentDescription = null,
                tint = MatchMatePink
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: MatchStatus,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = when (status) {

        MatchStatus.PENDING -> Triple(
            "NEW",
            MatchMateYellowSoft,
            Color(0xFFA66A00)
        )

        MatchStatus.ACCEPTED -> Triple(
            "✓ ACCEPTED",
            MatchMateGreenSoft,
            MatchMateGreen
        )

        MatchStatus.DECLINED -> Triple(
            "✕ PASSED",
            MatchMateRedSoft,
            MatchMateRed
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = contentColor
        )
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MatchMateBackground
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = MatchMatePinkSoft
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MatchMatePink
                    )
                }
            }

            Text(
                text = value,
                modifier = Modifier.padding(start = 9.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MatchMateTextSecondary
            )
        }
    }
}