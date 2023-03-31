package com.ymkwon.kb2023.searchjpa.repository

import com.ymkwon.kb2023.search.dto.SearchQueryCountDto
import com.ymkwon.kb2023.searchjpa.entity.SearchQueryCountEntity
import com.ymkwon.kb2023.searchjpa.entity.SearchQueryCountEntityId
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType
import javax.persistence.QueryHint

@Repository
interface SearchQueryCountRepository: JpaRepository<SearchQueryCountEntity, SearchQueryCountEntityId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    fun findByCategoryAndQuery(category: String, query: String): Optional<SearchQueryCountEntity>

    @Query("select s.query as query, s.cnt as cnt "+
            "from SearchQueryCountEntity s "+
            "where s.category = :category "+
            "order by s.cnt desc")
    fun findAllByCategory(category: String, page: Pageable): List<SearchQueryCountDto>

    @Query("select s.query as query, sum(s.cnt) as cnt "+
            "from SearchQueryCountEntity s "+
            "group by s.query "+
            "order by cnt desc")
    fun findAllSumOfCnt(page: Pageable): List<SearchQueryCountDto>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    @Query("select s "+
            "from SearchQueryCountEntity s "+
            "where s.category in :categories and s.query in :queries")
    fun findAllByCategoryInAndQueryIn(categories: Set<String>, queries: List<String>): Set<SearchQueryCountEntity>
}
