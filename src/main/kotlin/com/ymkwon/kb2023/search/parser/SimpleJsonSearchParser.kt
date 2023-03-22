package com.ymkwon.kb2023.search.parser

import com.ymkwon.kb2023.search.SearchResult
import com.ymkwon.kb2023.search.SearchResultDocument
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
            toSearchResultDocument: (jsonItem: JSONObject) -> SearchResultDocument
        ): SearchResult {
            val documents = ArrayList<SearchResultDocument>()

            // first
            val rowEndOffset =
                if (jsonItemsList.size > 1)
                    cachePageSize
                else
                    cacheRowEndOffset
            var docs = jsonItemsList[0]
            for(i in cacheRowBeginOffset until rowEndOffset)
                documents.add(toSearchResultDocument(docs.getJSONObject(i)))

            // middle
            for(i in 1 until jsonItemsList.size-1) {
                docs = jsonItemsList[i]
                for(j in 0 until cachePageSize)
                    documents.add(toSearchResultDocument(docs.getJSONObject(j)))
            }

            // last
            if (jsonItemsList.size > 1){
                docs = jsonItemsList.last()
                for(i in 0 until cacheRowEndOffset)
                    documents.add(toSearchResultDocument(docs.getJSONObject(i)))
            }

            return SearchResult(
                page = page + 1,
                pageSize = documents.size,
                documents = documents
            )
        }
    }
}