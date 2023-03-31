package com.ymkwon.kb2023.search

interface SearchHelper {
    fun getResult(requests: List<SearchRequest>): SearchResult?
}
