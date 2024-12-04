package org.example.appfirst.configuration

import org.example.common.configuration.RabbitConfiguration
import org.example.common.dto.Event
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues

@Configuration
@ConfigurationPropertiesScan
@Import(value = [RabbitConfiguration::class])
class ApplicationConfiguration {

    @Bean
    fun eventsSink(): Sinks.Many<Event> {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false)
    }
}