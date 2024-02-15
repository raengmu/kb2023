package com.ymkwon.kb2023.api.v1.service.search.retriever

import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchRetriever
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.math.min

class PerfJsonWebSearchRetriever(
    private val defaultWebClient: WebClient
): SearchRetriever {
    private val logger = KotlinLogging.logger {}

    override fun retrieve(
        searchSession: SearchSession,
        absentCachePages: Set<Int>
    ): SortedSet<SearchCachePage>? {
        val source = searchSession.request.source
        val webClient = try {
            defaultWebClient.mutate()
                .baseUrl(source.url)
                .defaultHeaders { source.headers.forEach { (k, v) -> it[k] = v } }
                .build()
        } catch (ex: WebClientException) {
            throw SearchException(SearchExceptionCode.RETRIEVER_EXCEPTION, ex.message, "$source")
        }
        val sortedAbsentCachePages = absentCachePages.sorted();
        val bodies = try {
            Flux.fromIterable(
                sortedAbsentCachePages.map { page0 ->
                    webClient
                        .get()
                        .uri {
                            var uriBuilder = it
                            for ((k, v) in searchSession.request.fixedQueryParams()) {
                                uriBuilder = uriBuilder.queryParam(k, v)
                            }
                            for ((k, v) in searchSession.request.pagedQueryParams(
                                page0,
                                searchSession.request.source.cachePageSize
                            )) {
                                uriBuilder = uriBuilder.queryParam(k, v)
                            }
                            uriBuilder.build()
                        }
                        .accept(MediaType.APPLICATION_JSON)
                        .exchangeToMono {
                            if (it.statusCode() == HttpStatus.OK)
                                it.bodyToMono(String::class.java)
                            else {
                                logger.warn("page:${page0} - statusCode:${it.statusCode()}")
                                Mono.just("")
                            }
                        }
                        .timeout(Duration.ofMillis(2_500))
                        .retry(1)
                }
            )
                .flatMapSequential({ it }, min(10, sortedAbsentCachePages.size))
                .timeout(Duration.ofMillis(5_000))
                .collectList()
                .block()
        } catch(tex: TimeoutException) {
            logger.warn("timeout: $tex")
            null
        } catch(wex: WebClientException) {
            throw SearchException(SearchExceptionCode.RETRIEVER_EXCEPTION, wex.message, "$source")
        } ?: return null

        if (bodies.size != sortedAbsentCachePages.size)
            return null

        val bodyIt = bodies.iterator()
        return sortedAbsentCachePages.map { SearchCachePage(it, bodyIt.next()) }.toSortedSet()
    }
}