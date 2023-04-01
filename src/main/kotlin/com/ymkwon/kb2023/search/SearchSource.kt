package com.ymkwon.kb2023.search

interface SearchSource {
    val name: String
    val url: String
    val headers: Map<String, String>
    val cachePageSize: Int
    val retriever: SearchRetriever?
        get() = null
    val parserMapper: SearchParserMapper
    fun isCacheEnabled(): Boolean = cachePageSize > 0
}
