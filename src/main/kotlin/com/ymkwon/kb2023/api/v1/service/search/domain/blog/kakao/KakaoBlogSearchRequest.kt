package com.ymkwon.kb2023.api.v1.service.search.domain.blog.kakao

import com.ymkwon.kb2023.api.v1.service.search.request.CommonSearchRequest
import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchRequest
import com.ymkwon.kb2023.search.SearchSource

class KakaoBlogSearchRequest(
    override val source: SearchSource,
    private val request: CommonSearchRequest
) : SearchRequest {
    override val page: Int
        get() = request.page

    override val pageSize: Int
        get() = request.pageSize

    override fun fixedQueryParams(): Map<String, String> =
        mapOf("query" to request.query,
              "sort" to if (request.sorder == SearchOrder.LATEST) "recency" else "accuracy")

    override fun pagedQueryParams(page: Int, pageSize: Int): Map<String, String> =
        mapOf("page" to (page + 1).toString(),
              "size" to pageSize.toString())
}
