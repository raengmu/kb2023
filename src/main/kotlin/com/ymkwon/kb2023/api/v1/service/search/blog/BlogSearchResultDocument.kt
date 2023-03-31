package com.ymkwon.kb2023.api.v1.service.search.blog

import java.time.LocalDateTime

class BlogSearchResultDocument(
    val title: String,
    val content: String,
    val url: String,
    val name: String,
    val thumbnailUrl: String,
    val datetime: LocalDateTime
)
