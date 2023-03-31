package com.ymkwon.kb2023.api.v1.service.searchjpa.blog

import com.ymkwon.kb2023.api.v1.service.search.SearchService
import com.ymkwon.kb2023.api.v1.service.config.SearchHelperConfig
import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.searchjpa.JpaSearchQueryCountAccumulator
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import org.springframework.stereotype.Service

@Service
class JpaBlogSearchService(
    private val searchHelperConfig: SearchHelperConfig,
    private val queryCountAccumulator: JpaSearchQueryCountAccumulator
): SearchService {
    override val category: String
        get() = "blog"

    override fun search(requests: List<SearchRequest>): SearchResult? =
        searchHelperConfig.searchHelper.getResult(requests)

    override fun searchQueryTop(num: Int): List<SearchQueryCountDto> =
        queryCountAccumulator.searchQueryTop(category, num)

    override fun accumulateQueryTop(query: String, count: Long) =
        queryCountAccumulator.accumulateCount(category, query, count)
}
