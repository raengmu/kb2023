package com.ymkwon.kb2023.api.v1.service.search.jpa

import com.ymkwon.kb2023.api.v1.service.search.SearchService
import com.ymkwon.kb2023.api.v1.service.search.source.kakao.KakaoBlogSearchSource
import com.ymkwon.kb2023.api.v1.service.search.source.naver.NaverBlogSearchSource
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.*
import com.ymkwon.kb2023.search.jpa.JpaSearchCache
import com.ymkwon.kb2023.search.jpa.repository.SearchQueryCountRepository
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JpaSearchService(
    searchCache: JpaSearchCache,
    appProperties: ApplicationProperties,
    sourceKakao: KakaoBlogSearchSource,
    sourceNaver: NaverBlogSearchSource,
    private val queryCountAccumulator: JpaAsyncSearchQueryCountAccumulator,
    private val queryCountRepository: SearchQueryCountRepository
): SearchService {
    private val searchHelper: SearchHelper =
            SearchHelperImpl(listOf(sourceKakao, sourceNaver), searchCache, appProperties)

    override fun searchBlog(
        query: String,
        sorder: SearchOrder,
        page: Int,
        pageSize: Int
    ): SearchResult? {
        queryCountAccumulator.searchQueryAccumulate(query)
        return searchHelper.getResult(query, sorder, page, pageSize)
    }

    @Transactional(readOnly = true)
    override fun searchQueryTop(
        num: Int
    ): List<SearchQueryCount> =
        queryCountRepository.findAll(
            PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "cnt")))
            .map { SearchQueryCount(query = it.query, count = it.cnt) }.toList()

}
