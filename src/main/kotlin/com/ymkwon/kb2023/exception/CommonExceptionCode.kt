package com.ymkwon.kb2023.exception

import org.springframework.http.HttpStatus

enum class CommonExceptionCode(
    val status: HttpStatus,
    val code: Int,
    val message: String)
{
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, 1000, "invalid parameter"),
    MISSING_HEADER(HttpStatus.BAD_REQUEST, 1001, "missing header"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1002, "internal server error"),

    NO_DATA(HttpStatus.INTERNAL_SERVER_ERROR, 2000, "no data"),

    FROM_SEARCH(HttpStatus.BAD_REQUEST, 9000, "<DO NOT USE>"); // delimiter for search

    companion object {
        fun getSearchExceptionCode(code: Int): Int = FROM_SEARCH.code + code
    }
}
