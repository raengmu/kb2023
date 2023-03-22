package com.ymkwon.kb2023.search.jpa

import com.ymkwon.kb2023.search.SearchQueryCountAccumulator
import com.ymkwon.kb2023.search.jpa.entity.SearchQueryCountEntity
import com.ymkwon.kb2023.search.jpa.repository.SearchQueryCountRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class JpaSearchQueryCountAccumulator(
    private val queryCountRepository: SearchQueryCountRepository
): SearchQueryCountAccumulator {

    @Transactional
    override fun update(query: String, count: Long) {
        queryCountRepository.findById(query).ifPresentOrElse( {
            it.cnt += count
            queryCountRepository.save(it)
        }, {
            queryCountRepository.save(SearchQueryCountEntity(query, count))
        })
    }

}