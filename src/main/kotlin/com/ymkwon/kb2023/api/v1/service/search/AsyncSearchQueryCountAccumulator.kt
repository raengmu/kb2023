package com.ymkwon.kb2023.api.v1.service.search

import com.ymkwon.kb2023.api.v1.service.config.SearchServiceConfig
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service
class AsyncSearchQueryCountAccumulator(
    private val searchServiceConfig: SearchServiceConfig
) {
    @Async("taskExecutor")
    fun accumulateQueryCount(searchService: SearchService, query: String) =
        SearchQueryCountAggregator.increment(searchService.category, query)
        //searchService.accumulateQueryTop(query, 1)

    @Scheduled(fixedDelay = 10000)
    fun cronAccumulateQueryCount() =
        SearchQueryCountAggregator.commit { searchServiceConfig.commonSearchService.accumulateQueryTop(it) }
}
