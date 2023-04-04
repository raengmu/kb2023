package com.ymkwon.kb2023.api.v1.service.search.request

import com.ymkwon.kb2023.search.SearchOrder

open class CommonSearchRequest(
    val query: String,
    val page: Int,
    val pageSize: Int,
    val sorder: SearchOrder
)
