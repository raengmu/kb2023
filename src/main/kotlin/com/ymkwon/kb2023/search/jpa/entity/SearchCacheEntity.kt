package com.ymkwon.kb2023.search.jpa.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "tb_searchCache")
@Entity
class SearchCacheEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val name: String,

    @Column
    val query: String,

    @Column
    val sorder: String,

    @Column
    val page: Int,

    @Column
    val pageSize: Int,

    @Lob
    @Column(columnDefinition = "text")
    val resultRaw: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
