package com.ymkwon.kb2023.search

interface SearchQueryCountAccumulator {
    fun update(query: String, count: Long)
}
