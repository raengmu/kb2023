package com.ymkwon.kb2023.api.v1.service.config

import com.ymkwon.kb2023.api.v1.service.search.retriever.PerfJsonWebSearchRetriever
import com.ymkwon.kb2023.api.v1.service.search.retriever.SimpleJsonWebSearchRetriever
import com.ymkwon.kb2023.config.WebClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SearchRetrieverConfig(
    webClientConfig: WebClientConfig
) {
    @get: Bean
    //val retriever = SimpleJsonWebSearchRetriever(webClientConfig)
    val retriever = PerfJsonWebSearchRetriever(webClientConfig)
}
