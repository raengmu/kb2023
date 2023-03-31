package com.ymkwon.kb2023.api.v1.service.search.blog

import com.ymkwon.kb2023.search.SearchOrder

class BlogSearchRequest(
    val query: String,
    val page: Int,
    val pageSize: Int,
    val sorder: SearchOrder
)