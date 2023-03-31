package com.ymkwon.kb2023.api.v1.service.search

import com.ymkwon.kb2023.search.dto.SearchQueryCountDto

interface CommonSearchService {
    fun searchQueryTop(num: Int): List<SearchQueryCountDto>
    fun accumulateQueryTop(queries: Map<String, Map<String, Long>>)
}