package com.ymkwon.kb2023.api.v1.service.search.domain.book

import com.ymkwon.kb2023.api.v1.service.search.request.BaseSearchServiceRequest
import com.ymkwon.kb2023.search.SearchOrder

class BookSearchServiceRequest(
    query: String,
    page: Int,
    pageSize: Int,
    sorder: SearchOrder,
    val target: BookSearchTarget
): BaseSearchServiceRequest(query, page, pageSize, sorder)
