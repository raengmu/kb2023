package com.ymkwon.kb2023.api.v1.service.search.book.kakao

import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchRequest
import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchTarget
import com.ymkwon.kb2023.search.SearchOrder
import com.ymkwon.kb2023.search.SearchRequest
import com.ymkwon.kb2023.search.SearchSource

class KakaoBookSearchRequest(
    override val source: SearchSource,
    private val request: BookSearchRequest
) : SearchRequest {
    override val page: Int
        get() = request.page

    override val pageSize: Int
        get() = request.pageSize

    override fun fixedQueryParams(): Map<String, String> =
        mapOf("query" to request.query,
              "sort" to if (request.sorder == SearchOrder.LATEST) "recency" else "accuracy",
              "target" to when(request.target) {
                  BookSearchTarget.ISBN -> "isbn"
                  BookSearchTarget.PUBLISHER -> "publisher"
                  BookSearchTarget.PERSON -> "person"
                  else -> "title"
              }
        )

    override fun pagedQueryParams(page: Int, pageSize: Int): Map<String, String> =
        mapOf("page" to (page + 1).toString(),
              "size" to pageSize.toString())
}
