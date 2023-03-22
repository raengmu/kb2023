package com.ymkwon.kb2023.search.exception

import org.springframework.http.HttpStatus

class SearchException(
    val status: HttpStatus,
    val code: Int,
    override val message: String,
    val context: String
): RuntimeException() {
    constructor(ex: SearchExceptionCode, message: String? = null, context: String = "")
            : this(ex.status, ex.code, message ?: ex.message, context)
}
