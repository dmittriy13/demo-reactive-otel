package org.example.appsecond.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import net.logstash.logback.marker.LogstashMarker
import net.logstash.logback.marker.Markers
import org.example.common.dto.Event
import org.example.common.dto.EventAcknowledgment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.rabbitmq.OutboundMessage
import reactor.rabbitmq.Sender

@RestController
@RequestMapping("/api/events")
class EventsController(
    private val sender: Sender,
    private val objectMapper: ObjectMapper
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostMapping
    suspend fun sendEvent(@RequestBody event: Event): EventAcknowledgment {
        logger.info(Markers.append("event", event), "Send event")
        sender.send(
            mono {
                OutboundMessage(
                    "events.ex",
                    "events.rk",
                    objectMapper.writeValueAsBytes(event)
                )
            }
        ).awaitSingleOrNull()
        logger.info("Event sent")
        return EventAcknowledgment(ack = true)
    }

}