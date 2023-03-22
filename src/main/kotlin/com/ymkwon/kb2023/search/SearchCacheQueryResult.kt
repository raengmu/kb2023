package com.ymkwon.kb2023.search

import java.util.*

class SearchCacheQueryResult(
    val foundExpired: Boolean,
    val cachePages: SortedSet<SearchCachePage>
)
