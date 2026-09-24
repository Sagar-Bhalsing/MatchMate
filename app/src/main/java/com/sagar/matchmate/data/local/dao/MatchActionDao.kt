package com.sagar.matchmate.data.local.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.*
import com.sagar.matchmate.data.local.entity.MatchActionEntity

@Dao
interface MatchActionDao {

    /**
     * Insert a new pending action.
     */
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertAction(
        action: MatchActionEntity
    )

    /**
     * Observe all actions that still need to be synchronized.
     */
    @Query(
        """
        SELECT *
        FROM match_actions
        WHERE synced = 0
        ORDER BY createdAt ASC
        """
    )
    fun observePendingActions(): Flow<List<MatchActionEntity>>

    /**
     * Get pending actions once.
     *
     * Actions are ordered by creation time so that if a user
     * performs multiple operations, we process them in the
     * same order.
     */
    @Query(
        """
        SELECT *
        FROM match_actions
        WHERE synced = 0
        ORDER BY createdAt ASC
        """
    )
    suspend fun getPendingActions(): List<MatchActionEntity>

    /**
     * Mark an action as successfully synchronized.
     */
    @Query(
        """
        UPDATE match_actions
        SET synced = 1
        WHERE id = :actionId
        """
    )
    suspend fun markAsSynced(
        actionId: Long
    ): Int

    /**
     * Remove an action after successful synchronization.
     *
     * We can use this instead of retaining synced actions
     * indefinitely.
     */
    @Query(
        """
        DELETE FROM match_actions
        WHERE id = :actionId
        """
    )
    suspend fun deleteAction(
        actionId: Long
    ): Int

    /**
     * Delete all successfully synchronized actions.
     *
     * Useful for periodic cleanup.
     */
    @Query(
        """
        DELETE FROM match_actions
        WHERE synced = 1
        """
    )
    suspend fun deleteSyncedActions(): Int

    /**
     * Number of pending synchronization operations.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM match_actions
        WHERE synced = 0
        """
    )
    suspend fun getPendingActionCount(): Int

    /**
     * Removes any existing unsynchronized action for a match.
     *
     * Used before inserting a new decision so that only the
     * latest offline decision remains pending.
     */
    @Query(
        """
    DELETE FROM match_actions
    WHERE matchId = :matchId
    AND synced = 0
    """
    )
    suspend fun deletePendingAction(
        matchId: String
    ): Int
}