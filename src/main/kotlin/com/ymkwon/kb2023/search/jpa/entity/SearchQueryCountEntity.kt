package com.ymkwon.kb2023.search.jpa.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tb_searchQueryCount")
class SearchQueryCountEntity(
    @Id
    @Column(unique = true)
    val query: String,

    @Column(updatable = true)
    var cnt: Long = 0,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
