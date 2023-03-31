package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.search.dto.SearchQueryCountDto

interface SearchQueryCountAccumulator {
    fun searchQueryTop(num: Int): List<SearchQueryCountDto>
    fun searchQueryTop(category: String, num: Int): List<SearchQueryCountDto>
    fun accumulateCount(category: String, query: String, count: Long)
    fun accumulateCounts(queries: Map<String, Map<String, Long>>)
}
