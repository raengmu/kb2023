package com.ymkwon.kb2023.search.parser

import com.ymkwon.kb2023.search.SearchCachePage
import com.ymkwon.kb2023.search.SearchResultDocument
import com.ymkwon.kb2023.search.SearchTestUtil
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.json.JSONObject
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SpringBootTest
class SimpleJsonSearchParserTest: FunSpec({
    test("end edge - various cache page count") {
        val prefix = "t_"
        val toSearchResultDocument = { jsonItem: JSONObject ->
            SearchResultDocument(
                title = jsonItem.getString("${prefix}title"),
                content = jsonItem.getString("${prefix}content"),
                url = jsonItem.getString("${prefix}url"),
                name = jsonItem.getString("${prefix}name"),
                thumbnailUrl = jsonItem.getString("${prefix}thumbnailUrl"),
                datetime = LocalDateTime.parse(
                    jsonItem.getString("${prefix}datetime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME
                )
            )}
        val page = 0
        val cacheRowBeginOffset = 5
        val cacheRowEndOffset = 40

        var cachePageSize = 40
        var cachePageCount = 1
        var cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        var result = SimpleJsonSearchParser.parse(
                page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 40, 0, 35)

        cachePageSize = 40
        cachePageCount = 2
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 80, 0, 75)

        cachePageSize = 40
        cachePageCount = 10
        cachePages = SearchTestUtil.createCachePageJSONArrayList(
            "t_", cachePageSize, cachePageCount)
        result = SimpleJsonSearchParser.parse(
            page, cacheRowBeginOffset, cacheRowEndOffset, cachePageSize, cachePages, toSearchResultDocument)
        SearchTestUtil.isDocumentJSONObjectListEqual(result, 5, 80, 0, 235)
    }
})