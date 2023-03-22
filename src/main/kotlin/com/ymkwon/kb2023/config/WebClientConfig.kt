package com.ymkwon.kb2023.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig(
    private val appProperties: ApplicationProperties
) {
    fun webClientBuilder(): WebClient.Builder =
        setDefaultConfig(WebClient.builder())

    private fun defaultHttpClient(): HttpClient =
        HttpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                appProperties.webClient["connectionTimeoutMsec"])
            .doOnConnected { connection-> connection
                .addHandlerLast(ReadTimeoutHandler(5))
                .addHandlerLast(WriteTimeoutHandler(5))
            }

    private fun setDefaultConfig(builder: WebClient.Builder): WebClient.Builder =
        builder.codecs { configurer->
                configurer.defaultCodecs().maxInMemorySize(
                    appProperties.webClient["maxInMemorySize"]!!)
            }.clientConnector(ReactorClientHttpConnector(defaultHttpClient()))
}