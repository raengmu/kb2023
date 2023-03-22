package com.ymkwon.kb2023.search

import java.time.LocalDateTime

class SearchResultDocument(
    val title: String,
    val content: String,
    val url: String,
    val name: String,
    val thumbnailUrl: String,
    val datetime: LocalDateTime
)
