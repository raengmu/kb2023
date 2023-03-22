package com.ymkwon.kb2023.search

interface SearchParser {
    fun parse(searchSession: SearchSession, raws: List<String>): SearchResult?
}
