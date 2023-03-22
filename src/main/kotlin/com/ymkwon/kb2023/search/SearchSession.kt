package com.ymkwon.kb2023.search

import java.time.LocalDateTime

class SearchSession(
    val source: SearchSource,
    val query: String,
    val sorder: SearchOrder,
    val page0: SearchPage0,
    cacheExpireSec: Int,
) {
    val expiredAt: LocalDateTime = LocalDateTime.now().minusSeconds(cacheExpireSec.toLong())

    override fun toString(): String =
        "{ expiredAt:$expiredAt source: $source, query: $query, "+
        "sorder: ${sorder.charCode}, page0: $page0 }"
}
