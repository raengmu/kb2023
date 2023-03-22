package com.ymkwon.kb2023.search

import java.time.LocalDateTime

interface SearchCache {
    fun tryLock(searchSession: SearchSession, lockTimeoutMsec: Int): Boolean
    fun query(searchSession: SearchSession): SearchCacheQueryResult?
    fun save(searchSession: SearchSession, cachePages: Set<SearchCachePage>)
    fun unlock(searchSession: SearchSession)
    fun pruneExpired(expiredAt: LocalDateTime)
}
