package com.ymkwon.kb2023.search

class SearchCachePage(
    val page: Int,
    val resRaw: String
): Comparable<SearchCachePage> {
    override fun compareTo(other: SearchCachePage): Int = this.page - other.page
}