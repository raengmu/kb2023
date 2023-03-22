package com.ymkwon.kb2023.search.jpa.repository

import com.ymkwon.kb2023.search.dto.SearchCacheResultRawDto
import com.ymkwon.kb2023.search.jpa.entity.SearchCacheEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface SearchCacheRepository: JpaRepository<SearchCacheEntity, Long> {

    //ATTENTION: SHOULD be ordered by createAt to remove dup page items in after-processing
    @Query("select s.page as page, s.resultRaw as resRaw, s.createdAt as createdAt "+
            "from SearchCacheEntity s "+
            "where s.name = :name and s.query = :query and s.sorder = :sorder "+
                "and :pageBegin <= s.page and s.page < :pageEnd "+
                "and s.pageSize = :pageSize "+
            "order by s.page, s.createdAt desc")
    fun findAllRawResultForUpdate(
        name: String,
        query: String,
        sorder: String,
        pageBegin: Int,
        pageEnd: Int,
        pageSize: Int
    ): List<SearchCacheResultRawDto>

    @Transactional
    @Modifying
    @Query("delete from SearchCacheEntity s where s.createdAt <= :expiredAt")
    fun deleteAllByCreatedAtLessThanOrEqual(expiredAt: LocalDateTime): Int
}
