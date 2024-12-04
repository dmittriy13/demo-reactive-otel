package org.example.common.configuration

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Connection
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.example.common.configuration.properties.BindingProperties
import org.example.common.configuration.properties.ExchangeProperties
import org.example.common.configuration.properties.QueueProperties
import org.example.common.configuration.properties.RabbitConfigurationProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.rabbitmq.BindingSpecification
import reactor.rabbitmq.ExchangeSpecification
import reactor.rabbitmq.QueueSpecification
import reactor.rabbitmq.Sender

@Configuration
@ComponentScan
class RabbitInitializer(
    private val connectionMono: Mono<Connection>,
    private val sender: Sender,
    private val rabbitConfigurationProperties: RabbitConfigurationProperties,
    private val rabbitInitializingSink: Sinks.Empty<Unit>
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun init() = runBlocking {
        val exchangeDeclarations = declareExchanges(sender, rabbitConfigurationProperties.exchanges)
        val queueDeclarations = declareQueues(sender, rabbitConfigurationProperties.queues)
        val bindingDeclarations = declareBindings(sender, rabbitConfigurationProperties.bindings)

        val allDeclarations = exchangeDeclarations + queueDeclarations + bindingDeclarations

        Mono.`when`(allDeclarations)
            .doOnError {
                logger.error("Error occurred while declaring exchanges, queues and bindings", it)
                rabbitInitializingSink.tryEmitError(it)
            }
            .doOnSuccess {
                logger.info("exchanges, queues and bindings are declared")
                rabbitInitializingSink.tryEmitEmpty()
            }
            .awaitSingleOrNull()
    }

    @PreDestroy
    fun close() = runBlocking {
        connectionMono
            .doOnError {
                logger.info("RabbitMQ connection closing error occurred", it)
            }
            .doOnSuccess {
                if (it.isOpen) {
                    it.close()
                }
                logger.info("RabbitMQ connection is closed")
            }
            .awaitSingleOrNull()
    }

    fun declareExchanges(sender: Sender, exchanges: List<ExchangeProperties>): List<Mono<AMQP.Exchange.DeclareOk>> {
        return exchanges.map { exchange ->
            sender.declare(
                ExchangeSpecification.exchange(exchange.name)
                    .type(exchange.type)
                    .durable(exchange.durable)
            )
        }
    }

    fun declareQueues(sender: Sender, queues: List<QueueProperties>): List<Mono<AMQP.Queue.DeclareOk>> {
        return queues.map { queue ->
            sender.declare(
                QueueSpecification.queue(queue.name)
                    .durable(queue.durable)
                    .arguments(queue.arguments)
            )
        }
    }

    fun declareBindings(sender: Sender, bindings: List<BindingProperties>): List<Mono<AMQP.Queue.BindOk>> {
        return bindings.map { binding ->
            sender.bind(
                BindingSpecification.binding(
                    binding.exchange,
                    binding.routingKey,
                    binding.queue
                )
            )
        }
    }
}