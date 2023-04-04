package com.ymkwon.kb2023.api.v1.service.search.domain.blog.kakao

import com.ymkwon.kb2023.api.v1.service.search.domain.KakaoSearchSource
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component

@Component
data class KakaoBlogSearchSource(
    private val searchParserMapper: KakaoBlogSearchParserMapper,
    private val appProperties: ApplicationProperties,
): KakaoSearchSource(appProperties) {
    override val name: String
        get() = "kakao.blog"

    override val url: String
        get() = appProperties.search.sources.kakao.blog.url

    override val parserMapper: SearchParserMapper
        get() = searchParserMapper
}
