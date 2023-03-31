package com.ymkwon.kb2023.api.v1.service.searchjpa

import com.ymkwon.kb2023.api.v1.service.search.CommonSearchService
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import com.ymkwon.kb2023.searchjpa.JpaSearchQueryCountAccumulator
import org.springframework.stereotype.Service

@Service
class JpaCommonSearchService(
    private val queryCountAccumulator: JpaSearchQueryCountAccumulator
): CommonSearchService {
    override fun searchQueryTop(num: Int): List<SearchQueryCountDto> =
        queryCountAccumulator.searchQueryTop(num)

    override fun accumulateQueryTop(queries: Map<String, Map<String, Long>>) =
        queryCountAccumulator.accumulateCounts(queries)
}
