package com.ymkwon.kb2023.search

import java.util.*

interface SearchRetriever {
    fun retrieve(
        searchSession: SearchSession,
        absentCachePages: Set<Int>
    ): SortedSet<SearchCachePage>?
}
