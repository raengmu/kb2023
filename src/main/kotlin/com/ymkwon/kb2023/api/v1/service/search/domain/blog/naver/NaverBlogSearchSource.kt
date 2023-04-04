package com.ymkwon.kb2023.api.v1.service.search.domain.blog.naver

import com.ymkwon.kb2023.api.v1.service.search.domain.NaverSearchSource
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component

@Component
data class NaverBlogSearchSource(
    private val searchParserMapper: NaverBlogSearchParserMapper,
    private val appProperties: ApplicationProperties,
): NaverSearchSource(appProperties) {
    override val name: String
        get() = "naver.blog"

    override val url: String
        get() = appProperties.search.sources.naver.blog.url

    override val parserMapper: SearchParserMapper
        get() = searchParserMapper
}
