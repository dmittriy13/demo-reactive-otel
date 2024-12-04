package org.example.appfirst.controller

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.asCoroutineDispatcher
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import net.logstash.logback.marker.Markers
import org.example.common.dto.Event
import org.example.common.dto.EventAcknowledgment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors
import kotlin.random.Random

@RestController
@RequestMapping("/api/events")
class EventsController(
    private val webClient: WebClient,
    private val eventsSink: Sinks.Many<Event>
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostMapping
    suspend fun sendEvent(@RequestBody event: Event): EventAcknowledgment {
        logger.info(Markers.append("event", event), "Send event")
        val ack = webClient.post()
            .uri("/api/events")
            .bodyValue(event)
            .retrieve()
            .bodyToMono(EventAcknowledgment::class.java)
            .awaitSingle()

        logger.info("Event sent")
        return ack
    }

    @GetMapping("/consume", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun consumeEvents(): Flux<Event> {
        return eventsSink.asFlux()
    }

}