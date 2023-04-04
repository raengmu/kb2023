package com.ymkwon.kb2023.api.v1.service.search.domain

import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*

abstract class KakaoSearchSource(
    private val appProperties: ApplicationProperties,
) : SearchSource {
    override val headers: Map<String, String>
        get() = mapOf(
            "Authorization" to appProperties.search.sources.kakao.restApiKey)

    override val cachePageSize: Int
        get() = appProperties.search.sources.kakao.cachePageSize
}
