package com.ymkwon.kb2023.api.v1.service.config

import com.ymkwon.kb2023.api.v1.service.search.domain.blog.kakao.KakaoBlogSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.domain.blog.kakao.KakaoBlogSearchSource
import com.ymkwon.kb2023.api.v1.service.search.domain.blog.naver.NaverBlogSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.domain.blog.naver.NaverBlogSearchSource
import com.ymkwon.kb2023.api.v1.service.search.domain.book.BookSearchServiceRequest
import com.ymkwon.kb2023.api.v1.service.search.domain.book.kakao.KakaoBookSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.domain.book.kakao.KakaoBookSearchSource
import com.ymkwon.kb2023.api.v1.service.search.request.BaseSearchServiceRequest
import org.springframework.context.annotation.Configuration

@Configuration
class FallbackRequestBuilder(
    private val kakaoBlogSearchSource: KakaoBlogSearchSource,
    private val naverBlogSearchSource: NaverBlogSearchSource,
    private val kakaoBookSearchSource: KakaoBookSearchSource,
) {
    fun createBlogFallbackRequests(request: BaseSearchServiceRequest) =
        listOf(KakaoBlogSearchRequest(kakaoBlogSearchSource, request),
               NaverBlogSearchRequest(naverBlogSearchSource, request))

    fun createBookFallbackRequests(request: BookSearchServiceRequest) =
        listOf(KakaoBookSearchRequest(kakaoBookSearchSource, request))
}