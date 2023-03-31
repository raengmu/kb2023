package com.ymkwon.kb2023.search

interface SearchRequest {
    val source: SearchSource
    val page: Int
    val pageSize: Int
    fun fixedQueryParams(): Map<String, String>
    fun pagedQueryParams(page: Int, pageSize: Int): Map<String, String>
}
