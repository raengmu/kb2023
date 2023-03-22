package com.ymkwon.kb2023.search

class SearchQueryCount(
    val query: String,
    val count: Long
): Comparable<SearchQueryCount> {
    override fun compareTo(other: SearchQueryCount): Int = (other.count - this.count).toInt()
}