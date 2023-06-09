package com.ymkwon.kb2023.search.parser

import com.ymkwon.kb2023.api.v1.service.search.domain.blog.BlogSearchResultDocument
import com.ymkwon.kb2023.search.SearchTestUtil
import com.ymkwon.kb2023.search.TestSearchParserMapper
import com.ymkwon.kb2023.search.exception.SearchException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val testPrefix = "t_"
private val parserMapper = TestSearchParserMapper("documents", testPrefix)

@SpringBootTest
class SimpleJsonSearchParserTest: FunSpec({
    test("invalid params") {
        val page = 0
        val cacheRowBeginOffset = 5
        val cacheRowEndOffset = 40

        val cachePageSize = 40
        var cachePages = List(1) { JSONArray() } // Wrong data
        shouldThrow<SearchException> {
            SearchTestUtil.parseInternal(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)
        }
        cachePages = List(2) { JSONArray() } // Wrong data
        shouldThrow<SearchException> {
            SearchTestUtil.parseInternal(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)
        }
        // wrong page offset info
        shouldThrow<SearchException> {
            SearchTestUtil.parseInternal(
                100, 3, 3, 44, cachePages, parserMapper)
        }

        val cachePageCount = 1
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "tt_", cachePageSize, cachePageCount) // wrong prefix
        shouldThrow<SearchException> {
            SearchTestUtil.parseInternal(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)
        }
    }

    test("end edge - various cache page count") {
        val page = 0
        val cacheRowBeginOffset = 5
        val cacheRowEndOffset = 40

        var cachePageSize = 40
        var cachePageCount = 1
        var cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        var result = SearchTestUtil.parseInternal(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 0, 35, SearchTestUtil.isBlogDocumentEqual)

        cachePageSize = 40
        cachePageCount = 2
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SearchTestUtil.parseInternal(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 0, 75, SearchTestUtil.isBlogDocumentEqual)

        cachePageSize = 40
        cachePageCount = 10
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SearchTestUtil.parseInternal(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 0, 395, SearchTestUtil.isBlogDocumentEqual)
    }

    test("normal cases - various cache page count") {
        val page = 10
        val cacheRowBeginOffset = 9
        val cacheRowEndOffset = 1

        val cachePageSize = 71
        var cachePageCount = 1  // begin offest > end offset in same page
        var cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        shouldThrow<SearchException> {
            SearchTestUtil.parseInternal(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)
        }

        cachePageCount = 2
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        var result = SearchTestUtil.parseInternal(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 9, 10, 63, SearchTestUtil.isBlogDocumentEqual)

        cachePageCount = 5
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SearchTestUtil.parseInternal(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, parserMapper)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 9, 10, 276, SearchTestUtil.isBlogDocumentEqual)
    }
})
