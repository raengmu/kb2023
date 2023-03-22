package com.ymkwon.kb2023.api.v1.service.search.source.naver

import com.ymkwon.kb2023.search.parser.SimpleJsonSearchParser
import com.ymkwon.kb2023.search.*
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class NaverBlogSearchParser: SearchParser {

    override fun parse(searchSession: SearchSession, raws: List<String>): SearchResult? {
        if (raws.isEmpty())
            return null

        return SimpleJsonSearchParser.parse(
            searchSession.page0.page,
            searchSession.page0.cacheRowBeginOffset,
            searchSession.page0.cacheRowEndOffset,
            searchSession.source.cachePageSize,
            List<JSONArray>(raws.size) { JSONObject(raws[it]).getJSONArray("items") }
        ) { jsonItem: JSONObject ->
            SearchResultDocument(
                title = jsonItem.getString("title"),
                content = jsonItem.getString("description"),
                url = jsonItem.getString("link"),
                name = jsonItem.getString("bloggername"),
                thumbnailUrl = "",
                datetime = LocalDateTime.of(LocalDate.parse(jsonItem.getString("postdate"),
                                            DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIDNIGHT)
            )
        }
    }
}