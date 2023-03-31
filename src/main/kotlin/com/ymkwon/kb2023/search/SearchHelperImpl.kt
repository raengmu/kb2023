package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import org.springframework.stereotype.Component

@Component
class SearchHelperImpl(
    private val cache: SearchCache,
    private val appProperties: ApplicationProperties
): SearchHelper {

    override fun getResult(requests: List<SearchRequest>): SearchResult? {
        var res: SearchResult? = null
        for (request in requests) {
            try {
                res = getResultPerSource(
                    getSession(request, SearchPage0(request.page, request.pageSize, request.source.cachePageSize)))
            } catch(ex: SearchException) {
                // if resource retrieving or parsing failed,
                // considered as source problem and retry with next source
                if (ex.code != SearchExceptionCode.FAILED_RETRIEVE_RESOURCE.code &&
                    ex.code != SearchExceptionCode.PARSING_ERROR.code &&
                    ex.code != SearchExceptionCode.RETRIEVER_EXCEPTION.code)
                    throw ex
            }
            if (res != null)
                break
        }
        return res
    }

    private fun getResultPerSource(
        session: SearchSession
    ): SearchResult? {
        val resRaws: List<String>?
        val source = session.request.source
        try {
            var queryResult: SearchCacheQueryResult? = null
            val absentCachePages =
                IntRange(session.page0.cachePageBegin, session.page0.cachePageEnd - 1).toSortedSet()
            if (appProperties.search.enableCache) {
                // lock cache session
                val isAvailable = cache.tryLock(session, appProperties.search.cacheTimeoutMsec)
                if (!isAvailable)
                    throw SearchException(SearchExceptionCode.TOO_BUSY, "try lock failed", "session: $session")
                // try to query in cache
                queryResult = cache.query(session)
                if (queryResult != null) {
                    if (queryResult.foundExpired) {
                        // delete all expired
                        //TODO: estimate prune parts or all and need async task?
                        cache.pruneExpired(session.expiredAt)
                    }
                    queryResult.cachePages.forEach { absentCachePages.remove(it.page) }
                }
            }
            resRaws = if (absentCachePages.isNotEmpty()) {
                // retrieve result and store to cache
                val cachePages = source.retriever.retrieve(session, absentCachePages)?:
                        throw SearchException(SearchExceptionCode.FAILED_RETRIEVE_RESOURCE,
                                              "no cache page retrieve", "session: $session")
                if (appProperties.search.enableCache) {
                    cache.save(session, cachePages)
                }
                queryResult?.cachePages?.forEach { cachePages.add(it) }
                cachePages.map { it.resRaw }
            } else {
                queryResult?.cachePages?.map { it.resRaw }
            }

            val expectedCachePageCount = session.page0.cachePageEnd - session.page0.cachePageBegin
            if (resRaws != null && resRaws.size != expectedCachePageCount)
                throw SearchException(SearchExceptionCode.ASSERT_FAILED, "invalid result cache page count",
                    "resRaws.size: ${resRaws.size} != expected: $expectedCachePageCount")
        } catch (ex: SearchException) {
            throw ex
        } catch (ex: Exception) {
            throw SearchException(SearchExceptionCode.UNDEFINED, "search failed", "session: $session")
        } finally {
            cache.unlock(session)
        }

        try {
            if (resRaws != null)
                return source.parser.parse(session, resRaws)
        } catch(ex: Exception) {
            throw SearchException(SearchExceptionCode.PARSING_ERROR, "failed to parse", "session: $session")
        }
        return null
    }

    private fun getSession(
        request: SearchRequest,
        page0: SearchPage0
    ): SearchSession {
        return SearchSession(request, page0, appProperties.search.cacheExpireSec)
    }
}
