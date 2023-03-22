package com.ymkwon.kb2023.search.dto

import java.time.LocalDateTime

interface SearchCacheResultRawDto {
    val page: Int
    val resRaw: String
    val createdAt: LocalDateTime
}