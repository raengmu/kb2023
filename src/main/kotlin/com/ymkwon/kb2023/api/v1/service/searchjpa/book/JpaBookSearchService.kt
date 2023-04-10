package com.ymkwon.kb2023.api.v1.service.searchjpa.book

import com.ymkwon.kb2023.api.v1.service.config.FallbackRequestBuilder
import com.ymkwon.kb2023.api.v1.service.search.SearchService
import com.ymkwon.kb2023.api.v1.service.config.SearchHelperConfig
import com.ymkwon.kb2023.api.v1.service.search.domain.book.BookSearchServiceRequest
import com.ymkwon.kb2023.api.v1.service.search.request.BaseSearchServiceRequest
import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.searchjpa.JpaSearchQueryCountAccumulator
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import org.springframework.stereotype.Service

@Service
class JpaBookSearchService(
    private val searchHelperConfig: SearchHelperConfig,
    private val fallbackRequestBuilder: FallbackRequestBuilder,
    private val queryCountAccumulator: JpaSearchQueryCountAccumulator
): SearchService {
    override val category: String
        get() = "book"

    override fun search(serviceRequest: BaseSearchServiceRequest): SearchResult? =
        searchHelperConfig.searchHelper.getResult(
            fallbackRequestBuilder.createBookFallbackRequests(serviceRequest as BookSearchServiceRequest))

    override fun searchQueryTop(num: Int): List<SearchQueryCountDto> =
        queryCountAccumulator.searchQueryTop(category, num)

    override fun accumulateQueryTop(query: String, count: Long) =
        queryCountAccumulator.accumulateCount(category, query, count)
}
