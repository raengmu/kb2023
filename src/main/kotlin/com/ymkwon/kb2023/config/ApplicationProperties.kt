package com.ymkwon.kb2023.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "properties")
data class ApplicationProperties(
    val webClient: Map<String, Int>,
    val search: SearchProperties,
    val asyncTaskPool: AsyncTaskPool
) {
    data class SearchProperties(
        val enableCache: Boolean,
        val cacheExpireSec: Int,
        val cacheTimeoutMsec: Int,
        val maxQueryPageSize: Int,
        val sources: SearchSourceProperties
    ) {
        data class SearchSourceProperties(
            val kakao: KakaoProperties,
            val naver: NaverProperties
        ) {
            data class KakaoProperties(
                val cachePageSize: Int,
                val restApiKey: String,
                val blog: Blog,
                val book: Book
            )

            data class NaverProperties(
                val cachePageSize: Int,
                val clientId: String,
                val clientSecret: String,
                val blog: Blog
            )

            data class Blog(
                val url: String
            )

            data class Book(
                val url: String
            )
        }
    }

    data class AsyncTaskPool(
        val maxPoolSize: Int
    )
}