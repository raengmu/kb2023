package com.ymkwon.kb2023.search

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.LocalDateTime

class SearchSession(
    val request: SearchRequest,
    val page0: SearchPage0,
    cacheExpireSec: Int
) {
    val expiredAt: LocalDateTime = LocalDateTime.now().minusSeconds(cacheExpireSec.toLong())
    val cacheKey: ByteArray by lazy {
        MessageDigest.getInstance("MD5").digest(
            ("${request.source.name}|${request.source.cachePageSize}|" +
            request.fixedQueryParams().values.joinToString(separator = "|"))
            .toByteArray(StandardCharsets.UTF_8))
    }

    override fun toString(): String =
        "cacheKey:$cacheKey, expiredAt:$expiredAt, source: { ${request.source} }, page0: { $page0 }"
}
