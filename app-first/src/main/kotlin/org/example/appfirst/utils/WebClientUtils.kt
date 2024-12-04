package org.example.appfirst.utils

import io.netty.handler.logging.LogLevel
import org.example.appfirst.configuration.properties.WebClientProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.Exceptions
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import reactor.util.retry.Retry
import java.time.Duration

/**
 * Utility class for creating a WebClient with OAuth2 authorization.
 */
class WebClientUtils {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(this::class.java)

        /**
         * Creates a WebClient with OAuth2 authorization.
         *
         * @param properties The properties for the WebClient.
         * @return The created WebClient.
         */
        fun createWebclient(
            properties: WebClientProperties
        ): WebClient {
            val httpClient = HttpClient.create()
                .compress(true)
                .responseTimeout(Duration.ofSeconds(properties.responseTimeout))
                .also {
                    if (properties.isDebug) {
                        it.wiretap(
                            "reactor.netty.http.client.HttpClient",
                            LogLevel.DEBUG,
                            AdvancedByteBufFormat.TEXTUAL
                        )
                    }
                }
            return WebClient.builder()
                .clientConnector(ReactorClientHttpConnector(httpClient))
                .baseUrl(properties.url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs { it.defaultCodecs().enableLoggingRequestDetails(properties.isDebug) }
                .filter(createRetryFilter(properties))
                .build()
        }

        private fun createRetryFilter(properties: WebClientProperties): ExchangeFilterFunction {
            return ExchangeFilterFunction { request, next ->
                next.exchange(request)
                    .doOnError { throwable: Throwable ->
                        logger.debug("Error occurred while sending request", throwable)
                    }
                    .retryWhen(
                        Retry.fixedDelay(properties.retry, Duration.ofMillis(25))
                            .filter(::isNeedRetry)
                            .doBeforeRetry { signal -> logger.debug("Retry number {}", signal.totalRetries() + 1) }
                    )
            }
        }

        private fun isNeedRetry(throwable: Throwable): Boolean {
            if (isRetriesExhausted(throwable)) return false
            return when (throwable) {
                is WebClientResponseException -> throwable.statusCode.is5xxServerError
                is WebClientRequestException -> true
                else -> false
            }
        }

        private fun isRetriesExhausted(throwable: Throwable): Boolean {
            if (Exceptions.isRetryExhausted(throwable)) {
                return true
            }

            val cause = throwable.cause
            if (cause != null) {
                return isRetriesExhausted(cause)
            }

            return false
        }

    }
}
