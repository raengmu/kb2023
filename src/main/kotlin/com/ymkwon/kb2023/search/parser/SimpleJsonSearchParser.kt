package com.ymkwon.kb2023.search.parser

import com.ymkwon.kb2023.search.SearchResult
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import org.json.JSONArray
import org.json.JSONObject

class SimpleJsonSearchParser {
    companion object {
        fun parse(
            page: Int,
            cacheRowBeginOffset: Int,
            cacheRowEndOffset: Int,
            cachePageSize: Int,
            jsonItemsList: List<JSONArray>,
            jsonItemConverter: (jsonItem: JSONObject) -> Any
        ): SearchResult {
            if (jsonItemsList.isEmpty() || (jsonItemsList.size < 2 && cacheRowBeginOffset > cacheRowEndOffset))
                throw SearchException(SearchExceptionCode.PARSING_ERROR, "assert failed",
                    "item size:${jsonItemsList.size}, cacheRowBeginOffset:$cacheRowBeginOffset, cacheRowEndOffset:$cacheRowEndOffset")

            val documents = ArrayList<Any>()

            // first
            val rowEndOffset =
                if (jsonItemsList.size > 1)
                    cachePageSize
                else
                    cacheRowEndOffset
            var docs = jsonItemsList[0]
            for(i in cacheRowBeginOffset until rowEndOffset)
                documents.add(jsonItemConverter(docs.getJSONObject(i)))

            // middle
            for(i in 1 until jsonItemsList.size-1) {
                docs = jsonItemsList[i]
                for(j in 0 until cachePageSize)
                    documents.add(jsonItemConverter(docs.getJSONObject(j)))
            }

            // last
            if (jsonItemsList.size > 1){
                docs = jsonItemsList.last()
                for(i in 0 until cacheRowEndOffset)
                    documents.add(jsonItemConverter(docs.getJSONObject(i)))
            }

            return SearchResult(
                page = page + 1,
                pageSize = documents.size,
                documents = documents
            )
        }
    }
}