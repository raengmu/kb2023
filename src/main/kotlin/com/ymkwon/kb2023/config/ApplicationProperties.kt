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
                val name: String,
                val cachePageSize: Int,
                val url: String,
                val restApiKey: String
            )

            data class NaverProperties(
                val name: String,
                val cachePageSize: Int,
                val url: String,
                val clientId: String,
                val clientSecret: String
            )
        }
    }

    data class AsyncTaskPool(
        val maxPoolSize: Int
    )
}