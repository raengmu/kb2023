package com.ymkwon.kb2023.api.v1.service.search.domain.book

import com.ymkwon.kb2023.api.v1.service.search.request.CommonSearchRequest
import com.ymkwon.kb2023.search.SearchOrder

class BookSearchRequest(
    query: String,
    page: Int,
    pageSize: Int,
    sorder: SearchOrder,
    val target: BookSearchTarget
): CommonSearchRequest(query, page, pageSize, sorder)
