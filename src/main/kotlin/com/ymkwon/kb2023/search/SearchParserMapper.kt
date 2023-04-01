package com.ymkwon.kb2023.search

interface SearchParserMapper {
    val itemListName: String
    fun mapItem(parsedItem: Map<String, Any>): Any
}
