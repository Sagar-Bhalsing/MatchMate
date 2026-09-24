package com.sagar.matchmate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sagar.matchmate.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow



@Dao
interface MatchDao {

    /**
     * Observe all cached matches.
     *
     * The UI ultimately observes the ViewModel StateFlow,
     * but the Repository observes this Room Flow.
     */
    @Query(
        """
        SELECT *
        FROM matches
        ORDER BY page ASC, updatedAt ASC
        """
    )
    fun observeMatches(): Flow<List<MatchEntity>>

    /**
     * Get all cached matches once.
     */
    @Query(
        """
        SELECT *
        FROM matches
        ORDER BY page ASC, updatedAt ASC
        """
    )
    suspend fun getMatches(): List<MatchEntity>

    @Query("""
    SELECT *
    FROM matches
    WHERE id IN (:ids)
""")
    suspend fun getMatchesByIds(
        ids: List<String>
    ): List<MatchEntity>

    /**
     * Get the number of locally cached profiles.
     */
    @Query(
        "SELECT COUNT(*) FROM matches"
    )
    suspend fun getMatchCount(): Int

    /**
     * Get the highest page stored locally.
     *
     * Returns null when there are no records.
     */
    @Query(
        "SELECT MAX(page) FROM matches"
    )
    suspend fun getLastCachedPage(): Int?

    /**
     * Get all matches belonging to a particular API page.
     */
    @Query(
        """
        SELECT *
        FROM matches
        WHERE page = :page
        ORDER BY updatedAt ASC
        """
    )
    suspend fun getMatchesForPage(
        page: Int
    ): List<MatchEntity>

    /**
     * Insert or update API data.
     *
     * IMPORTANT:
     * We will use this only after preserving the existing
     * local status in the Repository.
     */
    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertMatches(
        matches: List<MatchEntity>
    )

    /**
     * Update only the user's local decision.
     *
     * This prevents us from accidentally replacing all
     * profile information when accepting/declining.
     */
    @Query(
        """
        UPDATE matches
        SET status = :status,
            updatedAt = :updatedAt
        WHERE id = :matchId
        """
    )
    suspend fun updateMatchStatus(
        matchId: String,
        status: String,
        updatedAt: Long
    ): Int

    /**
     * Retrieve a single match.
     */
    @Query(
        """
        SELECT *
        FROM matches
        WHERE id = :matchId
        LIMIT 1
        """
    )
    suspend fun getMatch(
        matchId: String
    ): MatchEntity?

    /**
     * Delete all cached matches.
     *
     * We will NOT use this during normal refresh because
     * doing so could destroy locally persisted decisions.
     */
    @Query("DELETE FROM matches")
    suspend fun deleteAll(): Int
}