package com.ymkwon.kb2023.api.v1.service.search.book.kakao

import com.ymkwon.kb2023.api.v1.service.search.book.BookSearchResultDocument
import com.ymkwon.kb2023.search.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class KakaoBookSearchParserMapper : SearchParserMapper {
    override val itemListName: String
        get() = "documents"

    override fun mapItem(parsedItem: Map<String, Any>): Any =
        BookSearchResultDocument(
            authors = (parsedItem["authors"] as List<*>).map { it as String },
            translators = (parsedItem["translators"] as List<*>).map { it as String },
            publisher = parsedItem["publisher"] as String,
            isbn = parsedItem["isbn"] as String,
            title = parsedItem["title"] as String,
            content = parsedItem["contents"] as String,
            url = parsedItem["url"] as String,
            priceKrw = parsedItem["price"] as Int,
            priceDiscountedKrw = parsedItem["sale_price"] as Int,
            thumbnailUrl = parsedItem["thumbnail"] as String,
            datetime = LocalDateTime.parse(
                parsedItem["datetime"] as String, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            status = parsedItem["status"] as String
        )
}