package com.ymkwon.kb2023.api.v1.service.search.blog.kakao

import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.search.retriever.SimpleWebSearchRetriever
import org.springframework.stereotype.Component

@Component
data class KakaoBlogSearchSource(
    private val searchRetriver: SimpleWebSearchRetriever,
    private val searchParser: KakaoBlogSearchParser,
    private val appProperties: ApplicationProperties,
) : SearchSource {
    override val name: String
        get() = appProperties.search.sources.kakao.name

    override val url: String
        get() = appProperties.search.sources.kakao.blog.url

    override val headers: Map<String, String>
        get() = mapOf(
            "Authorization" to appProperties.search.sources.kakao.restApiKey)

    override val cachePageSize: Int
        get() = appProperties.search.sources.kakao.cachePageSize

    override val retriever: SearchRetriever
        get() = searchRetriver

    override val parser: SearchParser
        get() = searchParser
}