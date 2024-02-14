package com.ymkwon.kb2023.searchjpa.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

data class SearchQueryCountEntityId(
    val category: String = "",
    val query: String = ""
): Serializable

@Entity
@Table(name = "tb_searchQueryCount")
@IdClass(SearchQueryCountEntityId::class)
class SearchQueryCountEntity(
    @Id
    @Column
    val category: String,

    @Id
    @Column
    val query: String,

    @Column(updatable = true)
    var cnt: Long = 0,

    //@CreationTimestamp - ATTENTION: Already LocalDateTime.now() assigned. In addition, this directive is specific on H2
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)