package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchResultDocument
import com.ymkwon.kb2023.api.v1.service.search.parser.SimpleJsonSearchParser
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.InvocationTargetException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TestSearchParserMapper(
    override val itemListName: String,
    private val prefix: String
): SearchParserMapper {
    override fun mapItem(parsedItem: Map<String, Any>): Any {
        val d = LocalDateTime.parse(
            parsedItem["${prefix}datetime"] as String, DateTimeFormatter.ISO_LOCAL_DATE_TIME
        )
        return BlogSearchResultDocument(
            title = parsedItem["${prefix}title"] as String,
            content = parsedItem["${prefix}content"] as String,
            url = parsedItem["${prefix}url"] as String,
            name = parsedItem["${prefix}name"] as String,
            thumbnailUrl = parsedItem["${prefix}thumbnailUrl"] as String,
            datetime = d
        )
    }
}

class SearchTestUtil {
    companion object {
        fun createCachePageJSONArrayList(
            prefix: String,
            cachePageSize: Int,
            cachePageCount: Int
        ): List<JSONArray> {
            var idx = 0
            val cache = ArrayList<JSONArray>()
            repeat(cachePageCount) {
                val jsonArr = JSONArray()
                repeat(cachePageSize) {
                    val jsonObj = JSONObject(mapOf(
                        "${prefix}title" to "$idx:title",
                        "${prefix}content" to "$idx:content",
                        "${prefix}url" to "$idx:url",
                        "${prefix}name" to "$idx:name",
                        "${prefix}thumbnailUrl" to "$idx:thumbnailUrl",
                        "${prefix}datetime" to getDocumentDateTime(idx).toString()))
                    jsonArr.put(idx % cachePageSize, jsonObj)
                    ++idx
                }
                cache.add(jsonArr)
            }
            return cache
        }

        private fun cachePagesToRaws(cachePages: List<JSONArray>): List<String> =
            cachePages.map { "{ \"documents\": $it }" }

        private fun getDocumentDateTime(idx: Int): LocalDateTime =
                    LocalDateTime.of(
                        LocalDate.parse("${2000+idx}0101",
                            DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIDNIGHT)


        val isBlogDocumentEqual = { a: Any, idx: Int ->
            with(a as BlogSearchResultDocument) {
                title == "$idx:title" &&
                content == "$idx:content" &&
                url == "$idx:url" &&
                name == "$idx:name" &&
                thumbnailUrl == "$idx:thumbnailUrl" &&
                datetime == getDocumentDateTime(idx)
            }
        }

//        private fun isDocumentJSONObjectEqual(prefix: String, a: SearchResultDocument, idx: Int): Boolean =
//            a.getString("${prefix}title") == "$idx:title" &&
//                    a.getString("${prefix}content") == "$idx:content" &&
//                    a.getString("${prefix}url") == "$idx:url" &&
//                    a.getString("${prefix}name") == "$idx:name" &&
//                    a.getString("${prefix}thumbnailUrl") == "$idx:thumbnailUrl" &&
//                    a.getString("${prefix}datetime") == getDocumentDateTime(idx).toString()

        fun isDocumentJSONObjectListEqual(
            result: SearchResult,
            begin: Int,
            page: Int,
            pageSize: Int,
            equal: (Any, Int) -> Boolean
        ): Boolean {
            if (result.page != (page+1))
                return false
            if (result.pageSize != pageSize)
                return false
            val docs = result.documents
            if (docs.size != pageSize)
                return false
            var idx = begin
            for(doc in docs) {
                if (!equal(doc, idx))
                    return false
                ++idx
            }
            return true
        }

        private val parser = SimpleJsonSearchParser()
        fun parseInternal(
            page: Int,
            cacheRowBeginOffset: Int,
            cacheRowEndOffset: Int,
            cachePageSize: Int,
            cachePages: List<JSONArray>,
            parserMapper: SearchParserMapper
        ): SearchResult {
            val method = parser.javaClass.getDeclaredMethod("parseInternal",
                Int::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                List::class.java,
                SearchParserMapper::class.java)
            method.isAccessible = true
            val params = arrayOfNulls<Any>(6)
            val raws = cachePagesToRaws(cachePages)
            params[0] = page
            params[1] = cacheRowBeginOffset
            params[2] = cacheRowEndOffset
            params[3] = cachePageSize
            params[4] = raws
            params[5] = parserMapper
            try {
                return method.invoke(parser, *params) as SearchResult
            } catch(ex: InvocationTargetException) {
                throw ex.targetException
            }
        }
    }
}