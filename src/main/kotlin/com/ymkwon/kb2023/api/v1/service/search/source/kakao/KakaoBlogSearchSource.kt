package com.ymkwon.kb2023.api.v1.service.search.source.kakao

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
        get() = appProperties.search.sources.kakao.url

    override val headers: Map<String, String>
        get() = mapOf(
            "Authorization" to appProperties.search.sources.kakao.restApiKey)

    override val cachePageSize: Int
        get() = appProperties.search.sources.kakao.cachePageSize

    override val retriever: SearchRetriever
        get() = searchRetriver

    override val parser: SearchParser
        get() = searchParser

    override val qsNameQuery: String
        get() = "query"

    override val qsNameSOrder: String
        get() = "sort"

    override val qsNamePage: String
        get() = "page"

    override val qsNamePageSize: String
        get() = "size"

    override fun qsSOrderValue(sorder: SearchOrder): String =
        if (sorder == SearchOrder.LATEST)
            "recency"
        else
            "accuracy"

}
