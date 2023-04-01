package com.ymkwon.kb2023.api.v1.service.search.retriever

import com.ymkwon.kb2023.config.WebClientConfig
import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchRetriever
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientException
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Component
class SimpleJsonWebSearchRetriever(
    private val webClientConfig: WebClientConfig
): SearchRetriever {
    //private val logger = KotlinLogging.logger {}

    override fun retrieve(
        searchSession: SearchSession,
        absentCachePages: Set<Int>
    ): SortedSet<SearchCachePage>? {
        //TODO: connection pool?
        val source = searchSession.request.source
        val cachePages = TreeSet<SearchCachePage>()
        try {
            val webClient = webClientConfig
                .webClientBuilder()
                .baseUrl(source.url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeaders { source.headers.forEach { (k, v) -> it[k] = v } }
                .build()
            val pageIdx = IntArray(absentCachePages.size)
            val monoArr = Array<Mono<String>?>(pageIdx.size) { null }
            for((i, page0) in absentCachePages.withIndex()) {
                pageIdx[i] = page0
                monoArr[i] = webClient.mutate().build()
                    .get()
                    .uri {
                        var uriBuilder = it
                        for((k, v) in searchSession.request.fixedQueryParams())
                            uriBuilder = uriBuilder.queryParam(k, v)
                        for((k, v) in searchSession.request.pagedQueryParams(page0, searchSession.request.source.cachePageSize))
                            uriBuilder = uriBuilder.queryParam(k, v)
                        uriBuilder.build()
                    }
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String::class.java).subscribeOn(Schedulers.boundedElastic())
            }
            monoArr.forEachIndexed { i, mono ->
                cachePages.add(SearchCachePage(pageIdx[i], mono?.block()
                    ?: throw SearchException(
                       SearchExceptionCode.RETRIEVER_EXCEPTION, "failed to get resources", "$source")
                ))
            }
        } catch (ex: WebClientException) {
            throw SearchException(SearchExceptionCode.RETRIEVER_EXCEPTION, ex.message, "$source")
        }
        return if (cachePages.isEmpty()) null else cachePages
    }
}