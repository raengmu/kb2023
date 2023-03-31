package com.ymkwon.kb2023.api.v1.service.search.book

import java.time.LocalDateTime

class BookSearchResultDocument(
    val authors: List<String>,
    val translators: List<String>,
    val isbn: String,
    val priceKrw: Int,
    val priceDiscountedKrw: Int,
    val publisher: String,
    val title: String,
    val content: String,
    val url: String,
    val status: String,
    val thumbnailUrl: String,
    val datetime: LocalDateTime
)
