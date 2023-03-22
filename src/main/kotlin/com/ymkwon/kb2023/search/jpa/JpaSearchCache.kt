package com.ymkwon.kb2023.search.jpa

import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.search.jpa.repository.SearchCacheRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Component
class JpaSearchCache(
    private val cacheRepository: SearchCacheRepository,
    private val cacheAsync: JpaSearchCacheAsyncTask
): SearchCache {
    override fun tryLock(searchSession: SearchSession, lockTimeoutMsec: Int): Boolean {
        return true
    }

    @Transactional
    override fun query(searchSession: SearchSession): SearchCacheQueryResult? {
        val page0 = searchSession.page0
        val res = cacheRepository.findAllRawResultForUpdate(
            searchSession.source.name, searchSession.query, searchSession.sorder.charCode,
            page0.cachePageBegin, page0.cachePageEnd,
            searchSession.source.cachePageSize)
        if (res.isEmpty())
            return null
        val expired = { createdAt: LocalDateTime, expiredAt: LocalDateTime -> createdAt < expiredAt }
        val cachePages = res.filter { !expired(it.createdAt, searchSession.expiredAt) }
                            .map { SearchCachePage(it.page, it.resRaw) }
                            .toSortedSet()
        return SearchCacheQueryResult(
            foundExpired = res.find { expired(it.createdAt, searchSession.expiredAt) } != null,
            cachePages = cachePages)
    }

    override fun save(searchSession: SearchSession, cachePages: Set<SearchCachePage>) {
        //ATTENTION: cachePages may be modified after this call and should be cloned to aync task
        cacheAsync.save(searchSession, TreeSet(cachePages))
    }

    @Transactional
    override fun pruneExpired(expiredAt: LocalDateTime) {
        cacheAsync.pruneExpired(expiredAt)
    }

    override fun unlock(searchSession: SearchSession) {
    }

}
