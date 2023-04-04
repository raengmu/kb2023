package com.ymkwon.kb2023.api.v1.service.search.domain.blog.naver

import com.ymkwon.kb2023.api.v1.service.search.domain.blog.BlogSearchResultDocument
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class NaverBlogSearchParserMapper: SearchParserMapper {
    override val itemListName: String
        get() = "items"

    override fun mapItem(parsedItem: Map<String, Any>): Any =
        BlogSearchResultDocument(
            title = parsedItem["title"] as String,
            content = parsedItem["description"] as String,
            url = parsedItem["link"] as String,
            name = parsedItem["bloggername"] as String,
            thumbnailUrl = "",
            datetime = LocalDateTime.of(LocalDate.parse(parsedItem["postdate"] as String,
                DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIDNIGHT)
        )
}