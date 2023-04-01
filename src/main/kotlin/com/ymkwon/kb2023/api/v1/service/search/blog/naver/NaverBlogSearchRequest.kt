package com.ymkwon.kb2023.api.v1.service.search.blog.naver

import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchRequest
import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchRequest
import com.ymkwon.kb2023.search.SearchSource

class NaverBlogSearchRequest(
    override val source: SearchSource,
    private val request: BlogSearchRequest
) : SearchRequest {
    override val page: Int
        get() = request.page

    override val pageSize: Int
        get() = request.pageSize

    override fun fixedQueryParams(): Map<String, String> =
        mapOf("query" to request.query,
              "sort" to if (request.sorder == SearchOrder.LATEST) "date" else "sim")

    override fun pagedQueryParams(page: Int, pageSize: Int): Map<String, String> =
        mapOf("start" to (page + 1).toString(),
              "display" to pageSize.toString())
}
