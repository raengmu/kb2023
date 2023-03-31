package com.ymkwon.kb2023.api.v1.service.config

import com.ymkwon.kb2023.api.v1.service.searchjpa.blog.JpaBlogSearchService
import com.ymkwon.kb2023.api.v1.service.searchjpa.book.JpaBookSearchService
import com.ymkwon.kb2023.api.v1.service.searchjpa.JpaCommonSearchService
import org.springframework.context.annotation.Configuration

@Configuration
class SearchServiceConfig(
    val commonSearchService: JpaCommonSearchService,
    val blogSearchService: JpaBlogSearchService,
    val bookSearchService: JpaBookSearchService
)
