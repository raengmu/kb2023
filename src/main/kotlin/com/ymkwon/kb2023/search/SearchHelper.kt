package com.ymkwon.kb2023.search

interface SearchHelper {
    fun getResult(
        query: String,
        sorder: SearchOrder,
        page: Int,
        pageSize: Int
    ): SearchResult?
}
