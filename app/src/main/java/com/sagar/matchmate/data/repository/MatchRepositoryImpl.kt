package com.sagar.matchmate.data.repository

import com.sagar.matchmate.data.local.database.MatchDatabase
import com.sagar.matchmate.data.remote.RemoteConfig
import com.sagar.matchmate.data.remote.api.RandomUserApi
import com.sagar.matchmate.domain.mapper.toDomain
import com.sagar.matchmate.domain.mapper.toEntity
import com.sagar.matchmate.domain.mapper.toMatchStatus
import com.sagar.matchmate.domain.model.Match
import com.sagar.matchmate.domain.model.MatchStatus
import com.sagar.matchmate.domain.repository.MatchRepository
import com.sagar.matchmate.domain.repository.PageResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MatchRepositoryImpl(
    private val api: RandomUserApi,
    private val database: MatchDatabase
) : MatchRepository {

    private val matchDao = database.matchDao()
    private val matchActionDao = database.matchActionDao()

    /**
     * Protects pagination operations from concurrent execution.
     */
    private val paginationMutex = Mutex()

    override fun observeMatches(): Flow<List<Match>> {
        return matchDao
            .observeMatches()
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getLastCachedPage(): Int {
        return matchDao.getLastCachedPage() ?: 0
    }

    override suspend fun loadInitialPage(): PageResult {
        return paginationMutex.withLock {
            loadPageInternal(
                page = 1,
                pageSize = RemoteConfig.PAGE_SIZE
            )
        }
    }

    override suspend fun loadNextPage(
        page: Int,
        pageSize: Int
    ): PageResult {
        return paginationMutex.withLock {
            loadPageInternal(
                page = page,
                pageSize = pageSize
            )
        }
    }

    private suspend fun loadPageInternal(
        page: Int,
        pageSize: Int
    ): PageResult {

        require(page > 0) {
            "Page must be greater than 0."
        }

        require(page <= RemoteConfig.MAX_PAGES) {
            "Page exceeds maximum allowed pages."
        }

        val response = api.getUsers(
            results = pageSize,
            page = page,
            seed = RemoteConfig.MATCH_SEED
        )

        require(response.info.page == page) {
            "Unexpected page returned. " +
                    "Requested=$page, Received=${response.info.page}"
        }

        val userIds = response.results.map {
            it.login.uuid
        }

        val existingMatches = if (userIds.isEmpty()) {
            emptyList()
        } else {
            matchDao.getMatchesByIds(userIds)
        }

        val existingById = existingMatches.associateBy {
            it.id
        }

        val now = System.currentTimeMillis()

        val entities = response.results.map { user ->

            val existingEntity = existingById[user.login.uuid]

            user.toEntity(
                page = existingEntity?.page ?: page,
                status = existingEntity?.status?.toMatchStatus()
                    ?: MatchStatus.PENDING,
                now = now
            )
        }

        matchDao.insertMatches(entities)

        val hasMore =
            page < RemoteConfig.MAX_PAGES &&
                    response.results.size >= pageSize

        return PageResult(
            page = page,
            itemCount = response.results.size,
            hasMore = hasMore
        )
    }

    override suspend fun refresh(): PageResult {
        return paginationMutex.withLock {

            val page = 1
            val pageSize = RemoteConfig.PAGE_SIZE

            val response = api.getUsers(
                results = pageSize,
                page = page,
                seed = RemoteConfig.MATCH_SEED
            )

            require(response.info.page == page) {
                "Unexpected page returned during refresh. " +
                        "Requested=$page, Received=${response.info.page}"
            }

            val userIds = response.results.map {
                it.login.uuid
            }

            val existingMatches = if (userIds.isEmpty()) {
                emptyList()
            } else {
                matchDao.getMatchesByIds(userIds)
            }

            val existingById = existingMatches.associateBy {
                it.id
            }

            val now = System.currentTimeMillis()

            val refreshedEntities = response.results.map { user ->

                val existingEntity =
                    existingById[user.login.uuid]

                user.toEntity(
                    page = existingEntity?.page ?: page,
                    status = existingEntity?.status?.toMatchStatus()
                        ?: MatchStatus.PENDING,
                    now = now
                )
            }

            matchDao.insertMatches(refreshedEntities)

            PageResult(
                page = page,
                itemCount = response.results.size,
                hasMore =
                    page < RemoteConfig.MAX_PAGES &&
                            response.results.size >= pageSize
            )
        }
    }

    override suspend fun updateMatchStatus(
        matchId: String,
        status: MatchStatus
    ) {
        paginationMutex.withLock {

            database.updateMatchStatusAndQueueAction(
                matchId = matchId,
                status = status.name,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    override suspend fun syncPendingActions() {

        /**
         * The supplied RandomUser API does not expose
         * an Accept/Decline mutation endpoint.
         *
         * Therefore we intentionally do not invent
         * a network call here.
         *
         * Pending actions remain persisted locally
         * until a real backend mutation endpoint
         * is available.
         */
    }
}