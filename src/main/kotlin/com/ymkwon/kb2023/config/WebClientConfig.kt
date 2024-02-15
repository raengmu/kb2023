package com.ymkwon.kb2023.config

import io.netty.channel.ChannelOption
import io.netty.handler.logging.LogLevel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.transport.logging.AdvancedByteBufFormat

@Configuration
class WebClientConfig(
    private val appProperties: ApplicationProperties
) {
    @Bean
    fun webClient(): WebClient {
        val webClientProp = appProperties.webClient
        return WebClient.builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs().maxInMemorySize(webClientProp.maxInMemorySizeKb)
            }
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create(connectionProvider())
                        .wiretap(
                            "reactor.netty.http.client.HttpClient",
                            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL
                        )
                        .option<Int>(
                            ChannelOption.CONNECT_TIMEOUT_MILLIS,
                            webClientProp.connectionTimeout.toMillis().toInt()
                        )
                        .responseTimeout(webClientProp.responseTimeout)
                )
            )
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
            )
            .build()
    }

    fun connectionProvider(): ConnectionProvider {
        val pool = appProperties.webClient.connPool
        return ConnectionProvider.builder("http-pool")
            .maxConnections(pool.maxConnectionNum) // connection pool의 갯수
            .pendingAcquireTimeout(pool.pendingAcquireTimeout) //커넥션 풀에서 커넥션을 얻기 위해 기다리는 최대 시간
            .pendingAcquireMaxCount(pool.pendingAcquireMaxCount) //커넥션 풀에서 커넥션을 가져오는 시도 횟수 (-1: no limit)
            .maxIdleTime(pool.maxIdleTime) //커넥션 풀에서 idle 상태의 커넥션을 유지하는 시간
            .build()
    }
}