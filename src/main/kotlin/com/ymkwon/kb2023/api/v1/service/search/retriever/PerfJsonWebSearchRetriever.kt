package com.ymkwon.kb2023.api.v1.service.search.retriever

import com.ymkwon.kb2023.config.WebClientConfig
import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchRetriever
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClientException
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

class PerfJsonWebSearchRetriever(
    private val webClientConfig: WebClientConfig
): SearchRetriever {
    private val logger = KotlinLogging.logger {}

    override fun retrieve(
        searchSession: SearchSession,
        absentCachePages: Set<Int>
    ): SortedSet<SearchCachePage>? {
        val source = searchSession.request.source
        val webClient = try {
            webClientConfig
                .webClientBuilder()
                .baseUrl(source.url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders { source.headers.forEach { (k, v) -> it[k] = v } }
                .build()
        } catch (ex: WebClientException) {
            throw SearchException(SearchExceptionCode.RETRIEVER_EXCEPTION, ex.message, "$source")
        }

        val idxMap = absentCachePages.mapIndexed { i, page0 -> page0 to i }.toMap()
        val resArr = Array<Pair<Int, String?>?>(idxMap.size) { null }
        val cachePages = try {
            absentCachePages.parallelStream().map { page0 -> Pair<Int, Mono<String>?>(
                page0,
                webClient.mutate().build()
                    .get()
                    .uri {
                        var uriBuilder = it
                        for((k, v) in searchSession.request.fixedQueryParams()) {
                            uriBuilder = uriBuilder.queryParam(k, v)
                        }
                        for((k, v) in searchSession.request.pagedQueryParams(page0, searchSession.request.source.cachePageSize)) {
                            uriBuilder = uriBuilder.queryParam(k, v)
                        }
                        uriBuilder.build()
                    }
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String::class.java).subscribeOn(Schedulers.boundedElastic())
            ) }.forEach {
                resArr[idxMap[it.first]!!] =
                    Pair(it.first, it.second?.block() ?: throw SearchException(
                                        SearchExceptionCode.RETRIEVER_EXCEPTION, "failed to get resources", "$source"))
            }
            resArr.map { SearchCachePage(it!!.first, it.second!!) }.toSortedSet()
        } catch (ex: WebClientException) {
            throw SearchException(SearchExceptionCode.RETRIEVER_EXCEPTION, ex.message, "$source")
        }
        return if (cachePages.isEmpty()) null else cachePages
    }
}