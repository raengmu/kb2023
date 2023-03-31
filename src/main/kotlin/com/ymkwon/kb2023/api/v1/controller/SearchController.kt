package com.ymkwon.kb2023.api.v1.controller

import com.ymkwon.kb2023.api.v1.service.search.AsyncSearchQueryCountAccumulator
import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.blog.kakao.KakaoBlogSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.blog.kakao.KakaoBlogSearchSource
import com.ymkwon.kb2023.api.v1.service.search.blog.naver.NaverBlogSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.blog.naver.NaverBlogSearchSource
import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchTarget
import com.ymkwon.kb2023.api.v1.service.search.book.kakao.KakaoBookSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.book.kakao.KakaoBookSearchSource
import com.ymkwon.kb2023.api.v1.service.config.SearchServiceConfig
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.exception.CommonException
import com.ymkwon.kb2023.exception.CommonExceptionCode
import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchResult
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/v1/search")
class SearchController(
    private val searchServiceConfig: SearchServiceConfig,
    private val asyncSearchQueryCountAccumulator: AsyncSearchQueryCountAccumulator,
    private val appProperties: ApplicationProperties,
    // Blog
    private val kakaoBlogSearchSource: KakaoBlogSearchSource,
    private val naverBlogSearchSource: NaverBlogSearchSource,
    // Book
    private val kakaoBookSearchSource: KakaoBookSearchSource
) {
    @GetMapping("/blog")
    fun searchBlog(
        @RequestParam query: String,
        @RequestParam(defaultValue = "A") order: Char,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): SearchResult {
        if (appProperties.search.maxQueryPageSize < pageSize)
            throw CommonException(CommonExceptionCode.INVALID_PARAMETER,
                "invalid request of pagesize", "pageSize:$pageSize")

        asyncSearchQueryCountAccumulator.accumulateQueryCount(searchServiceConfig.blogSearchService, query)

        val request = BlogSearchRequest(
            query =  query,
            page = page - 1,
            pageSize = pageSize,
            sorder = if (order == 'L') SearchOrder.LATEST else SearchOrder.ACCURACY)
        return searchServiceConfig.blogSearchService.search(listOf(
            KakaoBlogSearchRequest(kakaoBlogSearchSource, request),
            NaverBlogSearchRequest(naverBlogSearchSource, request)
        )) ?: throw CommonException(CommonExceptionCode.NO_DATA)
    }

    @GetMapping("/blog/countTop")
    fun searchBlogQueryTop(
        @RequestParam(defaultValue = "10") num: Int,
    ): List<SearchQueryCountDto> =
        searchServiceConfig.blogSearchService.searchQueryTop(num)

    @GetMapping("/book")
    fun searchBook(
        @RequestParam query: String,
        @RequestParam(defaultValue = "A") order: Char,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(defaultValue = "T") target: Char
    ): SearchResult {
        if (appProperties.search.maxQueryPageSize < pageSize)
            throw CommonException(CommonExceptionCode.INVALID_PARAMETER,
                "invalid request of pagesize", "pageSize:$pageSize")

        asyncSearchQueryCountAccumulator.accumulateQueryCount(searchServiceConfig.bookSearchService, query)

        val request = BookSearchRequest(
            query =  query,
            page = page - 1,
            pageSize = pageSize,
            sorder = if (order == 'L') SearchOrder.LATEST else SearchOrder.ACCURACY,
            target = when(target) {
                'I' -> BookSearchTarget.ISBN
                'P' -> BookSearchTarget.PUBLISHER
                'E' -> BookSearchTarget.PERSON
                else -> BookSearchTarget.TITLE
            })
        return searchServiceConfig.bookSearchService.search(listOf(
            KakaoBookSearchRequest(kakaoBookSearchSource, request)
        )) ?: throw CommonException(CommonExceptionCode.NO_DATA)
    }

    @GetMapping("/book/countTop")
    fun searchBookQueryTop(
        @RequestParam(defaultValue = "10") num: Int,
    ): List<SearchQueryCountDto> =
        searchServiceConfig.bookSearchService.searchQueryTop(num)

    @GetMapping("/countTop")
    fun searchCommonQueryTop(
        @RequestParam(defaultValue = "10") num: Int,
    ): List<SearchQueryCountDto> =
        searchServiceConfig.commonSearchService.searchQueryTop(num)
}
