package org.example.appfirst.amqp

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asCoroutineDispatcher
import net.logstash.logback.marker.Markers
import org.example.appfirst.controller.EventsController
import org.example.appfirst.controller.EventsController.Companion
import org.example.common.configuration.properties.RabbitProperties
import org.example.common.dto.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import reactor.rabbitmq.ConsumeOptions
import reactor.rabbitmq.Receiver

@Component
@OptIn(ExperimentalCoroutinesApi::class)
class EventsReceiver(
    private val receiver: Receiver,
    private val objectMapper: ObjectMapper,
    private val rabbitProperties: RabbitProperties,
    private val rabbitInitializingSink: Sinks.Empty<Unit>,
    private val eventsSink: Sinks.Many<Event>
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @FlowPreview
    @PostConstruct
    fun init() = CoroutineScope(Dispatchers.Unconfined).launch {
        receiver.consumeManualAck("events.q", ConsumeOptions().qos(rabbitProperties.qos))
            .delaySubscription(rabbitInitializingSink.asMono())
            .asFlow() // to coroutine world
            .flatMapConcat { delivery ->
                flow { emit(delivery) }
                    .map { objectMapper.readValue(delivery.body, Event::class.java) }
                    .onEach {
                        logger.info(Markers.append("event", it), "Received event")
                        eventsSink.tryEmitNext(it).orThrow()
                    }
                    .onCompletion { delivery.ack() }
                    .catch { logger.error("Error occurred while receive event", it) }
            }
            .collect()
    }

}
