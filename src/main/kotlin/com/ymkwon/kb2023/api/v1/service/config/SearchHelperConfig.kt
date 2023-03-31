package com.ymkwon.kb2023.api.v1.service.config

import com.ymkwon.kb2023.config.ApplicationProperties
import com.ymkwon.kb2023.search.SearchHelper
import com.ymkwon.kb2023.search.SearchHelperImpl
import com.ymkwon.kb2023.searchjpa.JpaSearchCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchHelperConfig(
    searchCache: JpaSearchCache,
    appProperties: ApplicationProperties
) {
    @get:Bean
    val searchHelper: SearchHelper =
        SearchHelperImpl(searchCache, appProperties)
}
