package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.exception.CommonException
import com.ymkwon.kb2023.exception.CommonExceptionCode

class SearchPage0(
    page0: Int,
    page0Size: Int,
    cachePageSize: Int
) {
    val rowBegin: Int
    val rowEnd: Int // end exclusive
    val pageSize: Int
        get() = rowEnd - rowBegin
    val page: Int
        get() = rowBegin / pageSize

    val cachePageBegin: Int
    val cachePageEnd: Int // end exclusive
    val cachePageCount: Int
        get() = cachePageEnd - cachePageBegin

    val cacheRowBeginOffset: Int
    val cacheRowEndOffset: Int

    init {
        if  (page0 < 0 || page0Size < 1 || cachePageSize < 1)
            throw CommonException(CommonExceptionCode.INVALID_PARAMETER,
                "failed to initialize search page0",
                "page calc - page0=$page0, page0Size=$page0Size")

        rowBegin = page0 * page0Size
        rowEnd = rowBegin + page0Size
        cachePageBegin = rowBegin / cachePageSize
        cachePageEnd = (rowEnd - 1) / cachePageSize + 1
        cacheRowBeginOffset = rowBegin.mod(cachePageSize)
        cacheRowEndOffset = (rowEnd - 1).mod(cachePageSize) + 1
    }

    override fun toString(): String =
        "{ rowBegin:$rowBegin, rowEnd: $rowEnd, "+
        "cachePageBegin: $cachePageBegin, cachePageEnd: $cachePageEnd, "+
        "cacheRowBeginOffset: $cacheRowBeginOffset, cacheRowEndOffset: $cacheRowEndOffset }"
}
