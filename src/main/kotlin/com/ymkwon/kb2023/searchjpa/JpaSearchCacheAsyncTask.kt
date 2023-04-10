package com.ymkwon.kb2023.searchjpa

import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.searchjpa.entity.SearchCacheEntity
import com.ymkwon.kb2023.searchjpa.repository.SearchCacheRepository
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
    @Transactional
    fun save(session: SearchSession, cachePages: Set<SearchCachePage>) {
        saveInternal(session, cachePages)
    }

    @Async("taskExecutor")
    @Transactional
    fun pruneExpired(expiredAt: LocalDateTime) {
        pruneExpiredInternal(expiredAt)
    }

    //@Transactional ATTENTION: @Transactional should be public - need to study
    private fun saveInternal(searchSession: SearchSession, cachePages: Set<SearchCachePage>) {
        try {
            cachePages.forEach {
                cacheRepository.save(
                    SearchCacheEntity(
                        cacheKey = searchSession.cacheKey,
                        page = it.page,
                        pageSize = searchSession.request.source.cachePageSize,
                        resultRaw = it.resRaw
                    )
                )
            }
        } catch(ex: Exception) {
            logger.warn { "save: fail ignored - $ex" }
        }
    }

    //@Transactional ATTENTION: @Transactional should be public - need to study
    private fun pruneExpiredInternal(expiredAt: LocalDateTime) {
        try {
            cacheRepository.deleteAllByCreatedAtLessThanOrEqual(expiredAt)
        } catch(ex: Exception) {
            logger.warn { "pruneExpired: fail ignored - $ex" }
        }
    }

}