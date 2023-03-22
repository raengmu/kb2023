package com.ymkwon.kb2023.search.jpa

import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.search.jpa.entity.SearchCacheEntity
import com.ymkwon.kb2023.search.jpa.repository.SearchCacheRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class JpaSearchCacheAsyncTask(
    private val cacheRepository: SearchCacheRepository
) {
    private val logger = KotlinLogging.logger {}

    @Async("taskExecutor")
    fun save(session: SearchSession, cachePages: Set<SearchCachePage>) {
        saveInternal(session, cachePages)
    }

    @Async("taskExecutor")
    fun pruneExpired(expiredAt: LocalDateTime) {
        pruneExpiredInternal(expiredAt)
    }

    @Transactional
    private fun saveInternal(searchSession: SearchSession, cachePages: Set<SearchCachePage>) {
        try {
            cachePages.forEach {
                cacheRepository.save(
                    SearchCacheEntity(
                        name = searchSession.source.name,
                        query = searchSession.query,
                        sorder = searchSession.sorder.charCode,
                        page = it.page,
                        pageSize = searchSession.source.cachePageSize,
                        resultRaw = it.resRaw
                    )
                )
            }
        } catch(ex: Exception) {
            logger.warn { "save: fail ignored - $ex" }
        }
    }

    @Transactional
    private fun pruneExpiredInternal(expiredAt: LocalDateTime) {
        try {
            cacheRepository.deleteAllByCreatedAtLessThanOrEqual(expiredAt)
        } catch(ex: Exception) {
            logger.warn { "pruneExpired: fail ignored - $ex" }
        }
    }

}