package com.ymkwon.kb2023.api.v1.service.search.blog.kakao

import com.ymkwon.kb2023.api.v1.service.search.blog.BlogSearchResultDocument
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class KakaoBlogSearchParserMapper: SearchParserMapper {
    override val itemListName: String
        get() = "documents"

    override fun mapItem(parsedItem: Map<String, Any>): Any =
        BlogSearchResultDocument(
            title = parsedItem["title"] as String,
            content = parsedItem["contents"] as String,
            url = parsedItem["url"] as String,
            name = parsedItem["blogname"] as String,
            thumbnailUrl = parsedItem["thumbnail"] as String,
            datetime = LocalDateTime.parse(
                parsedItem["datetime"] as String, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
}
