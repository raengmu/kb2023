package com.ymkwon.kb2023.api.v1.service.search.book

import com.ymkwon.kb2023.search.SearchOrder

class BookSearchRequest(
    val query: String,
    val page: Int,
    val pageSize: Int,
    val sorder: SearchOrder,
    val target: BookSearchTarget,
)