package org.example.common.configuration.properties

import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.rabbitmq")
data class RabbitProperties(
    /**
     * RabbitMQ host
     */
    val host: String = "localhost",

    /**
     * RabbitMQ port. Default to 5672
     */
    val port: Int = 5672,

    /**
     * Login user to authenticate to the broker
     */
    val username: String = "guest",

    /**
     * Login to authenticate against the broker
     */
    val password: String = "guest",

    /**
     * Virtual host to use when connecting to the broker
     */
    val virtualHost: String = "/",

    /**
     * Quality of Service (prefetch count) when using acknowledgment
     */
    val qos: Int = 1
)

@ConfigurationProperties(prefix = "org.rabbit")
data class RabbitConfigurationProperties(
    val exchanges: List<ExchangeProperties>,
    val queues: List<QueueProperties>,
    val bindings: List<BindingProperties>
)

data class BindingProperties(
    @field:NotBlank
    val exchange: String,
    @field:NotBlank
    val queue: String,
    @field:NotBlank
    val routingKey: String
)

data class ExchangeProperties(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val type: String,
    @field:NotBlank
    val durable: Boolean
)

data class QueueProperties(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val durable: Boolean,
    @field:Nullable
    val arguments: Map<String, String> = emptyMap()
)