package com.ymkwon.kb2023.exception

import org.springframework.http.HttpStatus

class CommonException(
    val status: HttpStatus,
    val code: Int,
    override val message: String,
    val context: String
): RuntimeException() {
    constructor(ex: CommonExceptionCode, message: String? = null, context: String = "")
            : this(ex.status, ex.code, message ?: ex.message, context)
}
