package com.ymkwon.kb2023.search.parser

import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchResultDocument
import com.ymkwon.kb2023.search.SearchTestUtil
import com.ymkwon.kb2023.search.exception.SearchException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val prefix = "t_"
private val toSearchResultDocument = { jsonItem: JSONObject ->
    SearchResultDocument(
        title = jsonItem.getString("${prefix}title"),
        content = jsonItem.getString("${prefix}content"),
        url = jsonItem.getString("${prefix}url"),
        name = jsonItem.getString("${prefix}name"),
        thumbnailUrl = jsonItem.getString("${prefix}thumbnailUrl"),
        datetime = LocalDateTime.parse(
            jsonItem.getString("${prefix}datetime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
    )
}

@SpringBootTest
class SimpleJsonSearchParserTest: FunSpec({
    test("invalid params") {
        val page = 0
        val cacheRowBeginOffset = 5
        val cacheRowEndOffset = 40

        val cachePageSize = 40
        var cachePages = List(1) { JSONArray() } // Wrong data
        shouldThrow<JSONException> {
            SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        }
        cachePages = List(2) { JSONArray() } // Wrong data
        shouldThrow<JSONException> {
            SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        }
        // wrong page offset info
        shouldThrow<JSONException> {
            SimpleJsonSearchParser.parse(
                100, 3, 3, 44, cachePages, toSearchResultDocument)
        }

        val cachePageCount = 1
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "tt_", cachePageSize, cachePageCount) // wrong prefix
        shouldThrow<JSONException> {
            SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
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
        var result = SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 40, 0, 35)

        cachePageSize = 40
        cachePageCount = 2
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 80, 0, 75)

        cachePageSize = 40
        cachePageCount = 10
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 80, 0, 395)
    }

    test("normal cases - various cache page count") {
        val page = 10
        val cacheRowBeginOffset = 9
        val cacheRowEndOffset = 1

        var cachePageSize = 71
        var cachePageCount = 1  // begin offest > end offset in same page
        var cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        shouldThrow<SearchException> {
            SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        }

        cachePageCount = 2
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        var result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 9, 72, 10, 63)

        cachePageCount = 5
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)

        true shouldBe SearchTestUtil.isDocumentJSONObjectListEqual(result, 9, 72, 10, 276)
    }
})
