package com.ymkwon.kb2023.api.v1.controller

import com.ymkwon.kb2023.api.v1.service.search.jpa.JpaSearchService
import com.ymkwon.kb2023.api.v1.service.search.SearchService
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.exception.CommonException
import com.ymkwon.kb2023.exception.CommonExceptionCode
import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchQueryCount
import com.ymkwon.kb2023.search.SearchResult
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/search")
class SearchController(
    private val jpaSearchService: JpaSearchService,
    private val appProperties: ApplicationProperties
) {
    private val searchService: SearchService = jpaSearchService

    @GetMapping("/blog")
    fun searchBlog(
        @RequestParam query: String,
        @RequestParam(defaultValue = "A") order: Char,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): SearchResult {
        if (appProperties.search.maxQueryPageSize < pageSize)
            throw CommonException(CommonExceptionCode.INVALID_PARAMETER,
                "invalid request of pagesize", "pageSize:$pageSize")
        return searchService.searchBlog(
            query,
            if (order == 'L') SearchOrder.LATEST else SearchOrder.ACCURACY,
            page - 1,
            pageSize
        ) ?: throw CommonException(CommonExceptionCode.NO_DATA)
    }

    @GetMapping("/blog/countTop")
    fun searchQueryTop(
        @RequestParam(defaultValue = "10") num: Int,
    ): List<SearchQueryCount> =
        searchService.searchQueryTop(num)
}
