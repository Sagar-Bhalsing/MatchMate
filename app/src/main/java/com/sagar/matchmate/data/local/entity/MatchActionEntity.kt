package com.sagar.matchmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "match_actions"
)
data class MatchActionEntity(

    @PrimaryKey(
        autoGenerate = true
    )
    val id: Long = 0,

    /**
     * Match on which the action was performed.
     */
    val matchId: String,

    /**
     * ACCEPTED or DECLINED.
     */
    val status: String,

    /**
     * Time when the user performed the action.
     */
    val createdAt: Long,

    /**
     * Whether this action has successfully been
     * synchronized with the remote system.
     */
    val synced: Boolean = false
)