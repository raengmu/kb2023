package com.ymkwon.kb2023.api.v1.service.search.domain.book.kakao

import com.ymkwon.kb2023.api.v1.service.search.domain.KakaoSearchSource
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component

@Component
data class KakaoBookSearchSource(
    private val searchParserMapper: KakaoBookSearchParserMapper,
    private val appProperties: ApplicationProperties
) : KakaoSearchSource(appProperties) {
    override val name: String
        get() = "kakao.book"

    override val url: String
        get() = appProperties.search.sources.kakao.book.url

    override val parserMapper: SearchParserMapper
        get() = searchParserMapper
}
