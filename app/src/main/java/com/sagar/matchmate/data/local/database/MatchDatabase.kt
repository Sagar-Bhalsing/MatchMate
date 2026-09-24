package com.sagar.matchmate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.sagar.matchmate.data.local.dao.MatchActionDao
import com.sagar.matchmate.data.local.dao.MatchDao
import com.sagar.matchmate.data.local.entity.MatchActionEntity
import com.sagar.matchmate.data.local.entity.MatchEntity


@Database(
    entities = [
        MatchEntity::class,
        MatchActionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class MatchDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDao

    abstract fun matchActionDao(): MatchActionDao

    @Transaction
    suspend fun updateMatchStatusAndQueueAction(
        matchId: String,
        status: String,
        updatedAt: Long
    ) {
        val matchDao = matchDao()
        val matchActionDao = matchActionDao()

        val existingMatch = matchDao.getMatch(matchId)
            ?: return

        matchDao.updateMatchStatus(
            matchId = matchId,
            status = status,
            updatedAt = updatedAt
        )

        matchActionDao.deletePendingAction(
            matchId = matchId
        )

        matchActionDao.insertAction(
            MatchActionEntity(
                matchId = existingMatch.id,
                status = status,
                createdAt = updatedAt,
                synced = false
            )
        )
    }
}