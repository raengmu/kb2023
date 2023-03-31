package com.ymkwon.kb2023.searchjpa

import com.ymkwon.kb2023.search.SearchQueryCountAccumulator
import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import com.ymkwon.kb2023.searchjpa.entity.SearchQueryCountEntity
import com.ymkwon.kb2023.searchjpa.repository.SearchQueryCountRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JpaSearchQueryCountAccumulator(
    private val queryCountRepository: SearchQueryCountRepository
): SearchQueryCountAccumulator {
    @Transactional(readOnly = true)
    override fun searchQueryTop(num: Int): List<SearchQueryCountDto> =
        queryCountRepository.findAllSumOfCnt(PageRequest.of(0, num))
//        queryCountRepository.findAll(
//            PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "cnt")))
//            .map { SearchQueryCount(query = it.query, count = it.cnt) }.toList()

    @Transactional(readOnly = true)
    override fun searchQueryTop(category: String, num: Int): List<SearchQueryCountDto> =
        queryCountRepository.findAllByCategory(category, PageRequest.of(0, num))

    @Transactional
    override fun accumulateCount(category: String, query: String, count: Long) {
        queryCountRepository.findByCategoryAndQuery(category, query).ifPresentOrElse( {
            it.cnt += count
            queryCountRepository.save(it)
        }, {
            queryCountRepository.save(SearchQueryCountEntity(category, query, count))
        })
    }

    @Transactional
    override fun accumulateCounts(queries: Map<String, Map<String, Long>>) {
        val entityMap = queryCountRepository.findAllByCategoryInAndQueryIn(
                        queries.keys, queries.values.flatMap { it.keys })
                        .associateBy { it.category to it.query }
        queries.forEach { (category, queryMap) ->
            queryMap.forEach { (query, cnt) ->
                val entity = entityMap[Pair(category, query)]
                if (entity == null)
                    queryCountRepository.save(
                        SearchQueryCountEntity(category = category, query = query, cnt = cnt)
                    )
                else
                    entity.cnt += cnt
            }
        }
        queryCountRepository.saveAll(entityMap.values)
    }
}