package com.ymkwon.kb2023.api.v1.service.search.blog.naver

import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchResultDocument
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.search.retriever.SimpleWebSearchRetriever
import org.springframework.stereotype.Component

@Component
data class NaverBlogSearchSource(
    private val searchRetriver: SimpleWebSearchRetriever,
    private val searchParser: NaverBlogSearchParser,
    private val appProperties: ApplicationProperties,
) : SearchSource {
    override val name: String
        get() = appProperties.search.sources.naver.name

    override val url: String
        get() = appProperties.search.sources.naver.blog.url

    override val headers: Map<String, String>
        get() = mapOf(
            "X-Naver-Client-Id" to appProperties.search.sources.naver.clientId,
            "X-Naver-Client-Secret" to appProperties.search.sources.naver.clientSecret)

    override val cachePageSize: Int
        get() = appProperties.search.sources.naver.cachePageSize

    override val retriever: SearchRetriever
        get() = searchRetriver

    override val parser: SearchParser
        get() = searchParser
}