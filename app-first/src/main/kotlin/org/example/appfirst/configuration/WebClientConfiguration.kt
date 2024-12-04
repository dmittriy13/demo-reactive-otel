package org.example.appfirst.configuration

import org.example.appfirst.configuration.properties.WebClientProperties
import org.example.appfirst.utils.WebClientUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfiguration {

    @Bean
    fun appSecondWebClient(properties: WebClientProperties): WebClient {
        return WebClientUtils.createWebclient(properties)
    }
}