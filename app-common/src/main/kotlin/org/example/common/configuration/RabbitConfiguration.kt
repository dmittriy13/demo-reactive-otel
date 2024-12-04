package org.example.common.configuration

import com.rabbitmq.client.*
import com.rabbitmq.client.impl.MicrometerMetricsCollector
import com.rabbitmq.client.impl.recovery.AutorecoveringConnection
import io.micrometer.core.instrument.MeterRegistry
import org.example.common.configuration.properties.RabbitConfigurationProperties
import org.example.common.configuration.properties.RabbitProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScans
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import reactor.rabbitmq.*

@Configuration
@Import(RabbitInitializer::class)
@EnableConfigurationProperties(value = [RabbitProperties::class, RabbitConfigurationProperties::class])
class RabbitConfiguration {

    @Bean
    fun connectionMono(rabbitProperties: RabbitProperties): Mono<Connection> =
        with(ConnectionFactory()) {
            this.host = rabbitProperties.host
            this.port = rabbitProperties.port
            this.username = rabbitProperties.username
            this.password = rabbitProperties.password
            this.virtualHost = rabbitProperties.virtualHost
            this.useNio()
            Mono.fromCallable(this::newConnection)
                .map {
                    if (it is AutorecoveringConnection) {
                        it.addRecoveryListener(RabbitMQRecoveryListener())
                    }

                    it
                }
                .cache()
        }

    @Bean
    fun sender(connectionMono: Mono<Connection>, rabbitProperties: RabbitProperties): Sender = RabbitFlux.createSender(
        SenderOptions()
            .connectionMono(connectionMono)
            .resourceManagementScheduler(Schedulers.boundedElastic())
    )

    @Bean
    fun receiver(connectionMono: Mono<Connection>): Receiver = RabbitFlux.createReceiver(
        ReceiverOptions()
            .connectionMono(connectionMono)
            .connectionSubscriptionScheduler(Schedulers.boundedElastic())
    )

    @Bean
    fun rabbitInitializingSink(): Sinks.Empty<Unit> = Sinks.empty()
}
