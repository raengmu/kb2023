package com.ymkwon.kb2023.api.v1.service.search

import com.ymkwon.kb2023.api.v1.service.search.request.BaseSearchServiceRequest
import com.ymkwon.kb2023.search.SearchResult
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto

interface SearchService {
    val category: String

    fun search(serviceRequest: BaseSearchServiceRequest): SearchResult?

    fun searchQueryTop(num: Int): List<SearchQueryCountDto>

    fun accumulateQueryTop(query: String, count: Long)
}
