package com.ymkwon.kb2023.search

import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchResultDocument
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
    }
}