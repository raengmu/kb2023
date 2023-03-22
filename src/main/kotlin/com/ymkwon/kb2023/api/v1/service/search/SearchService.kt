package com.ymkwon.kb2023.api.v1.service.search

import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchQueryCount
import com.ymkwon.kb2023.search.SearchResult

interface SearchService {
    fun searchBlog(
        query: String,
        sorder: SearchOrder,
        page: Int,
        pageSize: Int
    ): SearchResult?

    fun searchQueryTop(
        num: Int
    ): List<SearchQueryCount>
}
