package com.ymkwon.kb2023.search.jpa.repository

import com.ymkwon.kb2023.search.jpa.entity.SearchQueryCountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType
import javax.persistence.QueryHint

@Repository
interface SearchQueryCountRepository: JpaRepository<SearchQueryCountEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    override fun findById(id: String): Optional<SearchQueryCountEntity>
}
