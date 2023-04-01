package com.ymkwon.kb2023.api.v1.service.config

import com.ymkwon.kb2023.api.v1.service.search.parser.SimpleJsonSearchParser
import com.ymkwon.kb2023.api.v1.service.search.retriever.SimpleJsonWebSearchRetriever
import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.SearchHelper
import com.ymkwon.kb2023.search.SearchHelperImpl
import com.ymkwon.kb2023.searchjpa.JpaSearchCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchHelperConfig(
    searchCache: JpaSearchCache,
    appProperties: ApplicationProperties,
    searchRetriever: SimpleJsonWebSearchRetriever,
    searchParser: SimpleJsonSearchParser
) {
    @get:Bean
    val searchHelper: SearchHelper =
        SearchHelperImpl(searchCache, appProperties, searchRetriever, searchParser)
}
