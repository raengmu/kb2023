package com.ymkwon.kb2023.search.exception

import org.springframework.http.HttpStatus

enum class SearchExceptionCode(
    val status: HttpStatus,
    val code: Int,
    val message: String
) {
    UNDEFINED(HttpStatus.INTERNAL_SERVER_ERROR, 1, "service not available temporarily"),
    FAILED_RETRIEVE_RESOURCE(HttpStatus.INTERNAL_SERVER_ERROR, 2, "failed to retrieve resources"),
    PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3, "failed to parse resources"),
    ASSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 4, "failed to process"),
    RETRIEVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, 5, "network or source not available"),
    TOO_BUSY(HttpStatus.INTERNAL_SERVER_ERROR, 6, "too busy")
}