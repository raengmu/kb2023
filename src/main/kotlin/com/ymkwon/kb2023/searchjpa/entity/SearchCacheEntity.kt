package com.ymkwon.kb2023.searchjpa.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(name = "tb_searchCache")
@Entity
class SearchCacheEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val cacheKey: ByteArray,

    @Column
    val page: Int,

    @Column
    val pageSize: Int,

    @Lob
    @Column(columnDefinition = "text")
    val resultRaw: String,

    //@CreationTimestamp - ATTENTION: Already LocalDateTime.now() assigned. In addition, this directive is specific on H2
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
