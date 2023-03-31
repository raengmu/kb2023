package com.ymkwon.kb2023.api.v1.service.search.book.kakao

import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchResultDocument
import com.ymkwon.kb2023.search.parser.SimpleJsonSearchParser
import com.ymkwon.kb2023.search.*
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class KakaoBookSearchParser: SearchParser {
    override fun parse(searchSession: SearchSession, raws: List<String>): SearchResult? {
        if (raws.isEmpty())
            return null

        return SimpleJsonSearchParser.parse(
            searchSession.page0.page,
            searchSession.page0.cacheRowBeginOffset,
            searchSession.page0.cacheRowEndOffset,
            searchSession.request.source.cachePageSize,
            List<JSONArray>(raws.size) { JSONObject(raws[it]).getJSONArray("documents") }
        ) { jsonItem: JSONObject ->
            BookSearchResultDocument(
                authors = jsonItem.getJSONArray("authors").map { it as String },
                translators = jsonItem.getJSONArray("translators").map { it as String },
                publisher = jsonItem.getString("publisher"),
                isbn = jsonItem.getString("isbn"),
                title = jsonItem.getString("title"),
                content = jsonItem.getString("contents"),
                url = jsonItem.getString("url"),
                priceKrw = jsonItem.getInt("price"),
                priceDiscountedKrw = jsonItem.getInt("sale_price"),
                thumbnailUrl = jsonItem.getString("thumbnail"),
                datetime = LocalDateTime.parse(
                    jsonItem.getString("datetime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                status = jsonItem.getString("status")
            )
        }
    }
}