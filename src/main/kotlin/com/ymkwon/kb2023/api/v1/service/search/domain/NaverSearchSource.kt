package com.ymkwon.kb2023.api.v1.service.search.domain

import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*

abstract class NaverSearchSource(
    private val appProperties: ApplicationProperties
) : SearchSource {
    override val headers: Map<String, String>
        get() = mapOf(
            "X-Naver-Client-Id" to appProperties.search.sources.naver.clientId,
            "X-Naver-Client-Secret" to appProperties.search.sources.naver.clientSecret)

    override val cachePageSize: Int
        get() = appProperties.search.sources.naver.cachePageSize
}
