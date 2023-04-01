package com.ymkwon.kb2023.api.v1.service.search.parser

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ymkwon.kb2023.search.SearchParser
import com.ymkwon.kb2023.search.SearchParserMapper
import com.ymkwon.kb2023.search.SearchResult
import com.ymkwon.kb2023.search.SearchSession
import com.ymkwon.kb2023.search.exception.SearchException
import com.ymkwon.kb2023.search.exception.SearchExceptionCode
import org.springframework.stereotype.Component

@Component
class SimpleJsonSearchParser: SearchParser {
    override fun parse(searchSession: SearchSession, raws: List<String>): SearchResult? {
        if (raws.isEmpty())
            return null

        return parseInternal(
            searchSession.page0.page,
            searchSession.page0.cacheRowBeginOffset,
            searchSession.page0.cacheRowEndOffset,
            searchSession.request.source.cachePageSize,
            raws,
            searchSession.request.source.parserMapper
        )
    }

    private fun parseInternal(
        page: Int,
        cacheRowBeginOffset: Int,
        cacheRowEndOffset: Int,
        cachePageSize: Int,
        raws: List<String>,
        parserMapper: SearchParserMapper
    ): SearchResult {
        try {
            val objectMapper = jacksonObjectMapper()
            val jsonItemsList = List(raws.size) {
                objectMapper.readValue(raws[it], object : TypeReference<Map<String, Any>>(){})[parserMapper.itemListName] as List<*>}

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
                @Suppress("UNCHECKED_CAST")
                documents.add(parserMapper.mapItem(docs[i] as Map<String, Any>))

            // middle
            for(i in 1 until jsonItemsList.size-1) {
                docs = jsonItemsList[i]
                for(j in 0 until cachePageSize)
                    @Suppress("UNCHECKED_CAST")
                    documents.add(parserMapper.mapItem(docs[j] as Map<String, Any>))
            }

            // last
            if (jsonItemsList.size > 1){
                docs = jsonItemsList.last()
                for(i in 0 until cacheRowEndOffset)
                    @Suppress("UNCHECKED_CAST")
                    documents.add(parserMapper.mapItem(docs[i] as Map<String, Any>))
            }

            return SearchResult(
                page = page + 1,
                pageSize = documents.size,
                documents = documents
            )
        } catch(ex: Exception) {
            throw SearchException(SearchExceptionCode.PARSING_ERROR, "parse error - ${ex.message}")
        }
    }
}