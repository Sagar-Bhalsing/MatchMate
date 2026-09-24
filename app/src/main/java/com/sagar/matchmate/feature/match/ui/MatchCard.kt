package com.sagar.matchmate.feature.match.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.domain.model.MatchStatus
import com.sagar.matchmate.ui.theme.MatchMateDivider
import com.sagar.matchmate.ui.theme.MatchMateGreen
import com.sagar.matchmate.ui.theme.MatchMateGreenSoft
import com.sagar.matchmate.ui.theme.MatchMatePink
import com.sagar.matchmate.ui.theme.MatchMatePinkDark
import com.sagar.matchmate.ui.theme.MatchMatePinkSoft
import com.sagar.matchmate.ui.theme.MatchMateRed
import com.sagar.matchmate.ui.theme.MatchMateRedSoft
import com.sagar.matchmate.ui.theme.MatchMateSurface
import com.sagar.matchmate.ui.theme.MatchMateTextPrimary
import com.sagar.matchmate.ui.theme.MatchMateTextSecondary
import com.sagar.matchmate.ui.theme.MatchMateYellow
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
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false
            )
            .animateContentSize(
                animationSpec = tween(250)
            ),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = MatchMateSurface
        ),

        border = BorderStroke(
            width = 1.dp,
            color = MatchMateDivider
        )
    ) {

        Column {

            /*
             * Profile image
             */
            AsyncImage(
                model = match.imageUrl,

                contentDescription =
                    "${match.firstName} ${match.lastName}",

                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(
                        RoundedCornerShape(
                            topEnd = 20.dp, topStart = 20.dp
                        )
                    )
                    .background(
                        MatchMatePinkSoft
                    ),

                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                /*
                 * Name + status
                 */
                Row(
                    modifier = Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "${match.firstName} ${match.lastName}, ${match.age}",

                            style =
                                MaterialTheme.typography.titleLarge,

                            fontWeight =
                                FontWeight.SemiBold,

                            color = MatchMateTextPrimary
                        )

                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.LocationOn,

                                contentDescription = null,

                                modifier =
                                    Modifier.size(17.dp),

                                tint = MatchMatePink
                            )

                            Text(
                                text =
                                    "${match.city}, ${match.country}",

                                modifier =
                                    Modifier.padding(start = 4.dp),

                                style =
                                    MaterialTheme.typography.bodyMedium,

                                color = MatchMateTextSecondary
                            )
                        }
                    }

                    StatusChip(
                        status = match.status
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                /*
                 * Contact information
                 */
                AssistChip(
                    onClick = {
                        showContactInfo = !showContactInfo
                    },

                    label = {
                        Text(
                            text = if (showContactInfo) {
                                "Hide contact"
                            } else {
                                "Contact information"
                            }
                        )
                    },

                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MatchMatePinkSoft,
                        labelColor = MatchMatePinkDark
                    ),

                    border =
                        AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MatchMatePink.copy(
                                alpha = 0.35f
                            )
                        )
                )

                AnimatedContent(
                    targetState = showContactInfo,

                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(200)
                        ) togetherWith fadeOut(
                            animationSpec = tween(150)
                        )
                    },

                    label = "contactInfo"
                ) { visible ->

                    if (visible) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            ContactRow(
                                icon = Icons.Default.Email,
                                value =
                                    match.email ?: "Not available"
                            )

                            ContactRow(
                                icon = Icons.Default.Phone,
                                value =
                                    match.phone ?: "Not available"
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                /*
                 * Pending actions
                 */
                if (match.status == MatchStatus.PENDING) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        /*
                         * Decline
                         */
                        OutlinedButton(
                            onClick = onDecline,

                            modifier =
                                Modifier.weight(1f),

                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor =
                                        MatchMateSurface,

                                    contentColor =
                                        MatchMateTextPrimary
                                ),

                            border =
                                androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = MatchMateDivider
                                ),

                            shape =
                                RoundedCornerShape(20.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,

                                contentDescription = null,

                                modifier =
                                    Modifier.size(18.dp),

                                tint = MatchMateRed
                            )

                            Spacer(
                                modifier = Modifier.size(6.dp)
                            )

                            Text(
                                text = "Decline"
                            )
                        }

                        /*
                         * Accept
                         */
                        Button(
                            onClick = onAccept,

                            modifier =
                                Modifier.weight(1f),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        MatchMatePink,

                                    contentColor =
                                        MatchMateSurface
                                ),

                            shape =
                                RoundedCornerShape(20.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Check,

                                contentDescription = null,

                                modifier =
                                    Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.size(6.dp)
                            )

                            Text(
                                text = "Accept"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: MatchStatus
) {
    val (
        text,
        containerColor,
        contentColor
    ) = when (status) {

        MatchStatus.PENDING -> Triple(
            "NEW MATCH",
            MatchMateYellowSoft,
            Color(0xFFA66A00)
        )

        MatchStatus.ACCEPTED -> Triple(
            "✓ ACCEPTED",
            MatchMateGreenSoft,
            MatchMateGreen
        )

        MatchStatus.DECLINED -> Triple(
            "✕ DECLINED",
            MatchMateRedSoft,
            MatchMateRed
        )
    }

    Surface(
        modifier = Modifier.padding(start = 8.dp),

        shape = RoundedCornerShape(50),

        color = containerColor
    ) {

        Text(
            text = text,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),

            style = MaterialTheme.typography.labelSmall,

            fontWeight = FontWeight.SemiBold,

            color = contentColor
        )
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    value: String
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,

            contentDescription = null,

            modifier = Modifier.size(18.dp),

            tint = MatchMatePink
        )

        Text(
            text = value,

            modifier =
                Modifier.padding(start = 8.dp),

            style =
                MaterialTheme.typography.bodySmall,

            color = MatchMateTextSecondary
        )
    }
}