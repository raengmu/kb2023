package com.ymkwon.kb2023.searchjpa.repository

import com.ymkwon.kb2023.search.dto.SearchCacheResultRawDto
import com.ymkwon.kb2023.searchjpa.entity.SearchCacheEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
interface SearchCacheRepository: JpaRepository<SearchCacheEntity, Long> {

    //ATTENTION: SHOULD be ordered by id(createAt) to remove dup page items in after-processing
    @Query("select s.page as page, s.resultRaw as resRaw, s.createdAt as createdAt "+
            "from SearchCacheEntity s "+
            "where s.cacheKey = :cacheKey "+
                "and :pageBegin <= s.page and s.page < :pageEnd "+
            "order by s.page, s.id desc")
    fun findAllRawResult(
        cacheKey: ByteArray,
        pageBegin: Int,
        pageEnd: Int
    ): List<SearchCacheResultRawDto>

    @Transactional
    @Modifying
    @Query("delete from SearchCacheEntity s where s.createdAt <= :expiredAt")
    fun deleteAllByCreatedAtLessThanOrEqual(expiredAt: LocalDateTime): Int
}
